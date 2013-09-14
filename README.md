Cruncher-plugins for Kick Assembler
===================================

**Kickass cruncher-plugins** makes it possible to crunch (pack) 6502 assembler code and other data compile-time with 
the MOS 65xx assembler **[Kick Assembler](http://www.theweb.dk/KickAssembler)**. This is done by the means of Kick Assembler's plugin support, in specific *Modifiers*.
The current version has support for two of the most popular crunchers for the Commodore 64, *ByteBoozer* and *Exomizer*.

####Example:
    
    .plugin "se.triad.kickass.byteboozer.ByteBoozer"
    .var music = LoadSid("res/Ruk - J.sid")
    
    .label crunchedMusic = *
    .modify ByteBoozer() {
        .pc = music.location "Music"
        .fill music.size, music.getData(i)
    }


Download
========

You can find the latest version right here on GitHub:

https://github.com/p-a/kickass-cruncher-plugins/releases

Usage
=====

You must first see to that the plugins is on the java classpath:
    
    java -cp "cruncher-plugins.jar:KickAss.jar" cml.kickass.KickAssembler demo.asm

The desired plugin must then be declared in the source-code in order for Kick Assembler to actually load it:
    
    .plugin "se.triad.kickass.byteboozer.ByteBoozer"

You can read more about plugins in Kick Assembler's online [documentation](http://www.theweb.dk/KickAssembler/webhelp/content/cpt_Plugins.html "Kick Assembler documentation").     

Supported Crunchers
===================

ByteBoozer
----------

The ByteBoozer plugin is a pure java implementation of David Malmborg's excellent cruncher.

####Syntax:
    .plugin "se.triad.kickass.byteboozer.ByteBoozer"

    .modify ByteBoozer(boolean reverse [false])

*Square brackets denotes default values used when argument is left out*
    
####Example:
    .plugin "se.triad.kickass.byteboozer.ByteBoozer"
    
    .modify ByteBoozer() {
        .pc = $1000
        //code
    }

You can also crunch several segments of memory. The cruncher will merge these together into one block spanning
the memory area required by all contained memory blocks, zero-filling any gaps.

####Example:
    .modify ByteBoozer() {
        .pc = $4000 "Bitmap"
        .fill picture.getBitmapSize(), picture.getBitmap(i)
        .pc = $6000 "Screendata"
        .fill picture.getScreenRamSize(), picture.getScreenRam(i)
    }
####Output:
    ByteBoozer: [ Bitmap, Screendata ] $4000 - $63e7 Packed size $0c28 (33%) 

There is also experimental support for reversed output.
   
####Example:   
    .modify ByteBoozer(true) {
        .pc = $1000
        //whatever
    }

Exomizer
--------

The Exomizer-plugin supports several modes of Magnus Lind's awesome tool **Exomizer**: *level*, *mem* and *raw*.
Also, backward and forward compression and compatibilty mode (no literals) are supported.

MemExomizer also supports assertion of safety offset. If a start address is provided (usually '\*') an error will occur if the data cannot be unpacked at that address.

The implementation is a small java wrapper around native code. Please see the Build-section for instructions how to rebuild the native code.

####Syntax:

    .plugin "se.triad.kickass.exomizer.ExomizerArchive" //Loads all Exomizer-related plugins

    .modify MemExomizer( boolean forwardCrunching [false], boolean useLiterals [true], int startAddress [no check])
    .modify LevelExomizer( boolean forwardCrunching [false], boolean useLiterals [true])
    .modify RawExomizer( boolean forwardCrunching [false], boolean useLiterals [true], boolean reverseOutput [false] )

*Square brackets denotes default values used when argument is left out*

There is also two convenience modifiers. They are equal to calling MemExomizer with `useLiterals` set to `true`.

     .modify BackwardMemExomizer(int startAddress [no check])
     .modify ForwardMemExomizer(int startAddress [no check])

####MemExomizer

MemExomizer will merge all memory areas contained in the modify block into one, single block spanning the entire memory region required.
Gaps will be zero-filled.
 

####LevelExomizer

####RawExomizer





 
Exomizer, (c) Magnus Lind
http://hem.bredband.net/magli143/exo/

ByteBoozer (c) David Malmborg
http://csdb.dk/release/?id=109317

Kick Assembler (c) Mads Nielsen
http://www.theweb.dk/KickAssembler/Main.php

JNA - Java Native Access
https://github.com/twall/jna

