package net.magli143.exo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;

import net.magli143.exo.membuf.ByReference;

public class ExoLibraryWithFallback implements ExoLibrary {

    public static final String EXECUTABLE_FALLBACK = "EXOMIZER_COMMANDLINE_FALLBACK";
    public static final String EXECUTABLE_FALLBACK_PARAMS = "EXOMIZER_COMMANDLINE_FALLBACK_EXTRA_PARAMS";
    
    private final static Collection<String> EXTRA_PARAMS;
    static {
    	final String params = System.getProperty(EXECUTABLE_FALLBACK_PARAMS, "");
    	EXTRA_PARAMS = Arrays.asList(params.split("//s*,//s*")).stream().filter(s -> !s.isEmpty()).collect(Collectors.toList());
	}
	private final ExoLibrary nativeInstance;
	private final String fallbackExecutable = System.getProperty(EXECUTABLE_FALLBACK);
	
	public final static ExoLibrary INSTANCE = new ExoLibraryWithFallback();
	
	private ExoLibraryWithFallback() {
		nativeInstance = ExoLibrary.INSTANCE;
	}
	
	@Override
	public void membuf_init(membuf sb) {
		nativeInstance.membuf_init(sb);
	}

	@Override
	public void membuf_clear(membuf sb) {
		nativeInstance.membuf_clear(sb);
	}

	@Override
	public void membuf_free(membuf sb) {
		nativeInstance.membuf_free(sb);
	}

	@Override
	public void membuf_new(PointerByReference sbp) {
		nativeInstance.membuf_new(sbp);
	}

	@Override
	public void membuf_new(ByReference[] sbp) {
		nativeInstance.membuf_new(sbp);
	}

	@Override
	public void membuf_delete(PointerByReference sbp) {
		nativeInstance.membuf_delete(sbp);
	}

	@Override
	public void membuf_delete(ByReference[] sbp) {
		nativeInstance.membuf_delete(sbp);
	}

	@Override
	public int membuf_memlen(membuf sb) {
		return nativeInstance.membuf_memlen(sb);
	}

	@Override
	public void membuf_truncate(membuf sb, int len) {
		nativeInstance.membuf_truncate(sb, len);
	}

	@Override
	public int membuf_trim(membuf sb, int pos) {
		return nativeInstance.membuf_trim(sb,  pos);
	}

	@Override
	public Pointer membuf_memcpy(membuf sb, int offset, Pointer mem, int len) {
		return nativeInstance.membuf_memcpy(sb,  offset, mem, len);
	}

	@Override
	public Pointer membuf_append(membuf sb, Pointer mem, int len) {
		return nativeInstance.membuf_append(sb,  mem, len);
	}

	@Override
	public Pointer membuf_append_char(membuf sb, byte c) {
		return nativeInstance.membuf_append_char(sb, c);
	}

	@Override
	public Pointer membuf_insert(membuf sb, int offset, Pointer mem, int len) {
		return nativeInstance.membuf_insert(sb,  offset,  mem, len);
	}

	@Override
	public void membuf_remove(membuf sb, int offset, int len) {
		nativeInstance.membuf_remove(sb, offset, len);
		
	}

	@Override
	public void membuf_atleast(membuf sb, int size) {
		nativeInstance.membuf_atleast(sb, size);
	}

	@Override
	public void membuf_atmost(membuf sb, int size) {
		nativeInstance.membuf_atmost(sb, size);
		
	}

	@Override
	public int membuf_get_size(membuf sb) {
		return nativeInstance.membuf_get_size(sb);
	}

	@Override
	public Pointer membuf_get(membuf sb) {
		return nativeInstance.membuf_get(sb);
	}

	@Override
	public void raw_log_formatter(FILE out, int level, Pointer context, Pointer log) {
		nativeInstance.raw_log_formatter(out, level, context, log);
	}

