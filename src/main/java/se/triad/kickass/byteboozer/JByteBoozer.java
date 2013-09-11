package se.triad.kickass.byteboozer;

import se.triad.kickass.CrunchedObject;
/**
 * Java-implementation of the excellent ByteBoozer
 * 
 * @copyright HCL/BD (David Malmborg) 2003
 * @copyright Ruk/TRIAD (P-a Baeckstroem) 2013
 *
 */
final class JByteBoozer {

	public final static int memSize = 65536;

	private final static int max_offs = 0x129f; // 12bit offset limit. $129f
	private final static int max_offs_short = 0x054f; // 10bit offset limit. $054f

	private final static int[] offsTab = {5,2,2,3};
	private final static int[] offsTabShort = {4,2,2,2};

	private byte[] ibuf;
	private byte obuf[] = new byte[memSize];

	private int ibufSize;
	private int get; //points to in[]
	private int put; //points to out[]

	private boolean copyFlag;
	private int curByte;
	private int curCnt;
	private int plainLen;

	private int theMatchLen, theMatchOffset; 

	private JByteBoozer(){

	}

	private void out(int b)
	{
		curByte >>= 1;
		curByte |= (b << 7);
		if((--curCnt) == 0) {
			obuf[put] = (byte)curByte;
			if(put == 0) {
				throw new RuntimeException("Error (C-1): Packed file too large.");
			}
			--put;

			curCnt = 8;
			curByte = 0;
		}
	}

	private void outLen(int b)
	{

		if(b < 0x80)
			out(0);

		while(b > 1) {
			out(b & 1);
			out(1);
			b >>= 1;
		}
	}

	private void outCopyFlag()
	{
		if(copyFlag) {
			out(1);
			copyFlag = false;
		}
	}

	private boolean scan()
	{
		int scn;
		int matchLen = 0;
		int matchOffset = 0;

		if(get < 2) {
			return false;
		}

		scn = get - 1;

		byte first = ibuf[get];
		byte second = ibuf[get - 1];

		while(((get - scn) <= max_offs) &&
				(scn > 0)){
			if((ibuf[scn] == first) &&
					(ibuf[scn - 1] == second)) {
				int len = 2;
				while((len < 255) &&
						(scn >= len) &&
						(ibuf[scn - len] == ibuf[get - len])) {
					++len;
				};

				if(len > matchLen) {
					matchLen = len;
					matchOffset = get - scn;
				}
			}
			--scn;
		};

		if((matchLen > 2) ||
				((matchLen == 2) && (matchOffset <= max_offs_short))) {
			theMatchLen = matchLen;
			theMatchOffset = matchOffset;
			return true;
		}
		else {
			return false;
		}
	}

	private void copy(int matchLen, int matchOffset)
	{
		int i = 0;

		copyFlag = true;

		// Put copy offset.
		while(i < 4) {
			int b;
			if(matchLen == 2) {
				b = offsTabShort[i];
			}
			else {
				b = offsTab[i];
			}
			while(b > 0) {
				out(matchOffset & 1);
				matchOffset >>= 1;
				--b;
			};

			if(matchOffset == 0)
				break;

			--matchOffset;
			++i;
		};

		// Put copy offset size.
		out(i & 1);
		out((i >> 1) & 1);

		// Put copy length.
		outLen(matchLen - 1);

		get -= matchLen;
	}

	void flush()
	{
		// Exit if there is nothing to flush.
		if(plainLen == 0) {
			outCopyFlag();
			return;
		}

		// Put extra copy-bit if necessary.
		if((plainLen % 255) == 0) {
			outCopyFlag();
		}

		// Put plain data.
		while(plainLen > 0) {
			int i;
			int len = ((plainLen - 1) % 255) + 1;

			if(put < len) {
				throw new RuntimeException("Error (C-2): Packed file too large.");
			}

			// Copy the data.
			for(i = 0; i < len; ++i) {
				obuf[put - i] = ibuf[get + plainLen - i];
			}

			plainLen -= len;
			put -= len;

			// Put plain length.
			outLen(len);

			// Put plain-bit.
			out(0);
		}

		plainLen = 0;
	}

	private CrunchedObject doCrunch(byte[] source, int startAdress) {
		int i;
		int packLen;

		ibufSize = source.length;
		ibuf = new byte[ibufSize];

		System.arraycopy(source,0, ibuf, 0, ibufSize);

		get = ibufSize - 1;
		put = memSize - 1;
		curByte = 0;
		curCnt = 8;

		plainLen = 0;

		outLen(0xff); // Put end of file.
		copyFlag = true;

		while(get >= 0) {
			if(scan()) {
				flush();
				copy(theMatchLen, theMatchOffset);
			}
			else {
				++plainLen;
				--get;
			}
		};
		flush();

		//Copy obuf into aTarget!!
		packLen = memSize - put - 1;
		int size = packLen + 3;
		byte[] target = new byte[size];

		target[0] = (byte)(curByte | (1 << (curCnt - 1)));
		target[1] = (byte)(startAdress & 0xff);
		target[2] = (byte)(startAdress >> 8 );

		for(i = 0; i < packLen; ++i) {
			target[i + 3] = obuf[put + i + 1];
		}

		int packStart = 0xfffa;
		packStart -= (packLen + 3);

		return new CrunchedObject(target, packStart);
	}
	
	public static CrunchedObject crunch(byte[] source, int startAdress)
	{
		return new JByteBoozer().doCrunch(source, startAdress);
	}	
}
