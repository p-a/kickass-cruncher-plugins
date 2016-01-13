package se.booze.byteboozer;

import se.triad.kickass.CrunchedObject;

public class ByteBoozer2Impl {

	/*
	 * 
#include "cruncher.h"
#include <stdio.h>
#include <stdlib.h>

#define log(format, ...)
//#define log(format, ...) fprintf (stderr, format, ## __VA_ARGS__)
	 */
	private final static int NUM_BITS_SHORT_0 = 3;
	private final static int NUM_BITS_SHORT_1 = 6;
	private final static int NUM_BITS_SHORT_2 = 8;
	private final static int NUM_BITS_SHORT_3 = 10;
	private final static int NUM_BITS_LONG_0 = 4;
	private final static int NUM_BITS_LONG_1 = 7;
	private final static int NUM_BITS_LONG_2 = 10;
	private final static int NUM_BITS_LONG_3 = 13;

	private final static int LEN_SHORT_0  = 1 << NUM_BITS_SHORT_0;
	private final static int LEN_SHORT_1 = 1 << NUM_BITS_SHORT_1;
	private final static int LEN_SHORT_2 = 1 << NUM_BITS_SHORT_2;
	private final static int LEN_SHORT_3 = 1 << NUM_BITS_SHORT_3;
	private final static int LEN_LONG_0 = 1 << NUM_BITS_LONG_0;
	private final static int LEN_LONG_1 = 1 << NUM_BITS_LONG_1;
	private final static int LEN_LONG_2 = 1 << NUM_BITS_LONG_2;
	private final static int LEN_LONG_3 = 1 << NUM_BITS_LONG_3;

	private static boolean COND_SHORT_0(int o) { return ((o) >= 0 && (o) < LEN_SHORT_0); }
	private static boolean COND_SHORT_1(int o) { return ((o) >= LEN_SHORT_0 && (o) < LEN_SHORT_1); }
	private static boolean COND_SHORT_2(int o) { return ((o) >= LEN_SHORT_1 && (o) < LEN_SHORT_2); }
	private static boolean COND_SHORT_3(int o) { return ((o) >= LEN_SHORT_2 && (o) < LEN_SHORT_3); }
	private static boolean COND_LONG_0(int o) { return ((o) >= 0 && (o) < LEN_LONG_0); }
	private static boolean COND_LONG_1(int o) { return ((o) >= LEN_LONG_0 && (o) < LEN_LONG_1); }
	private static boolean COND_LONG_2(int o) { return ((o) >= LEN_LONG_1 && (o) < LEN_LONG_2); }
	private static boolean COND_LONG_3(int o) { return ((o) >= LEN_LONG_2 && (o) < LEN_LONG_3); }

	private final static int MAX_OFFSET = LEN_LONG_3;
	private final static int MAX_OFFSET_SHORT = LEN_SHORT_3;

	private final static int MEM_SIZE = 65536;

	private byte[] ibuf;
	private byte[] obuf;

	private int ibufSize;
	private int get; //points to ibuf[]
	private int put; //points to obuf[]

	private class Node {
		int cost;
		int next;
		int litLen;
		int offset;
	};

	private class RLEInfo {
		byte value;
		byte valueAfter;
		int length;
	};

	private Node[] context;
	private int[] link;
	private RLEInfo[] rleInfo;
	private int[] first = new int[65536];
	private int[] last = new int[65536];

	private boolean copyFlag;
	private byte curByte;
	private byte curCnt;
	private int curIndex;
	private int plainLen;

	private void wBit(int bit) {
		if(curCnt == 0) {
			obuf[curIndex] = curByte;
			curIndex = put;
			curCnt = 8;
			curByte = 0;
			put++;
		}

		curByte <<= 1;
		curByte |= (bit & 1);
		curCnt--;
	}

	private void wFlush() {
		while(curCnt != 0) {
			curByte <<= 1;
			curCnt--;
		}
		obuf[curIndex] = curByte;
	}

	private void wByte(int b) {
		obuf[put] = (byte) b;
		put++;
	}

	private void wBytes(int get, int len) {
		int i;
		for(i = 0; i < len; i++) {
			wByte(ibuf[get]);
			get++;
		}
	}

	private void wLength(int len) {
		//  if(len == 0) return; // ERROR STATE!

		int bit = 0x80;
		while((len & bit) == 0) {
			bit >>= 1;
		}

		while(bit > 1) {
			wBit(1);
			bit >>= 1;
		wBit(((len & bit) == 0) ? 0 : 1);
		}

		if(len < 0x80) {
			wBit(0);
		}
	}

