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
import java.lang.Math;
import java.text.DecimalFormat;

public class Servo
{
  public static int SIMPLEDYNAMIXEL_VERSION = 10; // 0010

  static
  {
	System.out.println("SimpleDynamixel Version " + (SIMPLEDYNAMIXEL_VERSION / 100) + "." + (SIMPLEDYNAMIXEL_VERSION % 100));
	System.out.flush();
  }

    public static int DX_BEGIN				= 0xFF;
    public static int DX_BROADCAST_ID			= 0xFE;

    public static int DX_LAST_ID                          = 0xFD;

    // return values
    public static int DX_RET_OK				= 0;
    public static int DX_RET_ERROR_LEN		= 1;  // return package has wrong size
    public static int DX_RET_ERROR_START		= 2;  // can't find start
    public static int DX_RET_ERROR_CHECKSUM	= 3;  // can't find start

    // errors
    public static int DX_ERROR_INVOLT			= 1 << 0;
    public static int DX_ERROR_ANGLELIMIT		= 1 << 1;
    public static int DX_ERROR_OVERHEAT		= 1 << 2;
    public static int DX_ERROR_RANGE			= 1 << 3;
    public static int DX_ERROR_CHECKSUM		= 1 << 4;
    public static int DX_ERROR_OVERLOAD		= 1 << 5;
    public static int DX_ERROR_INST			= 1 << 6;

    // Instructions
    public static int DX_INST_PING			= 0x01;
    public static int DX_INST_READ_DATA		= 0x02;
    public static int DX_INST_WRITE_DATA		= 0x03;
    public static int DX_INST_REG_WRITE		= 0x04;
    public static int DX_INST_ACTION			= 0x05;
    public static int DX_INST_RESET			= 0x06;
    public static int DX_INST_SYNC_WRITE		= 0x83;

    // commands
    public static int DX_CMD_FIRMWARE			= 0x02;
    public static int DX_CMD_BAUDRATE			= 0x04;
    public static int DX_CMD_CW_ANGLE_LIMIT		= 0x06;
    public static int DX_CMD_CCW_ANGLE_LIMIT	= 0x08;
    public static int DX_CMD_TORQUE_ENABLE		= 0x18;
    public static int DX_CMD_LED_ENABLE			= 0x19;
    public static int DX_CMD_GOAL_POS			= 0x1E;
    public static int DX_CMD_MOV_SPEED			= 0x20;
    public static int DX_CMD_PRESENT_POS		= 0x24;
    public static int DX_CMD_PRESENT_SPEED		= 0x26;
    public static int DX_CMD_PRESENT_LOAD		= 0x28;
    public static int DX_CMD_PRESENT_VOLT		= 0x2A;
    public static int DX_CMD_PRESENT_TEMP		= 0x2B;

    public static int DX_CMD_MAX_TORQUE			= 0x0E;

    public static int DX_CMD_COMPLIANCE_MARGIN_CW		= 0x1A;
    public static int DX_CMD_COMPLIANCE_MARGIN_CCW	= 0x1B;

    public static int DX_CMD_COMPLIANCE_SLOPE_CW		= 0x1C;
    public static int DX_CMD_COMPLIANCE_SLOPE_CCW		= 0x1D;

    public static int DX_CMD_MOVING				= 0x2E;
    public static int DX_CMD_PUNCH_LEFT			= 0x30;
    public static int DX_CMD_PUNCH_RIGHT		= 0x31;

    public static int DX_DIR_CCW          	= 0;
    public static int DX_DIR_CW           	= 1;

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


    protected Serial 			_serial;
    protected ArrayList<Integer> 	        _data;
    protected int				_curChecksum;
    protected int				_error;
    protected int				_timeout = 40;
    protected int				_delay = 2;
    protected ReturnPacket      _returnPacket = new ReturnPacket();
	PApplet						_parent;

    public Servo()
    {}

/*
    public void init(Serial serial)
    {
        _serial = serial;
		_serial.clear();
    }
*/
    public void init(PApplet parent,String serialDev,int baudRate)
    {
        _serial = new Serial(parent,serialDev, baudRate);
		_parent = parent;
		_serial.clear();
    }

    public boolean setBaudrate(int id,int baudrate)
    {
        writeDataByte(id,DX_CMD_BAUDRATE,baudrate);

        // handle reply
        return handleReturnStatus(id);
    }

    public int baudrate(int id)
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

    public boolean ping(int id)
    {
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
        return handleReturnStatus(id);
    }


    public int[] pingAll()
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

