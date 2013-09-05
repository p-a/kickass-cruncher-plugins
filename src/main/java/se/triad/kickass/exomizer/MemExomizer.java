package se.triad.kickass.exomizer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;

import se.triad.kickass.exomizer.ExoHelper.ExoObject;

import cml.kickass.plugins.interf.IEngine;
import cml.kickass.plugins.interf.IMemoryBlock;
import cml.kickass.plugins.interf.IValue;

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
	protected byte[] finalizeData(List<IMemoryBlock> blocks, EnumMap<Options, Object> opts,
			List<ExoObject> exoObjects) {

		return exoObjects.get(0).data;
	}

	@Override
	protected void validateResult(List<IMemoryBlock> blocks, EnumMap<Options, Object> opts,
			IEngine engine, List<ExoObject> exoObjects) {
		if (exoObjects.size() != 1){
			engine.error("Fault in " + NAME + "! There are " + exoObjects.size() + " exomized blobs (should be one single item)");
		}

		if (opts.containsKey(Options.VALIDATE_SAFETY_OFFSET)){

			for (int i = 0; i < blocks.size(); i++){
				final int safeAddr = exoObjects.get(i).safetyOffset;
				final int finalSize = exoObjects.get(i).data.length;
				final int memAddr = (Integer) opts.get(Options.VALIDATE_SAFETY_OFFSET);
				final boolean forwardCrunching = opts.containsKey(Options.FORWARD_CRUNCHING);
				final int min = blocks.get(i).getStartAddress();
				final int max = blocks.get(i).getStartAddress()+blocks.get(i).getBytes().length;

				if ( (!forwardCrunching && memAddr > safeAddr && memAddr < max) ||
						(forwardCrunching && memAddr < safeAddr && memAddr+finalSize-1 >= min ))

				{
					String error = "WARNING! Exomized data '" + blocks.get(i).getName() + "' in block["+i+"] cannot be decompressed from location $"+asHex(memAddr) + 
							" Safety distance is $" + asHex(!forwardCrunching ? min - safeAddr : safeAddr - min) + " Decompressed data span $" + asHex(min) + " - $" + asHex(max);
					if (forwardCrunching)
						error = error + "\nPlace your data <= $" + asHex(min - finalSize ) + " or >= $" + asHex(safeAddr);
					else
						error = error + "\nPlace your data <= $"+asHex(safeAddr) + " or >= $" + asHex(max);

					engine.error(error);
				}	
			}
		}
	}

	/* Mem merges all blocks to a single one */
	@Override
	protected List<IMemoryBlock> preTransformBlocks(final List<IMemoryBlock> blocks) {

		Collections.sort(blocks, new Comparator<IMemoryBlock>() {

			@Override
			public int compare(IMemoryBlock o1, IMemoryBlock o2) {
				return o1.getStartAddress() - o2.getStartAddress();
			}
		});

		final int startAddress = blocks.get(0).getStartAddress();
		int endAddress = blocks.get(blocks.size()-1).getStartAddress()+blocks.get(blocks.size()-1).getBytes().length;

		final byte[] buf = new byte[endAddress-startAddress];

		StringBuilder name = new StringBuilder();
		for (IMemoryBlock block : blocks){
			System.arraycopy(block.getBytes(), 0, buf, block.getStartAddress()-startAddress, block.getBytes().length);
			name.append(block.getName());
			name.append(", ");
		}
		name.setLength(name.length()-2);

		if (blocks.size() > 1){
			name.append(" ]");
			name.insert(0, "Blob: [ ");
		}

		final String blobName = name.toString();

		IMemoryBlock block = new IMemoryBlock() {

			@Override
			public int getStartAddress() {
				return startAddress;
			}

			@Override
			public String getName() {
				return blobName;
			}

			@Override
			public byte[] getBytes() {
				return buf;
			}
		};

		List<IMemoryBlock> retVal = new ArrayList<IMemoryBlock>();
		retVal.add(block);

		return retVal;
	}

	@Override
	protected String getSyntax() {
		return getName()+"( boolean forwardCrunching [false], boolean useLiterals [true] ) ";
	}

	@Override
	protected EnumMap<Options, Object> validateArguments(List<IMemoryBlock> blocks,
			IValue[] values, IEngine engine) {

		EnumMap<Options, Object> opts = new EnumMap<Options, Object>(Options.class);
		try {
			opts.put(Options.APPEND_IN_LOAD,null);
			addBooleanOption(values, ARGNUM_FORWARD_CRUNCHING, opts, Options.FORWARD_CRUNCHING, false); 
			addBooleanOption(values, ARGNUM_USE_LITERALS, opts, Options.USE_LITERALS, true); 
			addSafetyOffsetCheckOption(values,ARGNUM_VALIDATE_SAFETY_OFFSET,opts);
		} catch (Exception ex){
			engine.error(ex.getMessage() + "\n" + getSyntax());
		}

		return opts;
	}



}
