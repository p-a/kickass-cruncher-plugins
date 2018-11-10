package se.triad.kickass.exomizer;

import java.util.Arrays;

import kickass.plugins.interf.general.IMemoryBlock;

public class MemBlock implements IMemoryBlock {

	private String name;
	private byte[] bytes;
	private int startAddress;

	private final int hashCode;
	
	public MemBlock(String name, byte[] bytes, int startAddress){
		
		this.name = name;
		this.bytes = bytes;
		this.startAddress = startAddress;
		
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + startAddress;
		result = prime * result + Arrays.hashCode(bytes);
		
		hashCode = result;
		
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
	
	@Override
	public int hashCode() {
		return hashCode;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MemBlock other = (MemBlock) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (startAddress != other.startAddress)
			return false;
		if (!Arrays.equals(bytes, other.bytes))
			return false;
		return true;
	}
}
