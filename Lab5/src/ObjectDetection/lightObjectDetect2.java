package ObjectDetection;

import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.robotics.Color;
import lejos.robotics.SampleProvider;
import lejos.utility.Delay;

public class lightObjectDetect2{
	private Odometer odo;
	private SampleProvider colorSensor;
	private float[] colorData;	
	public Navigation nav;// initialize navigation object
	
	private EV3LargeRegulatedMotor topleftMotor, toprightMotor;
	private SampleProvider usSensor;
	private float[] usData;
	private float r,g,b;
	
	public lightObjectDetect2(Odometer odo, SampleProvider colorSensor, float[] colorData,
			EV3LargeRegulatedMotor topleftMotor,EV3LargeRegulatedMotor toprightMotor,
			SampleProvider usSensor, float[] usData) {
		this.odo = odo;
		this.colorSensor = colorSensor;
		this.colorData = colorData;
		nav = new Navigation(this.odo);
		this.topleftMotor = topleftMotor;
		this.toprightMotor = toprightMotor;
		this.usSensor = usSensor;
		this.usData = usData;
	}
	
	

	public void objectDetect(){
		
		//odo.setPosition(new double [] {0, 0, 0}, new boolean [] {true, true, true});
		Delay.msDelay(1000);
		while(checkDetection() != 1){
			
		}
		
		
	}
	
	
	public int checkDetection(){
		
		int count = 0;
		colorSensor.fetchSample(colorData, 0);
		r = colorData[0];
		g = colorData[1];
		b = colorData[2];
		double turn = 0;
		
		while (getFilteredData() >= 8){

			nav.setSpeeds(200, 200);
			
			if (odo.getX() >= 80 || odo.getY() >= 75){
				
				nav.setSpeeds(-100, -100);
				Delay.msDelay(2500);
				nav.setSpeeds(0, 0);
				turn = odo.getAng() + 90;
				if (turn >= 360){
					turn = turn - 360;
				}
				nav.turnTo(turn, true);	
			}
		}
		
		if (r < b && getFilteredData() < 8 && count == 0){
			nav.setSpeeds(0, 0);
			Sound.beep();
			count++;
		
		if (count == 1){ 
			topleftMotor.rotate(-180);
			toprightMotor.rotate(-180);
		}
		
		}else if(r > b && getFilteredData() <= 8){
		
			Sound.beep();
			Sound.beep();
			nav.setSpeeds(-100,-100); // move backwards 
			Delay.msDelay(2000); // let the robot travel backwards for a specified amount of time found experimentally 
			nav.setSpeeds(0, 0);
			turn = odo.getAng() + 90;
			if (turn >= 360){
				turn = turn - 360;
			}
			nav.turnTo(turn, true);	
			
			}
	
	
		if (count == 1){
			nav.travelTo(90,60);
			Sound.beep();
			Sound.beep();
			Sound.beep();
			return 1;// code for success
		}
		//nav.turnTo(odo.getAng() + 90, true);
		nav.setSpeeds(0, 0);
		return 0;
	}
	
	public void floatDetection(){
		while(true){
		colorSensor.fetchSample(colorData, 0);
		r = colorData[0];
		g = colorData[1];
		b = colorData[2];
		
		if (r < b && getFilteredData() < 10){
			Sound.beep();
			System.out.println("Object detected: blue block!");
			Delay.msDelay(2000);
		}else if(r > b && getFilteredData() <= 10){
			System.out.println("Object detected: Wooden Block");
			Sound.beep();
			Sound.beep();
			Delay.msDelay(2000);
		}
	}
		
	}
	
	private float getFilteredData() { // filter the data 
		usSensor.fetchSample(usData, 0);
		float distance = usData[0] * 100;
		
		if (distance >= 100){
			distance = 100;
		}
		return distance;
	}

}
