package se.triad.kickass.exomizer;

import static se.triad.kickass.Utils.toHexString;

import java.util.EnumMap;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;

import se.triad.kickass.AbstractCruncher;
import se.triad.kickass.CrunchedObject;
import se.triad.kickass.Options;
import kickass.plugins.interf.general.IEngine;
import kickass.plugins.interf.general.IMemoryBlock;
import net.magli143.exo.ExoLibrary;
import net.magli143.exo.ExoLibraryWithFallback;
import net.magli143.exo.crunch_info;
import net.magli143.exo.crunch_options;
import net.magli143.exo.membuf;

public abstract class AbstractExomizer extends AbstractCruncher {

	@Deprecated
    public static final String DISABLE_EXOMIZER_CACHE = "DISABLE_EXOMIZER_CACHE";

    @Override
    protected CrunchedObject crunch(IMemoryBlock block,
            EnumMap<Options, Object> opts, IEngine iEngine) {

        return crunch(block.getBytes(),
                opts.containsKey(Options.FORWARD_CRUNCHING),
                opts.containsKey(Options.USE_LITERALS),
                opts.containsKey(Options.APPEND_IN_LOAD) ? block.getStartAddress() : -1,
                opts.containsKey(Options.MAXIMUM_OFFSET_SIZE) ? ((Integer) opts.get(Options.MAXIMUM_OFFSET_SIZE)).intValue() : -1);
    }

    @Override
    protected String formatAddress(int address) {
        return "Safety distance: "+toHexString(address);
    }
    
    public static final int MAX_OFFSET = 65535;
    private static final int PASSES = 65535;
    private static final int MAX_LENGTH = 65535;
    private static final int USE_IMPRECISE_RLE = 0;

    private CrunchedObject crunch(byte[] data, boolean forward, boolean useLiterals, int in_load, int max_offset){

        final ExoLibrary exolib = ExoLibraryWithFallback.INSTANCE;

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

	@Override
	protected boolean isCachingEnabled() {
        final boolean enabled = !Boolean.getBoolean(DISABLE_EXOMIZER_CACHE);
        return enabled && super.isCachingEnabled();
	}


    protected byte[] decrunch(byte[] data, boolean forward){

        final ExoLibrary exolib = ExoLibraryWithFallback.INSTANCE;

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