#
# Makefile for libexo. Expects SRC_DIR to point to Exomizer's src-dir
#  
# Usage:
# make SRC_DIR={path}
# 
# or if building the JNA-wrapper files
#
# make jar SRC_DIR={path} JNA_JAR={path to jnanerator.jar}
#

ifeq ($(SRC_DIR),)
$(error Please set SRC_DIR)
endif

ifeq ($(JNA_JAR),)
 JNA_JAR = jnaerator.jar
endif

ifeq ($(OS),Windows_NT)
    LIBFILE_EXT = dll
    LIB_LDFLAGS =
else
    UNAME_S := $(shell uname -s)
    ifeq ($(UNAME_S),Linux)
        LIBFILE_EXT = so
        LIB_LDFLAGS = -shared
        CFLAGS_OPT = -fPIC
    endif
    ifeq ($(UNAME_S),Darwin)
        LIBFILE_EXT = dylib
        LIB_LDFLAGS = -dynamiclib -undefined suppress -flat_namespace
    endif
endif

WFLAGS = -Wall -Wstrict-prototypes
CFLAGS = $(WFLAGS) -O3 $(CFLAGS_OPT) -ffast-math -mtune=i686 -fomit-frame-pointer -fgcse -pedantic
LDFLAGS = -s

LIBFILE =  libexo.$(LIBFILE_EXT)

LIBEXO_OBJS = getflag.o exo_helper.o output.o search.o match.o optimal.o chunkpool.o progress.o radix.o exo_util.o exodec.o membuf.o log.o

.PHONY: clean jar lib all
.DEFAULT_GOAL := lib

lib:	$(LIBFILE)

$(LIBFILE): $(addprefix $(SRC_DIR)/,$(LIBEXO_OBJS))
	@echo "Linking $@"
	@$(CC) $(LIB_LDFLAGS) $(addprefix $(SRC_DIR)/,$(LIBEXO_OBJS)) -o $@
	
jar:	$(LIBFILE)
	@$(RM) -rf net
	java -jar $(JNA_JAR) -mode Directory -library Exo -package net.magli143.exo $(SRC_DIR)/membuf.h $(SRC_DIR)/exo_helper.h $(LIBFILE) -o . -v -runtime JNA
	@$(RM) _jna*

clean:
	@echo "Cleaning libraryfiles"
	-@$(RM) $(LIBFILE)
	-@$(RM) -rf net
	-@$(RM) -rf lib

all:	lib	

