/**
 * @author Spiros Mavoridakos
 * @version V1
 */
package strategyDesignPattern;

public class PerDollarPoints implements Purchase{
	private double points = 0;
	@Override
	public double doPurchase(double purchaseAmount) {
		points = purchaseAmount;
		if (points >= 500){
			return 500.00;
		}else{
			return points;
		}
	}

}
