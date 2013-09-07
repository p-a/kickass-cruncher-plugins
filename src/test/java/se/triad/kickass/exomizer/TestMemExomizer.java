package se.triad.kickass.exomizer;

import java.util.ArrayList;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

import se.triad.kickass.common.BooleanValue;
import se.triad.kickass.common.IntValue;
import se.triad.kickass.common.StubEngine;

import cml.kickass.plugins.interf.IMemoryBlock;
import cml.kickass.plugins.interf.IValue;
import static se.triad.kickass.common.TestUtils.resourceToByteArray;

@Test
public class TestMemExomizer {

	@Test
	public void testLiteralsBackward() throws Exception {

		byte[] in = resourceToByteArray("./elgena.png");

		MemExomizer memExomizer = new MemExomizer();

		List<IMemoryBlock> blocks = new ArrayList<IMemoryBlock>();
		blocks.add(new MemBlock("elgena", in, 0x2000));

		byte[] result = memExomizer.execute(blocks, new IValue[]{}, new StubEngine());

		byte[] expected = resourceToByteArray("./elgena_mem_0x2000_literals.exo");

		Assert.assertEquals(result, expected);

	}

	@Test()
	public void testLiteralsBackwardMemoryAssertFail() throws Exception {

		byte[] in = resourceToByteArray("./elgena.png");

		MemExomizer memExomizer = new MemExomizer();

		List<IMemoryBlock> blocks = new ArrayList<IMemoryBlock>();
		blocks.add(new MemBlock("elgena", in, 0x2000));

		try {
			memExomizer.execute(blocks, new IValue[]{new BooleanValue(false), new BooleanValue(true), new IntValue(0x2000)}, new StubEngine());
			Assert.fail();
		} catch (RuntimeException ex){
			Assert.assertTrue(ex.getMessage().contains("cannot be decompressed at $"));
		}

	}

	@Test()
	public void testLiteralsBackwardMemoryAssert() throws Exception {

		byte[] in = resourceToByteArray("./elgena.png");

		MemExomizer memExomizer = new MemExomizer();

		List<IMemoryBlock> blocks = new ArrayList<IMemoryBlock>();
		blocks.add(new MemBlock("elgena", in, 0x2000));

		byte[] result = memExomizer.execute(blocks, new IValue[]{new BooleanValue(false), new BooleanValue(true), new IntValue(0x1ffe)}, new StubEngine());

		byte[] expected = resourceToByteArray("./elgena_mem_0x2000_literals.exo");

		Assert.assertEquals(result, expected);
	}

	@Test()
	public void testLiteralsBackwardMemoryAssertUpperLimitFail() throws Exception {

		byte[] in = resourceToByteArray("./elgena.png");

		MemExomizer memExomizer = new MemExomizer();

		List<IMemoryBlock> blocks = new ArrayList<IMemoryBlock>();
		blocks.add(new MemBlock("elgena", in, 0x2000));

		try {
			memExomizer.execute(blocks, new IValue[]{new BooleanValue(false), new BooleanValue(true), new IntValue(0x3445)}, new StubEngine());
			Assert.fail();
		} catch (RuntimeException ex){
			Assert.assertTrue(ex.getMessage().contains("cannot be decompressed at $"));
		}

	}
	
	@Test()
	public void testLiteralsBackwardMemoryAssertUpperLimit() throws Exception {

		byte[] in = resourceToByteArray("./elgena.png");

		MemExomizer memExomizer = new MemExomizer();

		List<IMemoryBlock> blocks = new ArrayList<IMemoryBlock>();
		blocks.add(new MemBlock("elgena", in, 0x2000));

		byte[] result =	memExomizer.execute(blocks, new IValue[]{new BooleanValue(false), new BooleanValue(true), new IntValue(0x3446)}, new StubEngine());
		byte[] expected = resourceToByteArray("./elgena_mem_0x2000_literals.exo");

		Assert.assertEquals(result, expected);		
	}

	@Test
	public void testBlobLiteralsBackward() throws Exception {

		byte[] in = resourceToByteArray("./elgena.png");

		MemExomizer memExomizer = new MemExomizer();

		List<IMemoryBlock> blocks = new ArrayList<IMemoryBlock>();
		blocks.add(new MemBlock("elgena", in, 0x2000));
		blocks.add(new MemBlock("elgena", in, 0x8000));

		byte[] result = memExomizer.execute(blocks, new IValue[]{}, new StubEngine());

		byte[] expected = resourceToByteArray("./elgena_mem_0x2000_0x8000.exo");

		Assert.assertEquals(result, expected);

	}

	@Test
	public void testBlobLiteralsForward() throws Exception {

		byte[] in = resourceToByteArray("./elgena.png");

		MemExomizer memExomizer = new MemExomizer();

		List<IMemoryBlock> blocks = new ArrayList<IMemoryBlock>();
		blocks.add(new MemBlock("elgena", in, 0x2000));
		blocks.add(new MemBlock("elgena", in, 0x8000));

		byte[] result = memExomizer.execute(blocks, new IValue[]{new BooleanValue(true)}, new StubEngine());

		byte[] expected = resourceToByteArray("./elgena_mem_0x2000_0x8000_forward.exo");

		Assert.assertEquals(result, expected);

	}

	@Test
	public void testLiteralsForward() throws Exception {

		byte[] in = resourceToByteArray("./elgena.png");

		MemExomizer memExomizer = new MemExomizer();

		List<IMemoryBlock> blocks = new ArrayList<IMemoryBlock>();
		blocks.add(new MemBlock("elgena", in, 0x2000));

		byte[] result = memExomizer.execute(blocks, new IValue[]{new BooleanValue(true)}, new StubEngine());

		byte[] expected = resourceToByteArray("./elgena_mem_0x2000_literals_forward.exo");

		Assert.assertEquals(result, expected);

	}

	@Test
	public void testNoLiteralsForward() throws Exception {

		byte[] in = resourceToByteArray("./elgena.png");

		MemExomizer memExomizer = new MemExomizer();

		List<IMemoryBlock> blocks = new ArrayList<IMemoryBlock>();
		blocks.add(new MemBlock("elgena", in, 0x2000));

		byte[] result = memExomizer.execute(blocks, new IValue[]{new BooleanValue(true), new BooleanValue(false)}, new StubEngine());

		byte[] expected = resourceToByteArray("./elgena_mem_0x2000_noliterals_forward.exo");

		Assert.assertEquals(result, expected);

	}

	@Test
	public void testNoLiteralsBackward() throws Exception {

		byte[] in = resourceToByteArray("./elgena.png");

		MemExomizer memExomizer = new MemExomizer();

		List<IMemoryBlock> blocks = new ArrayList<IMemoryBlock>();
		blocks.add(new MemBlock("elgena", in, 0x2000));

		byte[] result = memExomizer.execute(blocks, new IValue[]{new BooleanValue(false), new BooleanValue(false)}, new StubEngine());

		byte[] expected = resourceToByteArray("./elgena_mem_0x2000_noliterals.exo");

		Assert.assertEquals(result, expected);

	}
}
