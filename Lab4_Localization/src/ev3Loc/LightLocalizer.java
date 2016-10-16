package ev3Loc;
import lejos.robotics.SampleProvider;

public class LightLocalizer {
	private Odometer odo;
	private SampleProvider colorSensor;
	private float[] colorData;	
	public Navigation nav;
	private float Rot_Speed = 120;
	private double distSensor = 11.2;//Distance from the center of rotation to the sensor
	
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
		
		int gridLines = 0;
		double x,y;
		
		float intensity, prev, delta;
		
		prev = colorData[0];
		
		while (gridLines < 4){
			
			nav.setSpeeds(Rot_Speed, Rot_Speed);
			intensity = colorData[0];
			delta = intensity - prev;
			
			if (Math.abs(delta) > 0.1){
				
				if (delta > 0){
					
					angle [gridLines++] = odo.getAng();
					
				}
				
			}
			prev = intensity;
			
		}
		
		x = Math.abs(angle[2] - angle[0]);
	    y = Math.abs(angle[3] - angle[1]);
		double xpos = -distSensor * Math.cos(y / 2);
		double ypos = -distSensor * Math.cos(x / 2);
		double dtheta = 90-(ypos-180)+((ypos)/2);
		odo.setPosition(new double [] {xpos, ypos, dtheta}, new boolean [] {true, true, true});
		try{
			Thread.sleep(2000);
			
		}
		catch(Exception e){
			
		}
		
		nav.travelTo(0, 0);
		nav.turnTo(0, true);
		
	}

}
