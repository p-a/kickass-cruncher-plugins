package se.triad.kickass.common;

import java.util.List;

import kickass.plugins.interf.general.IValue;

public class IntValue implements IValue {
	
	private int v;

	public IntValue(int v) {
		this.v = v;
	}

	@Override
	public boolean getBoolean() {
		return false;
	}

	@Override
	public double getDouble() {
		return 0;
	}

	@Override
	public int getInt() {
		return v;
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
		return false;
	}

	@Override
	public boolean hasDoubleRepresentation() {
		return false;
	}

	@Override
	public boolean hasIntRepresentation() {
		return true;
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