	private void wOffset(int offset, int len) {
		int i = 0;
		int n = 0;
		int b;

		if(len == 1) {
			if(COND_SHORT_0(offset)) {
				i = 0;
				n = NUM_BITS_SHORT_0;
			}
			if(COND_SHORT_1(offset)) {
				i = 1;
				n = NUM_BITS_SHORT_1;
			}
			if(COND_SHORT_2(offset)) {
				i = 2;
				n = NUM_BITS_SHORT_2;
			}
			if(COND_SHORT_3(offset)) {
				i = 3;
				n = NUM_BITS_SHORT_3;
			}
		} else {
			if(COND_LONG_0(offset)) {
				i = 0;
				n = NUM_BITS_LONG_0;
			}
			if(COND_LONG_1(offset)) {
				i = 1;
				n = NUM_BITS_LONG_1;
			}
			if(COND_LONG_2(offset)) {
				i = 2;
				n = NUM_BITS_LONG_2;
			}
			if(COND_LONG_3(offset)) {
				i = 3;
				n = NUM_BITS_LONG_3;
			}
		}

		// First write number of bits
		wBit(((i & 2) == 0) ? 0 : 1);
		wBit(((i & 1) == 0) ? 0 : 1);

		if(n >= 8) { // Offset is 2 bytes

			// Then write the bits less than 8
			b = 1 << n;
			while(b > 0x100) {
				b >>= 1;
			wBit(((b & offset) == 0) ? 0 : 1);
			};

			// Finally write a whole byte, if necessary
			wByte(offset & 255 ^ 255); // Inverted(!)
			offset >>= 8;

		} else { // Offset is 1 byte

			// Then write the bits less than 8
			b = 1 << n;
			while(b > 1) {
				b >>= 1;
			wBit(((b & offset) == 0) ? 1 : 0); // Inverted(!)
			};

		}
	}


	/*
	 * Cost functions
	 */
	private int costOfLength(int len) {
		if(len == 1) return 1;
		if(len >= 2 && len <= 3) return 3;
		if(len >= 4 && len <= 7) return 5;
		if(len >= 8 && len <= 15) return 7;
		if(len >= 16 && len <= 31) return 9;
		if(len >= 32 && len <= 63) return 11;
		if(len >= 64 && len <= 127) return 13;
		if(len >= 128 && len <= 255) return 14;

		return 10000; //wrong value...
	}

	private int costOfOffset(int offset, int len) {
		if(len == 1) {
			if(COND_SHORT_0(offset)) return NUM_BITS_SHORT_0;
			if(COND_SHORT_1(offset)) return NUM_BITS_SHORT_1;
			if(COND_SHORT_2(offset)) return NUM_BITS_SHORT_2;
			if(COND_SHORT_3(offset)) return NUM_BITS_SHORT_3;
		} else {
			if(COND_LONG_0(offset)) return NUM_BITS_LONG_0;
			if(COND_LONG_1(offset)) return NUM_BITS_LONG_1;
			if(COND_LONG_2(offset)) return NUM_BITS_LONG_2;
			if(COND_LONG_3(offset)) return NUM_BITS_LONG_3;
		}
		
		return 10000; //wrong value;
	}

	private int calculateCostOfMatch(int len, int offset) {
		int cost = 1; // Copy-bit
		cost += costOfLength(len - 1);
		cost += 2; // NumOffsetBits
		cost += costOfOffset(offset - 1, len - 1);
		return cost;
	}

	private int calculateCostOfLiteral(int oldCost, int litLen) {
		int newCost = oldCost + 8;

		// FIXME, what if litLen > 255?
		//
		// FIXME, cost model for literals does not work.
		// Quick wins on short matches are prioritized before
		// a longer literal run, which in the end results in a
		// worse result.
		// Most obvious on files hard to crunch.
		switch(litLen) {
		case 1:
		case 128:
			newCost++;
			break;
		case 2:
		case 4:
		case 8:
		case 16:
		case 32:
		case 64:
			newCost += 2;
			break;
		default:
			break;
		}

		return newCost;
	}


	private void setupHelpStructures() {
		int i;

		// Setup RLE-info
		get = ibufSize - 1;
		while (get > 0) {

			byte cur = ibuf[get];
			if (cur == ibuf[get-1]) {

				int len = 2;
				while ((get >= len) && 
						(cur == ibuf[get-len])) {
					len++;
				}

				rleInfo[get].length = len;
				if (get >= len) {
					rleInfo[get].valueAfter = ibuf[get-len];
				} else {
					rleInfo[get].valueAfter = cur; // Avoid accessing ibuf[-1]
				}

				get -= len;
			} else {
				get--;
			}
		}


		// Setup Linked list
		for (i = 0; i < 65536; i++) {
			first[i] = 0;
			last[i] = 0;
		}

		get = ibufSize - 1;
		int cur = ibuf[get];

		while (get > 0) {

			cur = ((cur << 8) | ibuf[get-1]) & 65535;

			if (first[cur] == 0) {
				first[cur] = last[cur] = get;
			} else {
				link[last[cur]] = get;
				last[cur] = get;
			}

			if (rleInfo[get].length == 0) { // No RLE-match here..
				get--;
			} else { // if RLE-match..
				get -= (rleInfo[get].length - 1);
			}

		}
	}

