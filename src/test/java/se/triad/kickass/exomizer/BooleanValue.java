package se.triad.kickass.exomizer;

import java.util.List;

import cml.kickass.plugins.interf.IValue;

public class BooleanValue implements IValue {
	
	private boolean b;

	public BooleanValue(boolean b) {
		this.b = b;
	}

	@Override
	public boolean getBoolean() {
		return b;
	}

	@Override
	public double getDouble() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getInt() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<IValue> getList() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasBooleanRepresentation() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasDoubleRepresentation() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasIntRepresentation() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasListRepresentation() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasStringRepresentation() {
		// TODO Auto-generated method stub
		return false;
	}

}
