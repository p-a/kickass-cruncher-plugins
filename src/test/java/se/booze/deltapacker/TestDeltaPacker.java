package se.booze.deltapacker;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.testng.annotations.Test;

import kickass.plugins.interf.IMemoryBlock;
import kickass.plugins.interf.IValue;
import se.triad.kickass.common.BooleanValue;
import se.triad.kickass.common.StubEngine;
import se.triad.kickass.common.TestUtils;
import se.triad.kickass.exomizer.MemBlock;

@Test
public class TestDeltaPacker {

	@Test
	public void testPackIncremental() {
		DeltaPacker packer = new DeltaPacker();
		
		List<IMemoryBlock> mem = new ArrayList<IMemoryBlock>();
		byte[] bytes = { 0, 1, (byte) 255};
		mem.add(new MemBlock("bah", bytes, 0));
		byte[] execute = packer.execute(mem, new IValue[]{}, new StubEngine());
	
		byte[] expecteds = { 0, 1, (byte) 254 };
		Assert.assertArrayEquals(expecteds, execute);
	}
	
	@Test
	public void testPackIncremental2() {
		DeltaPacker packer = new DeltaPacker();
		
		List<IMemoryBlock> mem = new ArrayList<IMemoryBlock>();
		byte[] bytes = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 };
		mem.add(new MemBlock("bah", bytes, 0));
		byte[] execute = packer.execute(mem, new IValue[]{}, new StubEngine());
	
		byte[] expecteds = { 1,1,1,1,1,1,1,1,1,1,1,1 };
		Assert.assertArrayEquals(expecteds, execute);
	}
	
	@Test
	public void testPackOverflow() {
		DeltaPacker packer = new DeltaPacker();
		
		List<IMemoryBlock> mem = new ArrayList<IMemoryBlock>();
		byte[] bytes = { 0, 1, (byte) 255, (byte) 254};
		mem.add(new MemBlock("bah", bytes, 0));
		byte[] execute = packer.execute(mem, new IValue[]{}, new StubEngine());
	
		byte[] expecteds = { 0, 1, (byte) 254, (byte) 255};
		Assert.assertArrayEquals(expecteds, execute);
	}
	
	@Test
	public void testPackDecremental() {
		DeltaPacker packer = new DeltaPacker();
		
		List<IMemoryBlock> mem = new ArrayList<IMemoryBlock>();
		byte[] bytes = { 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1};
		mem.add(new MemBlock("bah", bytes, 0));
		byte[] execute = packer.execute(mem, new IValue[]{}, new StubEngine());
	
		byte[] expecteds = { 12, (byte) 255, (byte) 254, (byte) 254, (byte) 254, (byte) 254, (byte) 254, (byte) 254, (byte) 254, (byte) 254, (byte) 254, (byte) 254, };
		Assert.assertArrayEquals(expecteds, execute);
	}
	
	@Test
	public void testPackDecrementalReversed() {
		DeltaPacker packer = new DeltaPacker();
		
		List<IMemoryBlock> mem = new ArrayList<IMemoryBlock>();
		byte[] bytes = { 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1};
		mem.add(new MemBlock("bah", bytes, 0));
		byte[] execute = packer.execute(mem, new IValue[]{ new BooleanValue(true) }, new StubEngine());
	
		byte[] expecteds = { 1,1,1,1,1,1,1,1,1,1,1,1 };
		Assert.assertArrayEquals(expecteds, execute);
	}
	
	@Test
	public void testPackUnPackData() throws Exception {
		DeltaPacker packer = new DeltaPacker();

		List<IMemoryBlock> mem = new ArrayList<IMemoryBlock>();
		byte[] data = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 11,0, (byte)255, (byte) 254, (byte) 127, (byte) 255, (byte) 0, (byte) 129, 0,0,(byte) 128, (byte) 63 };
		mem.add(new MemBlock("bah", data, 0));
		byte[] unpacked = unpack(packer.execute(mem, new IValue[]{}, new StubEngine()), false);
		Assert.assertArrayEquals(unpacked,  data);
		
	}
	@Test
	public void testPackUVData() throws Exception {
		DeltaPacker packer = new DeltaPacker();

		List<IMemoryBlock> mem = new ArrayList<IMemoryBlock>();
		byte[] data = TestUtils.resourceToByteArray("org_data.bin");
		mem.add(new MemBlock("bah", data, 0));
		byte[] output = packer.execute(mem, new IValue[]{}, new StubEngine());
		
		byte[] unpacked = unpack(output, false);
		Assert.assertArrayEquals(unpacked,  data);
		
	}
	
	@Test
	public void testPackUVDataReversed() throws Exception {
		DeltaPacker packer = new DeltaPacker();

		List<IMemoryBlock> mem = new ArrayList<IMemoryBlock>();
		byte[] data = TestUtils.resourceToByteArray("org_data.bin");
		mem.add(new MemBlock("bah", data, 0));
		byte[] output = packer.execute(mem, new IValue[]{new BooleanValue(true)}, new StubEngine());
		
		byte[] unpacked = unpack(output, true);
		Assert.assertArrayEquals(unpacked,  data);
		
	}

	private byte[] unpack(byte[] packed, boolean reversed) {
		byte[] unpacked = new byte[packed.length];
		int previous = 0;
		boolean carry = false;
		for (int i=0; i < packed.length; i++){
			int pos = reversed ? packed.length - 1 - i : i;
			int tmp = (packed[pos] & 0xff) + previous + (carry ? 1 : 0);
			carry = tmp > 255;
			unpacked[pos] = (byte) tmp;
			previous = unpacked[pos] & 0xff;
		}
		
		return unpacked;
	}
}
