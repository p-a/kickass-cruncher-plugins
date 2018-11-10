package se.triad.kickass.common;

import java.io.File;

import kickass.plugins.interf.IEngine;

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
	
}