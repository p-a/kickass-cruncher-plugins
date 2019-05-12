package se.triad.kickass.exomizer;

import static se.triad.kickass.common.TestUtils.resourceToByteArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import se.triad.kickass.common.BooleanValue;
import se.triad.kickass.common.StubEngine;

import kickass.plugins.interf.general.IMemoryBlock;
import kickass.plugins.interf.general.IValue;

@Test
public class TestRawExomizer {

	@BeforeTest
	public void disableCache(){
		System.setProperty(AbstractExomizer.DISABLE_EXOMIZER_CACHE, "true");
	}

	@Test
	public void testAllPermutations() throws Exception {

		final String filename = "./elgena_raw_%s%s%s.exo";

		byte[] in = resourceToByteArray("./elgena.png");

		for (int i = 0; i < 1 << 3; i++){

			boolean forward = (i & 1) != 0;
			boolean literals = (i & 2) != 0;
			boolean reversed = (i & 4) != 0;

			RawExomizer exomizer = new RawExomizer();

			List<IMemoryBlock> blocks = new ArrayList<IMemoryBlock>();
			blocks.add(new MemBlock("elgena", in, 0x2000));

			final IValue[] values = new IValue[]{
					new BooleanValue(forward),
					new BooleanValue(literals),
					new BooleanValue(reversed)
			};

			byte[] result = exomizer.execute(blocks, values, new StubEngine());

			String aFilename = String.format(filename, 
					forward ? "forward_" : "backward_",
							reversed ? "rev_" : "",
									literals ? "literals" : "noliterals"
					);

			byte[] expected = resourceToByteArray(aFilename);

			if (!Arrays.equals(expected, result)){
				Assert.fail("Failed for " + aFilename);
			}
		}
	}

	@Test
	public void testFailForMultipleBlocks() throws Exception {

		byte[] in = resourceToByteArray("./elgena.png");

		RawExomizer exomizer = new RawExomizer();

		List<IMemoryBlock> blocks = new ArrayList<IMemoryBlock>();
		blocks.add(new MemBlock("elgena", in, 0x2000));
		blocks.add(new MemBlock("elgena", in, 0x2000));

		final IValue[] values = new IValue[]{};

		try {
			exomizer.execute(blocks, values, new StubEngine());
			Assert.fail("Should not have been reached");
		} catch (RuntimeException ex){
			Assert.assertTrue(ex.getMessage().contains("one, single"));
		}
	}
}
