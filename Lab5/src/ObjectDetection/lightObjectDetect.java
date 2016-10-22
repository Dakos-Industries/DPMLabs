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
		int count = 0;
		odo.setPosition(new double [] {0, 0, 0}, new boolean [] {true, true, true});
		nav.turnTo(45, true);
		nav.setSpeeds(200,200);
		Delay.msDelay(2000);
		nav.setSpeeds(0, 0);
		float r,g,b;
		
		while (true){
			nav.setSpeeds(100,100);
			Delay.msDelay(1000);
			check();
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
				rightTurn();
				Delay.msDelay(2000);
				nav.setSpeeds(200, 200);
				Delay.msDelay(3000);
				nav.setSpeeds(0, 0);
		}
		
		
		if (count == 1){
			nav.travelTo(80,60);
			break;
			
		}
	
		
		}
		
	}
	
	public void check(){
		int turn360 = 0;
		nav.setSpeeds(0, 0);
		while (turn360 < 360){
			nav.turnTo(turn360, true);
			
			if (getFilteredData() <= 32){
				System.out.println("30");
				nav.setSpeeds(0, 0);
				return;
			
			}
			turn360 += 15;
		}
		nav.turnTo(45,true);
	}
	
	private  void leftTurn(){ // rotate left
		nav.setSpeeds(-120, 120);
	}
	private  void rightTurn(){ // rotate left
		nav.setSpeeds(120, -120);
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
