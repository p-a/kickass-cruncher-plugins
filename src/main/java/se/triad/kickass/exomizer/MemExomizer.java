package se.triad.kickass.exomizer;

import java.util.EnumMap;
import java.util.List;

import se.triad.kickass.CrunchedObject;
import se.triad.kickass.Options;
import se.triad.kickass.Utils;

import kickass.plugins.interf.general.IEngine;
import kickass.plugins.interf.general.IMemoryBlock;
import kickass.plugins.interf.general.IValue;

public class MemExomizer extends AbstractExomizer {

    private static final int ARGNUM_VALIDATE_SAFETY_OFFSET = 2;
    private static final int ARGNUM_USE_LITERALS = 1;
    private static final int ARGNUM_FORWARD_CRUNCHING = 0;
    private static final String NAME = "MemExomizer";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    protected byte[] finalizeData(List<IMemoryBlock> blocks, EnumMap<Options, Object> opts, IEngine engine,
            List<CrunchedObject> exoObjects) {

        return exoObjects.get(0).data;
    }

    @Override
    protected void validateResult(List<IMemoryBlock> blocks, EnumMap<Options, Object> opts,
            IEngine engine, List<CrunchedObject> exoObjects) {

        if (exoObjects.size() != 1){
            engine.error("Fault in " + NAME + "! There are " + exoObjects.size() + " exomized blobs (should be one single item)");
        }

        super.validateResult(blocks, opts, engine, exoObjects);
    }

    /* Mem merges all blocks to a single one */
    @Override
    protected List<IMemoryBlock> preTransformBlocks(final List<IMemoryBlock> blocks) {

        return Utils.mergeBlocks(blocks);
    }

    @Override
    protected String getSyntax() {
        return getName()+"( boolean forwardCrunching [false], boolean useLiterals [true], int startAddress [no check] ) ";
    }

    @Override
    protected void validateArguments(EnumMap<Options, Object> opts, List<IMemoryBlock> blocks,
            IValue[] values, IEngine engine) {

        try {
            opts.put(Options.APPEND_IN_LOAD,null);
            addBooleanOption(values, ARGNUM_FORWARD_CRUNCHING, opts, Options.FORWARD_CRUNCHING, false);
            addBooleanOption(values, ARGNUM_USE_LITERALS, opts, Options.USE_LITERALS, true);
            addSafetyOffsetCheckOption(values,ARGNUM_VALIDATE_SAFETY_OFFSET,opts);
        } catch (Exception ex){
            engine.error(ex.getMessage() + "\n" + getSyntax());
        }
    }



}
