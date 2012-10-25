/* ----------------------------------------------------------------------------
 * SimpleDynamixel
 * ----------------------------------------------------------------------------
 * Copyright (C) 2012 Max Rheiner / Interaction Design Zhdk
 *
 * This file is part of SimpleDynamixel.
 *
 * SimpleDynamixel is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version (subject to the "Classpath" exception
 * as provided in the LICENSE.txt file that accompanied this code).
 *
 * SimpleDynamixel is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SimpleDynamixel.  If not, see <http://www.gnu.org/licenses/>.
 * ----------------------------------------------------------------------------
 */

package SimpleDynamixel;

import processing.core.*;
import processing.serial.*;

import java.lang.Thread;
import java.util.ArrayList;
import java.util.Arrays;
import java.lang.Math;
import java.text.DecimalFormat;

public class Servo
{
  public final static int SIMPLEDYNAMIXEL_VERSION = 11; // 0011

  static
  {
    System.out.println("SimpleDynamixel Version " + (SIMPLEDYNAMIXEL_VERSION / 100) + "." + (SIMPLEDYNAMIXEL_VERSION % 100));
    System.out.flush();
  }

  protected static int _loadExtLib = 0;

  protected static void loadExtLib()
  {
    if(_loadExtLib >= 1)
        return;

    _loadExtLib = 1;

    String sysStr = System.getProperty("os.name").toLowerCase();
    String libName = "SimpleDynamixel";
    String archStr = System.getProperty("os.arch").toLowerCase();

    // check which system + architecture
    if(sysStr.indexOf("win") >= 0)
    {   // windows
        if(archStr.indexOf("86") >= 0)
            // 32bit
            libName += "32";
        else if(archStr.indexOf("64") >= 0)
            libName += "64";
    }
    else if(sysStr.indexOf("nix") >= 0 || sysStr.indexOf("linux") >=  0 )
    {   // unix
        if(archStr.indexOf("86") >= 0)
            // 32bit
            libName += "32";
        else if(archStr.indexOf("64") >= 0)
            libName += "64";
    }
    else if(sysStr.indexOf("mac") >= 0)
    {     // mac
    }

    try{
      System.loadLibrary(libName);
    }
    catch(UnsatisfiedLinkError e)
    {
      System.out.println("Can't load SimpleDynamixel library (" +  libName  + ") : " + e);
}

  }

    public final static int DX_SERIALTYPE_ASYNC         = 0;
    public final static int DX_SERIALTYPE_SYNC          = 1;


    public final static int DX_BEGIN			= 0xFF;
    public final static int DX_BROADCAST_ID		= 0xFE;

    public final static int DX_LAST_ID                  = 0xFD;

    // return values
    public final static int DX_RET_OK			= 0;
    public final static int DX_RET_ERROR_LEN		= 1;  // return package has wrong size
    public final static int DX_RET_ERROR_START		= 2;  // can't find start
    public final static int DX_RET_ERROR_CHECKSUM	= 3;  // can't find start

    // errors
    public final static int DX_ERROR_NO 		= 0;
    public final static int DX_ERROR_INVOLT		= 1 << 0;
    public final static int DX_ERROR_ANGLELIMIT		= 1 << 1;
    public final static int DX_ERROR_OVERHEAT		= 1 << 2;
    public final static int DX_ERROR_RANGE		= 1 << 3;
    public final static int DX_ERROR_CHECKSUM		= 1 << 4;
    public final static int DX_ERROR_OVERLOAD		= 1 << 5;
    public final static int DX_ERROR_INST		= 1 << 6;

    public final static int DX_ERROR_USR_ID		= 1 << 10;
    public final static int DX_ERROR_USR_READSTATUS     = 1 << 11;
    public final static int DX_ERROR_USR_NO_BEGIN       = 1 << 12;
    public final static int DX_ERROR_USR_DATA_TIMEOUT   = 1 << 13;

    // Instructions
    public final static int DX_INST_PING		= 0x01;
    public final static int DX_INST_READ_DATA           = 0x02;
    public final static int DX_INST_WRITE_DATA		= 0x03;
    public final static int DX_INST_REG_WRITE           = 0x04;
    public final static int DX_INST_ACTION		= 0x05;
    public final static int DX_INST_RESET		= 0x06;
    public final static int DX_INST_SYNC_WRITE		= 0x83;

    // commands
    public final static int DX_CMD_MODELNR		= 0x00;
    public final static int DX_CMD_FIRMWARE		= 0x02;
    public final static int DX_CMD_ID 			= 0x03;
    public final static int DX_CMD_BAUDRATE		= 0x04;
    public final static int DX_CMD_DELAYTIME		= 0x05;
    public final static int DX_CMD_CW_ANGLE_LIMIT	= 0x06;
    public final static int DX_CMD_CCW_ANGLE_LIMIT      = 0x08;
    public final static int DX_CMD_HIGH_LIMIT_TEMP      = 0x0B;
    public final static int DX_CMD_LOW_LIMIT_VOLT       = 0x0C;
    public final static int DX_CMD_HIGH_LIMIT_VOLT      = 0x0D;
    public final static int DX_CMD_MAX_TORQUE           = 0x0E;
    public final static int DX_CMD_STATUSRETURNLEVEL    = 0x10;
    public final static int DX_CMD_ALARM_LED            = 0x11;
    public final static int DX_CMD_ALARM_SHUTDOWN       = 0x12;
    public final static int DX_CMD_TORQUE_ENABLE	= 0x18;
    public final static int DX_CMD_LED_ENABLE		= 0x19;
    public final static int DX_CMD_D_GAIN		= 0x1A;
    public final static int DX_CMD_I_GAIN		= 0x1B;
    public final static int DX_CMD_P_GAIN		= 0x1C;
    public final static int DX_CMD_GOAL_POS		= 0x1E;
    public final static int DX_CMD_MOV_SPEED		= 0x20;
    public final static int DX_CMD_LIMIT_TORQUE		= 0x22;
    public final static int DX_CMD_PRESENT_POS		= 0x24;
    public final static int DX_CMD_PRESENT_SPEED	= 0x26;
    public final static int DX_CMD_PRESENT_LOAD		= 0x28;
    public final static int DX_CMD_PRESENT_VOLT		= 0x2A;
    public final static int DX_CMD_PRESENT_TEMP		= 0x2B;
    public final static int DX_CMD_REGISTER		= 0x2C;
    public final static int DX_CMD_LOCK 		= 0x2F;


