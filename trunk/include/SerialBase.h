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

#ifndef SERIALBASE_H
#define	SERIALBASE_H

#include <vector>
#include <iostream>

// boost
#include <boost/thread.hpp>
#include <boost/thread/mutex.hpp>
#include <boost/thread/locks.hpp>
#include <boost/circular_buffer.hpp>

#include "AsyncSerial.h"

#define  MAX_BUFFER_SIZE (1024)  // 1k buffer


class SerialBase
{
public:
    SerialBase();
    ~SerialBase();

    bool open(const char* serialPortName,unsigned long baudRate = 9600);
    void close();

    bool isOpen() { return _open; }

    int available();

    void write(unsigned char byte);
    void write(int byte);
    void write(const std::string& str);

    int read();

    void clear();

    void received(const char *data, unsigned int len);

protected:

    bool            _open;

    unsigned char   _buffer[MAX_BUFFER_SIZE];
    unsigned char   _bufferPos;
    unsigned char   _bufferLength;
    boost::circular_buffer<int>     _circularBuffer;

    CallbackAsyncSerial*    _serialPort;
    boost::mutex            _readMutex;
    boost::mutex            _writeMutex;


};

#endif  // SERIALBASE_H
