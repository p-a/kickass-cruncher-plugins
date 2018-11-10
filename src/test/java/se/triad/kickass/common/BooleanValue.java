package se.triad.kickass.common;

import java.util.List;

import kickass.plugins.interf.general.IValue;

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
		return 0;
	}

	@Override
	public int getInt() {
		return 0;
	}

	@Override
	public List<IValue> getList() {
		return null;
	}

	@Override
	public String getString() {
		return null;
	}

	@Override
	public boolean hasBooleanRepresentation() {
		return true;
	}

	@Override
	public boolean hasDoubleRepresentation() {
		return false;
	}

	@Override
	public boolean hasIntRepresentation() {
		return false;
	}

	@Override
	public boolean hasListRepresentation() {
		return false;
	}

	@Override
	public boolean hasStringRepresentation() {
		return false;
	}

}
