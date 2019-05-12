package se.triad.kickass.exomizer;

import java.util.ArrayList;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import se.triad.kickass.common.BooleanValue;
import se.triad.kickass.common.TestUtils;
import se.triad.kickass.common.StubEngine;
import kickass.plugins.interf.general.IMemoryBlock;
import kickass.plugins.interf.general.IValue;

@Test
public class TestLevelExomizer {

	@BeforeTest
	public void disableCache(){
		System.setProperty(AbstractExomizer.DISABLE_EXOMIZER_CACHE, "true");
	}


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
	public void testMultiLiteralsBackwardOffsetArray() throws Exception {
		
		byte[] in = TestUtils.resourceToByteArray("./elgena.png");
		
		LevelExomizer levelExomizer = new LevelExomizer();
		
		List<IMemoryBlock> blocks = new ArrayList<IMemoryBlock>();
		blocks.add(new MemBlock("elgena", in, 0x2000));
		blocks.add(new MemBlock("elgena", in, 0x2000));
		blocks.add(new MemBlock("elgena", in, 0x3000));
		
		byte[] result = levelExomizer.execute(blocks, new IValue[]{new BooleanValue(false),new BooleanValue(true),new BooleanValue(true)}, new StubEngine());
		
		byte[] expected = TestUtils.resourceToByteArray("./elgena_lvl_0x2000_0x2000_0x3000_literals.exo");
		
		Assert.assertEquals(result.length-3*2, expected.length);
		Assert.assertEquals(getWord(result, result.length-3*2),0x144f);
		Assert.assertEquals(getWord(result, result.length-2*2), 0x289e);
		Assert.assertEquals(getWord(result, result.length-1*2), 0x144f * 3);
	}
	
	private static int getWord(byte[] result, int i) {
		return (result[i] & 0xFF ) | ( (result[i+1] & 0xFF) <<8 );
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
