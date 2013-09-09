Build instructions for Exomizer library-files
---------------------------------------------

Unzip the Exomizer source directory, and replace the Makefile with the provided one.
You will also need to copy the supplied patch-file to the directory. The patch prevents logging to the console.

The library file is then built by issuing `make lib`.
Copy the resulting file to the right directory for the current architecture.
For example, if the architecture is intel x86 win32, the resulting dll goes into src/main/resources/win32-x86


Rebuild the supporting JNA java-files
-------------------------------------

In order to build the java-files using JNA for the plugin, please download the latest version of the jnaerator jar:
https://code.google.com/p/jnaerator/downloads/list

And update the reference to the jar in the Makefile.
`make jar` will generate java files in the source dir. Copy these files to the src/main/java directory.


