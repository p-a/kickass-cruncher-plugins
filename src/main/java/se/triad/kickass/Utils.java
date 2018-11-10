package se.triad.kickass;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import se.triad.kickass.exomizer.MemBlock;

import kickass.plugins.interf.IMemoryBlock;

public class Utils {

	public static String toHexString(int i){
		return "$"+Integer.toHexString(0x10000 | i).substring(1);
	}

	public static List<IMemoryBlock> mergeBlocks(List<IMemoryBlock> blocks) {
		Collections.sort(blocks, new Comparator<IMemoryBlock>() {

			@Override
			public int compare(IMemoryBlock o1, IMemoryBlock o2) {
				return o1.getStartAddress() - o2.getStartAddress();
			}
		});

		final int startAddress = blocks.get(0).getStartAddress();
		int endAddress = blocks.get(blocks.size()-1).getStartAddress()+blocks.get(blocks.size()-1).getBytes().length;

		final byte[] buf = new byte[endAddress-startAddress];
		
		StringBuilder name = new StringBuilder();
		for (IMemoryBlock block : blocks){
			System.arraycopy(block.getBytes(), 0, buf, block.getStartAddress()-startAddress, block.getBytes().length);
			name.append(block.getName());
			name.append(", ");
		}
		name.setLength(name.length()-2);

		if (blocks.size() > 1){
			name.append(" ]");
			name.insert(0, "[ ");
		}

		final String blobName = name.toString();

		IMemoryBlock block = new MemBlock(blobName, buf, startAddress);
		
		List<IMemoryBlock> retVal = new ArrayList<IMemoryBlock>();
		retVal.add(block);
		
		return retVal;
	}
	
	public static CrunchedObject reverseBuffer(CrunchedObject obj) {
		byte[] buf;
		buf = new byte[obj.data.length];
		for(int b = buf.length-1; b >= 0; b--){
			buf[buf.length-1-b] = obj.data[b];
		}
		return new CrunchedObject(buf, obj.address);
	}
}
