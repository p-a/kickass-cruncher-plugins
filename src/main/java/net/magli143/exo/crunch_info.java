package net.magli143.exo;
import com.sun.jna.Structure;
import java.util.Arrays;
import java.util.List;
/**
 * <i>native declaration : exo_helper.h:923</i><br>
 * This file was autogenerated by <a href="http://jnaerator.googlecode.com/">JNAerator</a>,<br>
 * a tool written by <a href="http://ochafik.com/">Olivier Chafik</a> that <a href="http://code.google.com/p/jnaerator/wiki/CreditsAndLicense">uses a few opensource projects.</a>.<br>
 * For help, please visit <a href="http://nativelibs4java.googlecode.com/">NativeLibs4Java</a> , <a href="http://rococoa.dev.java.net/">Rococoa</a>, or <a href="http://jna.dev.java.net/">JNA</a>.
 */
public class crunch_info extends Structure {
	public int literal_sequences_used;
	public int needed_safety_offset;
	public crunch_info() {
		super();
	}
	protected List<? > getFieldOrder() {
		return Arrays.asList("literal_sequences_used", "needed_safety_offset");
	}
	public crunch_info(int literal_sequences_used, int needed_safety_offset) {
		super();
		this.literal_sequences_used = literal_sequences_used;
		this.needed_safety_offset = needed_safety_offset;
	}
	public static class ByReference extends crunch_info implements Structure.ByReference {
		
	};
	public static class ByValue extends crunch_info implements Structure.ByValue {
		
	};
}
