/**
 * @author Spiros Mavoridakos
 * @version V1
 */
package strategyDesignPattern;
import java.util.*;

public class Demo {
	private static String date;
	private static String customerName;
	private static double purchaseAmount;
	
	public static void main(String args[]){
		double flatRate, perDollar, threshold;
		System.out.println("Please enter the date of the purchase: ");
		Scanner input = new Scanner(System.in);
		date = input.nextLine();
		System.out.println("Please enter the name of the customer: ");
		customerName = input.nextLine();
		System.out.println("Please enter the purchase amount: ");
		purchaseAmount = input.nextDouble();
		
		Context context = new Context(new FlatRatePoints());
		System.out.println("Date: " + date + "\nCustomer Name: " + customerName+ "\nPurchase Amount: " + purchaseAmount);
		flatRate = context.executePurchase(purchaseAmount);
		
		context = new Context(new PerDollarPoints());
		perDollar = context.executePurchase(purchaseAmount);
		
		context = new Context(new ThresholdPoints());
		threshold = context.executePurchase(purchaseAmount);
		
		if (flatRate >= perDollar && flatRate >= threshold){
			System.out.println("Points: " + flatRate);
		}else if (perDollar >= flatRate && perDollar >= threshold){
			System.out.println("Points: " + perDollar);
		}else if (threshold >= flatRate && threshold >= perDollar){
			System.out.println("Points: " + threshold);
		}
		
	}

}
