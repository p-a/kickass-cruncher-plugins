package se.triad.kickass.exomizer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import se.triad.kickass.CrunchedObject;
import net.magli143.exo.*;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;

public final class ExoHelper  {

    public static final String DISABLE_EXOMIZER_CACHE = "DISABLE_EXOMIZER_CACHE";

    public static final int MAX_OFFSET = 65535;
    private static final int PASSES = 65535;
    private static final int MAX_LENGTH = 65535;
    private static final int USE_IMPRECISE_RLE = 0;

    private static CrunchedObject doCrunch(byte[] data, boolean forward, boolean useLiterals, int in_load, int max_offset){

        final ExoLibrary exolib = ExoLibrary.INSTANCE;

        if (max_offset < 0 || max_offset > 65535)
            max_offset = MAX_OFFSET;

        crunch_options options = new crunch_options(null, PASSES, max_offset, MAX_LENGTH, useLiterals ? 1 : 0, USE_IMPRECISE_RLE);
        crunch_info info = new crunch_info();

        membuf in = new membuf();
        membuf crunched = new membuf();

        exolib.membuf_init(in);
        exolib.membuf_init(crunched);

        Pointer m = new Memory(data.length);
        m.write(0, data, 0, data.length);

        exolib.membuf_append(in,m, data.length);

        if (forward){

            if (in_load > -1){
                exolib.membuf_append_char(crunched, (byte)( in_load >> 8));
                exolib.membuf_append_char(crunched, (byte)( in_load & 255));
            }
            exolib.crunch(in, crunched, options, info);

        } else {

            exolib.crunch_backwards(in,  crunched,  options,  info);

            if (in_load > -1){
                exolib.membuf_append_char(crunched, (byte)( (in_load+in.len) & 255));
                exolib.membuf_append_char(crunched, (byte)( (in_load+in.len) >> 8));
            }

        }

        int length = exolib.membuf_memlen(crunched);

        return new CrunchedObject(exolib.membuf_get(crunched).getByteArray(0, length), info.needed_safety_offset);
    }

    public static CrunchedObject crunch(byte[] data, boolean forward, boolean useLiterals, int in_load, int max_offset){

        boolean cache = !Boolean.getBoolean(DISABLE_EXOMIZER_CACHE);

        File f = cache ? getFilename(data, forward, useLiterals, in_load) : null;
        CrunchedObject retval = cache ? getCachedObject(f) : null;

        if (retval == null){
            retval = doCrunch(data, forward, useLiterals, in_load, max_offset);

            if (cache)
                cacheData(f, retval);
        }

        return retval;
    }

    private static void cacheData(File f, CrunchedObject obj) {

        DataOutputStream os = null;
        try {
            os = new DataOutputStream(new FileOutputStream(f, false));
            os.writeInt(obj.address);
            os.write(obj.data);
            os.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (os != null){
                try {
                    os.close();
                } catch (IOException e) {
                    //nvm
                }
            }
        }
    }

    protected static CrunchedObject getCachedObject(File f) {
        CrunchedObject retval = null;
        DataInputStream is = null;
        try {
            if (f.exists() && f.canRead() && f.length() > 0 ){
                is = new DataInputStream(new FileInputStream(f));
                f.length();
                int safety = is.readInt();
                byte[] filedata = new byte[(int)f.length() - 4];
                is.readFully(filedata);
                retval = new CrunchedObject(filedata, safety);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (is != null){
                try {
                    is.close();
                } catch (IOException e) {
                    //nvm
                }
            }
        }

        return retval;
    }

    private static File getFilename(byte[] data, boolean forward,
            boolean useLiterals, int in_load)  {
        File f = null;
        try {
            MessageDigest md;
            md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(data);
            StringBuilder buildr = new StringBuilder();
            buildr.append(asHex(digest))
            .append(forward ? 'F' : 'B')
            .append(useLiterals ? 'L' : 'N')
            .append(in_load).append(".exo");

            String tempDir = System.getProperty("java.io.tmpdir");
            f = new File(tempDir, buildr.toString());
        } catch (NoSuchAlgorithmException e) {
            // won't happen
        }
        return f;
    }

    private final static String HEX = "0123456789ABCDEF";

    private static String asHex(byte[] digest) {
        StringBuilder buildr = new StringBuilder();
        for (int i = 0; i < digest.length; i++){
            buildr.append(HEX.charAt((digest[i] & 0xF0) >> 4));
            buildr.append(HEX.charAt(digest[i] & 0x0F));
        }
        return buildr.toString();
    }

    public static byte[] decrunch(byte[] data, boolean forward){

        final ExoLibrary exolib = ExoLibrary.INSTANCE;

        membuf in = new membuf();
        membuf out = new membuf();

        exolib.membuf_init(in);
        exolib.membuf_init(out);

        Pointer m = new Memory(data.length);
        m.write(0, data, 0, data.length);

        exolib.membuf_append(in,m, data.length);

        if (forward){
            exolib.decrunch(0, in, out);
        } else {
            exolib.decrunch_backwards(0, in, out);
        }

        int length = exolib.membuf_memlen(out);

        return exolib.membuf_get(out).getByteArray(0, length);
    }


}
