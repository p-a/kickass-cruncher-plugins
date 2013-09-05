package se.triad.kickass.exomizer;

import net.magli143.exo.*;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;

public final class ExoHelper  {

	private static final int MAX_OFFSET = 65535;
	private static final int PASSES = 65535;

	public static class ExoObject {
		public byte[] data;
		public int safetyOffset;
	}


	public static ExoObject crunch(byte[] data, boolean forward, boolean useLiterals){
		return crunch(data, forward, useLiterals,-1);
	}
	public static ExoObject crunch(byte[] data, boolean forward, boolean useLiterals, int in_load){

		final ExoLibrary exolib = ExoLibrary.INSTANCE;

		crunch_options options = new crunch_options(null, PASSES, MAX_OFFSET, useLiterals ? 1 : 0);
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

		ExoObject retval = new ExoObject();
		retval.data = exolib.membuf_get(crunched).getByteArray(0, length);
		retval.safetyOffset = info.needed_safety_offset;

		return retval;
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
