package ev3Loc;


import lejos.hardware.Sound;
import lejos.robotics.SampleProvider;

public class USLocalizer2 {
	public enum LocalizationType { FALLING_EDGE, RISING_EDGE };
	
	public float ROTATION_SPEED = 100;
	
	//Variables for 45 and 225 degrees
	private static double newHead;
	private static double actualHead;
	
	public final int distError = 30;
	public final int rotSpeedError = 20;
	
	private Odometer odo;
	private SampleProvider usSensor;
	private float[] usData;
	private LocalizationType locType;
	public Navigation nav;
	
	public USLocalizer(Odometer odo,  SampleProvider usSensor, float[] usData, LocalizationType locType) {
		this.odo = odo;
		this.usSensor = usSensor;
		this.usData = usData;
		this.locType = locType;
		nav = new Navigation(this.odo);
	}
	
	
	
	public void doLocalization() {
		
		//nav = new Navigation(odo);
		
		double [] pos = new double [3];
		double angleA = 0;
		double angleB = 0;
		double  dtheta;
		
		boolean falling = false;
		boolean	rising = false;
		boolean noWall = false;
		
		if (locType == LocalizationType.FALLING_EDGE) {
			
			// rotate the robot until it sees no wall
			while(!noWall){
				
				rightTurn();
				
				if (getFilteredData() == 50){
					
					noWall = true;
				}
			
			}
			
			
			// keep rotating until the robot sees a wall, then latch the angle
			
			while (!falling){
				
				rightTurn();
				if (getFilteredData() < 50 - rotSpeedError){
					
					falling = true;
					
					
					noWall = false;
					
					
					angleA = odo.getAng();
					Sound.beep();
				}
				
			}
			
			
			
			
			
			// switch direction and wait until it sees no wall
			while(!noWall){
				
				leftTurn();
				
				if (getFilteredData() == 50){
					
					noWall = true;
					
					falling = false;
					
				}
				
			}
			
			// keep rotating until the robot sees a wall, then latch the angle
			
			while (!falling){
				
				leftTurn();
				
				if(getFilteredData() < 50 - rotSpeedError){
					
					angleB = (odo.getAng());
					Sound.beep();
					falling = true;
					
				}
				
				
				
				
			}
			
			nav.setSpeeds(0, 0);
			
			// angleA is clockwise from angleB, so assume the average of the
			// angles to the right of angleB is 45 degrees past 'north'
			
			newHead = calcHeading(angleA,angleB);
			actualHead = newHead + (odo.getAng());
			
			// update the odometer position (example to follow:)
			
			odo.setPosition(new double [] {0.0, 0.0, actualHead}, new boolean [] {true, true, true});
			
			nav.setSpeeds(ROTATION_SPEED,ROTATION_SPEED);
			
			nav.turnTo(0,true);
			
			System.out.println("     "+actualHead);
		} else {
			/*
			 * The robot should turn until it sees the wall, then look for the
			 * "rising edges:" the points where it no longer sees the wall.
			 * This is very similar to the FALLING_EDGE routine, but the robot
			 * will face toward the wall for most of it.
			 */
			noWall = true;
			// rotate the robot until it sees no wall
						while(noWall){
							
							rightTurn();
							
							if (getFilteredData() < 50 - rotSpeedError){
								
								noWall = false;
							}
						
						}
						
						while(!rising){
							rightTurn();
							
							if (getFilteredData() == 50){
								
								rising = true;
								noWall = true;
								angleB = (odo.getAng());
								Sound.beep();
							}
						}
						
						
						// keep switches direction
						
						while (noWall){
							
							leftTurn();
							if (getFilteredData() < 50 - rotSpeedError){
								
								rising = false;
								
								
								noWall = false;
								
								Sound.beep();
							}
							
						}
						
						
						
						
						// switch direction and wait until it sees no wall
						while(!rising){
							
							leftTurn();
							
							if (getFilteredData() == 50){
								
								angleA = (odo.getAng());
								rising = true;
								noWall = true;
								Sound.beep();
								
							}
							
						}
						
						
						nav.setSpeeds(0, 0);
						
						// angleA is clockwise from angleB, so assume the average of the
						// angles to the right of angleB is 45 degrees past 'north'
						
						newHead = calcHeading(angleA,angleB);
						actualHead = newHead + (odo.getAng());
						
						// update the odometer position (example to follow:)
						odo.setPosition(new double [] {0.0, 0.0, actualHead}, new boolean [] {true, true, true});
						nav.setSpeeds(ROTATION_SPEED,ROTATION_SPEED);
						nav.turnTo(0,true);
						
			
		}
	}
	
	private static double calcHeading(double angleA, double angleB){
			
		double heading;
		
		if (angleA < angleB){
			
			heading = 45 - (angleA + angleB) /2 ;
			
		}else {
			
			heading = 225 - (angleA + angleB) /2 ;
			
		}
			return heading;
		
	}
	
	private  void leftTurn(){
		
		nav.setSpeeds(-ROTATION_SPEED, ROTATION_SPEED);
		
	}
	private  void rightTurn(){
		
		nav.setSpeeds(ROTATION_SPEED, -ROTATION_SPEED);
		
	}
	private float getFilteredData() {
		usSensor.fetchSample(usData, 0);
		float distance = usData[0] * 100;	
		
		if (distance > 50){
		
				distance = 50;
		}
			
		return distance;
		
	}

}