	@Override
	public void raw_log_formatter(FILE out, int level, String context, String log) {
		nativeInstance.raw_log_formatter(out, level, context, log);
	}

	@Override
	public log_ctx log_new() {
		return nativeInstance.log_new();
	}

	@Override
	public void log_delete(log_ctx ctx) {
		nativeInstance.log_delete(ctx);
	}

	@Override
	public void log_set_level(log_ctx ctx, int level) {
		nativeInstance.log_set_level(ctx, level);
	}

	@Override
	public void log_add_output_stream(log_ctx ctx, int min, int max, log_formatter_f default_f, FILE out_stream) {
		nativeInstance.log_add_output_stream(ctx, min, max, default_f, out_stream);
	}

	@Override
	public void log_vlog(log_ctx ctx, int level, Pointer context, log_formatter_f f, Pointer printf_str, Pointer argp) {
		nativeInstance.log_vlog(ctx, level, context, f, printf_str, argp);
	}

	@Override
	public void log_vlog(log_ctx ctx, int level, String context, log_formatter_f f, String printf_str, Pointer argp) {
		nativeInstance.log_vlog(ctx, level, context, f, printf_str, argp);
	}

	@Override
	public void log_log_default(Pointer printf_str, Object... varargs) {
		nativeInstance.log_log_default(printf_str, varargs);
	}

	@Override
	public void log_log_default(String printf_str, Object... varargs) {
		nativeInstance.log_log_default(printf_str, varargs);
	}

	@Override
	public void hex_dump(int level, Pointer p, int len) {
		nativeInstance.hex_dump(level, p, len);
	}

	@Override
	public void hex_dump(int level, ByteBuffer p, int len) {
		nativeInstance.hex_dump(level, p, len);
	}

	@Override
	public void print_crunch_flags(int level, Pointer default_outfile) {
		nativeInstance.print_crunch_flags(level, default_outfile);
	}

	@Override
	public void print_crunch_flags(int level, String default_outfile) {
		nativeInstance.print_crunch_flags(level, default_outfile);
	}

	@Override
	public void print_base_flags(int level, Pointer default_outfile) {
		nativeInstance.print_base_flags(level, default_outfile);
	}

	@Override
	public void print_base_flags(int level, String default_outfile) {
		nativeInstance.print_base_flags(level, default_outfile);
	}

	@Override
	public void handle_crunch_flags(int flag_char, Pointer flag_arg, print_usage_f print_usage, Pointer appl,
			common_flags options) {
		nativeInstance.handle_crunch_flags(flag_char, flag_arg, print_usage, appl, options);
	}

	@Override
	public void handle_crunch_flags(int flag_char, String flag_arg, print_usage_f print_usage, String appl,
			common_flags options) {
		nativeInstance.handle_crunch_flags(flag_char, flag_arg, print_usage, appl, options);
	}

	@Override
	public void handle_base_flags(int flag_char, Pointer flag_arg, print_usage_f print_usage, Pointer appl,
			PointerByReference default_outfilep) {
		nativeInstance.handle_base_flags(flag_char, flag_arg, print_usage, appl, default_outfilep);
	}

	@Override
	public void handle_base_flags(int flag_char, String flag_arg, print_usage_f print_usage, String appl,
			String[] default_outfilep) {
		nativeInstance.handle_base_flags(flag_char, flag_arg, print_usage, appl, default_outfilep);
	}

	@Override
	public void print_license() {
		nativeInstance.print_license();
	}

	@Override
	public void crunch_backwards(membuf inbuf, membuf outbuf, crunch_options options, crunch_info info) {
		if (fallbackExecutable != null) {
			boolean crunchBackwards = true;
			boolean decrunch = false;
			boolean reverse = false;
			callExomizer(inbuf, outbuf, options, info, crunchBackwards, decrunch, reverse);
		} else {
			nativeInstance.crunch_backwards(inbuf, outbuf, options, info);
		}
		
	}

	

