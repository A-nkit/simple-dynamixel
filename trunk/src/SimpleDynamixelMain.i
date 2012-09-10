# -----------------------------------------------------------------------------
# SimpleDynamixelMain
# -----------------------------------------------------------------------------
# Processing Library for the Dynamixel Servo
# http://code.google.com/p/simple-dynamixel
# prog:  Max Rheiner / Interaction Design / Zhdk / http://iad.zhdk.ch/
# -----------------------------------------------------------------------------

%module(directors="1") SimpleDynamixelMain

%{
#include <string>
#include <vector>
%}

%include "arrays_java.i"
%include "cpointer.i"
%include "typemaps.i"
%include "carrays.i"

%apply int[] {int *};
%apply float[] {float *};


# ----------------------------------------------------------------------------
# stl

%include "std_vector.i"
%include "std_string.i"
%include "std_map.i"

%{
#include <SerialBase.h>
%}

# ----------------------------------------------------------------------------
# SerialBase

class SerialBase
{
public:
    SerialBase();
    ~SerialBase();

    bool open(const char* serialPortName,unsigned long baudRate = 9600);
    void close();

    bool isOpen();

    int available();

    void write(unsigned char byte);
    void write(int byte);
    void write(const std::string& str);

    int read();

    void clear();

    //void received(const char *data, unsigned int len);

};
