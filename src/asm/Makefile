JAVA=java
sources = $(wildcard *.asm)
binaries = $(sources:.asm=.prg)

ifeq ($(OS),Windows_NT)
    CLASSPATH = "../*;KickAss.jar}"
else
    CLASSPATH = "../*:KickAss.jar}"
endif

all: $(binaries)

clean :
	rm -f $(binaries)

%.prg : %.asm
	@$(JAVA) -cp $(CLASSPATH) cml.kickass.KickAssembler $<

