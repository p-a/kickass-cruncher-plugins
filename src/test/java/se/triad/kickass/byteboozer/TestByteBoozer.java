package se.triad.kickass.byteboozer;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.testng.annotations.Test;

import cml.kickass.plugins.interf.IMemoryBlock;
import cml.kickass.plugins.interf.IValue;

import se.triad.kickass.common.StubEngine;
import se.triad.kickass.common.TestUtils;
import se.triad.kickass.exomizer.MemBlock;

@Test
public class TestByteBoozer {

	@Test
	public void testBasicBoozer() throws Exception {
		
		byte[] input = TestUtils.resourceToByteArray("./elgena.png");
		
		ByteBoozer boozer = new ByteBoozer();
		
		List<IMemoryBlock> blocks = new ArrayList<IMemoryBlock>();
		blocks.add(new MemBlock("elgena", input, 0x2000));
		
		byte[] result = boozer.execute(blocks, new IValue[]{}, new StubEngine());
		
		byte[] expected = TestUtils.resourceToByteArray("./elgena_0x2000.prg.bb");
		
		Assert.assertEquals(expected[0], 0x62);
		Assert.assertEquals(expected[1], (byte)0xeb);
		
		for (int i =0; i < result.length; i++){
			if (result[i] != expected[i+2]){
				Assert.fail("Fail at position " + i + " " + expected[i+2] + " != " + result[i]);
			}
		}
		
		
		
	}
}