    public final static int DX_CMD_COMPLIANCE_MARGIN_CW	= 0x1A;
    public final static int DX_CMD_COMPLIANCE_MARGIN_CCW= 0x1B;

    public final static int DX_CMD_COMPLIANCE_SLOPE_CW	= 0x1C;
    public final static int DX_CMD_COMPLIANCE_SLOPE_CCW	= 0x1D;

    public final static int DX_CMD_MOVING		= 0x2E;
    public final static int DX_CMD_PUNCH		= 0x30;

    public final static int DX_DIR_CCW                  = 0;
    public final static int DX_DIR_CW                   = 1;

    public final static int DX_MOTOR_SERIE_MX          	= 0;
    public final static int DX_MOTOR_SERIE_AX          	= 1;
    public final static int DX_MOTOR_SERIE_RX          	= 2;
    public final static int DX_MOTOR_SERIE_EX          	= 3;
    public final static int DX_MOTOR_SERIE_DX          	= 4;

    public final static int DX_TYPE_DX_113              = 0x0071;
    public final static int DX_TYPE_DX_116              = 0x0074;
    public final static int DX_TYPE_DX_117              = 0x0075;
    public final static int DX_TYPE_AX_12W              = 0x012C;
    public final static int DX_TYPE_AX_12              	= 0x000C;
    public final static int DX_TYPE_AX_18              	= 0x0012;
    public final static int DX_TYPE_RX_10              	= 0x000A;
    public final static int DX_TYPE_RX_24F              = 0x00184;
    public final static int DX_TYPE_RX_28              	= 0x001C;
    public final static int DX_TYPE_RX_64              	= 0x0040;
    public final static int DX_TYPE_EX_104              = 0x006B;
    public final static int DX_TYPE_MX_28              	= 0x001D;
    public final static int DX_TYPE_MX_64              	= 0x0136;
    public final static int DX_TYPE_MX_106              = 0x0140;


    class ReturnPacket
    {
        public ReturnPacket()
        {
            id = -1;
            length = 0;
            param = new ArrayList<Integer>();
        }

        public int checksum()
        {
            int ret = 0;
            ret += id;
            ret += length;
            ret += error;

            for(int i=0; i < param.size(); i++)
                ret += param.get(i);

            return Servo.calcChecksum(ret);
        }

        public String toString()
        {
            String retStr = "";
            retStr += "id: " + id + "\n";
            retStr += "length: " + length + "\n";
            retStr += "error: " + error + "\n";
            for(int i=0; i < param.size(); i++)
                retStr += "param" + i + ": " + param.get(i).intValue() + "\n";

            return retStr;
        }

        public int 				id;
        public int 				length;
        public int 				error;
        public ArrayList<Integer> 	        param;
    }

    class SerialWrapper
    {
        protected Serial        _p5Serial;
        protected SerialBase    _nativeSerial;

        public SerialWrapper(PApplet parent,String devStr,int baudrate)
        {
            _nativeSerial = null;
            _p5Serial = new Serial(parent,devStr, baudrate);
        }

        public SerialWrapper(String devStr,int baudrate)
        {
            _p5Serial = null;
            _nativeSerial = new SerialBase();
            _nativeSerial.open(devStr, baudrate);
        }

        public void clear()
        {
            if(_nativeSerial != null)
                _nativeSerial.clear();
            else
                _p5Serial.clear();
        }

        public void write(int data)
        {
            if(_nativeSerial != null)
                _nativeSerial.write(data);
            else
                _p5Serial.write(data);
        }

        public int read()
        {
            if(_nativeSerial != null)
                return _nativeSerial.read();
            else
                return _p5Serial.read();
        }

        public int available()
        {
            if(_nativeSerial != null)
                return _nativeSerial.available();
            else
                return _p5Serial.available();
        }

        public void setReadBlock(boolean enable)
        {
            if(_nativeSerial != null)
                _nativeSerial.setReadBlock(enable);
                /*
            else
                _p5Serial.setReadBlock(enable);
                */
        }

        public void addReadBlockCount(int count)
        {
            if(_nativeSerial != null)
                _nativeSerial.addReadBlockCount(count);
                /*
            else
                _p5Serial.setReadBlock(enable);
                */
        }
    }


    protected SerialWrapper 				_serial;
    protected int                                       _serialType = DX_SERIALTYPE_ASYNC;
    protected ArrayList<Integer>                        _data;
    protected int					_curChecksum;
    protected int					_error;
    protected int					_timeout = 1000;
    protected int					_delay = 1;
    protected ReturnPacket                              _returnPacket = new ReturnPacket();
    protected boolean                                   _regWriteFlag = false;
    protected int 					_regWriteDelay = 2;
    protected Object					_lock = new Object();

    PApplet						_parent;
	

    public Servo()
    {
    }

/*
    public void init(Serial serial)
    {
        _serial = serial;
		_serial.clear();
    }
*/
    public void init(PApplet parent,String serialDev,int baudRate)
    {
        _serial = new SerialWrapper(parent,serialDev, baudRate);
        _parent = parent;
        _serial.clear();
    }

    public void init(String serialDev,int baudRate)
    {
        loadExtLib();

        _serial = new SerialWrapper(serialDev, baudRate);
        _parent = null;
        _serial.clear();
    }

    public void setSerialType(int type)
    {
        _serialType = type;
    }

    public int serialType()
    {
        return _serialType;
    }

    public int error()
    {
        return _error;
    }

    public String errorStr()
    {
        return errorStr(_error);
    }

    public SerialWrapper serial() { return _serial; }


    public final static int getMotorSerie(int modelNr)
    {
        switch(modelNr)
        {
        // dx
        case DX_TYPE_DX_113:
        case DX_TYPE_DX_116:
        case DX_TYPE_DX_117:
            return DX_MOTOR_SERIE_DX;
        // ax
        case DX_TYPE_AX_12W:
        case DX_TYPE_AX_12:
        case DX_TYPE_AX_18:
            return DX_MOTOR_SERIE_AX;
        // rx
        case DX_TYPE_RX_10:
        case DX_TYPE_RX_24F:
        case DX_TYPE_RX_28:
        case DX_TYPE_RX_64:
            return DX_MOTOR_SERIE_RX;
        // ex
        case DX_TYPE_EX_104:
            return DX_MOTOR_SERIE_EX;
        // mx
        case DX_TYPE_MX_28:
        case DX_TYPE_MX_64:
        case DX_TYPE_MX_106:
            return DX_MOTOR_SERIE_MX;
        default:
            return -1;
        }
    }

