/**
 * @author Spiros Mavoridakos
 * @version V1
 */
package strategyDesignPattern;

public class ThresholdPoints implements Purchase{
	private double points = 0;
	@Override
	public double doPurchase(double purchaseAmount) {
		if (purchaseAmount < 100){
			return points;
		}else{
			points = (purchaseAmount - 99) * 2;
			if (points > 500){
				return 500.00;
			}else{
				return points;
			}
			
		}
	}

}
