package se.booze.byteboozer;

import java.util.Arrays;

import org.testng.annotations.Test;

import junit.framework.Assert;
import se.triad.kickass.common.TestUtils;

@Test
public class TestByteBoozer2 {

	@Test
	public void testCrunchPNG() throws Exception {
        byte[] input = TestUtils.resourceToByteArray("./elgena.png");
        byte[] expected = TestUtils.resourceToByteArray("./elgena.png.b2");
       
        int loadAddr = input[0] & 0xff | (input[1] & 0xff) << 8;
        byte[] inputSkip2 = Arrays.copyOfRange(input, 2, input.length);
        byte[] result = ByteBoozer2Impl.crunch(inputSkip2, loadAddr, -1).data;

        Assert.assertEquals(expected.length - 2, result.length);
        for (int i =0; i < result.length; i++){
            if (result[i] != expected[i+2]){
                Assert.fail("Fail at position " + i + " " + expected[i+2] + " != " + result[i]);
            }
        }
	}
}