    public final static int getMotorRange(int modelNr)
    {
        switch(modelNr)
        {
        // dx
        case DX_TYPE_DX_113:
        case DX_TYPE_DX_116:
        case DX_TYPE_DX_117:
            return 0x3FF;
        // ax
        case DX_TYPE_AX_12W:
        case DX_TYPE_AX_12:
        case DX_TYPE_AX_18:
            return 0x3FF;
        // rx
        case DX_TYPE_RX_10:
        case DX_TYPE_RX_24F:
        case DX_TYPE_RX_28:
        case DX_TYPE_RX_64:
            return 0x3FF;
        // ex
        case DX_TYPE_EX_104:
            return 0xFFF;
        // mx
        case DX_TYPE_MX_28:
        case DX_TYPE_MX_64:
        case DX_TYPE_MX_106:
            return 0xFFF;
        default:
            return 0;
        }
    }

    public int modelNr(int id)
    {
        synchronized(_lock)
        {
            readData(id,DX_CMD_MODELNR,2);
            if(handleReturnStatus(id))
            {
                if(_returnPacket.param.size() != 2)
                    return -1;
                return((_returnPacket.param.get(1).intValue() << 8) + _returnPacket.param.get(0).intValue());
            }
            else
                return -1;
        }
    }

    public int firmware(int id)
    {
        synchronized(_lock)
        {
            readData(id,DX_CMD_FIRMWARE,1);
            if(handleReturnStatus(id))
            {
                if(_returnPacket.param.size() != 1)
                    return -1;
                return(_returnPacket.param.get(0).intValue());
            }
            else
                return -1;
        }
    }

    public boolean setId(int id,int newId)
    {
        synchronized(_lock)
        {
            if(newId >= DX_BROADCAST_ID)
                return false;

            writeDataByte(id,DX_CMD_ID,newId);

            // handle reply
            return handleReturnStatus(id);
        }
    }

    public boolean setBaudrate(int id,int baudrate)
    {
        synchronized(_lock)
        {
            writeDataByte(id,DX_CMD_BAUDRATE,baudrate);

            // handle reply
            return handleReturnStatus(id);
        }
    }

    public int baudrate(int id)
    {
        synchronized(_lock)
        {
            readData(id,DX_CMD_BAUDRATE,1);
            if(handleReturnStatus(id))
            {
                if(_returnPacket.param.size() != 1)
                    return -1;
                return(_returnPacket.param.get(0).intValue());
            }
            else
                return -1;
        }
    }

    public boolean setDelayTime(int id,int delayTime)
    {
        synchronized(_lock)
        {
            writeDataByte(id,DX_CMD_DELAYTIME,delayTime);

            // handle reply
            return handleReturnStatus(id);
        }
    }

    public int delayTime(int id)
    {
        synchronized(_lock)
        {
            readData(id,DX_CMD_DELAYTIME,1);
            if(handleReturnStatus(id))
            {
                if(_returnPacket.param.size() != 1)
                    return -1;
                return(_returnPacket.param.get(0).intValue());
            }
            else
                return -1;
        }
    }

    public boolean setHighLimitTemp(int id,int limitTemp)
    {
        synchronized(_lock)
        {
            writeDataByte(id,DX_CMD_HIGH_LIMIT_TEMP,limitTemp);

            // handle reply
            return handleReturnStatus(id);
        }
    }

    public int highLimitTemp(int id)
    {
        synchronized(_lock)
        {
            readData(id,DX_CMD_HIGH_LIMIT_TEMP,1);
            if(handleReturnStatus(id))
            {
                if(_returnPacket.param.size() != 1)
                    return -1;
                return(_returnPacket.param.get(0).intValue());
            }
            else
                return -1;
        }
    }

    public boolean setLowLimitVolt(int id,int limitVolt)
    {
        synchronized(_lock)
        {
            writeDataByte(id,DX_CMD_LOW_LIMIT_VOLT,limitVolt);

            // handle reply
            return handleReturnStatus(id);
        }
    }

    public int lowLimitVolt(int id)
    {
        synchronized(_lock)
        {
            readData(id,DX_CMD_LOW_LIMIT_VOLT,1);
            if(handleReturnStatus(id))
            {
                if(_returnPacket.param.size() != 1)
                    return -1;
                return(_returnPacket.param.get(0).intValue());
            }
            else
                return -1;
        }
    }

    public boolean setHightLimitVolt(int id,int limitVolt)
    {
        synchronized(_lock)
        {
            writeDataByte(id,DX_CMD_HIGH_LIMIT_VOLT,limitVolt);

            // handle reply
            return handleReturnStatus(id);
        }
    }

    public int highLimitVolt(int id)
    {
        synchronized(_lock)
        {
            readData(id,DX_CMD_HIGH_LIMIT_VOLT,1);
            if(handleReturnStatus(id))
            {
                if(_returnPacket.param.size() != 1)
                    return -1;
                return(_returnPacket.param.get(0).intValue());
            }
            else
                return -1;
        }
    }

    public boolean setMaxTorque(int id,int maxTorque)
    {
        synchronized(_lock)
        {
            writeData2Bytes(id,DX_CMD_MAX_TORQUE,maxTorque,_regWriteFlag);

            // handle reply
            return handleReturnStatus(id);
        }
    }

    public int maxTorque(int id)
    {
        synchronized(_lock)
        {
            readData(id,DX_CMD_MAX_TORQUE,2);
            if(handleReturnStatus(id))
            {
                if(_returnPacket.param.size() != 2)
                    return -1;
                return((_returnPacket.param.get(1).intValue() << 8) + _returnPacket.param.get(0).intValue());
            }
            else
                return -1;
        }
    }

    public boolean setStatusReturnLevel(int id,int statusReturnLevel)
    {
        synchronized(_lock)
        {
            if(statusReturnLevel >= 3)
                return false;
            writeDataByte(id,DX_CMD_STATUSRETURNLEVEL,statusReturnLevel);

            // handle reply
            return handleReturnStatus(id);
        }
    }

