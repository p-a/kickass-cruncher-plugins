package se.triad.kickass.exomizer;

import java.util.ArrayList;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

import se.triad.kickass.common.BooleanValue;
import se.triad.kickass.common.TestUtils;
import se.triad.kickass.common.StubEngine;
import cml.kickass.plugins.interf.IMemoryBlock;
import cml.kickass.plugins.interf.IValue;

@Test
public class TestLevelExomizer {


	@Test
	public void testLiteralsBackward() throws Exception {

		byte[] in = TestUtils.resourceToByteArray("./elgena.png");

		LevelExomizer levelExomizer = new LevelExomizer();

		List<IMemoryBlock> blocks = new ArrayList<IMemoryBlock>();
		blocks.add(new MemBlock("elgena", in, 0x2000));

		byte[] result = levelExomizer.execute(blocks, new IValue[]{new BooleanValue(false)}, new StubEngine());

		byte[] expected = TestUtils.resourceToByteArray("./elgena_lvl_0x2000_literals.exo");

		Assert.assertEquals(result, expected);

	}

	@Test
	public void testLiteralsForward() throws Exception {
		
		byte[] in = TestUtils.resourceToByteArray("./elgena.png");
		
		LevelExomizer levelExomizer = new LevelExomizer();
		
		List<IMemoryBlock> blocks = new ArrayList<IMemoryBlock>();
		blocks.add(new MemBlock("elgena", in, 0x2000));
		
		byte[] result = levelExomizer.execute(blocks, new IValue[]{new BooleanValue(true)}, new StubEngine());
		
		byte[] expected = TestUtils.resourceToByteArray("./elgena_lvl_0x2000_forward_literals.exo");
		
		Assert.assertEquals(result, expected);
		
	}
	
	@Test
	public void testMultiLiteralsBackward() throws Exception {

		byte[] in = TestUtils.resourceToByteArray("./elgena.png");

		LevelExomizer levelExomizer = new LevelExomizer();

		List<IMemoryBlock> blocks = new ArrayList<IMemoryBlock>();
		blocks.add(new MemBlock("elgena", in, 0x2000));
		blocks.add(new MemBlock("elgena", in, 0x2000));
		blocks.add(new MemBlock("elgena", in, 0x3000));

		byte[] result = levelExomizer.execute(blocks, new IValue[]{new BooleanValue(false)}, new StubEngine());

		byte[] expected = TestUtils.resourceToByteArray("./elgena_lvl_0x2000_0x2000_0x3000_literals.exo");

		Assert.assertEquals(result, expected);

	}
	
	@Test
	public void testMultiLiteralsForward() throws Exception {
		
		byte[] in = TestUtils.resourceToByteArray("./elgena.png");
		
		LevelExomizer levelExomizer = new LevelExomizer();
		
		List<IMemoryBlock> blocks = new ArrayList<IMemoryBlock>();
		blocks.add(new MemBlock("elgena", in, 0x2000));
		blocks.add(new MemBlock("elgena", in, 0x2000));
		blocks.add(new MemBlock("elgena", in, 0x3000));
		
		byte[] result = levelExomizer.execute(blocks, new IValue[]{new BooleanValue(true)}, new StubEngine());
		
		byte[] expected = TestUtils.resourceToByteArray("./elgena_lvl_0x2000_0x2000_0x3000_forward_literals.exo");
		
		Assert.assertEquals(result, expected);
		
	}
}
