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
  public static int SIMPLEDYNAMIXEL_VERSION = 11; // 0011

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
    public static int DX_ERROR_OVERHEAT			= 1 << 2;
    public static int DX_ERROR_RANGE			= 1 << 3;
    public static int DX_ERROR_CHECKSUM			= 1 << 4;
    public static int DX_ERROR_OVERLOAD			= 1 << 5;
    public static int DX_ERROR_INST				= 1 << 6;

    public static int DX_ERROR_USR_ID			= 1 << 10;
    public static int DX_ERROR_USR_READSTATUS	= 1 << 11;

    // Instructions
    public static int DX_INST_PING			= 0x01;
    public static int DX_INST_READ_DATA                 = 0x02;
    public static int DX_INST_WRITE_DATA		= 0x03;
    public static int DX_INST_REG_WRITE                 = 0x04;
    public static int DX_INST_ACTION			= 0x05;
    public static int DX_INST_RESET			= 0x06;
    public static int DX_INST_SYNC_WRITE		= 0x83;

    // commands
    public static int DX_CMD_MODELNR			= 0x00;
    public static int DX_CMD_FIRMWARE			= 0x02;
    public static int DX_CMD_ID 			= 0x03;
    public static int DX_CMD_BAUDRATE			= 0x04;
    public static int DX_CMD_DELAYTIME			= 0x05;
    public static int DX_CMD_CW_ANGLE_LIMIT		= 0x06;
    public static int DX_CMD_CCW_ANGLE_LIMIT            = 0x08;
    public static int DX_CMD_STATUSRETURNLEVEL          = 0x10;
    public static int DX_CMD_TORQUE_ENABLE		= 0x18;
    public static int DX_CMD_LED_ENABLE			= 0x19;
    public static int DX_CMD_D_GAIN			= 0x1A;
    public static int DX_CMD_I_GAIN			= 0x1B;
    public static int DX_CMD_P_GAIN			= 0x1C;
    public static int DX_CMD_GOAL_POS			= 0x1E;
    public static int DX_CMD_MOV_SPEED			= 0x20;
    public static int DX_CMD_PRESENT_POS		= 0x24;
    public static int DX_CMD_PRESENT_SPEED		= 0x26;
    public static int DX_CMD_PRESENT_LOAD		= 0x28;
    public static int DX_CMD_PRESENT_VOLT		= 0x2A;
    public static int DX_CMD_PRESENT_TEMP		= 0x2B;

    public static int DX_CMD_MAX_TORQUE			= 0x0E;

    public static int DX_CMD_COMPLIANCE_MARGIN_CW	= 0x1A;
    public static int DX_CMD_COMPLIANCE_MARGIN_CCW	= 0x1B;

    public static int DX_CMD_COMPLIANCE_SLOPE_CW	= 0x1C;
    public static int DX_CMD_COMPLIANCE_SLOPE_CCW	= 0x1D;

    public static int DX_CMD_MOVING			= 0x2E;
    public static int DX_CMD_PUNCH			= 0x30;

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
    }


    protected SerialWrapper 				_serial;
    protected ArrayList<Integer>                        _data;
    protected int					_curChecksum;
    protected int					_error;
    protected int					_timeout = 100;
    protected int					_delay = 1;
    protected ReturnPacket                              _returnPacket = new ReturnPacket();
    protected boolean                                   _regWriteFlag = false;
    protected int 					_regWriteDelay = 2;
    PApplet						_parent;
	

    public Servo()
    {
        loadExtLib();
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
        _serial = new SerialWrapper(serialDev, baudRate);
        _parent = null;
        _serial.clear();
    }

    public SerialWrapper serial() { return _serial; }


    public int modelNr(int id)
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

    public int firmware(int id)
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

    public boolean setId(int id,int newId)
    {
        if(newId >= DX_BROADCAST_ID)
            return false;

        writeDataByte(id,DX_CMD_ID,newId);

        // handle reply
        return handleReturnStatus(id);
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

    public boolean setDelayTime(int id,int delayTime)
    {
        writeDataByte(id,DX_CMD_DELAYTIME,delayTime);

        // handle reply
        return handleReturnStatus(id);
    }

    public int delayTime(int id)
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

    public boolean setStatusReturnLevel(int id,int statusReturnLevel)
    {
        if(statusReturnLevel >= 3)
            return false;
        writeDataByte(id,DX_CMD_STATUSRETURNLEVEL,statusReturnLevel);

        // handle reply
        return handleReturnStatus(id);
    }

    public int statusReturnLevel(int id)
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
/*
    public synchronized boolean ping(int id,int timeout)
	{

	}
*/
    // watch out, this resets the servo to the default factory settings
    public boolean reset(int id)
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
        _serial.write(DX_INST_ACTION);
        _curChecksum += DX_INST_ACTION;

        // no param

        // checksum
        _serial.write(calcChecksum(_curChecksum));

        // handle reply
        boolean ret = handleReturnStatus(id);

        return ret;
    }

    public synchronized boolean ping(int id)
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
        int oldTimeout = _timeout;
        _timeout = 50;
        boolean ret = handleReturnStatus(id);
        _timeout = oldTimeout;

        return ret;
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

    public boolean setAngleLimitCW(int id,int limit)
    {
        writeData2Bytes(id,DX_CMD_CW_ANGLE_LIMIT,limit,_regWriteFlag);

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
        writeData2Bytes(id,DX_CMD_CCW_ANGLE_LIMIT,limit,_regWriteFlag);

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
        writeData2Bytes(id,DX_CMD_MOV_SPEED,speed,_regWriteFlag);

        // handle reply
        return handleReturnStatus(id);
    }

    public int movingSpeed(int id)
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

    public boolean setDGain(int id,int gain)
    {
        writeDataByte(id,DX_CMD_D_GAIN,gain,_regWriteFlag);

        // handle reply
        return handleReturnStatus(id);
    }

    public int dGain(int id)
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

    public boolean setIGain(int id,int gain)
    {
        writeDataByte(id,DX_CMD_I_GAIN,gain,_regWriteFlag);

        // handle reply
        return handleReturnStatus(id);
    }

    public int iGain(int id)
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


    public boolean setPGain(int id,int gain)
    {
        writeDataByte(id,DX_CMD_P_GAIN,gain,_regWriteFlag);

        // handle reply
        return handleReturnStatus(id);
    }

    public int pGain(int id)
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

    public boolean setGoalPosition(int id,int pos)
    {
        writeData2Bytes(id,DX_CMD_GOAL_POS,pos,_regWriteFlag);

        // handle reply
        return handleReturnStatus(id);
    }

    public int goalPosition(int id)
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
        readData(id,DX_CMD_PRESENT_POS,2);
        if(handleReturnStatus(id))
        {
            if(_returnPacket.param.size() != 2)
			  return -1;
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
	

    public boolean setTorqueEnable(int id,boolean enable)
    {
      if(enable)
            writeDataByte(id,DX_CMD_TORQUE_ENABLE,1,_regWriteFlag);
      else
            writeDataByte(id,DX_CMD_TORQUE_ENABLE,0,_regWriteFlag);

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
            writeDataByte(id,DX_CMD_LED_ENABLE,1,_regWriteFlag);
      else
            writeDataByte(id,DX_CMD_LED_ENABLE,0,_regWriteFlag);

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


    public boolean setPunch(int id,int punch)
    {
        writeData2Bytes(id,DX_CMD_PUNCH,punch,_regWriteFlag);

        // handle reply
        return handleReturnStatus(id);
    }

    public int punch(int id)
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

    protected synchronized boolean writeData2Bytes(int id,int addr,int data)
    {
        return writeData2Bytes(id, addr, data, false);
    }
  
    protected synchronized boolean writeData2Bytes(int id,int addr,int data,boolean regWrite)
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
        {
		  _error = DX_ERROR_USR_READSTATUS;
		  return false;
		}

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
            return false;

		if(waitForData(_serial,_timeout,_delay,3) == false)
		  return false;

        _curChecksum = 0;

        // read id
        returnPacket.id = _serial.read();

        // read length
        returnPacket.length = _serial.read();
		
        // read error
        returnPacket.error = _serial.read();

		// wait for the rest of the data
		if(waitForData(_serial,_timeout,_delay,returnPacket.length - 1) == false)
		  return false;

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

    public static int calcChecksum(int checksumVal)
    {
        return(0xFF & ~checksumVal);
    }

    protected static boolean locSleep(int delay)
    {
        try
        {
            //Thread.sleep(delay);
            //Thread.sleep(0,delay*10);
            Thread.sleep(0,1);
        }
        catch(InterruptedException excetpion)
        {
            System.out.println("InterruptedException: " + excetpion);
            return false;
        }
        return true;
    }

}

