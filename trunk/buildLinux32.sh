#!/bin/sh
# --------------------------------------------------------------------------
# buildscript for linux 32bit
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
rm -r build32

# check if build folder exists
if [ ! -d "build32" ]; then
    mkdir build32
fi

cd ./build32

echo "--- generate cmake ---"
# changes this according to your environment
cmake -DCMAKE_BUILD_TYPE=Release \
	  -DCMAKE_BUILD_ARCH=32 \
	  -DMACH_ARCH=32 \
	  -DEIGEN3D_INCLUDE=/usr/include/eigen3/ \
          -DBOOST_ROOT=~/Documents/development/libs/boost/boost_1_46_1-32/ \
          -DP5_JAR=~/Documents/localApps/processing-1.5.1/lib/core.jar \
          -DKDL_LIBDIR=./dist/all/SimpleKDL/library/lib32/ \
	  ..


echo "--- build ---"
# build with 6 threads, verbose is optional, but otherwise you can't see the compiler directives
# make -j 6 VERBOSE=1
make -j 6

echo "--- copy ---"
# copy the library
cp SimpleKDL.jar ../dist/all/SimpleKDL/library
cp libSimpleKDL*.so ../dist/all/SimpleKDL/library

# copy the doc
cp -r ./doc/* ../dist/all/SimpleKDL/documentation/

