#!/bin/sh
# --------------------------------------------------------------------------
# buildscript for windows
# --------------------------------------------------------------------------
# Processing Library for the Dynamixel Servo
# http://code.google.com/p/simple-dynamixel
# --------------------------------------------------------------------------
# prog:  Max Rheiner / Interaction Design / zhdk / http://iad.zhdk.ch/
# date:  08/12/2012 (m/d/y)
# ----------------------------------------------------------------------------


# copy the libs/doc/examples to the processing folders
P5_Path=~/Documents/Processing

# check if libraries folder exists
if [ ! -d $P5_Path/libraries ]; then
    mkdir $P5_Path/libraries
fi

# copie the files
cp -r ./dist/all/SimpleOpenNI  $P5_Path/libraries/

# remove all subversion folders
cd $P5_Path/libraries/SimpleOpenNI
rm -rf `find . -type d -name .svn`

echo "--- installed SimpleDynamixel ---"
