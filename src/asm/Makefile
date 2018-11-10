JAVA=java
sources = $(wildcard *.asm)
binaries = $(sources:.asm=.prg)

ifeq ($(OS),Windows_NT)
    CLASSPATH = "../*;KickAss.jar"
else
    CLASSPATH = "../*:KickAss.jar"
endif

ifneq ($(EXOMIZER_COMMANDLINE_FALLBACK),)
	EXO_FALLBACK := "-DEXOMIZER_COMMANDLINE_FALLBACK=$(EXOMIZER_COMMANDLINE_FALLBACK)"	
endif

all: $(binaries)

clean :
	rm -f $(binaries)

%.prg : %.asm
	@$(JAVA) $(EXO_FALLBACK) -cp $(CLASSPATH) cml.kickass.KickAssembler $<

