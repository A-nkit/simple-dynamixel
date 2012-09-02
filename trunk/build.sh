#!/bin/sh
# --------------------------------------------------------------------------
# buildscript
# --------------------------------------------------------------------------
# SimpleDynamixel
# http://code.google.com/simple-dynamixel
# --------------------------------------------------------------------------
# prog:  Max Rheiner / Interaction Design / Zhdk / http://iad.zhdk.ch/
# date:  09/01/2012 (m/d/y)
# ----------------------------------------------------------------------------

echo "--- Build SimpleDynmixel ---"
cPath="/home/max/Documents/localApps/processing-1.5.1/lib/core.jar:/home/max/Documents/localApps/processing-1.5.1/modes/java/libraries/serial/library/serial.jar"

echo "----------"
echo "Compile"
javac -classpath $cPath ./src/*.java -d ./ 

echo "----------"
echo "Build Jar"
jar cvf ./dist/SimpleDynamixel/library/SimpleDynamixel.jar SimpleDynamixel

echo "----------"
echo "Build Doc"
javadoc -classpath $cPath -quiet -author -public -nodeprecated -nohelp -d ./doc  -version ./src/*.java

# copy the doc
cp -r ./doc/* ./dist/SimpleDynamixel/documentation/