    public int statusReturnLevel(int id)
    {
        synchronized(_lock)
        {
            readData(id,DX_CMD_STATUSRETURNLEVEL,1);
            if(handleReturnStatus(id))
            {
                if(_returnPacket.param.size() != 1)
                    return -1;
                return(_returnPacket.param.get(0).intValue());
            }
            else
                return -1;
        }
    }

    public boolean setAlarmLed(int id,int alarmLed)
    {
        synchronized(_lock)
        {
            writeDataByte(id,DX_CMD_ALARM_LED,alarmLed);

            // handle reply
            return handleReturnStatus(id);
        }
    }

    public int alarmLed(int id)
    {
        synchronized(_lock)
        {
            readData(id,DX_CMD_ALARM_LED,1);
            if(handleReturnStatus(id))
            {
                if(_returnPacket.param.size() != 1)
                    return -1;
                return(_returnPacket.param.get(0).intValue());
            }
            else
                return -1;
        }
    }

    public boolean setAlarmShutdown(int id,int alarmShutdown)
    {  
        synchronized(_lock)
        {
            writeDataByte(id,DX_CMD_ALARM_SHUTDOWN,alarmShutdown);

            // handle reply
            return handleReturnStatus(id);
        }
    }

    public int alarmShutdown(int id)
    {
        synchronized(_lock)
        {
            readData(id,DX_CMD_ALARM_SHUTDOWN,1);
            if(handleReturnStatus(id))
            {
                if(_returnPacket.param.size() != 1)
                    return -1;
                return(_returnPacket.param.get(0).intValue());
            }
            else
                return -1;
        }
    }

/*
    public synchronized boolean ping(int id,int timeout)
	{

	}
*/
    // watch out, this resets the servo to the default factory settings
    public boolean reset(int id)
    {
        synchronized(_lock)
        {
            if(_serialType == DX_SERIALTYPE_SYNC)
                // block next few bytes for receiving
                _serial.addReadBlockCount(6);

            _curChecksum = 0;

            _serial.write(DX_BEGIN);
            _serial.write(DX_BEGIN);

            // id
            _serial.write(id);
            _curChecksum += id;

            // length
            _serial.write(2);
            _curChecksum += 2;

            // instruction
            _serial.write(DX_INST_ACTION);
            _curChecksum += DX_INST_ACTION;

            // no param

            // checksum
            _serial.write(calcChecksum(_curChecksum));

            // handle reply
            boolean ret = handleReturnStatus(id);

            return ret;
        }
    }

    public synchronized boolean ping(int id)
    {
        synchronized(_lock)
        {
            if(_serialType == DX_SERIALTYPE_SYNC)
                // block next few bytes for receiving
                _serial.addReadBlockCount(6);

            _curChecksum = 0;

            _serial.write(DX_BEGIN);
            _serial.write(DX_BEGIN);

            // id
            _serial.write(id);
            _curChecksum += id;

            // length
            _serial.write(2);
            _curChecksum += 2;

            // instruction
            _serial.write(DX_INST_PING);
            _curChecksum += DX_INST_PING;

            // no param

            // checksum
            _serial.write(calcChecksum(_curChecksum));

            // handle reply
            int oldTimeout = _timeout;
            _timeout = 80;
            boolean ret = handleReturnStatus(id);
            _timeout = oldTimeout;

            return ret;
        }
    }

    public synchronized int[] pingAll()
    {
        ArrayList<Integer> servoList = new ArrayList<Integer>();
        for(int i=0; i < DX_LAST_ID; i++)
        {
            if(ping(i))
                servoList.add(i);
        }

        int[] retArray = new int[servoList.size()];
        for(int i=0; i<servoList.size(); i++)
            retArray[i] = servoList.get(i).intValue();

        return retArray;
    }

    public synchronized int[] pingRange(int start, int end)
    {
        if(start > DX_LAST_ID)
            start = DX_LAST_ID;
        if(end > DX_LAST_ID)
            end = DX_LAST_ID;

        ArrayList<Integer> servoList = new ArrayList<Integer>();
        for(int i=start; i <= end; i++)
        {
            if(ping(i))
                servoList.add(i);
        }

        int[] retArray = new int[servoList.size()];
        for(int i=0; i<servoList.size(); i++)
            retArray[i] = servoList.get(i).intValue();

        return retArray;
    }
	
    public synchronized boolean action(int id)
    {
        synchronized(_lock)
        {
            if(_serialType == DX_SERIALTYPE_SYNC)
                // block next few bytes for receiving
                _serial.addReadBlockCount(6);

            _curChecksum = 0;

            _serial.write(DX_BEGIN);
            _serial.write(DX_BEGIN);

            // id
            _serial.write(id);
            _curChecksum += id;

            // length
            _serial.write(2);
            _curChecksum += 2;

            // instruction
            _serial.write(DX_INST_ACTION);
            _curChecksum += DX_INST_ACTION;

            // no param

            // checksum
            _serial.write(calcChecksum(_curChecksum));

            if(id != DX_BROADCAST_ID)
                // handle reply
                return handleReturnStatus(id);
            else
                return true;
        }
    }

    public synchronized void beginRegWrite()
    {
      if(_regWriteFlag == true)
            return;

      _regWriteFlag = true;
    }

    public synchronized boolean endRegWrite()
    {
      if(_regWriteFlag == false)
            return false;

      _regWriteFlag = false;
      // activate the commands
      return action(DX_BROADCAST_ID);
    }


    public boolean syncWrite(int addr,int length,int[] idList,int[][] dataList)
    {
        synchronized(_lock)
        {
            if(_serialType == DX_SERIALTYPE_SYNC)
                // block next few bytes for receiving
                _serial.addReadBlockCount(8 + idList.length + idList.length * length);

            _curChecksum = 0;

            _serial.write(DX_BEGIN);
            _serial.write(DX_BEGIN);

            // id
            _serial.write(DX_BROADCAST_ID);
            _curChecksum += DX_BROADCAST_ID;

            // length
            int l = (length + 1) * idList.length + 4;

            _serial.write(l);
            _curChecksum += l;  // 3 bytes param

            // instruction
            _serial.write(DX_INST_SYNC_WRITE);
            _curChecksum += DX_INST_SYNC_WRITE;

            // param - addr
            _serial.write(addr);
            _curChecksum += addr;

            // param - length
            _serial.write(length);
            _curChecksum += length;

            // write data
            for(int i=0;i < idList.length;i++)
            {
                // param - id
                _serial.write(idList[i]);
                _curChecksum += idList[i];

                for(int j=0;j < length;j++)
                {
                    // param - data
                    _serial.write(dataList[i][j]);
                    _curChecksum += dataList[i][j];
                }
            }

            // checksum
            _serial.write(calcChecksum(_curChecksum));

            // no return because of broadcast sending
            return false;
        }
    }

