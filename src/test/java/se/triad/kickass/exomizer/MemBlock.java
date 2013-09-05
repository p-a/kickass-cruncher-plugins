package se.triad.kickass.exomizer;

import cml.kickass.plugins.interf.IMemoryBlock;

public class MemBlock implements IMemoryBlock {

	private String name;
	private byte[] bytes;
	private int startAddress;

	public MemBlock(String name, byte[] bytes, int startAddress){
		this.name = name;
		this.bytes = bytes;
		this.startAddress = startAddress;
		
	}
	@Override
	public byte[] getBytes() {
		return bytes;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public int getStartAddress() {
		return startAddress;
	}

}
