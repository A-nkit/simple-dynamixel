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
import java.lang.Math;
import java.text.DecimalFormat;

public class ServoViz
{
  public static int	VIZ_FULL 	= 0;
  public static int	VIZ_FAST 	= 1;

  protected Servo	_servo;
  protected int		_id;
  protected int		_range;
  protected int		_deadAngle;
  protected int		_limitCW;
  protected int		_limitCCW;
  
  protected int		_pos=0;
  protected int		_speed=0;
  protected int		_load=0;
  protected int		_voltage=0;
  protected int		_temp=0;

  protected long _updateCounter=0;
  protected long _lastUpdate=0;
  protected long _updateFreq=20;

  protected int _vizType = VIZ_FAST;
  protected float	_resQ;
  
  public ServoViz(Servo servo,int id,int range,int deadAngle)
  {
	_servo 	= servo;
	_id		= id;
	_range	= range;
	_deadAngle	= deadAngle;

	_resQ = (float)(2.0f * Math.PI - Math.toRadians(_deadAngle)) / (float)_range;

	// init values
	readValues();
  }

  public void readValues()
  {
	_speed	 = _servo.presentSpeed(_id);
	_load	 = _servo.presentLoad(_id);
	_voltage = _servo.presentVolt(_id);
	_temp	 = _servo.presentTemp(_id);

	_limitCW	 = _servo.angleLimitCW(_id);
	_limitCCW	 = _servo.angleLimitCCW(_id);
  }

  public void readTempValues()
  {
	int curPos = _servo.presentPosition(_id);
	if(curPos >= 0)
	  _pos = curPos;
  }

  public int id() { return _id; }

  public void update()
  {
	readTempValues();
	if(_vizType == VIZ_FULL && _updateCounter > _lastUpdate + _updateFreq)
	{
	  readValues();
	  _lastUpdate = _updateCounter;
	  System.out.println("x");
	}
	_updateCounter++;
  }

    public void draw(PGraphics g,float r)
    {	
        g.pushStyle();

		// range
        g.fill(255,255,255,40);
        g.noStroke();
		g.pushMatrix();
		g.rotate((float)Math.toRadians(-90.0f - (360.0f - _deadAngle) * .5f));
		g.arc(0.0f,0.0f,r * 2,r * 2,(float)Math.toRadians(0.0f),(float)Math.toRadians(360.0f - _deadAngle));
		g.popMatrix();
			
		// draw circle
        g.noFill();
        g.strokeWeight(2.0f);
        g.stroke(255,255,255,170);
        g.ellipse(0,0,r * 2,r * 2);

        // middle marker
        g.line(0,-r,0,-r * .7f);

		// ticks
		g.strokeWeight(1.0f);
        g.pushMatrix();
		for(int i=0;i<12;i++)
		{
		  g.rotate(2.0f * (float)Math.PI / 12.0f);
		  g.line(0,-r,0,-r * .9f);
		}		
        g.popMatrix();

        // draw pos
        float angle = _resQ * (_range - _pos);

        // draw the current pos
        g.strokeWeight(3.0f);
        g.stroke(240,10,3,150);
        g.pushMatrix();
        g.rotate(angle);
        g.line(0,r,0,0);
        g.popMatrix();

        // limits
        float limitCWAngle = _resQ * (_range - _limitCW);
        float limitCCWAngle = _resQ * (_range - _limitCCW);
        
		g.strokeWeight(1.0f);
        g.stroke(10,255,3,100);

        g.pushMatrix();
        g.rotate(limitCWAngle);
        g.line(0,r * .9f,0,r * .4f);
        g.popMatrix();

        g.pushMatrix();
        g.rotate(limitCCWAngle);
        g.line(0,r * .9f,0,r * .4f);
        g.popMatrix();

        int posX = (int)-r;
        int posY = (int)r + 20;

        DecimalFormat df = new DecimalFormat("0.00");
		
		g.stroke(255);
		g.fill(255);
        g.text("id: " + _id, posX,posY);

		posY += 20;
        g.text("angle: " + Float.parseFloat(df.format(Math.toDegrees(_resQ * _pos))), posX,posY);

		posY += 20;
        g.text("pos: " + _pos, posX, posY);

		if(_vizType == VIZ_FULL)
		{
		  posY += 20;
		  g.text("speed: " + _speed, posX, posY);
		  
		  posY += 20;
		  g.text("load: " + _load, posX, posY);
		  
		  posY += 20;
		  g.text("voltage: " + _voltage, posX, posY);
		  
		  posY += 20;
		  g.text("temp: " + _temp, posX, posY);
       }

		g.popStyle();

	}


}