    public boolean syncWriteGoalPosition(int[] idList,int[] posList)
    {
        int[][] dataList = new int[idList.length][2];
        for(int i=0;i < idList.length;i++)
        {
            dataList[i] = new int[2];
            dataList[i][0] = posList[i] & 0x00FF;
            dataList[i][1] = (posList[i] & 0xFF00) >> 8;
        }

        return syncWrite(DX_CMD_GOAL_POS,2,idList,dataList);
    }

    public boolean syncWriteMovingSpeed(int[] idList,int[] speedList)
    {
        int[][] dataList = new int[idList.length][2];
        for(int i=0;i < idList.length;i++)
        {
            dataList[i] = new int[2];
            dataList[i][0] = speedList[i] & 0x00FF;
            dataList[i][1] = (speedList[i] & 0xFF00) >> 8;
        }

        return syncWrite(DX_CMD_MOV_SPEED,2,idList,dataList);
    }

    public boolean syncWriteMovingSpeed(int[] idList,int speed)
    {
        int[][] dataList = new int[idList.length][2];
        for(int i=0;i < idList.length;i++)
        {
            dataList[i] = new int[2];
            dataList[i][0] = speed & 0x00FF;
            dataList[i][1] = (speed & 0xFF00) >> 8;
        }

        return syncWrite(DX_CMD_MOV_SPEED,2,idList,dataList);
    }

    public boolean syncWriteTorqueEnable(int[] idList,boolean[] torqueList)
    {
        int[][] dataList = new int[idList.length][1];
        for(int i=0;i < idList.length;i++)
        {
            dataList[i] = new int[1];
            dataList[i][0] = torqueList[i] ? 1:0;
        }

        return syncWrite(DX_CMD_TORQUE_ENABLE,1,idList,dataList);
    }

    public boolean syncWriteTorqueEnable(int[] idList,boolean torque)
    {
        int[][] dataList = new int[idList.length][1];
        for(int i=0;i < idList.length;i++)
        {
            dataList[i] = new int[1];
            dataList[i][0] = torque ? 1:0;
        }

/*
        for(int i=0;i < idList.length;i++)
        {
            System.out.println("dataList: " + i + ":" + dataList[i][0]);

        }
*/
        return syncWrite(DX_CMD_TORQUE_ENABLE,1,idList,dataList);
    }

    public boolean setWheelMode(int id,boolean enable,int modelNr)
    {
        if(enable)
        {   // on
            return setAngleLimitCW(id,0) &&
                   setAngleLimitCCW(id,0);
        }
        else
        {   // off
            return setAngleLimitCW(id,0) &&
                   setAngleLimitCCW(id,getMotorRange(modelNr));
            /*
            // mx etc...
            setAngleLimitCCW(id,0xFFF);
            // ax etc...
            setAngleLimitCCW(id,0x3FF);
            */
        }
    }

    public boolean setAngleLimitCW(int id,int limit)
    {
        synchronized(_lock)
        {
            writeData2Bytes(id,DX_CMD_CW_ANGLE_LIMIT,limit,_regWriteFlag);

            // handle reply
            return handleReturnStatus(id);
        }
    }

    public int angleLimitCW(int id)
    {
        synchronized(_lock)
        {
            readData(id,DX_CMD_CW_ANGLE_LIMIT,2);
            if(handleReturnStatus(id))
            {
                if(_returnPacket.param.size() != 2)
                    return -1;
                return((_returnPacket.param.get(1).intValue() << 8) + _returnPacket.param.get(0).intValue());
            }
            else
                return -1;
        }
    }

    public boolean setAngleLimitCCW(int id,int limit)
    {
        synchronized(_lock)
        {
            writeData2Bytes(id,DX_CMD_CCW_ANGLE_LIMIT,limit,_regWriteFlag);

            // handle reply
            return handleReturnStatus(id);
        }
    }

    public int angleLimitCCW(int id)
    {
        synchronized(_lock)
        {
            readData(id,DX_CMD_CCW_ANGLE_LIMIT,2);
            if(handleReturnStatus(id))
            {
                if(_returnPacket.param.size() != 2)
                    return -1;
                return((_returnPacket.param.get(1).intValue() << 8) + _returnPacket.param.get(0).intValue());
            }
            else
                return -1;
        }
    }

    public boolean setMovingSpeed(int id,int speed)
    {
        synchronized(_lock)
        {
            writeData2Bytes(id,DX_CMD_MOV_SPEED,speed,_regWriteFlag);

            // handle reply
            return handleReturnStatus(id);
        }
    }

    public int movingSpeed(int id)
    {
        synchronized(_lock)
        {
            readData(id,DX_CMD_MOV_SPEED,2);
            if(handleReturnStatus(id))
            {
                if(_returnPacket.param.size() != 2)
                    return -1;
                return((_returnPacket.param.get(1).intValue() << 8) + _returnPacket.param.get(0).intValue());
            }
            else
                return -1;
        }
    }

    public boolean setTorqueLimit(int id,int torqueLimit)
    {
        synchronized(_lock)
        {
            writeData2Bytes(id,DX_CMD_LIMIT_TORQUE,torqueLimit,_regWriteFlag);

            // handle reply
            return handleReturnStatus(id);
        }
    }

    public int torqueLimit(int id)
    {
        synchronized(_lock)
        {
            readData(id,DX_CMD_LIMIT_TORQUE,2);
            if(handleReturnStatus(id))
            {
                if(_returnPacket.param.size() != 2)
                    return -1;
                return((_returnPacket.param.get(1).intValue() << 8) + _returnPacket.param.get(0).intValue());
            }
            else
                return -1;
        }
    }

    // mx commands
    public boolean setDGain(int id,int gain)
    {
        synchronized(_lock)
        {
            writeDataByte(id,DX_CMD_D_GAIN,gain,_regWriteFlag);

            // handle reply
            return handleReturnStatus(id);
        }
    }

