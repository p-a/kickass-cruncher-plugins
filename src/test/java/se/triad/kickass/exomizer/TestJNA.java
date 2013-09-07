package se.triad.kickass.exomizer;
import junit.framework.Assert;
import net.magli143.exo.ExoLibrary;
import net.magli143.exo.crunch_info;
import net.magli143.exo.crunch_options;
import net.magli143.exo.membuf;

import org.testng.annotations.Test;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;


@Test
public class TestJNA {

	@Test
	public void testCrunch() throws Exception {

		ExoLibrary exolib = ExoLibrary.INSTANCE;

		crunch_options options = new crunch_options(null, 1000,1000, 0);
		crunch_info info = new crunch_info();

		final String txt = "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.";

		membuf in = new membuf();
		membuf crunched = new membuf();
		membuf out = new membuf();

		exolib.membuf_init(in);
		exolib.membuf_init(crunched);
		exolib.membuf_init(out);

		Pointer m = new Memory(txt.length() + 1);
		m.setString(0, txt);

		exolib.membuf_append(in, m, txt.length());

		exolib.crunch(in, crunched, options, info);
		
		System.out.printf("Crunched %d bytes to %d bytes, needed safety offset is %d\n", 
				exolib.membuf_memlen(in),
				exolib.membuf_memlen(crunched),
				info.needed_safety_offset);

		exolib.decrunch(0, crunched, out);

		Assert.assertEquals(txt.length(), exolib.membuf_memlen(out));
		Assert.assertEquals(txt, exolib.membuf_get(out).getString(0));

	}

}
