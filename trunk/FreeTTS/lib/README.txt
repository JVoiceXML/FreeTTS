Contains JAR files for FreeTTS


JSAPI 1.0 specification implementation - FreeTTS requires an
implementation of the JSAPI 1.0 specification.  The JSAPI 1.0
specification implementation (i.e., the javax.speech.* classes) is not
available under a BSD-style license. Instead, we are making the
binaries available under a separate binary code license.

Unix Systems - This distribution includes a self-extracting shell
archive containing the jsapi.jar file. If you are running under Linux
or the SolarisTM Operating Environment, (or any other system that
supports sh) follow these instructions:

        1. Go to the FreeTTS/lib directory 
	2. Type sh ./jsapi.sh
       	3. If the binary license agreement is acceptable, accept
	   it by typing "y". The jsapi.jar file will be unpacked
	   and deposited into the lib directory.

Win32 - This distribution includes an installer program containing the
jsapi.jar file. If you are running on a Win32 Operating system follow
these instructions:

        1. Go to the FreeTTS/lib directory 
	2. Type .\jsapi.exe
        3. If the binary license agreement is acceptable, accept
	   it by clicking "I Agree". The jsapi.jar file will be unpacked 
 	   and deposited into the lib directory.