    public int dGain(int id)
    {
        synchronized(_lock)
        {
            readData(id,DX_CMD_D_GAIN,1);
            if(handleReturnStatus(id))
            {
                if(_returnPacket.param.size() != 1)
                    return -1;
                return(_returnPacket.param.get(0).intValue());
            }
            else
                return -1;
        }
    }

    public boolean setIGain(int id,int gain)
    {
        synchronized(_lock)
        {
            writeDataByte(id,DX_CMD_I_GAIN,gain,_regWriteFlag);

            // handle reply
            return handleReturnStatus(id);
        }
    }

    public int iGain(int id)
    {
        synchronized(_lock)
        {
            readData(id,DX_CMD_I_GAIN,1);
            if(handleReturnStatus(id))
            {
                if(_returnPacket.param.size() != 1)
                    return -1;
                return(_returnPacket.param.get(0).intValue());
            }
            else
                return -1;
        }
    }

    public boolean setPGain(int id,int gain)
    {
        synchronized(_lock)
        {
            writeDataByte(id,DX_CMD_P_GAIN,gain,_regWriteFlag);

            // handle reply
            return handleReturnStatus(id);
        }
    }

    public int pGain(int id)
    {
        synchronized(_lock)
        {
            readData(id,DX_CMD_P_GAIN,1);
            if(handleReturnStatus(id))
            {
                if(_returnPacket.param.size() != 1)
                    return -1;
                return(_returnPacket.param.get(0).intValue());
            }
            else
                return -1;
        }
    }

    // ax commands
    public boolean setComplianceMarginCW(int id,int value)
    {
        synchronized(_lock)
        {
            writeDataByte(id,DX_CMD_COMPLIANCE_MARGIN_CW,value,_regWriteFlag);

            // handle reply
            return handleReturnStatus(id);
        }
    }

    public int complianceMarginCW(int id)
    {
        synchronized(_lock)
        {
            readData(id,DX_CMD_COMPLIANCE_MARGIN_CW,1);
            if(handleReturnStatus(id))
            {
                if(_returnPacket.param.size() != 1)
                    return -1;
                return(_returnPacket.param.get(0).intValue());
            }
            else
                return -1;
        }
    }

    public boolean setComplianceMarginCCW(int id,int value)
    {
        synchronized(_lock)
        {
            writeDataByte(id,DX_CMD_COMPLIANCE_MARGIN_CCW,value,_regWriteFlag);

            // handle reply
            return handleReturnStatus(id);
        }
    }

    public int complianceMarginCCW(int id)
    {
        synchronized(_lock)
        {
            readData(id,DX_CMD_COMPLIANCE_MARGIN_CCW,1);
            if(handleReturnStatus(id))
            {
                if(_returnPacket.param.size() != 1)
                    return -1;
                return(_returnPacket.param.get(0).intValue());
            }
            else
                return -1;
        }
    }

    public boolean setComplianceSlopeCW(int id,int value)
    {
        synchronized(_lock)
        {
            writeDataByte(id,DX_CMD_COMPLIANCE_SLOPE_CW,value,_regWriteFlag);

            // handle reply
            return handleReturnStatus(id);
        }
    }

    public int complianceSlopeCW(int id)
    {
        synchronized(_lock)
        {
            readData(id,DX_CMD_COMPLIANCE_SLOPE_CW,1);
            if(handleReturnStatus(id))
            {
                if(_returnPacket.param.size() != 1)
                    return -1;
                return(_returnPacket.param.get(0).intValue());
            }
            else
                return -1;
        }
    }

    public boolean setComplianceSlopeCCW(int id,int value)
    {
        synchronized(_lock)
        {
            writeDataByte(id,DX_CMD_COMPLIANCE_SLOPE_CCW,value,_regWriteFlag);

            // handle reply
            return handleReturnStatus(id);
        }
    }

    public int complianceSlopeCCW(int id)
    {
        synchronized(_lock)
        {
            readData(id,DX_CMD_COMPLIANCE_SLOPE_CCW,1);
            if(handleReturnStatus(id))
            {
                if(_returnPacket.param.size() != 1)
                    return -1;
                return(_returnPacket.param.get(0).intValue());
            }
            else
                return -1;
        }
    }


    public boolean setGoalPosition(int id,int pos)
    {
        synchronized(_lock)
        {
            writeData2Bytes(id,DX_CMD_GOAL_POS,pos,_regWriteFlag);

            // handle reply
            return handleReturnStatus(id);
        }
    }

    public int goalPosition(int id)
    {
        synchronized(_lock)
        {
            readData(id,DX_CMD_GOAL_POS,2);
            if(handleReturnStatus(id))
            {
                if(_returnPacket.param.size() != 2)
                    return -1;
                // System.out.println(_returnPacket.toString());
                return((_returnPacket.param.get(1).intValue() << 8) + _returnPacket.param.get(0).intValue());
            }
            else
                return -1;
        }
    }
	
    public int presentPosition(int id)
    {
/*
        readData(id,DX_CMD_PRESENT_POS,2);
long startTime = System.currentTimeMillis();	
        if(handleReturnStatus(id))
        {
System.out.println("xxx readtime:" + (System.currentTimeMillis()- startTime));
            if(_returnPacket.param.size() != 2)
			  return -1;
            return((_returnPacket.param.get(1).intValue() << 8) + _returnPacket.param.get(0).intValue());
        }
        else
            return -1;
*/
        synchronized(_lock)
        {
            readData(id,DX_CMD_PRESENT_POS,2);
            if(handleReturnStatus(id))
            {
                if(_returnPacket.param.size() != 2)
                {
                    //System.out.println("less parameter error: " + errorStr(_error));
                    return -1;
                }
                return((_returnPacket.param.get(1).intValue() << 8) + _returnPacket.param.get(0).intValue());
            }
            else
            {
                //System.out.println("handleReturnStatus error: " + errorStr(_error));
                return -1;
            }
        }
    }
	
    public int presentSpeed(int id)
    {
        synchronized(_lock)
        {
            readData(id,DX_CMD_PRESENT_SPEED,2);
            if(handleReturnStatus(id))
            {
                if(_returnPacket.param.size() != 2)
                    return -1;
                return((_returnPacket.param.get(1).intValue() << 8) + _returnPacket.param.get(0).intValue());
            }
            else
                return -1;
        }
    }
	
