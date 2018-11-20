package se.booze.byteboozer;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;

import kickass.plugins.interf.general.IEngine;
import kickass.plugins.interf.general.IMemoryBlock;
import kickass.plugins.interf.general.IValue;
import se.triad.kickass.CrunchedObject;

public class B2Exe extends B2 {

	private byte[] bytes;

	{
		int decrCode[] = {
			  0x0b, 0x08, 0x00, 0x00, 0x9e, 0x32, 0x30, 0x36, 0x31, 0x00, 0x00, 0x00, 0x78, 0xa9, 0x34, 0x85,
			  0x01, 0xa2, 0xb7, 0xbd, 0x1e, 0x08, 0x95, 0x0f, 0xca, 0xd0, 0xf8, 0x4c, 0x10, 0x00, 0xbd, 0xd6,
			  0x07, 0x9d, 0x00, 0xff, 0xe8, 0xd0, 0xf7, 0xc6, 0x12, 0xc6, 0x15, 0xa5, 0x12, 0xc9, 0x07, 0xb0,
			  0xed, 0x20, 0xa0, 0x00, 0xb0, 0x17, 0x20, 0x8e, 0x00, 0x85, 0x36, 0xa0, 0x00, 0x20, 0xad, 0x00,
			  0x91, 0x77, 0xc8, 0xc0, 0x00, 0xd0, 0xf6, 0x20, 0x83, 0x00, 0xc8, 0xf0, 0xe4, 0x20, 0x8e, 0x00,
			  0xaa, 0xe8, 0xf0, 0x71, 0x86, 0x7b, 0xa9, 0x00, 0xe0, 0x03, 0x2a, 0x20, 0x9b, 0x00, 0x20, 0x9b,
			  0x00, 0xaa, 0xb5, 0xbf, 0xf0, 0x07, 0x20, 0x9b, 0x00, 0xb0, 0xfb, 0x30, 0x07, 0x49, 0xff, 0xa8,
			  0x20, 0xad, 0x00, 0xae, 0xa0, 0xff, 0x65, 0x77, 0x85, 0x74, 0x98, 0x65, 0x78, 0x85, 0x75, 0xa0,
			  0x00, 0xb9, 0xad, 0xde, 0x99, 0x00, 0x00, 0xc8, 0xc0, 0x00, 0xd0, 0xf5, 0x20, 0x83, 0x00, 0xd0,
			  0xa0, 0x18, 0x98, 0x65, 0x77, 0x85, 0x77, 0x90, 0x02, 0xe6, 0x78, 0x60, 0xa9, 0x01, 0x20, 0xa0,
			  0x00, 0x90, 0x05, 0x20, 0x9b, 0x00, 0x10, 0xf6, 0x60, 0x20, 0xa0, 0x00, 0x2a, 0x60, 0x06, 0xbe,
			  0xd0, 0x08, 0x48, 0x20, 0xad, 0x00, 0x2a, 0x85, 0xbe, 0x68, 0x60, 0xad, 0xed, 0xfe, 0xe6, 0xae,
			  0xd0, 0x02, 0xe6, 0xaf, 0x60, 0xa9, 0x37, 0x85, 0x01, 0x4c, 0x00, 0x00, 0x80, 0xdf, 0xfb, 0x00,
			  0x80, 0xef, 0xfd, 0x80, 0xf0
			};
			bytes = new byte[decrCode.length];
			for(int i = 0; i <decrCode.length; i++) {
				bytes[i] = (byte) decrCode[i];
			}
		}
	
	public byte[] getExecutable() {
		return Arrays.copyOf(bytes, bytes.length);
	}
	
	@Override
	public String getName() {
		return "B2exe";
	}
	
    @Override
    protected String getSyntax() {
        return getName() + " (int jmpaddress [-1])";
    }
	    
    @Override
    protected CrunchedObject crunch(IMemoryBlock block,
            EnumMap<Options, Object> opts, IEngine iEngine) {

        CrunchedObject crunchedObject = _b2.crunch(block.getBytes(), block.getStartAddress(), 0xfffa);
        int jmpAddress = (Integer) opts.get(Options.JMP_ADDRESS);
       
        byte[] decrCode = getExecutable();
        
        int packLen = crunchedObject.data.length - 2;
        int fileLen = packLen + decrCode.length + 2;
                
        int startAddress = 0x10000 - packLen;
        int transfAddress = fileLen + 0x6ff;

        decrCode[0x1f] = (byte) (transfAddress & 0xff); // Transfer from..
        decrCode[0x20] = (byte) (transfAddress >> 8);   //
        decrCode[0xbc] = (byte) (startAddress & 0xff); // Depack from..
        decrCode[0xbd] = (byte) (startAddress >> 8);   //
        decrCode[0x85] = crunchedObject.data[0]; // Depack to..
        decrCode[0x86] = crunchedObject.data[1]; //
        decrCode[0xca] = (byte) (jmpAddress & 0xff); // Jump to..
        decrCode[0xcb] = (byte) (jmpAddress >> 8);   //

        byte[] target = new byte[fileLen];
        System.arraycopy(decrCode, 0, target, 0, decrCode.length);
        System.arraycopy(crunchedObject.data, 2, target, decrCode.length, packLen);
        
        return new CrunchedObject(target, 0x0801);

    }
    
    @Override
    protected void validateArguments(EnumMap<Options, Object> opts,
            List<IMemoryBlock> blocks, IValue[] values, IEngine engine) {

        addIntegerOption(values, 0, opts, Options.JMP_ADDRESS, blocks.get(0).getStartAddress());
    }

}
