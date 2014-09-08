package se.triad.kickass.exomizer;

import java.util.EnumMap;
import java.util.List;

import se.triad.kickass.CrunchedObject;
import se.triad.kickass.Utils;


import cml.kickass.plugins.interf.IEngine;
import cml.kickass.plugins.interf.IMemoryBlock;
import cml.kickass.plugins.interf.IValue;

public class RawExomizer extends AbstractExomizer {

    private static final int ARGNUM_MAX_OFFSET_SIZE = 3;
    private static final int ARGNUM_REVERSE_OUTPUT = 2;
    private static final int ARGNUM_USE_LITERALS = 1;
    private static final int ARGNUM_FORWARD_CRUNCHING = 0;
    private static final String NAME = "RawExomizer";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    protected byte[] finalizeData(List<IMemoryBlock> blocks, EnumMap<Options, Object> opts, IEngine engine,
            List<CrunchedObject> exoObjects) {

        CrunchedObject obj = exoObjects.get(0);

        if (opts.containsKey(Options.REVERSE_OUTPUT)){
            obj = Utils.reverseBuffer(obj);
        }

        return obj.data;
    }

    @Override
    protected String getSyntax() {
        return getName()+"( boolean forwardCrunching [false], boolean useLiterals [true], boolean reverseOutput [false], int max_offset [65535] ) ";
    }

    @Override
    protected void validateArguments(EnumMap<Options, Object> opts, List<IMemoryBlock> blocks,
            IValue[] values, IEngine engine) {

        if (blocks.size() > 1){
            engine.error(NAME + " only handles one, single memory block");
        }

        //else
        try {
            addBooleanOption(values, ARGNUM_FORWARD_CRUNCHING, opts, Options.FORWARD_CRUNCHING, false);
            addBooleanOption(values, ARGNUM_USE_LITERALS, opts, Options.USE_LITERALS, true);
            addBooleanOption(values, ARGNUM_REVERSE_OUTPUT, opts, Options.REVERSE_OUTPUT, false);
            addIntegerOption(values, ARGNUM_MAX_OFFSET_SIZE, opts, Options.MAXIMUM_OFFSET_SIZE, ExoHelper.MAX_OFFSET);
        } catch (Exception ex){
            engine.error(ex.getMessage() + "\n" + getSyntax());
        }
    }



}