    public int presentLoad(int id)
    {
        synchronized(_lock)
        {
            readData(id,DX_CMD_PRESENT_LOAD,2);
            if(handleReturnStatus(id))
            {
                if(_returnPacket.param.size() != 2)
                    return -1;
                return((_returnPacket.param.get(1).intValue() << 8) + _returnPacket.param.get(0).intValue());
            }
            else
                return -1;
        }
    }
	
    public int presentVolt(int id)
    {
        synchronized(_lock)
        {
            readData(id,DX_CMD_PRESENT_VOLT,2);
            if(handleReturnStatus(id))
            {
                if(_returnPacket.param.size() != 2)
                    return -1;
                return((_returnPacket.param.get(1).intValue() << 8) + _returnPacket.param.get(0).intValue());
            }
            else
                return -1;
        }
    }
	
    public int presentTemp(int id)
    {
        synchronized(_lock)
        {
            readData(id,DX_CMD_PRESENT_TEMP,2);
            if(handleReturnStatus(id))
            {
                if(_returnPacket.param.size() != 2)
                    return -1;
                return((_returnPacket.param.get(1).intValue() << 8) + _returnPacket.param.get(0).intValue());
            }
            else
                return -1;
        }
    }
		
    public boolean register(int id)
    {
        synchronized(_lock)
        {
            readData(id,DX_CMD_REGISTER,1);
            if(handleReturnStatus(id))
            {
                if(_returnPacket.param.size() != 1)
                    return false;
                return(_returnPacket.param.get(0).intValue() > 0);
            }
            else
                return false;
        }
    }


    public boolean moving(int id)
    {
        synchronized(_lock)
        {
            readData(id,DX_CMD_MOVING,1);
            if(handleReturnStatus(id))
            {
                if(_returnPacket.param.size() != 1)
                    return false;
                return(_returnPacket.param.get(0).intValue() > 0);
            }
            else
                return false;
        }
    }


    public boolean setTorqueEnable(int id,boolean enable)
    {
        synchronized(_lock)
        {
          if(enable)
                writeDataByte(id,DX_CMD_TORQUE_ENABLE,1,_regWriteFlag);
          else
                writeDataByte(id,DX_CMD_TORQUE_ENABLE,0,_regWriteFlag);

          // handle reply
          return handleReturnStatus(id);
        }
    }

    public boolean torqueEnable(int id)
    {
        synchronized(_lock)
        {
            readData(id,DX_CMD_TORQUE_ENABLE,1);
            if(handleReturnStatus(id))
            {
                if(_returnPacket.param.size() != 1)
                    return false;
                            return(_returnPacket.param.get(0).intValue() > 0);
            }
            else
                return false;
        }
    }

    public boolean setLock(int id,boolean enable)
    {
        synchronized(_lock)
        {
          if(enable)
                writeDataByte(id,DX_CMD_LOCK,1,_regWriteFlag);
          else
                writeDataByte(id,DX_CMD_LOCK,0,_regWriteFlag);

          // handle reply
          return handleReturnStatus(id);
        }
    }

    public boolean lock(int id)
    {
        synchronized(_lock)
        {
            readData(id,DX_CMD_LOCK,1);
            if(handleReturnStatus(id))
            {
                if(_returnPacket.param.size() != 1)
                    return false;
                            return(_returnPacket.param.get(0).intValue() > 0);
            }
            else
                return false;
        }
    }

    public boolean setLed(int id,boolean enable)
    {
        synchronized(_lock)
        {
          if(enable)
                writeDataByte(id,DX_CMD_LED_ENABLE,1,_regWriteFlag);
          else
                writeDataByte(id,DX_CMD_LED_ENABLE,0,_regWriteFlag);

          // handle reply
          return handleReturnStatus(id);
        }
    }

    public boolean led(int id)
    {
        synchronized(_lock)
        {
            readData(id,DX_CMD_LED_ENABLE,1);
            if(handleReturnStatus(id))
            {
                if(_returnPacket.param.size() != 1)
                    return false;
                            return(_returnPacket.param.get(0).intValue() > 0);
            }
            else
                return false;
        }
    }


    public boolean setPunch(int id,int punch)
    {
        synchronized(_lock)
        {
            writeData2Bytes(id,DX_CMD_PUNCH,punch,_regWriteFlag);

            // handle reply
            return handleReturnStatus(id);
        }
    }

    public int punch(int id)
    {
        synchronized(_lock)
        {
            readData(id,DX_CMD_PUNCH,2);
            if(handleReturnStatus(id))
            {
                if(_returnPacket.param.size() != 2)
                    return -1;
                return((_returnPacket.param.get(1).intValue() << 8) + _returnPacket.param.get(0).intValue());
            }
            else
                return -1;
        }
    }

    protected synchronized boolean writeData2Bytes(int id,int addr,int data)
    {
        return writeData2Bytes(id, addr, data, false);
    }
  
    protected synchronized boolean writeData2Bytes(int id,int addr,int data,boolean regWrite)
    {
        if(_serialType == DX_SERIALTYPE_SYNC)
            // block next few bytes for receiving
            _serial.addReadBlockCount(9);

        _curChecksum = 0;

        _serial.write(DX_BEGIN);
        _serial.write(DX_BEGIN);

        // id
        _serial.write(id);
        _curChecksum += id;

        // length
        _serial.write(2 + 3);
        _curChecksum += 2 + 3;  // 3 bytes param

        // instruction
        if(regWrite)
        {
          _serial.write(DX_INST_REG_WRITE);
          _curChecksum += DX_INST_REG_WRITE;
        }
        else
        {
          _serial.write(DX_INST_WRITE_DATA);
          _curChecksum += DX_INST_WRITE_DATA;
        }

        // param - addr
        _serial.write(addr);
        _curChecksum += addr;
        // param - low byte
        _serial.write(data & 0x00FF);
        _curChecksum += data & 0x00FF;
        // param - high byte
        _serial.write((data & 0xFF00) >> 8);
        _curChecksum += (data & 0xFF00) >> 8;

        // checksum
        _serial.write(calcChecksum(_curChecksum));

        return true;
    }


    protected synchronized boolean writeDataByte(int id,int addr,int data)
    {    
	  return writeDataByte(id, addr, data, false);
    }

