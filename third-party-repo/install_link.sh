#!/bin/bash

echo Installing in Linux environment

: <<'END_COMMENT'
This script installs the following libraries in the /usr/lib/ directory.
If you do not want to install these files in /usr/lib directly,
either put them on your PATH or add them to the Java classpath at runtime.

libgdalconstjni.la
libgdalconstjni.so
libgdaljni.la
libgdaljni.so
libjnetpcap.so
libogrjni.la
libogrjni.so
libosrjni.la
libosrjni.so

END_COMMENT

sudo cp target/ubuntu/*.so /usr/lib/

e=$?

sudo cp target/ubuntu/*.la /usr/lib/

if [ $? -gt 0 ] || [ $e -gt 0 ]
then
        echo Error installing third party dependencies.
        exit 1
fi

sudo ldconfig
exit

