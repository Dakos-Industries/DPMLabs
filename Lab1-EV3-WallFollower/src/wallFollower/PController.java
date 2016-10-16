package wallFollower;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
//Spiros Mavroidakos 260689391, David Ugo 260605697
public class PController implements UltrasonicController {
	
	private final int bandCenter, bandwidth;
	private final int motorStraight = 125, FILTER_OUT = 20;
	private EV3LargeRegulatedMotor leftMotor, rightMotor;
	private int distance;
	private int filterControl;
	
	public PController(EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor,
					   int bandCenter, int bandwidth) {
		//Default Constructor
		this.bandCenter = bandCenter - 5;
		this.bandwidth = bandwidth;
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		leftMotor.setSpeed(motorStraight);					// Initalize motor rolling forward
		rightMotor.setSpeed(motorStraight);
		leftMotor.forward();
		rightMotor.forward();
		filterControl = 0;
	}
	
	@Override
	public void processUSData(int distance) {
		
				// rudimentary filter - toss out invalid samples corresponding to null
				// signal.
				// (n.b. this was not included in the Bang-bang controller, but easily
				// could have).
				//
				if (distance >= 255 && filterControl < FILTER_OUT) {
					// bad value, do not set the distance var, however do increment the
					// filter value
					filterControl++;
				} else if (distance >= 255) {
					// We have repeated large values, so there must actually be nothing
					// there: leave the distance alone
					this.distance = distance;
				} else {
					// distance went below 255: reset filter and leave
					// distance alone.
					filterControl = 0;
					this.distance = distance;
				}
		
		// TODO: process a movement based on the us distance passed in (P style)	
		
		int distError = bandCenter - this.distance;	// Compute error
		int newSpeedLeft = motorStraight +  Math.abs( (15 * distError));	//new speed value for the left wheel
		int newSpeedRight = motorStraight + Math.abs(distError * 2);		// new speed value for the right wheel
		if (newSpeedRight >= 400) newSpeedRight = 400;
		
		if (Math.abs(distError) <= bandwidth) {		// Within limits, same speed
			leftMotor.setSpeed(motorStraight);		// Start moving forward
			rightMotor.setSpeed(motorStraight);
			leftMotor.forward();
			rightMotor.forward();				
		}
		
		else if (distError > 0) {					// Too close to the wall
			
			leftMotor.setSpeed(newSpeedLeft);	//sets new speed
			rightMotor.setSpeed(0);				//sets new speed
			leftMotor.forward();
			rightMotor.forward();		
			
		}
		
		else if (distError < 0) {
			leftMotor.setSpeed(motorStraight); //keeps wheel moving straight
			rightMotor.setSpeed(newSpeedRight); // sets new speed
			leftMotor.forward();
			rightMotor.forward();								
		}	
	}

	
	@Override
	public int readUSDistance() {
		return this.distance;
	}

}
