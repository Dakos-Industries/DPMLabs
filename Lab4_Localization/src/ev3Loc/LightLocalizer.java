package ev3Loc;
import lejos.hardware.Button;
import lejos.robotics.SampleProvider;
import lejos.utility.Delay;

public class LightLocalizer {
	private Odometer odo;
	private SampleProvider colorSensor;
	private float[] colorData;	
	public Navigation nav;
	private float Rot_Speed = 60;
	private double distSensor = 15.5;//Distance from the center of rotation to the sensor
	
	public LightLocalizer(Odometer odo, SampleProvider colorSensor, float[] colorData) {
		this.odo = odo;
		this.colorSensor = colorSensor;
		this.colorData = colorData;
		nav = new Navigation(this.odo);
	}
	
	public void doLocalization() {
		// drive to location listed in tutorial
		// start rotating and clock all 4 gridlines
		// do trig to compute (0,0) and 0 degrees
		// when done travel to (0,0) and turn to 0 degrees
		
		
		double angle [] = new double [4];
		
		
		double x,y;
		float intensity;
		colorSensor.fetchSample(colorData, 0);
		
		
		while(Button.waitForAnyPress() != Button.ID_ESCAPE){}
		boolean cont = false;
		nav.turnTo(45,true);
		while(true){
			
			
			nav.setSpeeds(100,100);
			
			colorSensor.fetchSample(colorData, 0);
			intensity = colorData[0];
			
			if (intensity  < 0.60){
				
				//cont = true;
				break;
				
			}
			
			
		}
		
		nav.setSpeeds(0,0);
		
		nav.setSpeeds(-100,-100);
		Delay.msDelay(4000);
		
		int gridLines = 0;
		
		while (gridLines < 4 ){
			
			colorSensor.fetchSample(colorData, 0);
			nav.setSpeeds(Rot_Speed, -Rot_Speed);
			intensity = colorData[0];
			
			
			if (intensity > 0.60){
				
				cont = true;
			}
			
			if (intensity  < 0.60 && cont){
		
				angle [gridLines] = odo.getAng();
				gridLines++;
				if (gridLines < 4)
				{
					Delay.msDelay(750);
				}
				cont = false;
				
			}
			
		}
		nav.setSpeeds(0, 0);
		
		x = Math.abs(angle[2] - angle[0]);
	    y = Math.abs(angle[3] - angle[1]);
		double xpos = -distSensor * Math.cos(Math.toRadians(y) / 2);
		double ypos = -distSensor * Math.cos(Math.toRadians(x) / 2);
		double dtheta = -(angle[3]-180)-((y)/2);
		odo.setPosition(new double [] {xpos, ypos, odo.getAng() + dtheta}, new boolean [] {true, true, true});
		
		
		
		nav.travelTo(0, 0);
		nav.turnTo(0, true);
		
	}

}
