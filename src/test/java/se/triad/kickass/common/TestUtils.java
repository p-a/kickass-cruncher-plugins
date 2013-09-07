package se.triad.kickass.common;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import se.triad.kickass.exomizer.TestMemExomizer;

public final class TestUtils {

	public static byte[] resourceToByteArray(String resource) throws IOException {
		InputStream is = TestMemExomizer.class.getClassLoader().getResourceAsStream(resource);
		byte [] buf = new byte[8192];
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int read;
		while ( (read = is.read(buf)) > 0){
			baos.write(buf,0,read);
		}
		is.close();
		return baos.toByteArray();
	}
}
