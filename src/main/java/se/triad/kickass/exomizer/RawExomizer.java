package se.triad.kickass.exomizer;

import java.util.EnumMap;
import java.util.List;

import se.triad.kickass.exomizer.ExoHelper.ExoObject;

import cml.kickass.plugins.interf.IEngine;
import cml.kickass.plugins.interf.IMemoryBlock;
import cml.kickass.plugins.interf.IValue;

public class RawExomizer extends AbstractExomizer {

	private static final int ARGNUM_REVERSE_OUTPUT = 2;
	private static final int ARGNUM_USE_LITERALS = 1;
	private static final int ARGNUM_FORWARD_CRUNCHING = 0;
	private static final String NAME = "RawExomizer";

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	protected byte[] finalizeData(List<IMemoryBlock> blocks, EnumMap<Options, Object> opts,
			List<ExoObject> exoObjects) {

		ExoObject obj = exoObjects.get(0);

		byte buf[];
		if (opts.containsKey(Options.REVERSE_OUTPUT)){
			buf = new byte[obj.data.length];
			for(int b = buf.length-1; b >= 0; b--){
				buf[buf.length-1-b] = obj.data[b];
			}
		} else {
			buf = obj.data;
		}

		return buf;
	}

	@Override
	protected String getSyntax() {
		return getName()+"( boolean forwardCrunching [false], boolean useLiterals [true], boolean reverseOutput [false] ) ";
	}

	@Override
	protected EnumMap<Options, Object> validateArguments(List<IMemoryBlock> blocks,
			IValue[] values, IEngine engine) {

		if (blocks.size() > 1){
			engine.error(NAME + " only handles one, single memory block");
		}

		//else

		EnumMap<Options, Object> opts = new EnumMap<Options, Object>(Options.class);
		try {
			addBooleanOption(values, ARGNUM_FORWARD_CRUNCHING, opts, Options.FORWARD_CRUNCHING, false); 
			addBooleanOption(values, ARGNUM_USE_LITERALS, opts, Options.USE_LITERALS, true); 
			addBooleanOption(values, ARGNUM_REVERSE_OUTPUT, opts, Options.REVERSE_OUTPUT, false); 
		} catch (Exception ex){
			engine.error(ex.getMessage() + "\n" + getSyntax());
		}

		return opts;
	}



}
