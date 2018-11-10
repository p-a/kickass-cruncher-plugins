package se.triad.kickass.exomizer;

import static se.triad.kickass.Utils.toHexString;

import java.util.EnumMap;

import se.triad.kickass.AbstractCruncher;
import se.triad.kickass.CrunchedObject;
import kickass.plugins.interf.IEngine;
import kickass.plugins.interf.IMemoryBlock;

public abstract class AbstractExomizer extends AbstractCruncher {

    @Override
    protected CrunchedObject crunch(IMemoryBlock block,
            EnumMap<Options, Object> opts, IEngine iEngine) {

        return ExoHelper.crunch(block.getBytes(),
                opts.containsKey(Options.FORWARD_CRUNCHING),
                opts.containsKey(Options.USE_LITERALS),
                opts.containsKey(Options.APPEND_IN_LOAD) ? block.getStartAddress() : -1,
                opts.containsKey(Options.MAXIMUM_OFFSET_SIZE) ? ((Integer) opts.get(Options.MAXIMUM_OFFSET_SIZE)).intValue() : -1);
    }

    @Override
    protected String formatAddress(int address) {
        return "Safety distance: "+toHexString(address);
    }

}