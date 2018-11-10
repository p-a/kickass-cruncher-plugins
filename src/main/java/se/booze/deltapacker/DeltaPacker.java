package se.booze.deltapacker;

import java.util.List;

import kickass.plugins.interf.general.IEngine;
import kickass.plugins.interf.general.IMemoryBlock;
import kickass.plugins.interf.modifier.IModifier;
import kickass.plugins.interf.modifier.ModifierDefinition;
import kickass.plugins.interf.general.IValue;

public class DeltaPacker implements IModifier {

	private ModifierDefinition modifierDefinition = new ModifierDefinition();
	{
		modifierDefinition.setName("DeltaPacker");
	}
	@Override
	public byte[] execute(List<IMemoryBlock> mem, IValue[] opts, IEngine engine) {
		
		if (mem.size() != 1)
			engine.error("Can only handle a single memory area");
		
		boolean reversed = opts.length == 1 && opts[0].getBoolean();
		
		byte[] org = mem.get(0).getBytes();
		byte[] output = new byte[org.length];
		
		int previous = 0;
		boolean withCarry = false;
		for (int i=0; i < org.length; i++){
			int pos = reversed ? output.length-i-1 : i;
			int diff = ((org[pos] & 0xff) - (previous & 0xff)) & 0x1ff;
			if (withCarry){
				diff = (diff-1) & 0x1ff;
				withCarry = false;
			}

			if (diff > 255) {
				diff -= 256;
				withCarry = true;
			}
			output[pos] = (byte) diff;
			previous = org[pos];
		}
		
		return output;
	}

	@Override
	public ModifierDefinition getDefinition() {
		return modifierDefinition;
	}

}
