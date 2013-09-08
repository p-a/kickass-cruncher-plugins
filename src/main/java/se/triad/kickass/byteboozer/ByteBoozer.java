package se.triad.kickass.byteboozer;

import java.util.EnumMap;
import java.util.List;


import se.triad.kickass.AbstractCruncher;
import se.triad.kickass.CrunchedObject;
import se.triad.kickass.Utils;

import cml.kickass.plugins.interf.IEngine;
import cml.kickass.plugins.interf.IMemoryBlock;
import cml.kickass.plugins.interf.IValue;

public class ByteBoozer extends AbstractCruncher {

	private static final String NAME = "ByteBoozer";

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	protected String getSyntax() {
		return NAME + " ()";
	}

	@Override
	protected CrunchedObject crunch(IMemoryBlock block,
			EnumMap<Options, Object> opts, IEngine iEngine) {
		
		return new JByteBoozer().crunch(block.getBytes(), block.getStartAddress());
	}

	@Override
	protected byte[] finalizeData(List<IMemoryBlock> blocks,
			EnumMap<Options, Object> options, List<CrunchedObject> exoObjects) {
		return exoObjects.get(0).data;
	}

	@Override
	protected void validateResult(List<IMemoryBlock> blocks,
			EnumMap<Options, Object> opts, IEngine engine,
			List<CrunchedObject> exoObjects) {
	}
	
	@Override
	protected List<IMemoryBlock> preTransformBlocks(List<IMemoryBlock> blocks) {
		return Utils.mergeBlocks(blocks);
	}

	@Override
	protected EnumMap<Options, Object> validateArguments(
			List<IMemoryBlock> blocks, IValue[] values, IEngine engine) {
		return new EnumMap<AbstractCruncher.Options, Object>(Options.class);
	}

}
