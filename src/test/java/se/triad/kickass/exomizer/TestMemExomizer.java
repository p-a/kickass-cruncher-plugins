package se.triad.kickass.exomizer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.triad.kickass.common.BooleanValue;
import se.triad.kickass.common.IntValue;
import se.triad.kickass.common.StubEngine;

import kickass.plugins.interf.general.IMemoryBlock;
import kickass.plugins.interf.general.IValue;
import static se.triad.kickass.common.TestUtils.resourceToByteArray;

@Test
public class TestMemExomizer {

	@BeforeMethod
	public void disableCache(){
		System.setProperty(ExoHelper.DISABLE_EXOMIZER_CACHE, "true");
	}

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

	@Test(enabled = false) // fallback impl does not handle this case
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
	public void testBlobLiteralsBackwardCached() throws Exception {

		System.setProperty(ExoHelper.DISABLE_EXOMIZER_CACHE, "false");
		
		byte[] in = resourceToByteArray("./elgena.png");
		
		File f = new File(System.getProperty("java.io.tmpdir"), "3DEFEC21D64D00DA86B0860EB3651A06BL12288.exo");
		if (f.exists())
			f.delete();
		
		byte[] data, cachedData;
		long clocked = System.currentTimeMillis();
		{
			MemExomizer memExomizer = new MemExomizer();
			List<IMemoryBlock> blocks = new ArrayList<IMemoryBlock>();
			blocks.add(new MemBlock("elgena", in, 0x3000));
			blocks.add(new MemBlock("elgena", in, 0x8000));
			data = memExomizer.execute(blocks, new IValue[]{}, new StubEngine());
		}
		clocked = System.currentTimeMillis() - clocked;
		
		long cachedClocked = System.currentTimeMillis();
		{
			MemExomizer memExomizer = new MemExomizer();
			List<IMemoryBlock> blocks = new ArrayList<IMemoryBlock>();
			blocks.add(new MemBlock("elgena", in, 0x3000));
			blocks.add(new MemBlock("elgena", in, 0x8000));
			cachedData = memExomizer.execute(blocks, new IValue[]{}, new StubEngine());
		}
		cachedClocked = System.currentTimeMillis() - cachedClocked;
		
		//System.err.println("Clocked: " + clocked + " CachedClocked: " + cachedClocked);
		Assert.assertEquals(data,  cachedData);
		Assert.assertTrue(cachedClocked < clocked / 2);
		
		if (f.exists())
			f.delete();

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
