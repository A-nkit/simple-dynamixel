#!/bin/sh
# --------------------------------------------------------------------------
# buildscript for osx 32bit/64bit
# --------------------------------------------------------------------------
# Processing Wrapper for the KDL - Kinematic Dynamics Library
# http://code.google.com/p/simple-kdl
# --------------------------------------------------------------------------
# prog:  Max Rheiner / Interaction Design / zhdk / http://iad.zhdk.ch/
# date:  08/12/2012 (m/d/y)
# ----------------------------------------------------------------------------
# Change those vars to the folders you have on your system:
#	-DEIGEN3D_INCLUDE 	= folder of Eigen3d headers
#	-DBOOST_ROOT 		= folder of Boost root
#	-DBOOST_LIBRARYDIR 	= folder of Boost library folder
#	-DP5_JAR 			= filepath to your core.jar (Processing)
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
	  -DCMAKE_OSX_ARCHITECTURES="i386;x86_64" \
	  ..


echo "--- build ---"
# build with 6 threads, verbose is optional, but otherwise you can't see the compiler directives
make -j 6 VERBOSE=1


echo "--- copy ---"
# copy the library
cp SimpleKDL.jar ../dist/all/SimpleKDL/library
cp libSimpleKDL.jnilib ../dist/all/SimpleKDL/library

# copy the doc
cp -r ./doc/* ../dist/all/SimpleKDL/documentation/