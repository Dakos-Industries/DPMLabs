/**
 * @author Spiros Mavoridakos
 * @version V1
 */
package strategyDesignPattern;

public class FlatRatePoints implements Purchase{
	private final double points = 25.00;
	@Override
	public  double doPurchase(double purchaseAmount) {
		if (purchaseAmount == 0){
			return 0;
		}
		return points;
	}
}
