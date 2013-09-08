package se.triad.kickass.byteboozer;

class BBObject {

	final byte[] data;
	final int packStart;

	public BBObject(byte[] data, int packStart) {
		this.data = data;
		this.packStart = packStart;
	}
	
}
