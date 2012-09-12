/* --------------------------------------------------------------------------
 * SimpleDynamixel Servo Read
 * --------------------------------------------------------------------------
 * Processing Wrapper for the Robotis Dynamixel Servos
 * http://code.google.com/p/simple-dynamixel
 * --------------------------------------------------------------------------
 * prog:  Max Rheiner / Interaction Design / Zhdk / http://iad.zhdk.ch/
 * date:  08/26/2012 (m/d/y)
 * ----------------------------------------------------------------------------
 * This example shows how to visualize the servos
 * ----------------------------------------------------------------------------
 */

import java.text.DecimalFormat;

import SimpleDynamixel.*;
import processing.serial.*;


Servo   	servo;
ServoViz[]	servoVizList;

// mx-28
int			servoRange = 0xFFF; // mx-28 12bit
int			servoDeadAngle = 0;
/*
// ax-12
int			servoRange = 0x3FF; // ax-12 10bit
int			servoDeadAngle = 60;
*/

float		vizRadius = 0;
float		vizDist = 10;

long 		startTime = 0;
long 		totalTime = 0;
long 		count = 0;
long 		errors = 0;
long		resetCount = 0;


DecimalFormat df = new DecimalFormat("0.000");

void setup()
{
  smooth();
  size(800,500);

  // init the dynamixel library
  String serialDev = "/dev/ttyUSB0"; // linux
  servo = new Servo();
  //servo.init(this,serialDev,1000000); 	// 1Mbit baudrate, use processing serial
  servo.init(serialDev,1000000); 	// 1Mbit baudrate, use c++ serial

  int[]   servoList = servo.pingRange(0,20);
  println("---------------------");
  println("servoList: " + Arrays.toString( servoList ));

  servoVizList = new ServoViz[servoList.length];
  for(int i=0;i < servoVizList.length;i++)
  {
	// disable the torque
	servo.setTorqueEnable(servoList[i],false);

	// set the return delay time for the status packet
	servo.setDelayTime(servoList[i],0);	// 2us * x
	//servo.setDelayTime(servoList[i],0xFE);	// default 2us * 0xFE = 2 * 250 = 0.5ms


	// init the servo viz
	servoVizList[i] = new ServoViz(servo,servoList[i],servoRange,servoDeadAngle);
  }

  if(servoVizList.length == 1)
	vizRadius = (height - 2 * vizDist) * .2f;
  else
	vizRadius = (width - 2 * vizDist - (servoVizList.length -1) * vizDist) / servoVizList.length * .5f;
}

void draw()
{
  background(0);
 
  pushMatrix();
  translate(vizRadius + vizDist,height/2);
  
  for(int i=0;i < servoVizList.length;i++)
  {
	
	startTime = System.nanoTime();
		// update the data
		servoVizList[i].update();

	totalTime += System.nanoTime()- startTime;
    count++;
      
	servoVizList[i].draw(g,vizRadius);
    translate(vizRadius * 2 + vizDist,0);

	if(servoVizList[i].error() != Servo.DX_ERROR_NO)
	  println("Servo Error: " + servoVizList[i].errorStr());
  }  
  popMatrix();

  // print out the time consumed by the return
  float timeDif = Float.parseFloat(df.format( (float)totalTime / count / 1000.0f / 1000.0f));
  text("Readtime/Servo: " + timeDif + " ms",20,20);

  if(count >= resetCount)
  {
	count = 0;
	totalTime = 0;
  }
}