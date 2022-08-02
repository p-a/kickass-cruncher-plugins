package se.triad.kickass.common;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;

import kickass.plugins.interf.general.IEngine;
import kickass.plugins.interf.general.IMemoryBlock;
import kickass.plugins.interf.general.ISourceRange;
import se.triad.kickass.MemBlock;

public class StubEngine implements IEngine {

	@Override
	public void error(String arg0) {
		System.err.println(arg0);
		throw new RuntimeException(arg0);
	}

	@Override
	public File getCurrentDirectory() {
		return null;
	}

	@Override
	public File getFile(String arg0) {
		return null;
	}

	@Override
	public void print(String arg0) {
		System.out.println(arg0);
	}

	@Override
	public void printNow(String arg0) {
		System.out.println(arg0);
	}

	@Override
	public void addError(String arg0, ISourceRange arg1) {
		error(arg0, arg1);
	}

	@Override
	public byte charToByte(char arg0) {
		return (byte) arg0;
	}
	
	@Override
	public IMemoryBlock createMemoryBlock(String name, int startAddress, byte[] bytes) {
		return new MemBlock(name, bytes, startAddress);
	}

	@Override
	public void error(String arg0, ISourceRange arg1) {
		System.err.println(arg0);
		
	}

	@Override
	public OutputStream openOutputStream(String arg0) throws Exception {
		return new ByteArrayOutputStream();
	}

	@Override
	public byte[] stringToBytes(String arg0) {
		return arg0.getBytes();
	}

	@Override
	public String normalizeFileName(String arg0) {
		return arg0;
	}
	
}