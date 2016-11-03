package ObjectDetection;

import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.robotics.Color;
import lejos.robotics.SampleProvider;
import lejos.utility.Delay;

public class lightObjectDetect{
	private Odometer odo;
	private SampleProvider colorSensor;
	private float[] colorData;	
	public Navigation nav;// initialize navigation object
	
	private EV3LargeRegulatedMotor topleftMotor, toprightMotor;
	private SampleProvider usSensor;
	private float[] usData;
	private float r,g,b;
	
	public lightObjectDetect(Odometer odo, SampleProvider colorSensor, float[] colorData,
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
		System.out.println("go");
		nav.setSpeeds(0, 0);
		double prevX = 0 , prevY = 0;
		odo.setPosition(new double [] {0, 0, 0}, new boolean [] {true, true, true});
		
		int checker = -1;
	
		if (checkDetection(90) == -1){
			nav.turnTo(45, true);
			nav.setSpeeds(200, 200);
			Delay.msDelay(2000);
			nav.setSpeeds(0, 0);
		}else if (checker == 1){
			return;
		}else{
			while (checker == 2){
				
				checker = checkDetection(360);
			}
		}
		
		while (checkDetection(360) != 1){
			if (getFilteredData() > 37){
				prevX = odo.getX();
				prevY = odo.getY();
				nav.setSpeeds(200, 200);
				Delay.msDelay(2000);
			}
			if ( odo.getX() > 65 || odo.getY() > 65  ){
				
				nav.turnTo(odo.getAng() + 90, true);
				nav.setSpeeds(200, 200);
				Delay.msDelay(1000);
			}
			nav.setSpeeds(0, 0);
		
		}
		return;
		
		
	}
	
	
	public int checkDetection(int rotation){
		int count = 0;
		int turn360 = 0;
		double prevangle = 0;
		nav.setSpeeds(0, 0);
		boolean objectDetected = false;
		
		while (turn360 < rotation){
			nav.turnTo(turn360, true);
			
			if (getFilteredData() <= 30){
				nav.setSpeeds(0, 0);
				objectDetected = true;
				break;
			
			}
			turn360 += 20;
		}
		while (getFilteredData() >= 10 && objectDetected){
			
			nav.setSpeeds(150, 150);
			Delay.msDelay(1000);
			
		}
		nav.setSpeeds(0, 0);
		
		colorSensor.fetchSample(colorData, 0);
		r = colorData[0];
		g = colorData[1];
		b = colorData[2];
		if (r < b && getFilteredData() < 10 && count == 0){
			Sound.beep();
			Sound.beep();
			count++;
		
		if (count == 1){ 
			topleftMotor.rotate(-180);
			toprightMotor.rotate(-180);
		}
		
		}else if(r > b && getFilteredData() <= 25){
		
			nav.setSpeeds(-100,-100); // move backwards 
			Delay.msDelay(4000); // let the robot travel backwards for a specified amount of time found experimentally 
			prevangle = odo.getAng();
			nav.turnTo(odo.getAng() + 90, true);
			
			if (getFilteredData() <= 25 ){
		
				nav.turnTo(odo.getAng()  + 90, true);
				prevangle = odo.getAng();
			}
			
			nav.setSpeeds(200, 200);
			Delay.msDelay(2000);
			nav.turnTo(prevangle, true);
			nav.setSpeeds(0, 0);
			return 0; //error code for wall
	}
	
	
		if (count == 1){
			nav.travelTo(90,60);
			return 1; // code for success
		}else { 
			return -1; // code for nothing detected
		}
	}
	
	public void floatDetection(){
		while(true){
		colorSensor.fetchSample(colorData, 0);
		r = colorData[0];
		g = colorData[1];
		b = colorData[2];
		
		if (r < b && getFilteredData() < 10){
			Sound.beep();
			Sound.beep();
			System.out.println("Object detected: blue block!");
			Delay.msDelay(2000);
		}else if(r > b && getFilteredData() <= 10){
			System.out.println("Object detected: Wooden Block");
			Delay.msDelay(2000);
		}
	}
		
	}
	
	private float getFilteredData() { // filter the data 
		usSensor.fetchSample(usData, 0);
		float distance = usData[0] * 100;
		
		if (distance >= 50){
			distance = 50;
		}
		return distance;
	}

}
