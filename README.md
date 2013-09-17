KickAss Cruncher Plugins
========================

**KickAss Cruncher Plugins** makes it possible to crunch (pack) 6502 assembler code and other data compile-time with 
the MOS 65xx assembler **[Kick Assembler](http://www.theweb.dk/KickAssembler)**. This is done by the means of Kick Assembler's plugin support, in specific *Modifiers*.
The current version has support for two of the most popular crunchers for the Commodore 64, *[ByteBoozer](http://csdb.dk/release/?id=109317)* and *[Exomizer](http://hem.bredband.net/magli143/exo/)*.

####Example:
    
    .plugin "se.triad.kickass.byteboozer.ByteBoozer"
    .var music = LoadSid("res/Ruk - J.sid")
    
    .label crunchedMusic = *
    .modify ByteBoozer() {
        .pc = music.location "Music"
        .fill music.size, music.getData(i)
    }

License
=======

KickAss Cruncher Plugins is licensed under the MIT License.  
[LICENSE](LICENSE.txt)

Download
========

You can find the latest version right here on GitHub:

https://github.com/p-a/kickass-cruncher-plugins/releases

Usage
=====

You must first see to that the plugins are on the java classpath:
    
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

There is also experimental support for reversed output:
   
####Example:   
    .modify ByteBoozer(true) {
        .pc = $1000
        //whatever
    }

Exomizer
--------

The Exomizer-plugin supports several modes of Magnus Lind's awesome tool **Exomizer**: *level*, *mem* and *raw*.
Also, backward and forward compression and compatibilty mode (no literals) are supported.
The implementation is a small java wrapper around native code in form of shared libraries. 
Currently included are shared libraries for Mac OS X, Windows (both 32 and 64-bit) and Linux (i386 and x86_64).
Please see the Build-section for instructions on how to rebuild the native libraries.

All Exomizer-plugins are included in an IArchive `se.triad.kickass.exomizer.ExomizerArchive`, for convenient loading of all plugins.

###MemExomizer

MemExomizer will merge all memory areas contained in the modify block into one, single block spanning the entire memory region required.
Gaps will be zero-filled. Crunching is done either forward or backward and the use of literals is optional.
Forward crunching is often the case for loaders (e.g Krill's loader). Backward-decrunch is more common for in-memory decrunching where 
decrunched data may overlap input. Exomizer outputs a safety offset, and this is the minimum offset needed between uncrunched and decrunched data.
The plugin can help out with asserting this if a start-address for the compressed data is provided (e.g. \'*\' for current PC).

####Syntax:

    .plugin "se.triad.kickass.exomizer.ExomizerArchive" //Loads all Exomizer-related plugins

    .modify MemExomizer( boolean forwardCrunching [false], boolean useLiterals [true], int startAddress [no check])

There is also two convenience modifiers. They are equal to calling MemExomizer with `useLiterals` set to `true`.

     .modify BackwardMemExomizer(int startAddress [no check])
     .modify ForwardMemExomizer(int startAddress [no check])
 
*Square brackets denotes default values used when argument is left out*

####Example:

    .plugin "se.triad.kickass.exomizer.ExomizerArchive" //Loads all Exomizer-related plugins

    // Backward-crunching using literals and also asserts that the result can be decrypted at the current PC
    .modify MemExomizer( false, true, *){

        .pc = $4000 "Bitmap"
        .fill picture.getBitmapSize(), picture.getBitmap(i)
        .pc = $6000 "Screendata"
        .fill picture.getScreenRamSize(), picture.getScreenRam(i)
    }
     
 
###LevelExomizer

LevelExomizer will crunch all memory areas contained in the modify-block as separate blocks.
On decrunch, each block must be decrunched separately. Can be more convenient than having multiple MemExomized-blocks.
As of now, there is no support for assertions of start-addresses, but the safety distance is reported for each block in the console.

There is also an option to output the offsets for each block. The  offsets are output as an array of words (little endian) at the end 
of the crunched data. The offsets can be used to calculate the address of each crunched block. 

 
    +-------------+
    | block one   |
    +-------------+
    | block two   |
    +-------------+
    | block three |
    +-------------+
    | offsets     |
    +-------------+

The first offset for forward decrunching is always 0. Backward decrunching offsets are end-positions of block + 1.
 

####Syntax:

    .plugin "se.triad.kickass.exomizer.ExomizerArchive" //Loads all Exomizer-related plugins

    .modify LevelExomizer( boolean forwardCrunching [false], boolean useLiterals [true], boolean outputOffsets [false])

*Square brackets denotes default values used when argument is left out*
 
####Example:


    .plugin "se.triad.kickass.exomizer.ExomizerArchive" //Loads all Exomizer-related plugins

    .modify LevelExomizer(){
    
        .pc = $1000 "SID number one"
        //Load SID 1
        
        .pc = $1000 "SID number two"
        //Load SID 2
    }


###RawExomizer

RawExomizer only handles one memory block. In the case of inline crunching, you are probably better off with the Mem or Level modes.

####Syntax:

    .plugin "se.triad.kickass.exomizer.ExomizerArchive" //Loads all Exomizer-related plugins

    .modify RawExomizer( boolean forwardCrunching [false], boolean useLiterals [true], boolean reverseOutput [false] )
        
####Example:

    .modify RawExomizer( true, true ) {
        
        // data
    }
    
*Square brackets denotes default values used when argument is left out*


Examples
--------

The release zip-file contains a couple of small examples to get you going.

Build
-----

####Prerequisites

 * [Apache Maven](http://maven.apache.org)
 * [Java SE, JDK 6+](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
 
At the project root-dir, type:

    mvn clean package

This builds, tests and packages the jar, which you will find in the target directory.

The release zipfile is built with

    mvn clean assembly:assembly
    

###Build instructions for Exomizer library-files

In order to build the native libraries for Exomizer, you will need a proper toolchain.
For example [MinGW](http://www.mingw.org) in the case of MS Windows.
Mac OS X users should download [Command Line Tools for Xcode](https://developer.apple.com/xcode/)
Linux users usually know these things already. 

The library needs the Exomizer source-code. Download Exomizer from the Exomizer 2 website:
http://hem.bredband.net/magli143/exo/

Unzip the src-directory somewhere.

The library file is then built by issuing 
    make -f support-files/Makefile lib SRC_DIR={path_to_exomizer_src}

Copy the resulting library-file to the correct directory for the current architecture which must be one of:

    ${project.basedir}/src/main/resources/darwin
    ${project.basedir}/src/main/resources/linux-x86
    ${project.basedir}/src/main/resources/linux-x86-64
    ${project.basedir}/src/main/resources/win32-x86
    ${project.basedir}/src/main/resources/win32-x86-64


###Rebuild the supporting JNA java-files

In order to rebuild the java-files using JNA for the plugin, please download the latest version of the jnaerator jar:
<https://code.google.com/p/jnaerator/downloads/list>

Generate the JNA java-files by entering

    make -f support-files/Makefile jar SRC_DIR={path_to_exomizer_src} JNA_JAR={path_to_jarfile}

Copy the generated files to the src/main/java directory, i.e. the resulting 'net' directory and all its contents.
You may have to manually edit the lookup of library/libraries in the file `net/magli143/exo/ExoLibrary.java`

Credits
-------

KickAss cruncher plugins was created by P-a B&auml;ckstr&ouml;m, aka Ruk / TRIAD.

Special thanks to

 * Magnus Lind for helping out with Exomizer.
 * Mads Nielsen for Kick Assembler support. 
 * David Malmborg
 * Andreas Lindqvist
 * Lennart Marklund

References
----------

Exomizer, Copyright (c) Magnus Lind  
<http://hem.bredband.net/magli143/exo>

ByteBoozer, Copyright (c) David Malmborg  
<http://csdb.dk/release/?id=109317>

Kick Assembler, Copyright (c) Mads Nielsen  
<http://www.theweb.dk/KickAssembler/Main.php>

JNA - Java Native Access  
<https://github.com/twall/jna>

JNAerator - Native C / C++ / Objective-C libraries come to Java !  
<https://code.google.com/p/jnaerator/>

Apache Maven  
<http://maven.apache.org>

