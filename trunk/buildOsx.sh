#!/bin/sh
# --------------------------------------------------------------------------
# buildscript for osx 32/64
# --------------------------------------------------------------------------
# Processing Library for the Dynamixel Servo
# http://code.google.com/p/simple-dynamixel
# --------------------------------------------------------------------------
# prog:  Max Rheiner / Interaction Design / Zhdk / http://iad.zhdk.ch/
# date:  08/12/2012 (m/d/y)
# ----------------------------------------------------------------------------
# Change those vars to the folders you have on your system:
#	-DBOOST_ROOT 		= folder of Boost root
#	-DP5_JAR 		= filepath to your core.jar (Processing)
# ----------------------------------------------------------------------------


# optional, but gives a clean build
rm -r build

# check if build folder exists
if [ ! -d "build" ]; then
    mkdir build
fi

cd ./build

echo "--- generate cmake ---"
cmake -DCMAKE_BUILD_TYPE=Release \
	  -DEIGEN3D_INCLUDE=/usr/local/include/eigen3/ \
	  -DP5_JAR=/Applications/Processing.app/Contents/Resources/Java/core.jar \
          -DP5_JAR_SERIAL=~/Applications/Processing.app/Contents/Resources/Java/modes/java/libraries/serial/library/serial.jar \
          -DCMAKE_OSX_ARCHITECTURES="i386;x86_64" \
	  ..


echo "--- build ---"
# build with 6 threads, verbose is optional, but otherwise you can't see the compiler directives
make -j 6 VERBOSE=1


echo "--- copy ---"
# copy the library
cp SimpleDynamixel.jar ../dist/all/SimpleDynamixel/library
cp libSimpleDynamixel.jnilib ../dist/all/SimpleDynamixel/library

# copy the doc
cp -r ./doc/* ../dist/all/SimpleDynamixel/documentation/
