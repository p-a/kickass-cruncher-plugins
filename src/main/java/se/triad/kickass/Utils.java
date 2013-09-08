package se.triad.kickass;

public class Utils {

	public static String asHex(int i){
		return Integer.toHexString(0x10000 | i).substring(1);
	}

}
