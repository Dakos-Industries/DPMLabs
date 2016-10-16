package wallFollower;
import lejos.hardware.motor.*;
//Spiros Mavroidakos 260689391, David Ugo 260605697
public class BangBangController implements UltrasonicController{
	private final int bandCenter, bandwidth;
	private final int motorLow, motorHigh;
	private int distance;
	private EV3LargeRegulatedMotor leftMotor, rightMotor;
	private final int FILTER_OUT = 20; private int filterControl;
	
	public BangBangController(EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor,
							  int bandCenter, int bandwidth, int motorLow, int motorHigh) {
		//Default Constructor
		this.bandCenter = bandCenter + 3;
		this.bandwidth = bandwidth;
		this.motorLow = motorLow;
		this.motorHigh = motorHigh;
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		leftMotor.setSpeed(motorHigh);				// Start robot moving forward
		rightMotor.setSpeed(motorHigh);
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
		
		// TODO: process a movement based on the us distance passed in (BANG-BANG style)
		
		int distError = bandCenter - this.distance;			// Compute error
		
		if (Math.abs(distError) <= bandwidth) {	// Within limits, same speed
			leftMotor.setSpeed(motorHigh);		// Start moving forward
			rightMotor.setSpeed(motorHigh);
			leftMotor.forward();
			rightMotor.forward();				
		}
		
		else if (distError > 0) {				// Too close to the wall
			leftMotor.setSpeed(motorHigh);
			rightMotor.setSpeed(motorLow);
			leftMotor.forward();
			rightMotor.forward();				
		}
		
		else if (distError < 0) {
			leftMotor.setSpeed(motorLow);
			rightMotor.setSpeed(motorHigh);
			leftMotor.forward();
			rightMotor.forward();								
		}		
	}
	/////////////////////////////////////////////////////////////////////////////

	@Override
	public int readUSDistance() {
		return this.distance;
	}
}
