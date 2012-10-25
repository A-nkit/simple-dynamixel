/* --------------------------------------------------------------------------
 * SimpleDynamixel Servo Set
 * --------------------------------------------------------------------------
 * Processing Wrapper for the Robotis Dynamixel Servos
 * http://code.google.com/p/simple-dynamixel
 * --------------------------------------------------------------------------
 * prog:  Max Rheiner / Interaction Design / Zhdk / http://iad.zhdk.ch/
 * date:  08/26/2012 (m/d/y)
 * ----------------------------------------------------------------------------
 * This example shows set servo parameters
 * ----------------------------------------------------------------------------
 */

import SimpleDynamixel.*;

Servo   	servo;
ServoViz[]	servoVizList;

int             baudrateBefore = 1000000;  // 1mbit
//int             baudrateBefore = 57600;

// see table in http://support.robotis.com/en/product/dynamixel/mx_series/mx-64.htm#Actuator_Address_04
int             baudrateAfter = 1;

void setup()
{
  smooth();
  size(800,500);

  // init the dynamixel library
  String serialDev = "/dev/ttyUSB0"; // linux
  servo = new Servo();
  //servo.init(this,serialDev,baudrateBefore); 	// 1Mbit baudrate, use processing serial
  servo.init(serialDev,baudrateBefore); 	// 1Mbit baudrate, use c++ serial

  int[]   servoList = servo.pingRange(0,20);
  println("---------------------");
  println("servoList: " + Arrays.toString( servoList ));

  // set all servos to the fixed baudrate
  for(int i=0;i < servoList.length;i++)
  {
    println("Servo Id: " + servoList[i]);
    //servo.setId(servoList[i],1);
    //servo.setBaudrate(servoList[i],baudrateAfter);
  }

  exit();
}

