rem "build SimpleDynamixel 32bit"

mkdir build32
cd build32
cmake -G "Visual Studio 9 2008" ^
-DMACH_ARCH="32" ^
-DCMAKE_BUILD_TYPE=Release ^
-DBOOST_ROOT=C:\Users\Public\Documents\development\libs\os\boost\boost_1_46_1-win32 ^
-DP5_JAR="C:\\Program Files (x86)\\processing-1.2.1\\lib\\core.jar" ^
-DP5_JAR_SERIAL="C:\\Program Files (x86)\\processing-1.2.1\\libraries\\serial\\library\\serial.jar" ^
-DUSE_ASIO=1 ^
..
cd ..
