# -----------------------------------------------------------------------------
# SimpleDynamixel
# -----------------------------------------------------------------------------
# Processing Library for the Dynamixel Servo
# http://code.google.com/p/simple-dynamixel
# --------------------------------------------------------------------------
# prog:  Max Rheiner / Interaction Design / zhdk / http://iad.zhdk.ch/
# date:  08/12/2012 (m/d/y)
# -----------------------------------------------------------------------------
#
#	this envVar should be set:
#		JAVA_HOME
#
# -----------------------------------------------------------------------------
	
CMAKE_MINIMUM_REQUIRED(VERSION 2.8)

INCLUDE(CheckIncludeFiles)

PROJECT(SimpleDynamixel)

IF(NOT MACH_ARCH)
    # default 64bit
    SET(MACH_ARCH "64")
ENDIF()

# -----------------------------------------------------------------------------
# check swig
FIND_PACKAGE(SWIG REQUIRED)
INCLUDE(${SWIG_USE_FILE})

# -----------------------------------------------------------------------------
# check java/jni

FIND_PACKAGE(Java REQUIRED)
FIND_PACKAGE(JNI REQUIRED)
INCLUDE_DIRECTORIES(${JAVA_INCLUDE_PATH})
INCLUDE_DIRECTORIES(${JAVA_INCLUDE_PATH2})
 
INCLUDE_DIRECTORIES(${CMAKE_CURRENT_SOURCE_DIR})

# -----------------------------------------------------------------------------
# java tools
IF(WIN32)
	SET(JAVA_RUNTIME2  $ENV{JAVA_HOME}/bin/java)
	SET(JAVA_ARCHIVE2  $ENV{JAVA_HOME}/bin/jar)
	SET(JAVA_COMPILE2  $ENV{JAVA_HOME}/bin/javac)
	SET(JAVA_DOC	   $ENV{JAVA_HOME}/bin/javadoc)
ELSE()
	# unix
	SET(JAVA_RUNTIME2  java)
	SET(JAVA_ARCHIVE2  jar)
	SET(JAVA_COMPILE2  javac)
	SET(JAVA_DOC	   javadoc)
ENDIF()


# -----------------------------------------------------------------------------
# boost
FIND_PACKAGE(Boost REQUIRED)
IF(Boost_INCLUDE_DIRS MATCHES "Boost_INCLUDE_DIR-NOTFOUND")
	MESSAGE("Boost should be set, for example:")
	MESSAGE("> cmake -DBOOST_ROOT=/usr/??? ..")	
ELSE()
	INCLUDE_DIRECTORIES ( ${Boost_INCLUDE_DIRS} )
        LINK_DIRECTORIES ( ${Boost_LIBRARY_DIRS} )
	message(${Boost_INCLUDE_DIRS})
	message(${Boost_LIBRARY_DIRS})
  
	IF(APPLE)
		# osx
                set(Boost_LIBRARIES  "boost_thread.a" "boost_system.a")
	ELSEIF(UNIX)
		# unix/linux,shared libs of boost should be installed
                set(Boost_LIBRARIES  "boost_thread.a" "boost_system.a")
	ELSE()
		# windows will find the right libs by itself
	ENDIF()
ENDIF()

# -----------------------------------------------------------------------------
# serial library

IF(NOT DEFINED USE_ASIO)
    IF(APPLE)
        SET(LIBS "serial.a" "System.B")
    ELSEIF(UNIX)
        SET(LIBS "serial.a" "rt")
    ELSE()
        # windows
    ENDIF()
ELSE()
    ADD_DEFINITIONS(-DUSE_ASIO_SERIAL_LIB)
ENDIF()

# -----------------------------------------------------------------------------
# swig 
SET(CMAKE_SWIG_FLAGS -package SimpleDynamixel)
INCLUDE_DIRECTORIES(${CMAKE_CURRENT_SOURCE_DIR}/include)

# -----------------------------------------------------------------------------
# processing wrappers
SET(P5_WRAPPER ${CMAKE_CURRENT_SOURCE_DIR}/src/p5_src/)

# processing core lib
# shoud use find_program
IF(WIN32)
	SET(P5_JAR "C:/Program Files (x86)/processing-1.2.1/lib/core.jar")	
ELSEIF(APPLE)
	SET(P5_JAR "/Applications/Processing.app/Contents/Resources/Java/core.jar")
ELSE()
	# unix/linux
	# SET(P5_JAR "/?")
	IF(NOT DEFINED P5_JAR)
		MESSAGE("Set P5_JAR to the folder where Processing installed the core.jar file, for example:")
		MESSAGE(FATAL_ERROR "> cmake -DP5_JAR=/usr/???/core.jar ..")	
	ENDIF()
ENDIF()

