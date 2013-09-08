package se.triad.kickass.byteboozer;

import static se.triad.kickass.Utils.asHex;

import java.util.List;


import se.triad.kickass.Utils;

import cml.kickass.plugins.interf.IEngine;
import cml.kickass.plugins.interf.IMemoryBlock;
import cml.kickass.plugins.interf.IModifier;
import cml.kickass.plugins.interf.IValue;

public class ByteBoozer implements IModifier {

	private static final String NAME = "ByteBoozer";

	@Override
	public byte[] execute(List<IMemoryBlock> paramList,
			IValue[] paramArrayOfIValue, IEngine iEngine) {

		BBObject crunchedObj = null;
		
		try {
			
			IMemoryBlock block = paramList.get(0);
			crunchedObj = new ByteBoozerImpl().crunch(block.getBytes(), block.getStartAddress());
		
			int percent = 100 * crunchedObj.data.length / block.getBytes().length;
			iEngine.printNow(getName() + ": " +block.getName() +  " $" +asHex(block.getStartAddress()) + " - $" + asHex(block.getStartAddress()-1+block.getBytes().length) + 
					" Packed size $" + asHex(crunchedObj.data.length)+" ("+percent+ "%) " + "Safety distance: $"+asHex(crunchedObj.packStart));
		
			iEngine.print("Suggested packStart: $"+Utils.asHex(crunchedObj.packStart));
			
		} catch (RuntimeException ex){
			iEngine.error(ex.getMessage());
		}

		return crunchedObj != null ? crunchedObj.data : null;
	}

	@Override
	public String getName() {
		return NAME;
	}

}
