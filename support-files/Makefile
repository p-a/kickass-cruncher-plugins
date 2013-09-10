#
# Makefile for exolib
#

ifeq ($(OS),Windows_NT)
    LIBFILE_EXT = dll
    LIB_LDFLAGS =
else
    UNAME_S := $(shell uname -s)
    ifeq ($(UNAME_S),Linux)
        LIBFILE_EXT = so
        LIB_LDFLAGS = -shared
    endif
    ifeq ($(UNAME_S),Darwin)
        LIBFILE_EXT = dylib
        LIB_LDFLAGS = -dynamiclib -undefined suppress -flat_namespace
    endif
endif

WFLAGS = -Wall -Wstrict-prototypes
CFLAGS = $(WFLAGS) -O3 -ffast-math -mtune=i686 -fomit-frame-pointer -fgcse -pedantic
LDFLAGS = -s

#CFLAGS = -g -mtune=i686 $(WFLAGS)
#LDFLAGS = -g -mtune=i686

LIBFILE =  libexo.$(LIBFILE_EXT)

LIBEXO_OBJS = getflag.o exo_helper.o output.o search.o match.o optimal.o chunkpool.o progress.o radix.o exo_util.o exodec.o membuf.o log.o

#.SILENT:

.PHONY: build clean

jar:	$(LIBFILE)
	@$(RM) -rf net
	java -jar jnaerator.jar -mode Directory -library Exo -package net.magli143.exo $(SRC_DIR)/membuf.h $(SRC_DIR)/exo_helper.h $(LIBFILE) -o . -v -runtime JNA
	@$(RM) _jna*

lib:	$(LIBFILE)

$(LIBFILE): $(addprefix $(SRC_DIR)/,$(LIBEXO_OBJS))
	@echo "Linking $@"
	@$(CC) $(LIB_LDFLAGS) $(addprefix $(SRC_DIR)/,$(LIBEXO_OBJS)) -o $@
	
clean:
	@echo "Cleaning libraryfiles"
	-@$(RM) $(LIBFILE)
	-@$(RM) -rf net

