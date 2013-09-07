package se.triad.kickass.exomizer;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import se.triad.kickass.exomizer.ExoHelper.ExoObject;

import cml.kickass.plugins.interf.IEngine;
import cml.kickass.plugins.interf.IMemoryBlock;
import cml.kickass.plugins.interf.IModifier;
import cml.kickass.plugins.interf.IValue;

public abstract class AbstractExomizer implements IModifier {

	public static enum Options {
		FORWARD_CRUNCHING,
		USE_LITERALS, 
		APPEND_IN_LOAD, 
		VALIDATE_SAFETY_OFFSET, 
		REVERSE_OUTPUT
	}

	@Override
	public byte[] execute(List<IMemoryBlock> blocks, IValue[] values,
			IEngine engine) {

		EnumMap<Options,Object> opts = validateArguments(blocks,values,engine);

		blocks = preTransformBlocks(blocks);
		List<ExoObject> exoObjects = new ArrayList<ExoObject>();

		for (IMemoryBlock block: blocks){

			ExoObject crunchedObj = ExoHelper.crunch(block.getBytes(), 
					opts.containsKey(Options.FORWARD_CRUNCHING),
					opts.containsKey(Options.USE_LITERALS),
					opts.containsKey(Options.APPEND_IN_LOAD) ? block.getStartAddress() : -1);

			exoObjects.add(crunchedObj);
		
			int percent = 100 * crunchedObj.data.length / block.getBytes().length;
			engine.printNow(getName() + ": " +block.getName() +  " $" +asHex(block.getStartAddress()) + " - $" + asHex(block.getStartAddress()-1+block.getBytes().length) + 
					" Packed size $" + asHex(crunchedObj.data.length)+" ("+percent+ "%) " + "Safety distance: $"+asHex(crunchedObj.safetyOffset));
		}

		validateResult(blocks, opts, engine, exoObjects);

		return finalizeData(blocks, opts, exoObjects);

	}

	protected abstract byte[] finalizeData(List<IMemoryBlock> blocks, EnumMap<Options, Object> options,
			List<ExoObject> exoObjects);

	protected List<IMemoryBlock> preTransformBlocks(List<IMemoryBlock> blocks) {
		return blocks;
	}

	protected void validateResult(List<IMemoryBlock> blocks, EnumMap<Options, Object> opts,
			IEngine engine, List<ExoObject> exoObjects){

		if (opts.containsKey(Options.VALIDATE_SAFETY_OFFSET)){

			for (int i = 0; i < blocks.size(); i++){
				final int safeAddr = exoObjects.get(i).safetyOffset;
				final int finalSize = exoObjects.get(i).data.length;
				final int memAddr = (Integer) opts.get(Options.VALIDATE_SAFETY_OFFSET);
				final boolean forwardCrunching = opts.containsKey(Options.FORWARD_CRUNCHING);
				final int min = blocks.get(i).getStartAddress();
				final int max = blocks.get(i).getStartAddress()+blocks.get(i).getBytes().length;

				if ( (!forwardCrunching && memAddr > min-safeAddr && memAddr < max) ||
					(forwardCrunching && !(memAddr+finalSize >= max-safeAddr || min >= memAddr+finalSize))) 
				{
					String error = "WARNING! Exomized data '" + blocks.get(i).getName() + "' in block["+i+"] cannot be decompressed at $"+asHex(memAddr) + 
							" Safety distance is $" + asHex(safeAddr) + " Decompressed data span $" + asHex(min) + " - $" + asHex(max);
					if (forwardCrunching)
						error = error + "\nPlace your data >= $" + asHex(max+safeAddr - finalSize ) + " or <= $" + asHex(min-finalSize);
					else
						error = error + "\nPlace your data <= $"+asHex(min-safeAddr) + " or >= $" + asHex(max);

					engine.error(error);
				}	
			}
		}

	}

	protected abstract String getSyntax();

	protected abstract EnumMap<Options,Object> validateArguments(List<IMemoryBlock> blocks, IValue[] values,
			IEngine engine);

	protected static String asHex(int i){
		return Integer.toHexString(0x10000 | i).substring(1);
	}

	protected static void addBooleanOption(IValue[] values, int index, EnumMap<Options, Object> opts,
			Options opt, boolean defaultValue) {
		if ( values.length > index && values[index].getBoolean() || values.length <= index && defaultValue) {
			opts.put(opt,null);
		} 
	}

	protected static void addSafetyOffsetCheckOption(IValue[] values, int index, EnumMap<Options, Object> opts) {
		if ( values.length > index) {
			if (values[index].hasIntRepresentation() && values[index].getInt() >= 0 && values[index].getInt() < 65536) {
				opts.put(Options.VALIDATE_SAFETY_OFFSET, values[index].getInt() );
			} else if (!values[index].hasIntRepresentation()){
				throw new IllegalArgumentException("Not an integer or value of out range: "  + values[index].getInt());
			}
		}
	}
}