	private class Match {
		int length;
		int offset;
		int cost;
	}

	private void findMatches() {

		Match[] matches = new Match[256];

		Node lastNode = new Node();
		int i;

		get = ibufSize - 1;
		int cur = ibuf[get];

		lastNode.cost = 0;
		lastNode.next = 0;
		lastNode.litLen = 0;

		while (get >= 0) {

			// Clear matches for current position
			for (i = 0; i < 256; i++) {
				matches[i].length = 0;
				matches[i].offset = 0;
				matches[i].cost = 0;
			}

			cur = (cur << 8) & 65535; // Table65536 lookup
			if (get > 0) cur |= ibuf[get-1];
			int scn = first[cur];
			scn = link[scn];

			int longestMatch = 0;

			if (rleInfo[get].length == 0) { // No RLE-match here..

				// Scan until start of file, or max offset
				while (((get - scn) <= MAX_OFFSET) &&
						(scn > 0) &&
						(longestMatch < 255)) {

					// Ok, we have a match of length 2
					// ..or longer, but max 255 or file start
					int len = 2;
					while ((len < 255) &&
							(scn >= len) &&
							(ibuf[scn - len] == ibuf[get - len])) {
						++len;
					}

					// Calc offset
					int offset = get - scn;

					// Store match only if it's the longest so far
					if(len > longestMatch) {
						longestMatch = len;

						// Store the match only if first (= best) of this length
						while(len >= 2 && matches[len].length == 0) {

							// If len == 2, check against short offset!!
							if ((len > 2) ||
									((len == 2) && (offset <= MAX_OFFSET_SHORT))) {
								matches[len].length = len;
								matches[len].offset = get - scn;
								matches[len].cost = calculateCostOfMatch(len, offset);
							}

							len--;
						};
					}

					scn = link[scn]; // Table65535 lookup
				};

				first[cur] = link[first[cur]]; // Waste first entry

			} else { // if RLE-match..

				int rleLen = rleInfo[get].length;
				byte rleVal = rleInfo[get].value;
				byte rleValAfter = rleInfo[get].valueAfter;


				// First match with self-RLE, which is always
				// one byte shorter than the RLE itself.
				int len = rleLen - 1;
				if (len > 1) {
					if (len > 255) len = 255;
					longestMatch = len;

					// Store the match
					while(len >= 2) {
						matches[len].length = len;
						matches[len].offset = 1;
						matches[len].cost = calculateCostOfMatch(len, 1);

						len--;
					};
				}


				// Search for more RLE-matches..
				// Scan until start of file, or max offset
				while (((get - scn) <= MAX_OFFSET) &&
						(scn > 0) &&
						(longestMatch < 255)) {

					// Check for longer matches with same value and after..
					// FIXME, that is not what it does, is it?!
					if ((rleInfo[scn].length > longestMatch) &&
							(rleLen > longestMatch)) {

						int offset = get - scn;
						len = rleInfo[scn].length;

						if (len > rleLen)
							len = rleLen;

						if ((len > 2) ||
								((len == 2) && (offset <= MAX_OFFSET_SHORT))) {
							matches[len].length = len;
							matches[len].offset = offset;
							matches[len].cost = calculateCostOfMatch(len, offset);

							longestMatch = len;
						}
					}


					// Check for matches beyond the RLE..
					if ((rleInfo[scn].length >= rleLen) &&
							(rleInfo[scn].valueAfter == rleValAfter)) {

						// Here is a match that goes beyond the RLE..
						// Find out correct offset to use valueAfter..
						// Then search further to see if more bytes equal.

						len = rleLen;
						int offset = get - scn + (rleInfo[scn].length - rleLen);

						if (offset <= MAX_OFFSET) {
							while ((len < 255) &&
									(get >= (offset + len)) &&
									(ibuf[get - (offset + len)] == ibuf[get - len])) {
								++len;
							}
							if (len > longestMatch){
								longestMatch = len;

								// Store the match only if first (= best) of this length
								while(len >= 2 && matches[len].length == 0) {

									// If len == 2, check against short offset!!
									if ((len > 2) ||
											((len == 2) && (offset <= MAX_OFFSET_SHORT))) {
										matches[len].length = len;
										matches[len].offset = offset;
										matches[len].cost = calculateCostOfMatch(len, offset);
									}

									len--;
								}; //while
							}
						}
					}

					scn = link[scn]; // Table65535 lookup
				}


				if (rleInfo[get].length > 2) {
					// Expand RLE to next position
					rleInfo[get-1].length = rleInfo[get].length - 1;
					rleInfo[get-1].value = rleInfo[get].value;
					rleInfo[get-1].valueAfter = rleInfo[get].valueAfter;
				} else {
					// End of RLE, advance link.
					first[cur] = link[first[cur]]; // Waste first entry
				}
			}


			// Now we have all matches from this position..
			// ..visit all nodes reached by the matches.

			for (i = 255; i > 0; i--) {

				// Find all matches we stored
				int len = matches[i].length;
				int offset = matches[i].offset;

				if (len != 0) {

					int targetI = get - len + 1;
					Node target = context[targetI];

					// Calculate cost for this jump
					int currentCost = lastNode.cost;
					currentCost += calculateCostOfMatch(len, offset);

					// If this match is first or cheapest way to get here
					// then update node
					if (target.cost == 0 ||
							target.cost > currentCost) {

						target.cost = currentCost;
						target.next = get + 1;
						target.litLen = 0;
						target.offset = offset;
					}
				}
			}


			// Calc the cost for this node if using one more literal
			int litLen = lastNode.litLen + 1;
			int litCost = calculateCostOfLiteral(lastNode.cost, litLen);

			// If literal run is first or cheapest way to get here
			// then update node
			Node _this = context[get];
			if (_this.cost == 0 ||
					_this.cost >= litCost) {
				_this.cost = litCost;
				_this.next = get + 1;
				_this.litLen = litLen;
			}

			lastNode.cost = _this.cost;
			lastNode.next = _this.next;
			lastNode.litLen = _this.litLen;

			// Loop to the next position
			get--;
		};

	}


