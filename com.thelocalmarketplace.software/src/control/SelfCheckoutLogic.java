package control;

import com.jjjwelectronics.printer.ReceiptPrinterBronze;
import com.jjjwelectronics.printer.ReceiptPrinterGold;
import com.jjjwelectronics.printer.ReceiptPrinterSilver;
import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.hardware.external.CardIssuer;

import attendant.Maintain;
import payment.CoinController;
import payment.PaymentCardController;
import payment.BanknoteController;
import item.AddItemBarcode;
import item.PrintController;

/**
 * This class links all the controllers to an instance of the SelfCheckoutStations.
 * All the controllers are registered to the hardware of the station as listeners
 * and respond to events accordingly.
 */

public class SelfCheckoutLogic {
	
	// Software simulation of SelfCheckoutStation hardware
	public AbstractSelfCheckoutStation station;
	
	// Controller to manage payments
	public CoinController coinController;
	
	// Controller to manage banknote payments
	public BanknoteController banknoteController;
	
	// Controller to manage credit card payments
	public PaymentCardController creditController;
	
	// Controller to manage handheld barcode scans
	public AddItemBarcode barcodeController;
		
	// Controller to manage session
	public SessionController session;
	
	// Controller to manage scale weight
	public WeightController weightController;
	
	// Controller to manage printing
	public PrintController printController;
	
	//	Card Issuer
	public CardIssuer cardIssuer;
	
	//Controller for Maintain
	public Maintain maintain;
	
	//Gold Printer
	private ReceiptPrinterGold receiptPrinterGold;
	//Bronze Printer
	private ReceiptPrinterBronze receiptPrinterBronze;
	//Silver Printer
	private ReceiptPrinterSilver ReceiptPrinterSilver;
	
	
	/**
	 * This method links our software to our hardware (simulation) and initializes 
	 * all the controllers that we need.
	 * @param scs The AbstractSelfCheckoutStation hardware we need to link it to 
	 * (currently simulated when a customer presses "Start Session")
	 * @return the instance of the software logic
	 */
	
	public static SelfCheckoutLogic installOn(AbstractSelfCheckoutStation scs) {
		return new SelfCheckoutLogic(scs);
	}
	
	/**
	 * Every customer session will have one logic instance.
	 * @param scs The SelfCheckoutStation hardware we need to link it to 
	 * (currently simulated when a customer presses "Start Session")
	 */
	public SelfCheckoutLogic(AbstractSelfCheckoutStation scs) {
		
		
		
		station = scs;
		session = new SessionController(this);
		session.start();
		
		//Issues with this logic. Cant seem to use unless every instances works
			//SOLUTION: wrap every instance of a controller with a try catch OR fix everything
		
		maintain = new Maintain(scs);	
		barcodeController = new AddItemBarcode(session, scs);
		weightController = new WeightController(session, scs);
		banknoteController = new BanknoteController(session, scs);
		coinController = new CoinController(session, scs);
		creditController = new PaymentCardController(session, scs, cardIssuer);
		printController = new PrintController(session, scs);
		
		// Disable banknote insertion slot so customer does not insert banknotes
		// before going to the payment page.
		scs.getBanknoteInput().disable();
		// Same for coin insertion slot
		scs.getCoinSlot().disable();
		// Same for debit/credit card reader
		scs.getCardReader().disable();
		
	}
}
