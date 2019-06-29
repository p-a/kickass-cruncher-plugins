package se.triad.kickass;

public enum Options {
    FORWARD_CRUNCHING("_forwardCrunching"),
    USE_LITERALS("_useLiterals"),
    APPEND_IN_LOAD("_appendInLoad"),
    VALIDATE_SAFETY_OFFSET("_validateSafetyOffset"),
    REVERSE_OUTPUT("_reverseOutput"),
    OUTPUT_BLOCK_OFFSETS("_outputBlockOffsets"),
    MAXIMUM_OFFSET_SIZE("_maximumOffsetSize"),
    JMP_ADDRESS("_jmpAdress");
    
    private String name;

	Options(String name) {
    	this.name = name;
    }
	
	public String getName() {
		return this.name;
	}
}