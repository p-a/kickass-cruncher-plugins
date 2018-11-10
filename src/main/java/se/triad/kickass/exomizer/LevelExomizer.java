package se.triad.kickass.exomizer;

import java.util.EnumMap;
import java.util.List;

import se.triad.kickass.CrunchedObject;
import se.triad.kickass.Utils;


import kickass.plugins.interf.general.IEngine;
import kickass.plugins.interf.general.IMemoryBlock;
import kickass.plugins.interf.general.IValue;

public class LevelExomizer extends AbstractExomizer {

	private static final int ARGNUM_OUTPUT_OFFSETS = 2;
	private static final int ARGNUM_USE_LITERALS = 1;
	private static final int ARGNUM_FORWARD_CRUNCHING = 0;
	private static final String NAME = "LevelExomizer";

	@Override
	public String getName() {
		return NAME;
	}
	
	@Override
	protected byte[] finalizeData(List<IMemoryBlock> blocks, EnumMap<Options, Object> opts, IEngine engine,
			List<CrunchedObject> exoObjects) {

		int size = 0;
		for (CrunchedObject obj: exoObjects){
			size+=obj.data.length;
		}

		if (opts.containsKey(Options.OUTPUT_BLOCK_OFFSETS)){
			size = size + 2*exoObjects.size();
		}
		byte[] output = new byte [size];
		
		int addr = 0;
		engine.print("\nCrunched Memory layout:");
		for (int i = 0; i < exoObjects.size(); i++){

			CrunchedObject obj = exoObjects.get(i);
			IMemoryBlock block = blocks.get(i);
			byte[] buf;
			if (!opts.containsKey(Options.FORWARD_CRUNCHING)){
				buf = new byte[obj.data.length];
				for(int b = buf.length-1; b >= 0; b--){
					buf[buf.length-1-b] = obj.data[b];
				}

			} else {
				buf = obj.data;
			}
			engine.print(Utils.toHexString(addr) + " : ["+ i + "] " + block.getName());
			System.arraycopy(buf, 0, output, addr, obj.data.length);
			addr+=obj.data.length;
			
			if (opts.containsKey(Options.OUTPUT_BLOCK_OFFSETS)){
				
				int offset = addr;
				if (opts.containsKey(Options.FORWARD_CRUNCHING)){
					offset -= obj.data.length;
				}
				
				int pos = size-2*exoObjects.size();
				output[pos+2*i] = (byte)(offset & 0xFF);
				output[pos+2*i+1] = (byte)(offset >> 8);
			}
		}
		
		return output;
	}

	@Override
	protected void validateResult(List<IMemoryBlock> blocks, EnumMap<Options, Object> opts,
			IEngine engine, List<CrunchedObject> exoObjects) {

		if (exoObjects.size() != blocks.size()){
			engine.error("Fault in " + NAME + "! There are " + exoObjects.size() + " exomized blobs, should be " + blocks.size());
		}
	}

	@Override
	protected List<IMemoryBlock> preTransformBlocks(final List<IMemoryBlock> blocks) {
		return blocks;
	}

	@Override
	protected String getSyntax() {
		return getName()+"( boolean forwardCrunching [false], boolean useLiterals [true]) ";
	}

	@Override
	protected void validateArguments(EnumMap<Options, Object> opts, List<IMemoryBlock> blocks,
			IValue[] values, IEngine engine) {

		try {
			opts.put(Options.APPEND_IN_LOAD,null);
			addBooleanOption(values, ARGNUM_FORWARD_CRUNCHING, opts, Options.FORWARD_CRUNCHING, false); 
			addBooleanOption(values, ARGNUM_USE_LITERALS, opts, Options.USE_LITERALS, true); 
			addBooleanOption(values, ARGNUM_OUTPUT_OFFSETS, opts, Options.OUTPUT_BLOCK_OFFSETS, false);
		} catch (Exception ex){
			engine.error(ex.getMessage() + "\n" + getSyntax());
		}
	}

}
