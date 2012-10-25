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
int[]           servoList;

int             baudrate = 1000000;  // 1mbit
int             maxSpeed = 500;      // till 0X3FF

// mx-XX
int			servoRange = 0xFFF; // mx-28 12bit
int			servoDeadAngle = 0;
/*
// ax-XX
int			servoRange = 0x3FF; // ax-12 10bit
int			servoDeadAngle = 60;
*/

PVector lastPos = new PVector(0,0);
float   lastAngle=0;

void setup()
{
  smooth();
  size(800,500);

  // init the dynamixel library
  String serialDev = "/dev/ttyUSB0"; // linux
  servo = new Servo();
  //servo.init(this,serialDev,baudrateBefore); 	// 1Mbit baudrate, use processing serial
  servo.init(serialDev,baudrate); 	// 1Mbit baudrate, use c++ serial

  servoList = servo.pingRange(0,20);
  println("---------------------");
  println("servoList: " + Arrays.toString( servoList ));

  // set all servos to the fixed baudrate
  for(int i=0;i < servoList.length;i++)
  {
    println("Servo Id: " + servoList[i]);
    servo.setMovingSpeed(servoList[i],maxSpeed); 
  }    

  lastPos = new PVector(width/2,height/2);

}

void draw()
{
  background(0);
  
  stroke(255);
  noFill();
  ellipse(width/2,height/2,300,300);
  
  int count = 12;
  pushMatrix();
  translate(width/2,height/2);
  for(int i=0;i<count;i++)
  {
    line(110,0,150,0);
    rotate(radians(360.0/count));
  }
  popMatrix();
  
  stroke(255,0,0);
  pushMatrix();
    translate(width/2,height/2);
    rotate(-lastAngle);
    line(0,50,0,150);
  popMatrix();
}

void mousePressed()
{
  // set the angle
  PVector center = new PVector(width/2,height/2);
  PVector pos = new PVector(mouseX,mouseY);
  PVector dir = PVector.sub(pos,center);
  lastAngle =  - (atan2(dir.y,dir.x) - PI/2.0);
  if(lastAngle < 0)
    lastAngle += TWO_PI;
  
  int motorPos = (int)(servoRange / TWO_PI * lastAngle);
  
  println("Set motor goal position: " + degrees(lastAngle) + "\tMotorPos: " + motorPos);
  
  // set the motors to this angle
  for(int i=0;i < servoList.length;i++)
    servo.setGoalPosition(servoList[i],motorPos); 
  
}



