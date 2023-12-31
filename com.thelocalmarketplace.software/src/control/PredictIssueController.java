/*Group P3-6***
Andy Tang 10139121
Ayman Inayatali Momin 30192494
Darpal Patel 30088795
Dylan Dizon 30173525
Ellen Bowie 30191922
Emil Huseynov 30171501
Ishita Udasi 30170034
Jason Very 30222040
Jesse Leinan 00335214
Joel Parker 30021079
Kear Sang Heng 30087289
Khadeeja Abbas 30180776
Kian Sieppert 30134666
Michelle Le 30145965
Raja Muhammed Omar 30159575
Sean Gilluley 30143052
Shenuk Perera 30086618
Simrat Virk 30000516
Sina Salahshour 30177165
Tristan Van Decker 30160634
Usharab Khan 30157960
YiPing Zhang 30127823*/
package control;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import com.tdc.banknote.IBanknoteDispenser;
import com.tdc.coin.ICoinDispenser;
import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;

import attendant.NotifyAttendantScreen;

/**
 * This class predicts any issue that might occur within the self checkout
 * station
 * 
 * Issues that this controller will predict: 1. Low Ink 2. Low Paper 3. Low
 * Coins 4. Low Banknotes 5. Full Coins 6. Full Banknotes
 */
public class PredictIssueController {

	/**
	 * This declares all the variables and initializes only most of them.
	 */
	private AbstractSelfCheckoutStation scs;
	private ArrayList<String> listOfIssues = new ArrayList<>();
	private NotifyAttendantScreen attendantGUI = new NotifyAttendantScreen();
	private boolean lowInkIssueExists = false;
	private boolean lowPaperIssueExists = false;;
	private boolean lowCoinIssueExists = false;
	private boolean lowBanknoteIssueExists = false;
	private boolean fullCoinIssueExists = false;;
	private boolean fullBanknoteIssueExists = false;
	private int linesUsed = 0;

	public PredictIssueController(SessionController sessionController, AbstractSelfCheckoutStation scs, int linesUsed) {

		/**
		 * This initializes the self checkout station
		 */
		this.scs = scs;
		this.linesUsed += linesUsed;

		/**
		 * This if statement is important to ensure that the software is only checking
		 * for issues if the session is currently not occurring
		 */
		if (sessionController.isStarted() == false) {
			predictLowInk();
			predictLowPaper();
			predictLowCoin();
			predictLowBanknote();
			predictFullCoin();
			predictFullBanknote();

			/**
			 * This updates the Attendant Station's GUI for new issues within the self
			 * checkout station
			 */
			String text = "";
			for (int i = 0; i < listOfIssues.size(); i++) {
				text += "<html>" + listOfIssues.get(i) + "<br/>";
			}
			attendantGUI.update(text);
		}
	}

	/**
	 * This method predicts low ink within the station's printer. It will not check
	 * this for the bronze printer.
	 * 
	 * This also assumes that ink is always refilled to the maximum every refill
	 * session
	 */
	public void predictLowInk() {
		if (linesUsed * 60 >= 1 << 20 - 500) {
			lowInkIssueExists = true;
			listOfIssues.add("Printer is almost out of ink");
		}
	} 

	/**
	 * This method predicts low paper within the station's printer. It will not
	 * check this for the bronze printer
	 */
	public void predictLowPaper() {
		if (linesUsed >= 1020) {
			lowPaperIssueExists = true;
			listOfIssues.add("Printer is almost out of paper");
		}
	}

	/**
	 * This method predicts low coin within the station's coin dispenser
	 */
	public void predictLowCoin() {
		Iterator<Map.Entry<BigDecimal, ICoinDispenser>> itr = scs.getCoinDispensers().entrySet().iterator();
		while (itr.hasNext()) {
			Map.Entry<BigDecimal, ICoinDispenser> coin = itr.next();
			if (coin.getValue().size() <= 5) {
				lowCoinIssueExists = true;
			}
		}
		if (lowCoinIssueExists == true) {
			listOfIssues.add("One or More of the Coin Dispensers is Almost Empty");

		}
	}

	/**
	 * This method predicts low banknote within the station's banknote dispenser
	 */
	public void predictLowBanknote() {
		Iterator<Map.Entry<BigDecimal, IBanknoteDispenser>> itr = scs.getBanknoteDispensers().entrySet().iterator();
		while (itr.hasNext()) {
			Map.Entry<BigDecimal, IBanknoteDispenser> banknote = itr.next();
			if (banknote.getValue().size() <= 5) {
				lowBanknoteIssueExists = true;
			}
		}
		if (lowBanknoteIssueExists == true) {
			listOfIssues.add("One or More of the Banknote Dispensers is Almost Empty");
		}
	}

	/**
	 * This method predicts full coin within the station's coin storage unit
	 */
	public void predictFullCoin() {
		if (scs.getCoinStorage().getCoinCount() >= scs.getCoinStorage().getCapacity() - 5) {
			listOfIssues.add("Coin Storage Unit is almost full");
			fullCoinIssueExists = true;
		}
	}

	/**
	 * This method predicts full banknote within the station's banknote storage unit
	 */
	public void predictFullBanknote() {
		if (scs.getBanknoteStorage().getBanknoteCount() >= scs.getBanknoteStorage().getCapacity() - 5) {
			listOfIssues.add("Banknote Storage Unit is almost full");
			fullBanknoteIssueExists = true;
		}
	}

	/**
	 * This method returns whether the ink is almost empty or not
	 * 
	 * @return the boolean lowInkIssueExists
	 */
	public boolean inkAlmostEmpty() {
		return lowInkIssueExists;
	}

	/**
	 * This method returns whether the paper is almost empty or not
	 * 
	 * @return the boolean lowPaperIssueExists
	 */
	public boolean paperAlmostEmpty() {
		return lowPaperIssueExists;
	}

	/**
	 * This method returns whether the coin is almost empty or not
	 * 
	 * @return the boolean lowCoinIssueExists
	 */
	public boolean coinAlmostEmpty() {
		return lowCoinIssueExists;
	}

	/**
	 * This method returns whether the banknote is almost empty or not
	 * 
	 * @return the boolean lowBanknoteIssueExists
	 */
	public boolean banknoteAlmostEmpty() {
		return lowBanknoteIssueExists;
	}

	/**
	 * This method returns whether the coin is almost full or not
	 * 
	 * @return the boolean fullCoinIssueExists
	 */
	public boolean coinAlmostFull() {
		return fullCoinIssueExists;
	}

	/**
	 * This method returns whether the banknote is almost full or not
	 * 
	 * @return fullBanknoteIssueExists
	 */
	public boolean banknoteAlmostFull() {
		return fullBanknoteIssueExists;
	}

	public int numberOfIssues() {
		return listOfIssues.size();

	}
}
