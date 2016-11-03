/**
 * @author Spiros Mavoridakos
 * @version V1
 */
package strategyDesignPattern;

public class Context {
	private Purchase purchase;
	
	public Context(Purchase purchase){
		this.purchase = purchase;
	}
	
	public double executePurchase(double purchaseAmount){
		return purchase.doPurchase(purchaseAmount);
	}

}