    protected synchronized boolean writeDataByte(int id,int addr,int data,boolean regWrite)
    {
        if(_serialType == DX_SERIALTYPE_SYNC)
            // block next few bytes for receiving
            _serial.addReadBlockCount(8);

        _curChecksum = 0;

        _serial.write(DX_BEGIN);
        _serial.write(DX_BEGIN);

        // id
        _serial.write(id);
        _curChecksum += id;

        // length
        _serial.write(1 + 3);
        _curChecksum += 1 + 3;  // 3 bytes param

        // instruction
        if(regWrite)
        {
          _serial.write(DX_INST_REG_WRITE);
          _curChecksum += DX_INST_REG_WRITE;
        }
        else
        {
          _serial.write(DX_INST_WRITE_DATA);
          _curChecksum += DX_INST_WRITE_DATA;
        }

        // param - addr
        _serial.write(addr);
        _curChecksum += addr;
        // param - low byte
        _serial.write(data & 0x00FF);
        _curChecksum += data & 0x00FF;

        // checksum
        _serial.write(calcChecksum(_curChecksum));

        return true;
    }

    protected  boolean readData(int id,int addr,int readLength)
    {
        if(_serialType == DX_SERIALTYPE_SYNC)
            // block next few bytes for receiving
            _serial.addReadBlockCount(8);

        _curChecksum = 0;

        _serial.write(DX_BEGIN);
        _serial.write(DX_BEGIN);

        // id
        _serial.write(id);
        _curChecksum += id;

        // length
        _serial.write(2 + 2);
        _curChecksum += 2 + 2;  // 2 bytes param

        // instruction
        _serial.write(DX_INST_READ_DATA);
        _curChecksum += DX_INST_READ_DATA;

        // param - addr
        _serial.write(addr);
        _curChecksum += addr;
        // param - read length
        _serial.write(readLength);
        _curChecksum += readLength;

        // checksum
        _serial.write(calcChecksum(_curChecksum));

        return true;
    }
	
    public int lastError() { return _error; }

    public ReturnPacket returnPacket() { return _returnPacket; }

    protected boolean handleReturnStatus(int id)
    {
        if(readStatus(_returnPacket) == false)
            return false;

        _error = _returnPacket.error;
        if(_returnPacket.id != id || _error != 0)
        {
            _error |= DX_ERROR_USR_ID;
            return false;
        }
        else
            return true;
    }

    protected boolean handleReturnStatus()
    {
        if(readStatus(_returnPacket) == false)
        {
            _error = DX_ERROR_USR_READSTATUS;
            return false;
        }

        _error = _returnPacket.error;
        return true;
    }

    protected synchronized boolean readStatus(ReturnPacket returnPacket)
    {
        if(readStart(_timeout) == false)
        {
            _error |= DX_ERROR_USR_NO_BEGIN;
            return false;
        }

        if(waitForData(_serial,_timeout,_delay,3) == false)
        {
            _error |= DX_ERROR_USR_DATA_TIMEOUT;
            return false;
        }

        _curChecksum = 0;

        // read id
        returnPacket.id = _serial.read();

        // read length
        returnPacket.length = _serial.read();
		
        // read error
        returnPacket.error = _serial.read();

        // wait for the rest of the data
        if(waitForData(_serial,_timeout,_delay,returnPacket.length - 1) == false)
        {
            _error |= DX_ERROR_USR_DATA_TIMEOUT;
            return false;
        }

        // read param
        returnPacket.param.clear();
        for(int i=0; i < returnPacket.length-2; i++)
            returnPacket.param.add(_serial.read());

        return(returnPacket.checksum() == _serial.read());
    }

    protected boolean readStart(int timeout)
    {
        _error = 0;
		
        int origTimeout = timeout;
        int failCount = 100;

        // read all the data till the first 0xff
        timeout = origTimeout;
        while(failCount > 0)
        {
          timeout = origTimeout;
          if(waitForData(_serial,timeout,_delay,1) == false)
                return false;

          if(_serial.read() == DX_BEGIN)
                break;
          else
                failCount--;
        }

        // second begin
        timeout = origTimeout;
        if(waitForData(_serial,timeout,_delay,1) == false)
            return false;

        if(_serial.read() != DX_BEGIN)
            return false;

        // data
        timeout = origTimeout;
        if(waitForData(_serial,timeout,_delay,4) == false)
            return false;

        return true;
    }

    public static boolean waitForData(SerialWrapper serial,int timeout,int delay,int dataCount)
    {
        while(serial.available() < dataCount && (timeout--) >=0)
        {
            if(locSleep(delay) == false)
                return false;
        }

        if(timeout < 0 && serial.available() < dataCount)
            return false;
        return true;
    }

    public final static int calcChecksum(int checksumVal)
    {
        return(0xFF & ~checksumVal);
    }

    protected static boolean locSleep(int delay)
    {
        try
        {
            //Thread.sleep(delay);
            //Thread.sleep(0,delay*10);
            Thread.sleep(delay);
        }
        catch(InterruptedException excetpion)
        {
            System.out.println("InterruptedException: " + excetpion);
            return false;
        }
        return true;
    }

    public static String errorStr(int error)
    {
        String retStr = new String();

        if((error & DX_ERROR_INVOLT) > 0)
            retStr += "Input Voltage Error\n";

        if((error & DX_ERROR_ANGLELIMIT) > 0)
            retStr += "Angle Limit Error\n";

        if((error & DX_ERROR_OVERHEAT) > 0)
            retStr += "OverHeating Error\n";

        if((error & DX_ERROR_RANGE) > 0)
            retStr += "Range Error\n";

        if((error & DX_ERROR_CHECKSUM) > 0)
            retStr += "CheckSum Error\n";

        if((error & DX_ERROR_OVERLOAD) > 0)
            retStr += "Overload Error\n";

        if((error & DX_ERROR_INST) > 0)
            retStr += "Instruction  Error\n";


        if((error & DX_ERROR_USR_ID) > 0)
            retStr += "Status Packet - Wrong id\n";

        if((error & DX_ERROR_USR_READSTATUS) > 0)
            retStr += "Status Packet - \n";

        if((error & DX_ERROR_USR_NO_BEGIN) > 0)
            retStr += "Status Packet - No begin found\n";

        if((error & DX_ERROR_USR_DATA_TIMEOUT) > 0)
            retStr += "Status Packet - Data timeout\n";

        return retStr;
    }

}

