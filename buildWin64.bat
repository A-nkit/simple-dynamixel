rem "build SimpleDynamixel 64bit"

mkdir build64
cd build64
cmake -G "Visual Studio 9 2008 Win64" ^
-DCMAKE_BUILD_TYPE=Release ^
-DBOOST_ROOT=C:\Users\Public\Documents\development\libs\os\boost\boost_1_46_1 ^
-DP5_JAR="C:/Program Files (x86)/processing-1.2.1/lib/core.jar" \
-DP5_JAR_SERIAL="C:/Program Files (x86)/processing-1.2.1/modes/java/libraries/serial/libraries/serial.jar" \
..
cd ..