	@Override
	public void crunch(membuf inbuf, membuf outbuf, crunch_options options, crunch_info info) {
		if (fallbackExecutable != null) {
			boolean crunchBackwards = false;
			boolean decrunch = false;
			boolean reverse = false;
			callExomizer(inbuf, outbuf, options, info, crunchBackwards, decrunch, reverse);
		} else {
			nativeInstance.crunch(inbuf, outbuf, options, info);
		}
	}

	@Override
	public void decrunch(int level, membuf inbuf, membuf outbuf) {
		if (fallbackExecutable != null) {
			boolean crunchBackwards = false;
			boolean decrunch = true;
			boolean reverse = false;
			callExomizer(inbuf, outbuf, null, null, crunchBackwards, decrunch, reverse);
			
		} else {
			nativeInstance.decrunch(level, inbuf, outbuf);
		}
	}

	@Override
	public void decrunch_backwards(int level, membuf inbuf, membuf outbuf) {
		if (fallbackExecutable != null) {
			boolean crunchBackwards = true;
			boolean decrunch = true;
			boolean reverse = false;
			callExomizer(inbuf, outbuf, null, null, crunchBackwards, decrunch, reverse);
		} else {
			nativeInstance.decrunch_backwards(level, inbuf, outbuf);
		}
	}

	@Override
	public void reverse_buffer(Pointer start, int len) {
		nativeInstance.reverse_buffer(start, len);
		
	}

	@Override
	public void reverse_buffer(ByteBuffer start, int len) {
		nativeInstance.reverse_buffer(start, len);
		
	}

	protected void callExomizer(membuf inbuf, membuf outbuf, crunch_options options, crunch_info info,
			boolean crunchBackwards, boolean decrunch, boolean reverse) {
		
		FileOutputStream fos = null;
		File inFile = null, outFile = null;
		try {
			inFile = File.createTempFile("exo", "in");
			fos = new FileOutputStream(inFile);
			writeMembuf(inbuf, fos);
			outFile = File.createTempFile("exo", "out");
			
			List<String> cmdline = new ArrayList<>();
			cmdline.add(fallbackExecutable);
			cmdline.add("raw");
			if (decrunch) {
				cmdline.add("-d");
			}
			if (reverse) {
				cmdline.add("-r");
			}
			cmdline.add("-q");
			if (!decrunch) {
				if (options.use_imprecise_rle != 0) {
					cmdline.add("-C");
				}
				if (options.use_literal_sequences == 0) {
					cmdline.add("-c");
				}
			}
			if (crunchBackwards) {
				cmdline.add("-b");
			} 
			cmdline.addAll(EXTRA_PARAMS);
			
			cmdline.add("-o");
			cmdline.add(outFile.getAbsolutePath());
			cmdline.add(inFile.getAbsolutePath());
			Process exec = Runtime.getRuntime().exec(
					cmdline.toArray(new String[]{}));
			try {
				exec.waitFor();
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
			if ( exec.exitValue() != 0) {
				throw new RuntimeException("Exit value was: " + exec.exitValue());
			}
			byte[] outBytes = Files.readAllBytes(outFile.toPath());
			outbuf.setAutoSynch(true);
			Pointer p = new Memory(outBytes.length);
			p.write(0, outBytes, 0, outBytes.length);
			membuf_append(outbuf, p, outBytes.length);
			if (!decrunch) {
				info.needed_safety_offset = 0;
				info.literal_sequences_used = options.use_literal_sequences;
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			try {
				fos.close();
			} catch (Exception e) {
			}
			try { inFile.delete(); } catch (Exception e) {}
			try { outFile.delete(); } catch (Exception e) {}
		}
	}

	protected void writeMembuf(membuf inbuf, OutputStream fos) throws IOException {
		Pointer membuf_get = membuf_get(inbuf);
		byte[] byteArray = membuf_get.getByteArray(0L, membuf_memlen(inbuf));
		fos.write(byteArray);
		fos.flush();
	}
}
