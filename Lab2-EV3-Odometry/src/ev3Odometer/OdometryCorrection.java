/* 
 * OdometryCorrection.java
 */
package ev3Odometer;

import lejos.hardware.Sound;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;

public class OdometryCorrection extends Thread {
	private static final long CORRECTION_PERIOD = 10;
	
	//public static Port sensorPort = LocalEV3.get().getPort("S1");   
	public static Port portColor = LocalEV3.get().getPort("S1");
	public static SensorModes myColor = new EV3ColorSensor(portColor);
	public static SampleProvider myColorSample = myColor.getMode("Red");
	public static float[] sampleColor = new float[myColor.sampleSize()];
	public static int numSamples = 0;
	private Odometer odometer;
	public static int count = 0;
	boolean testLine = false;
	// constructor
	public OdometryCorrection(Odometer odometer) {
		this.odometer = odometer;
	}

	// run method (required for Thread)
	public void run() {
		long correctionStart, correctionEnd = 0;
		double tempDist = 0; //temporary distance for y axis
		double tempXDist = 0; // temporary distance for x axis
		double initDist = 0; // used to measure the initial x-distance
		
		while (true) {
			correctionStart = System.currentTimeMillis();
	
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////		
			myColorSample.fetchSample(sampleColor,0);
			
			// If a black Line is detected make boolean true
			
			if ( (sampleColor[0]*1000) < 200) {
				testLine = true;
				
				
			}
			
			// If the line is there increment the amount of lines crossed and make the correction
			
			if ((sampleColor[0]*1000) > 200 && testLine == true){
				
				testLine = false;
				count++;
		////////////////////////////////////Y-CORRECTION////////////////////////////////////////
				
				if (count == 1){ // gets first reading of y 
					
					tempDist =  odometer.getY();
				}
				
				if ( count > 1  && count <= 3){ // adds correct distance when robot passes 2 and 3rd line
					
					tempDist += 30.48;
					odometer.setY(tempDist); // set odometer reading
					
					
				}else if (count > 7  && count <= 9){ // corrects distance at the 8th and 9th line 
					
				
						odometer.setY(tempDist - 30.48);
						tempDist -= 30.48;
						
					
				}
				
				if ( count == 7){ // updates temp distance at the 7th line and use this distance to reduce error
					
					tempDist = odometer.getY();
					
				}else if ( count == 10 ){ // same as when equal to 7 
					
					tempDist = odometer.getY();
					
				}
				
		////////////////////////////////END-Y-CORRECTION////////////////////////////////////////
			
		////////////////////////////////////X-CORRECTION////////////////////////////////////////
				
				if (count == 4 && odometer.getX() < 15){ // at the 4th line, if the odometer reads below 15cm 
														 //assume it is right else assume reading is false and corrects
					tempXDist = odometer.getX();
					initDist = tempXDist;
					
				}else if (count ==  4 ){
					
					tempXDist = 15.24;
					initDist = 15.24;
				}

				if ( count > 4  && count <= 6){ // updates reading at 5th and 6th line
					
					tempXDist += 30.48;
					odometer.setX(tempXDist);


				}else if (count > 10  && count <= 12){ // updates reading at 11 and 15th line

					tempXDist -= 30.48;	
					odometer.setX(tempXDist);
	

				}
				
				if ( count == 10 && (odometer.getX() - 15.00) < (tempXDist - initDist)){ // checks to see if odometer reading is within error and if it is leave 
																						// the reading alone and if not correct it based on initial distance 
					
					tempXDist = tempXDist - initDist;
					odometer.setX(tempXDist);
					
				}else if ( count == 10){
					
					tempXDist = odometer.getX();
				}
				if ( count == 6 && odometer.getX() < 75){ // same as above, checks to see if reading is what we expect and if it isn't correct it
					
					tempXDist = odometer.getX();
					
				}


////////////////////////////////END-X-CORRECTION////////////////////////////////////////
				
				//Sound.beep();
			}
			
			
			numSamples++;
				
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// this ensure the odometry correction occurs only once every period
			correctionEnd = System.currentTimeMillis();
			
			
			if (correctionEnd - correctionStart < CORRECTION_PERIOD) {
				try {
					Thread.sleep(CORRECTION_PERIOD
							- (correctionEnd - correctionStart));
				} catch (InterruptedException e) {
					// there is nothing to be done here because it is not
					// expected that the odometry correction will be
					// interrupted by another thread
				}
			}
		}
	}
}