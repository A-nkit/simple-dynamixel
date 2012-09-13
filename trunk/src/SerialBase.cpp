/* ----------------------------------------------------------------------------
 * SimpleDynamixel
 * ----------------------------------------------------------------------------
 * Copyright (C) 2011 Max Rheiner / Interaction Design Zhdk
 *
 * This file is part of SimpleDynamixel.
 *
 * SimpleOpenNI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version (subject to the "Classpath" exception
 * as provided in the LICENSE.txt file that accompanied this code).
 *
 * SimpleOpenNI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SimpleDynamixel.  If not, see <http://www.gnu.org/licenses/>.
 * ----------------------------------------------------------------------------
 */

#include "SerialBase.h"

#include <iostream>

SerialBase::SerialBase():
    _open(false),
    _serialPort(NULL),
    _serial(NULL),
    _circularBuffer(MAX_BUFFER_SIZE),
    _readBlock(false),
    _readBlockCount(0)
{}

SerialBase::~SerialBase()
{
    close();
}


bool SerialBase::open(const char* serialPortName,unsigned long baudRate)
{
    if(_open)
        return true;

    _serial = new serial::Serial(serialPortName,
                                 baudRate,
                                 serial::Timeout::simpleTimeout(20));
    if(_serial->isOpen() == false)
        return false;


    std::cout << "--------------- SerialBase::open" << std::endl;

    _open = true;
    _circularBuffer.clear();
    return _open;
}

void SerialBase::close()
{
    boost::mutex::scoped_lock l1(_readMutex);
    boost::mutex::scoped_lock l2(_writeMutex);

    if(_serial)
    {
        _serial->close();
        delete _serial;
        _serial = NULL;
    }

    _circularBuffer.clear();
    _open = false;
}

int SerialBase::available()
{
    boost::mutex::scoped_lock l(_readMutex);

    return _serial->available();
}

void SerialBase::write(unsigned char byte)
{
    if(!_open)
        return;

    boost::mutex::scoped_lock l(_writeMutex);

    _serial->write (&byte,1);
}

void SerialBase::write(int byte)
{
    uint8_t data = byte;
    _serial->write (&data,1);
}

void SerialBase::write(const std::string& str)
{
    if(!_open)
        return;

    boost::mutex::scoped_lock l(_writeMutex);

  //  _serialPort->writeString(str);
}

int SerialBase::read()
{
    if(!_open)
        return 0;

    boost::mutex::scoped_lock l(_readMutex);

    uint8_t data = 0;
    _serial->read(&data,1);

    return (int)data;
}


void SerialBase::clear()
{
    boost::mutex::scoped_lock l(_readMutex);

    _serial->flush();
}

void SerialBase::setReadBlock(bool enable)
{
    boost::mutex::scoped_lock l(_readMutex);
    _readBlock = enable;
}

bool SerialBase::readBlock()
{
    boost::mutex::scoped_lock l(_readMutex);
    return _readBlock;
}

void SerialBase::setReadBlockCount(int count)
{
    boost::mutex::scoped_lock l(_readMutex);
    _readBlockCount = count;
}

void SerialBase::addReadBlockCount(int count)
{
    boost::mutex::scoped_lock l(_readMutex);
    _readBlockCount += count;
}

int  SerialBase::readBlockCount()
{
    boost::mutex::scoped_lock l(_readMutex);
    return _readBlockCount;
}

void SerialBase::received(const char *data, unsigned int len)
{
    boost::mutex::scoped_lock l(_readMutex);

    for(int i=0;i < len;i++)
        _circularBuffer.push_back(data[i]);

}

/*
SerialBase::SerialBase():
    _open(false),
    _serialPort(NULL),
    _circularBuffer(MAX_BUFFER_SIZE),
    _readBlock(false),
    _readBlockCount(0)
{}

SerialBase::~SerialBase()
{
    close();
}


bool SerialBase::open(const char* serialPortName,unsigned long baudRate)
{
    if(_open)
        return true;
    try{
        _serialPort = new CallbackAsyncSerial(std::string(serialPortName),baudRate);
        _serialPort->setCallback(boost::bind( &SerialBase::received,this,_1,_2 ));
    }
    catch(std::exception& e)
    {
        std::cout << "SerialBase Error: " << e.what() << std::endl;
        return false;
    }

    _open = true;
    _circularBuffer.clear();
    return _open;
}

void SerialBase::close()
{
    boost::mutex::scoped_lock l1(_readMutex);
    boost::mutex::scoped_lock l2(_writeMutex);

    if(_serialPort)
    {
        _serialPort->close();
        delete _serialPort;
        _serialPort = NULL;
    }

    _circularBuffer.clear();
    _open = false;
}

int SerialBase::available()
{
    boost::mutex::scoped_lock l(_readMutex);

    return _circularBuffer.size();
}

void SerialBase::write(unsigned char byte)
{
    if(!_open)
        return;

    boost::mutex::scoped_lock l(_writeMutex);

    _serialPort->write((char*)&byte,1);
}

void SerialBase::write(int byte)
{
    write((unsigned char)byte);
}

void SerialBase::write(const std::string& str)
{
    if(!_open)
        return;

    boost::mutex::scoped_lock l(_writeMutex);

    _serialPort->writeString(str);
}

int SerialBase::read()
{
    if(!_open)
        return 0;

    boost::mutex::scoped_lock l(_readMutex);

    unsigned char ret = _circularBuffer.front();
    _circularBuffer.pop_front();
    return (int)ret;
}


void SerialBase::clear()
{
    boost::mutex::scoped_lock l(_readMutex);

    _circularBuffer.clear();
}

void SerialBase::setReadBlock(bool enable)
{
    boost::mutex::scoped_lock l(_readMutex);
    std::cout << "setReadBlock :"<< enable << std::endl;
    _readBlock = enable;
}

bool SerialBase::readBlock()
{
    boost::mutex::scoped_lock l(_readMutex);
    return _readBlock;
}

void SerialBase::setReadBlockCount(int count)
{
    boost::mutex::scoped_lock l(_readMutex);
    _readBlockCount = count;
}

void SerialBase::addReadBlockCount(int count)
{
    boost::mutex::scoped_lock l(_readMutex);
    _readBlockCount += count;
}

int  SerialBase::readBlockCount()
{
    boost::mutex::scoped_lock l(_readMutex);
    return _readBlockCount;
}

void SerialBase::received(const char *data, unsigned int len)
{
    boost::mutex::scoped_lock l(_readMutex);


//    int startIndex = 0;
//    if(_readBlockCount > 0)
//    {

//        std::cout << "--------------- dont read" << std::endl;
//        for(int i=0;i < _readBlockCount && i <len ;i++)
//        {
//            std::cout << " Ox" << std::hex << (int)(data[i] & 0xff) << "," << std::dec << (int)(data[i] & 0xff) << "|" << std::flush;
//        }
//        std::cout << "xxxxxxxxxxxx dont read" << std::endl;


//        if(_readBlockCount >= len)
//            _readBlockCount -= len;
//        else
//        {
//            startIndex = _readBlockCount;
//            len -= _readBlockCount;

//            _readBlockCount = 0;
//        }

//        return;
//    }


//    for(int i=startIndex;i < len;i++)
//    {
//        std::cout << " Ox" << std::hex << (int)(data[i] & 0xff) << "," << std::dec << (int)(data[i] & 0xff) << "|" << std::flush;
//        _circularBuffer.push_back(data[i]);
//    }
//    std::cout << std::endl;

    for(int i=0;i < len;i++)
        _circularBuffer.push_back(data[i]);

}
*/
