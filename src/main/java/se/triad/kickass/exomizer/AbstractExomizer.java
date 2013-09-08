package se.triad.kickass.exomizer;

import static se.triad.kickass.Utils.asHex;

import java.util.EnumMap;
import java.util.List;

import se.triad.kickass.AbstractCruncher;
import se.triad.kickass.CrunchedObject;
import cml.kickass.plugins.interf.IEngine;
import cml.kickass.plugins.interf.IMemoryBlock;

public abstract class AbstractExomizer extends AbstractCruncher {

	@Override
	protected CrunchedObject crunch(IMemoryBlock block,
			EnumMap<Options, Object> opts, IEngine iEngine) {

		return ExoHelper.crunch(block.getBytes(), 
				opts.containsKey(Options.FORWARD_CRUNCHING),
				opts.containsKey(Options.USE_LITERALS),
				opts.containsKey(Options.APPEND_IN_LOAD) ? block.getStartAddress() : -1);
	}

	@Override
	protected void validateResult(List<IMemoryBlock> blocks, EnumMap<Options, Object> opts, IEngine engine,
			List<CrunchedObject> exoObjects) {

		if (opts.containsKey(Options.VALIDATE_SAFETY_OFFSET)){

			for (int i = 0; i < blocks.size(); i++){
				final int safetyOffset = exoObjects.get(i).address;
				final int finalSize = exoObjects.get(i).data.length;
				final int memAddr = (Integer) opts.get(Options.VALIDATE_SAFETY_OFFSET);
				final boolean forwardCrunching = opts.containsKey(Options.FORWARD_CRUNCHING);
				final int min = blocks.get(i).getStartAddress();
				final int max = blocks.get(i).getStartAddress()+blocks.get(i).getBytes().length;

				if ( (!forwardCrunching && memAddr > min-safetyOffset && memAddr < max) ||
						(forwardCrunching && !(memAddr+finalSize >= max-safetyOffset || min >= memAddr+finalSize))) 
				{
					String error = "WARNING! Exomized data '" + blocks.get(i).getName() + "' in block["+i+"] cannot be decompressed at $"+asHex(memAddr) + 
							" Safety distance is $" + asHex(safetyOffset) + " Decompressed data span $" + asHex(min) + " - $" + asHex(max);
					if (forwardCrunching)
						error = error + "\nPlace your data >= $" + asHex(max+safetyOffset - finalSize ) + " or <= $" + asHex(min-finalSize);
					else
						error = error + "\nPlace your data <= $"+asHex(min-safetyOffset) + " or >= $" + asHex(max);

					engine.error(error);
				}	
			}
		}

	}
}