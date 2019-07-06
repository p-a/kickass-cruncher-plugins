package se.booze.byteboozer;

import static se.triad.kickass.Utils.toHexString;

import java.util.EnumMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import se.triad.kickass.AbstractCruncher;
import se.triad.kickass.CrunchedObject;
import se.triad.kickass.Options;
import se.triad.kickass.Utils;
import kickass.plugins.interf.general.IEngine;
import kickass.plugins.interf.general.IMemoryBlock;
import kickass.plugins.interf.general.IParameterMap;
import kickass.plugins.interf.general.IValue;

public class B2 extends AbstractCruncher {

    private static final String NAME = "B2";

    protected B2Impl _b2 = new B2Impl();
    
    @Override
    public String getName() {
        return NAME;
    }

    @Override
    protected String getSyntax() {
        return NAME + " (int startaddress [-1])";
    }

    @Override
    protected CrunchedObject crunch(IMemoryBlock block,
            EnumMap<Options, Object> opts, IEngine iEngine) {

        return _b2.crunch(block.getBytes(), block.getStartAddress(), (Integer) opts.get(Options.VALIDATE_SAFETY_OFFSET));
    }

    @Override
    protected byte[] finalizeData(List<IMemoryBlock> blocks,
            EnumMap<Options, Object> options, IEngine engine, List<CrunchedObject> objects) {

        CrunchedObject obj = objects.get(0);
        return obj.data;
    }

    @Override
    protected void validateResult(List<IMemoryBlock> blocks, EnumMap<Options, Object> opts, IEngine engine,
    		List<CrunchedObject> exoObjects) {
    	// FIXME
    }
    
    @Override
    protected List<IMemoryBlock> preTransformBlocks(List<IMemoryBlock> blocks) {
        return Utils.mergeBlocks(blocks);
    }

    @Override
    protected void validateArguments(EnumMap<Options, Object> opts,
            List<IMemoryBlock> blocks, IValue[] values, IEngine engine) {

        addIntegerOption(values, 0, opts, Options.VALIDATE_SAFETY_OFFSET, 0xfffa);
        addBooleanOption(values, 1, opts, Options.FORWARD_CRUNCHING, true);
    }

    @Override
   	protected void validateArguments(EnumMap<Options, Object> opts, List<IMemoryBlock> blocks,
   			IParameterMap params, IEngine engine) {
    
        addIntegerOption(params, opts, Options.VALIDATE_SAFETY_OFFSET, 0xfffa);
        addBooleanOption(params, opts, Options.FORWARD_CRUNCHING, true);
    }
    
    @Override
    protected String formatAddress(int address) {
        return "Pack start: "+toHexString(address);
    }
    
	@Override
	protected Set<String> getParams() {
		return List.of(Options.VALIDATE_SAFETY_OFFSET, Options.FORWARD_CRUNCHING)
			.stream()
			.map(Options::getName)
			.collect(Collectors.toSet());
	}

}
