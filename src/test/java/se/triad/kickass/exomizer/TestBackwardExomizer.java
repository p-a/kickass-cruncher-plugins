package se.triad.kickass.exomizer;

import static se.triad.kickass.common.TestUtils.resourceToByteArray;

import java.util.ArrayList;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import se.triad.kickass.common.StubEngine;
import kickass.plugins.interf.general.IMemoryBlock;
import kickass.plugins.interf.general.IValue;

@Test
public class TestBackwardExomizer {

	@BeforeTest
	public void disableCache(){
		System.setProperty(ExoHelper.DISABLE_EXOMIZER_CACHE, "true");
	}
	
	@Test
	public void testLiteralsBackward() throws Exception {

		byte[] in = resourceToByteArray("./elgena.png");

		BackwardMemExomizer memExomizer = new BackwardMemExomizer();

		List<IMemoryBlock> blocks = new ArrayList<IMemoryBlock>();
		blocks.add(new MemBlock("elgena", in, 0x2000));

		byte[] result = memExomizer.execute(blocks, new IValue[]{}, new StubEngine());

		byte[] expected = resourceToByteArray("./elgena_mem_0x2000_literals.exo");

		Assert.assertEquals(result, expected);

	}
}