	// Returns margin
	int writeOutput() {
		int i;

		//  get = 0;
		put = 0;

		curByte = 0;
		curCnt = 8;
		curIndex = put;
		put++;

		int maxDiff = 0;

		boolean needCopyBit = true;

		for (i = 0; i < ibufSize;) {

			int link = context[i].next;
			int cost = context[i].cost;
			int litLen = context[i].litLen;
			int offset = context[i].offset;

			if (litLen == 0) {
				// Put Match
				int len = link - i;

				if(needCopyBit) {
					wBit(1);
				}
				wLength(len - 1);
				wOffset(offset - 1, len - 1);

				i = link;

				needCopyBit = true;
			} else {
				// Put LiteralRun
				needCopyBit = false;

				while(litLen > 0) {
					int len = litLen < 255 ? litLen : 255;

					wBit(0);
					wLength(len);
					wBytes(i, len);

					if (litLen == 255) {
						needCopyBit = true;
					}

					litLen -= len;
					i += len;
				};
			}

			if ((int)(i - put) > maxDiff) {
				maxDiff = i - put;
			} else {
				//      if((int)(i - put) != maxDiff)
				//	printf("lost %i bytes :)\n", maxDiff - (int)(i - put));
			}

		}

		wBit(1);
		wLength(0xff);
		wFlush();

		//  printf("Get = %i, Put = %i, Diff = %i\n",i, put, i - put);

		int margin = (maxDiff - (i - put));
		//  printf("Margin = %i\n", margin);

		return margin;
	}


	private CrunchedObject doCrunch(byte[] aSource, int loadAddress, int packStart) {
		int i;
		byte[] target;

		ibufSize = aSource.length; // - 2;
		ibuf = new byte[ibufSize];
		context = new Node[ibufSize];
		link = new int[ibufSize];
		rleInfo = new RLEInfo[ibufSize];

		// Load ibuf and clear context
		for(i = 0; i < ibufSize; ++i) {
			ibuf[i] = aSource[i];
			context[i].cost = 0;
			link[i] = 0;
			rleInfo[i].length = 0;
		}

		setupHelpStructures();
		findMatches();
		obuf = new byte[MEM_SIZE];
		int margin = writeOutput();

		int packLen = put;
		int fileLen = put + 4;

		target = new byte[fileLen];
		/*
		aTarget.size = fileLen;
		aTarget->data = (byte*)malloc(aTarget->size);
		target = aTarget->data;
		 */
		// Experimantal decision of start address
		//    uint startAddress = 0xfffa - packLen - 2;
		int startAddress = loadAddress;
		startAddress += (ibufSize - packLen - 2 + margin);

		/*
			if (isRelocated) {
				startAddress = address - packLen - 2;
			}
		 */

		target[0] = (byte) (startAddress & 0xff); // Load address
		target[1] = (byte) (startAddress >> 8);
		target[2] = (byte) (loadAddress & 0xff); // Depack to address
		target[3] = (byte) (loadAddress >> 8);

		for(i = 0; i < put; ++i) {
			target[i + 4] = obuf[i];
		}

		//  printf("File len: %i\n", fileLen);
		//  printf("Final cost: %i bytes\n", (context[0].cost + 7) / 8);

		return new CrunchedObject(target, margin);
	}

}
