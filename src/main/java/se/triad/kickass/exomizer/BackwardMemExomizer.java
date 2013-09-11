package se.triad.kickass.exomizer;

import java.util.EnumMap;
import java.util.List;

import cml.kickass.plugins.interf.IEngine;
import cml.kickass.plugins.interf.IMemoryBlock;
import cml.kickass.plugins.interf.IValue;

public class BackwardMemExomizer extends MemExomizer {

	private static final int ARGNUM_VALIDATE_SAFETY_OFFSET = 0;
	private static final String NAME = "BackwardMemExomizer";

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	protected String getSyntax() {
		return getName()+"( int startAddress [no check] ) ";
	}

	@Override
	protected void validateArguments(EnumMap<Options, Object> opts, List<IMemoryBlock> blocks,
			IValue[] values, IEngine engine) {

		try {
			opts.put(Options.APPEND_IN_LOAD,null);
			opts.put(Options.USE_LITERALS, null);
			addSafetyOffsetCheckOption(values,ARGNUM_VALIDATE_SAFETY_OFFSET,opts);
		} catch (Exception ex){
			engine.error(ex.getMessage() + "\n" + getSyntax());
		}
	}

}
