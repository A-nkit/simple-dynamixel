#!/bin/sh
# --------------------------------------------------------------------------
# buildscript for linux 64bit
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
rm -r build64

# check if build folder exists
if [ ! -d "build64" ]; then
    mkdir build64
fi

cd ./build64

echo "--- generate cmake ---"
# changes this according to your environment
cmake -DCMAKE_BUILD_TYPE=Release \
      -DBOOST_ROOT=~/Documents/development/libs/boost/boost_1_46_1/ \
      -DP5_JAR=~/Documents/localApps/processing-1.5.1/lib/core.jar \
      -DP5_JAR_SERIAL=~/Documents/localApps/processing-1.5.1/modes/java/libraries/serial/library/serial.jar \
      -
      ..

echo "--- build ---"
# build with 6 threads, verbose is optional, but otherwise you can't see the compiler directives
# make -j 6 VERBOSE=1
make -j 6

echo "--- copy ---"
# copy the library
cp SimpleDynamixel.jar ../dist/all/SimpleDynamixel/library
cp libSimpleDynamixel*.so ../dist/all/SimpleDynamixel/library

# copy the doc
cp -r ./doc/* ../dist/all/SimpleDynamixel/documentation/

