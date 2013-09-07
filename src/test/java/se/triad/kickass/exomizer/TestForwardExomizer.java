package se.triad.kickass.exomizer;

import static se.triad.kickass.common.TestUtils.resourceToByteArray;

import java.util.ArrayList;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

import se.triad.kickass.common.BooleanValue;
import se.triad.kickass.common.StubEngine;
import cml.kickass.plugins.interf.IMemoryBlock;
import cml.kickass.plugins.interf.IValue;

@Test
public class TestForwardExomizer {

	@Test
	public void testLiteralsForward() throws Exception {

		byte[] in = resourceToByteArray("./elgena.png");

		ForwardMemExomizer memExomizer = new ForwardMemExomizer();

		List<IMemoryBlock> blocks = new ArrayList<IMemoryBlock>();
		blocks.add(new MemBlock("elgena", in, 0x2000));

		byte[] result = memExomizer.execute(blocks, new IValue[]{}, new StubEngine());

		byte[] expected = resourceToByteArray("./elgena_mem_0x2000_literals_forward.exo");

		Assert.assertEquals(result, expected);

	}

	@Test(expectedExceptions = {RuntimeException.class})
	public void testLiteralsForwardFailsOnWrongArgs() throws Exception {

		byte[] in = resourceToByteArray("./elgena.png");

		ForwardMemExomizer memExomizer = new ForwardMemExomizer();

		List<IMemoryBlock> blocks = new ArrayList<IMemoryBlock>();
		blocks.add(new MemBlock("elgena", in, 0x2000));

		memExomizer.execute(blocks, new IValue[]{new BooleanValue(true)}, new StubEngine());

	}
}