    public int[] pingRange(int start, int end)
    {
        if(start > DX_LAST_ID)
            start = DX_LAST_ID;
        if(end > DX_LAST_ID)
            end = DX_LAST_ID;

        ArrayList<Integer> servoList = new ArrayList<Integer>();
        for(int i=start; i < end; i++)
        {
            if(ping(i))
                servoList.add(i);
        }

        int[] retArray = new int[servoList.size()];
        for(int i=0; i<servoList.size(); i++)
            retArray[i] = servoList.get(i).intValue();

        return retArray;
    }


    public boolean setAngleLimitCW(int id,int limit)
    {
        writeData2Bytes(id,DX_CMD_CW_ANGLE_LIMIT,limit);

        // handle reply
        return handleReturnStatus(id);
    }

    public int angleLimitCW(int id)
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

    public boolean setAngleLimitCCW(int id,int limit)
    {
        writeData2Bytes(id,DX_CMD_CCW_ANGLE_LIMIT,limit);

        // handle reply
        return handleReturnStatus(id);
    }

    public int angleLimitCCW(int id)
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

    public boolean setMovingSpeed(int id,int speed)
    {
        writeData2Bytes(id,DX_CMD_MOV_SPEED,speed);

        // handle reply
        return handleReturnStatus(id);
    }

    public boolean setGoalPosition(int id,int pos)
    {
        writeData2Bytes(id,DX_CMD_GOAL_POS,pos);

        // handle reply
        return handleReturnStatus(id);
    }

    public int goalPostition(int id)
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
	
    public int presentPostition(int id)
    {
        readData(id,DX_CMD_PRESENT_POS,2);
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
	
    public int presentSpeed(int id)
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
	
    public int presentLoad(int id)
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
	
    public int presentVolt(int id)
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
	
    public int presentTemp(int id)
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
		
    public boolean moving(int id)
    {
        readData(id,DX_CMD_PRESENT_TEMP,1);
        if(handleReturnStatus(id))
        {
            if(_returnPacket.param.size() != 1)
                return false;
            return(_returnPacket.param.get(0).intValue() > 0);
        }
        else
            return false;
    }
	

	public boolean setTorqueEnable(int id,boolean enable)
	{
	  if(enable)
		writeDataByte(id,DX_CMD_TORQUE_ENABLE,1);
	  else
		writeDataByte(id,DX_CMD_TORQUE_ENABLE,0);

	  // handle reply
       return handleReturnStatus(id);
	}
	
	public boolean torqueEnable(int id)
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

	public boolean setLed(int id,boolean enable)
	{
	  if(enable)
		writeDataByte(id,DX_CMD_LED_ENABLE,1);
	  else
		writeDataByte(id,DX_CMD_LED_ENABLE,0);

	  // handle reply
       return handleReturnStatus(id);
	}
	
	public boolean led(int id)
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


    protected boolean writeData2Bytes(int id,int addr,int data)
    {
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
        _serial.write(DX_INST_WRITE_DATA);
        _curChecksum += DX_INST_WRITE_DATA;

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


    protected boolean writeDataByte(int id,int addr,int data)
    {
        _curChecksum = 0;

        _serial.write(DX_BEGIN);
        _serial.write(DX_BEGIN);

        // id
        _serial.write(id);
        _curChecksum += id;

        // length
        _serial.write(2 + 2);
        _curChecksum += 2 + 2;  // 3 bytes param

        // instruction
        _serial.write(DX_INST_WRITE_DATA);
        _curChecksum += DX_INST_WRITE_DATA;

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



    protected boolean readData(int id,int addr,int readLength)
    {
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

    protected boolean handleReturnStatus(int id)
    {
        if(readStatus(_returnPacket) == false)
            return false;

        _error = _returnPacket.error;
        if(_returnPacket.id != id || _error != 0)
            return false;
        else
            return true;
    }

    protected boolean readStatus(ReturnPacket returnPacket)
    {
        if(readStart(_timeout) == false)
            return false;

        _curChecksum = 0;

        // read id
        returnPacket.id = _serial.read();

        // read length
        returnPacket.length = _serial.read();

        // read error
        returnPacket.error = _serial.read();

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
		int failCount = 30;

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

	public static boolean waitForData(Serial serial,int timeout,int delay,int dataCount)
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

    public static int calcChecksum(int checksumVal)
    {
        return(0xFF & ~checksumVal);
    }

    protected static boolean locSleep(int delay)
    {
        try
        {
            Thread.sleep(delay);
        }
        catch(InterruptedException excetpion)
        {
            System.out.println("InterruptedException: " + excetpion);
            return false;
        }
        return true;
    }

}

