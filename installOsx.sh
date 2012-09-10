#!/bin/sh
# --------------------------------------------------------------------------
# install script for Osx
# --------------------------------------------------------------------------
# Processing Wrapper for the KDL - Kinematic Dynamics Library
# http://code.google.com/p/simple-openni
# --------------------------------------------------------------------------
# prog:  Max Rheiner / Interaction Design / zhdk / http://iad.zhdk.ch/
# date:  08/12/2012 (m/d/y)
# ----------------------------------------------------------------------------
# Change P5_Path to the folder where Processing stores the libraries
# On Osx it should be in '~Documents/Processing' (Processing 1.5.1)
# ----------------------------------------------------------------------------

# copy the libs/doc/examples to the processing folders
P5_Path=~/Documents/Processing

# check if libraries folder exists
if [ ! -d $P5_Path/libraries ]; then
    mkdir $P5_Path/libraries
fi

# copie the files
cp -r ./dist/all/SimpleKDL  $P5_Path/libraries/

# remove all subversion folders
cd $P5_Path/libraries/SimpleKDL
rm -rf `find . -type d -name .svn`

# change name path for a local library
cd ./library
install_name_tool -change /usr/local/lib/liborocos-kdl.1.1.dylib @loader_path/liborocos-kdl.1.1.dylib libSimpleKDL.jnilib
echo "paths:"
otool -L libSimpleKDL.jnilib

echo "--- installed SimpleKDL ---"