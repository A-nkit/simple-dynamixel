#!/bin/sh
# --------------------------------------------------------------------------
# installscript
# --------------------------------------------------------------------------
# SimpleDynamixel
# http://code.google.com/simple-dynamixel
# --------------------------------------------------------------------------
# prog:  Max Rheiner / Interaction Design / Zhdk / http://iad.zhdk.ch/
# date:  09/01/2012 (m/d/y)
# ----------------------------------------------------------------------------

echo "--- Install SimpleDynmixel ---"

# copy the libs/doc/examples to the processing folders
P5_Path=~/sketchbook

# check if libraries folder exists
if [ ! -d $P5_Path/libraries ]; then
    mkdir $P5_Path/libraries
fi

# copie the files
cp -r ./dist/SimpleDynamixel/  $P5_Path/libraries/

# remove all subversion folders
cd $P5_Path/libraries/SimpleKDL
rm -rf `find . -type d -name .svn`