IF(WIN32)
#        SET(P5_JAR_SERIAL "C:/Program Files (x86)/processing-1.2.1/modes/java/libraries/serial/libraries/serial.jar")
#ELSEIF(APPLE)
#        SET(P5_JAR_SERIAL "/Applications/Processing.app/Contents/Resources/Java/modes/java/libraries/serial/library/serial.jar")
#ELSE()
        # unix/linux
        # SET(P5_JAR_SERIAL "/?")
        IF(NOT DEFINED P5_JAR_SERIAL)
                MESSAGE("Set P5_JAR_SERIAL to the folder where Processing installed the opengl.jar file, for example:")
                MESSAGE(FATAL_ERROR "> cmake -DP5_JAR=/usr/???/serial.jar.zip ..")
        ENDIF()
ENDIF()


SET(SWIG_SOURCES
src/SimpleDynamixelMain.i
src/AsyncSerial.cpp
src/SerialBase.cpp
)

SET_SOURCE_FILES_PROPERTIES(${SWIG_SOURCES} PROPERTIES CPLUSPLUS ON)

# set the folder where the swig files should land
SET(CMAKE_SWIG_OUTDIR ${CMAKE_CURRENT_BINARY_DIR}/swig_src)
MAKE_DIRECTORY(${CMAKE_CURRENT_BINARY_DIR}/src)


SWIG_ADD_MODULE(SimpleDynamixel Java ${SWIG_SOURCES})
SWIG_LINK_LIBRARIES(SimpleDynamixel ${JAVA_LIBRARIES} ${Boost_LIBRARIES} ${LIBS})
#SWIG_LINK_LIBRARIES(SimpleDynamixel ${JAVA_LIBRARIES} ${JNI_LIBRARIES} ${Boost_LIBRARIES} ${LIBS})

# set the arch extension for the lib
IF(NOT APPLE)
  IF(${MACH_ARCH} MATCHES "64")
	#64bit
	message("set 64bit extension") 
	SET(NEW_SUFFIX "64")
  ELSE()
	# 32bit
	message("set 32bit extension") 
	SET(NEW_SUFFIX "32")
  ENDIF()
  IF(WIN32)
	SET(NEW_SUFFIX ${NEW_SUFFIX}.dll)	
  ELSE()
	# unix linux
	SET(NEW_SUFFIX ${NEW_SUFFIX}.so)	
  ENDIF()

  SET_TARGET_PROPERTIES(SimpleDynamixel PROPERTIES SUFFIX ${NEW_SUFFIX})

ENDIF()


# only for linux
IF(UNIX)
  IF(${MACH_ARCH} MATCHES "32")
        SET_TARGET_PROPERTIES(SimpleDynamixel PROPERTIES COMPILE_FLAGS "-m32" LINK_FLAGS "-m32")
  ENDIF()
ENDIF()

set(P5_JAR "${P5_JAR}")
set(P5_JAR_SERIAL "${P5_JAR_SERIAL}")

# get full paths, otherwise javac/javadoc has problems with multiple jar locations
GET_FILENAME_COMPONENT(P5_JAR "${P5_JAR}" ABSOLUTE)
GET_FILENAME_COMPONENT(P5_JAR_SERIAL "${P5_JAR_SERIAL}" ABSOLUTE)

ADD_CUSTOM_COMMAND(TARGET SimpleDynamixel
				   POST_BUILD
				   COMMAND cmake -E echo "-----------------------------"
				   COMMAND cmake -E echo "Compiling Java files..."
				   COMMAND cmake -E make_directory ./SimpleDynamixel
				   COMMAND cmake -E make_directory ${CMAKE_SWIG_OUTDIR}
				   #COMMAND cmake -E copy_directory ${P5_WRAPPER} ${CMAKE_SWIG_OUTDIR}
				   # cmake copy doesn't support wildcards, otherwise it just copies the hidden folder of svn, etc.
				   # doesn't works on windows backslash
				   COMMAND cp "${P5_WRAPPER}*.java" "${CMAKE_SWIG_OUTDIR}"
                                   COMMAND ${JAVA_COMPILE2} -classpath \"${P5_JAR}:${P5_JAR_SERIAL}\" ${CMAKE_SWIG_OUTDIR}/*.java -d ./
#                                   COMMAND ${JAVA_COMPILE2} -classpath \"${P5_JAR}\" ${CMAKE_SWIG_OUTDIR}/*.java -d ./
                                   COMMAND cmake -E echo "-----------------------------"
				   COMMAND cmake -E echo "Creating jar file..."
				   COMMAND ${JAVA_ARCHIVE2} cvf SimpleDynamixel.jar SimpleDynamixel
                                   COMMAND cmake -E echo "-----------------------------"
				   COMMAND cmake -E echo "Creating doc files..."
				   COMMAND cmake -E make_directory ./doc
                                   COMMAND ${JAVA_DOC} -classpath \"${P5_JAR}:${P5_JAR_SERIAL}\" -quiet -author -public -nodeprecated -nohelp -d ./doc  -version ${CMAKE_SWIG_OUTDIR}/*.java)
#                                   COMMAND ${JAVA_DOC} -classpath \"${P5_JAR}\" -quiet -author -public -nodeprecated -nohelp -d ./doc  -version ${CMAKE_SWIG_OUTDIR}/*.java)
