package se.booze.byteboozer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

import kickass.plugins.interf.general.IMemoryBlock;
import kickass.plugins.interf.general.IValue;
import se.triad.kickass.MemBlock;
import se.triad.kickass.common.IntValue;
import se.triad.kickass.common.StubEngine;
import se.triad.kickass.common.TestUtils;

@Test
public class TestB2 {

	@Test
	public void testCrunchPNG() throws Exception {
        byte[] input = TestUtils.resourceToByteArray("./elgena.png");
        byte[] expected = TestUtils.resourceToByteArray("./elgena.png.b2");
       
        int loadAddr = input[0] & 0xff | (input[1] & 0xff) << 8;
        byte[] inputSkip2 = Arrays.copyOfRange(input, 2, input.length);
        byte[] result = new B2Impl().crunch(inputSkip2, loadAddr, -1).data;

        Assert.assertEquals(expected.length - 2, result.length);
        for (int i =0; i < result.length; i++){
            if (result[i] != expected[i+2]){
                Assert.fail("Fail at position " + i + " " + expected[i+2] + " != " + result[i]);
            }
        }
	}
	
	  @Test
	    public void testBasicB2() throws Exception {

	        byte[] input = TestUtils.resourceToByteArray("./elgena.png");

	        B2 boozer = new B2();

	        List<IMemoryBlock> blocks = new ArrayList<IMemoryBlock>();
	        blocks.add(new MemBlock("elgena", input, 0x2000));

	        byte[] result = boozer.execute(blocks, new IValue[]{}, new StubEngine());

	        byte[] expected = TestUtils.resourceToByteArray("./elgena_0x2000.prg.b2");

	        for (int i =0; i < result.length; i++){
	            if (result[i] != expected[i+2]){
	                Assert.fail("Fail at position " + i + " " + expected[i+2] + " != " + result[i]);
	            }
	        }
	    }

	    @Test
	    public void testB2() throws Exception {

	        byte[] input = TestUtils.resourceToByteArray("./lorem_ipsum.txt");

	        B2 boozer = new B2();

	        List<IMemoryBlock> blocks = new ArrayList<IMemoryBlock>();
	        blocks.add(new MemBlock("elgena", input, 0x2000));

	        byte[] result = boozer.execute(blocks, new IValue[]{new IntValue(0x203d)}, new StubEngine());
	        Assert.assertEquals(result.length, 6270);
	        
	        
	    }

	    @Test
	    public void testBasicB2Mem() throws Exception {

	        byte[] input = TestUtils.resourceToByteArray("./elgena.png");

	        B2 boozer = new B2();

	        List<IMemoryBlock> blocks = new ArrayList<IMemoryBlock>();
	        blocks.add(new MemBlock("elgena", input, 0x2000));
	        blocks.add(new MemBlock("elgena", input, 0x8000));

	        byte[] result = boozer.execute(blocks, new IValue[]{}, new StubEngine());

	        byte[] expected = TestUtils.resourceToByteArray("./elgena_0x2000_0x8000.prg.b2");

	        for (int i =0; i < result.length; i++){
	            if (result[i] != expected[i+2]){
	                Assert.fail("Fail at position " + i + " " + expected[i+2] + " != " + result[i]);
	            }
	        }
	    }
}
