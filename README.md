KickAss Cruncher Plugins
========================

**KickAss Cruncher Plugins** makes it possible to crunch (pack) 6502 assembler code and other data compile-time with 
the MOS 65xx assembler **[Kick Assembler](http://www.theweb.dk/KickAssembler)**. This is done by the means of Kick Assembler's plugin support, in specific *Modifiers*.
The current version has support for two of the most popular crunchers for the Commodore 64, *[ByteBoozer](http://csdb.dk/release/?id=109317)* and *[Exomizer](http://hem.bredband.net/magli143/exo/)*.

#### Example:
    
    .plugin "se.booze.kickass.CruncherPlugins"
 
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
    
    java -cp "kickass-cruncher-plugins-1.1.jar:KickAss.jar" kickass.KickAssembler demo.asm

The desired plugin must then be declared in the source-code in order for Kick Assembler to actually load it:
    
    .plugin "se.booze.kickass.CruncherPlugins"

You can read more about plugins in Kick Assembler's online [documentation](http://www.theweb.dk/KickAssembler/webhelp/content/cpt_Plugins.html "Kick Assembler documentation").     

The pre-compiled Exomizer dylib for MacOS High Sierra seems to have problems, resulting with SIGSEGV.
As a workaround, the plugin can fallback to execute exomizer via commandline.
This must be added as a system property to the commandline:

    java -cp "kickass-cruncher-plugins-2.0.jar:KickAss.jar" \
        -D EXOMIZER_COMMANDLINE_FALLBACK=path_to_exomizer_executable \
        kickass.KickAssembler demo.asm

You can provide extra parameters to the fallback command by providing a comma separated list of parameters via the property EXOMIZER_COMMANDLINE_FALLBACK_EXTRA_PARAMS

    java -cp "kickass-cruncher-plugins-2.0.jar:KickAss.jar" \
        -D EXOMIZER_COMMANDLINE_FALLBACK=exomizer3 \
        -D EXOMIZER_COMMANDLINE_FALLBACK_EXTRA_PARAMS=-T5
        kickass.KickAssembler demo.asm

Caching the results of the cruncher may improve performance if you have a large project.
It is enabled by the property KICKASS_CRUNCHER_CACHE

    java -cp "kickass-cruncher-plugins-2.0.jar:KickAss.jar" \
        -D KICKASS_CRUNCHER_CACHE=true \
        kickass.KickAssembler demo.asm


Please note that out of laziness, the safety offset is always returned as $0000
when using this fall back.

Changelog
=========

*   Added general support for caching, via the property KICKASS_CRUNCHER_CACHE.
    This deprecates the DISABLE_EXOMIZER_CACHE flag. 2019-05-12
    
*   Added support for extra parameters when using the fallback command.
    EXOMIZER_COMMANDLINE_FALLBACK_EXTRA_PARAMS. 2019-05-11

*   Experimental support for segment modifiers. All plugins are supported. WIP.

*   Added experimental B2EXE cruncher which crunches and adds a basic upstart routine. WIP.


Supported Crunchers
===================

ByteBoozer
----------

The ByteBoozer plugin is a pure java implementation of David Malmborg's excellent cruncher.

#### Syntax:
    .modify ByteBoozer(boolean reverse [false])

*Square brackets denotes default values used when argument is left out*
    
#### Example:
    .plugin "se.booze.kickass.CruncherPlugins"

    .modify ByteBoozer() {
        .pc = $1000
        //code
    }

You can also crunch several segments of memory. The cruncher will merge these together into one block spanning
the memory area required by all contained memory blocks, zero-filling any gaps.

#### Example:
    .plugin "se.booze.kickass.CruncherPlugins"

    .modify ByteBoozer() {
        .pc = $4000 "Bitmap"
        .fill picture.getBitmapSize(), picture.getBitmap(i)
        .pc = $6000 "Screendata"
        .fill picture.getScreenRamSize(), picture.getScreenRam(i)
    }

There is also experimental support for reversed output:
   
#### Example:   
    .plugin "se.booze.kickass.CruncherPlugins"

    .modify ByteBoozer(true) {
        .pc = $1000
        //whatever
    }
    
B2
--

Next generation of ByteBoozer. Faster and better. Use this instead of BB if you can


#### Syntax:
    .modify B2(int startAdress (optional for margin check))

*Square brackets denotes default values used when argument is left out*
    
#### Example:
    .plugin "se.booze.kickass.CruncherPlugins"

    .modify B2() {
        .pc = $1000
        //code
    }



Exomizer
--------

The Exomizer-plugin supports several modes of Magnus Lind's awesome tool **Exomizer**: *level*, *mem* and *raw*.
Also, backward and forward compression and compatibilty mode (no literals) are supported.
The implementation is a small java wrapper around native code in form of shared libraries. 
Currently included are shared libraries for Mac OS X, Windows (both 32 and 64-bit) and Linux (i386 and x86_64).
Please see the Build-section for instructions on how to rebuild the native libraries.

All Exomizer-plugins are included in an IArchive `se.booze.kickass.exomizer.ExomizerArchive`, for convenient loading of all plugins.

Exomized data is now cached. The caching mechanism can be disabled by setting the system property `DISABLE_EXOMIZER_CACHE=true`.
Cached data is stored as files in `java.io.tmpdir`, e.g. `/tmp` on a Linux system with the extension `.exo`.


### MemExomizer

MemExomizer will merge all memory areas contained in the modify block into one, single block spanning the entire memory region required.
Gaps will be zero-filled. Crunching is done either forward or backward and the use of literals is optional.
Forward crunching is often the case for loaders (e.g Krill's loader). Backward-decrunch is more common for in-memory decrunching where 
decrunched data may overlap input. Exomizer outputs a safety offset, and this is the minimum offset needed between uncrunched and decrunched data.
The plugin can help out with asserting this if a start-address for the compressed data is provided (e.g. `*` for current PC).

#### Syntax:
    .modify MemExomizer( boolean forwardCrunching [false], boolean useLiterals [true], int startAddress [no check])

There is also two convenience modifiers. They are equal to calling MemExomizer with `useLiterals` set to `true`.

     .modify BackwardMemExomizer(int startAddress [no check])
     .modify ForwardMemExomizer(int startAddress [no check])
 
*Square brackets denotes default values used when argument is left out*

#### Example:
    .plugin "se.booze.kickass.CruncherPlugins"

    // Backward-crunching using literals and also asserts that the result can be decrypted at the current PC
    .modify MemExomizer( false, true, *){

        .pc = $4000 "Bitmap"
        .fill picture.getBitmapSize(), picture.getBitmap(i)
        .pc = $6000 "Screendata"
        .fill picture.getScreenRamSize(), picture.getScreenRam(i)
    }
     
 
### LevelExomizer

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
 

#### Syntax:
    .modify LevelExomizer( boolean forwardCrunching [false], boolean useLiterals [true], boolean outputOffsets [false])

*Square brackets denotes default values used when argument is left out*
 
#### Example:
    .plugin "se.booze.kickass.CruncherPlugins"

    .modify LevelExomizer(){
    
        .pc = $1000 "SID number one"
        //Load SID 1
        
        .pc = $1000 "SID number two"
        //Load SID 2
    }


### RawExomizer

RawExomizer only handles one memory block. In the case of inline crunching, you are probably better off with the Mem or Level modes.

#### Syntax:
    .modify RawExomizer( boolean forwardCrunching [false], boolean useLiterals [true], boolean reverseOutput [false] )
        
#### Example:
    .plugin "se.booze.kickass.CruncherPlugins"

    .modify RawExomizer( true, true ) {
        
        // data
    }
    
*Square brackets denotes default values used when argument is left out*

Exomizer 3
----------

Using `EXOMIZER_COMMANDLINE_FALLBACK` you should be able to crunch using Exomizer 3.
Just add `-D EXOMIZER_COMMANDLINE_FALLBACK=/path/to/exomizer3` as argument to java.

Since the plugin uses the raw mode when compiling, the output will not match what
exomizer3 mem or level will output. You will have to add the option -T5 for it to do that.
It can be done via the EXOMIZER_COMMANDLINE_FALLBACK_EXTRA_PARAMS property.

    java -cp "kickass-cruncher-plugins-2.0.jar:KickAss.jar" \
        -D EXOMIZER_COMMANDLINE_FALLBACK=exomizer3 \
        -D EXOMIZER_COMMANDLINE_FALLBACK_EXTRA_PARAMS=-T5
        kickass.KickAssembler demo.asm

Note. Exomizer 3 comes with a new decruncher which is not included with this package.

Please see Exomizer's home page for details

https://bitbucket.org/magli143/exomizer/wiki/Home 


Examples
--------

The release zip-file contains a couple of small examples to get you going.

Build
-----

#### Prerequisites

 * [Apache Maven](http://maven.apache.org)
 * [Java SE, JDK 8+](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
 
At the project root-dir, type:

    mvn clean package

Please add the exomizer fallback option if you get errors on MacOS High Sierra:

    mvn clean package -D EXOMIZER_COMMANDLINE_FALLBACK=/path/to/exomizer 

This compiles, tests and packages the release zip-file, which you will find in the project's target/release directory.

### Build instructions for Exomizer library-files

In order to build the native libraries for Exomizer, you will need a proper toolchain.
For example [MinGW](http://www.mingw.org) in the case of MS Windows.
Mac OS X users should download [Command Line Tools for Xcode](https://developer.apple.com/xcode/)
Linux users usually know these things already. 

The library needs the Exomizer source-code. Download Exomizer from the Exomizer 2 website:
http://hem.bredband.net/magli143/exo/

Unzip the src directory somewhere.

The library file is then built by issuing 
    make -f support-files/Makefile lib SRC_DIR={path_to_exomizer_src}

Copy the resulting library-file to the correct directory for the current architecture which must be one of:

    ${project.basedir}/src/main/resources/darwin
    ${project.basedir}/src/main/resources/linux-x86
    ${project.basedir}/src/main/resources/linux-x86-64
    ${project.basedir}/src/main/resources/win32-x86
    ${project.basedir}/src/main/resources/win32-x86-64


### Rebuild the supporting JNA java-files

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

