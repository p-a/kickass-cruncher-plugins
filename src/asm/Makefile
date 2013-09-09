JAVA=java
sources = $(wildcard *.asm)
binaries = $(sources:.asm=.prg)

all: $(binaries)

clean :
	rm -f $(binaries)

%.prg : %.asm
	$(JAVA) -cp "../*:KickAss.jar" cml.kickass.KickAssembler $<

