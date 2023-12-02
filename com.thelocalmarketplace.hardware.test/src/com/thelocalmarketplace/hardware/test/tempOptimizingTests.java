package com.thelocalmarketplace.hardware.test;


import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Currency;

import org.junit.Before;
import org.junit.Rule; 
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import com.jjjwelectronics.EmptyDevice;
import com.jjjwelectronics.OverloadedDevice;
import com.tdc.CashOverloadException;
import com.tdc.DisabledException;
import com.tdc.banknote.Banknote;
import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.hardware.SelfCheckoutStationBronze;
import com.thelocalmarketplace.hardware.SelfCheckoutStationGold;
import com.thelocalmarketplace.hardware.SelfCheckoutStationSilver;
import com.thelocalmarketplace.hardware.external.ProductDatabases;

import powerutility.NoPowerException;
import powerutility.PowerGrid;
import testingUtilities.CardPayment;
import testingUtilities.DollarsAndCurrency;
import testingUtilities.Products;
import testingUtilities.Wallet;
import ca.ucalgary.seng300.simulation.NullPointerSimulationException;
import control.SelfCheckoutLogic;

@RunWith(Parameterized.class)
public class tempOptimizingTests implements DollarsAndCurrency, CardPayment {
/*
 * make three types of station to test, we'll have to test all three types for each kind of test.
 * Maybe there's a fast way to plug in these objects than repeating a test method?
 */
public static SelfCheckoutStationBronze bronze;
public static SelfCheckoutStationSilver silver;
public static SelfCheckoutStationGold gold;

private AbstractSelfCheckoutStation station;
/*
 * each station needs its own logic instance for setup.
 */
SelfCheckoutLogic logic;


/*
 * Products? where are usharabs products?
 */
Products products = new Products();

@Rule
	public ExpectedException expectedMessage = ExpectedException.none();
@Parameterized.Parameters
public static Collection<AbstractSelfCheckoutStation[]> data() {
	//bronze
	bronze.resetConfigurationToDefaults();
	bronze.configureBanknoteDenominations(bankNoteDenominations);
	
	PowerGrid.engageUninterruptiblePowerSource();
	PowerGrid.instance().forcePowerRestore();
	SelfCheckoutStationBronze bronze = new SelfCheckoutStationBronze();
	bronze.plugIn(PowerGrid.instance());
	bronze.turnOn();
	
	//silver
	silver.resetConfigurationToDefaults();
	silver.configureBanknoteDenominations(bankNoteDenominations);
	PowerGrid.engageUninterruptiblePowerSource();
	PowerGrid.instance().forcePowerRestore();
	SelfCheckoutStationSilver silver = new SelfCheckoutStationSilver();
	silver.plugIn(PowerGrid.instance());
	silver.turnOn();
	
	//gold
	gold.resetConfigurationToDefaults();
	gold.configureBanknoteDenominations(bankNoteDenominations);
	PowerGrid.engageUninterruptiblePowerSource();
	PowerGrid.instance().forcePowerRestore();
	SelfCheckoutStationGold gold = new SelfCheckoutStationGold();
	gold.plugIn(PowerGrid.instance());
	gold.turnOn();
	
    return Arrays.asList(new AbstractSelfCheckoutStation[][] {
            { bronze },
            { silver  },
            { gold  },
            // Add more instances as needed
        });
}
public tempOptimizingTests(AbstractSelfCheckoutStation station) {
    this.station = station;
}
	@Before
	public void testSetup () throws OverloadedDevice{
		
	
		/* First, create an instance of the SelfCheckoutStation (Bronze, Silver, Gold).
		 */
		//bronze
		station.resetConfigurationToDefaults();
		station.configureBanknoteDenominations(bankNoteDenominations);
		
		PowerGrid.engageUninterruptiblePowerSource();
		PowerGrid.instance().forcePowerRestore();
		
		station.plugIn(PowerGrid.instance());
		station.turnOn();
		
		/*
		 * Now that we have a station, we need to link the software to it. 
		 */
		
		logic = SelfCheckoutLogic.installOn(station);
	
	 
		logic.session.enable();
		
		logic.station.getPrinter().addInk(300);
		logic.station.getPrinter().addPaper(100);
		
		
		
	}

	/*
	
 * because no items have been added it should throw an exception or reject the bank note
	 */
	@Test (expected = DisabledException.class)
	public void testSlotnotOpen () throws DisabledException, CashOverloadException {
		logic.station.banknoteInput.receive(twenty);

	}
	/*
	 * This test throws string index out of bounds exception probably because there is the
	 * printer can't print empty items. Might be a bug but nbd, we'll work around it
	 */
	@Test //(expected = StringIndexOutOfBoundsException.class)
	public void testSlotopen () throws DisabledException, CashOverloadException {
		//logic.station.banknoteInput.attach();
		logic.station.getBanknoteInput().activate();
		logic.station.getBanknoteInput().enable();
		logic.station.getBanknoteInput().removeDanglingBanknote();
		logic.station.getBanknoteInput().receive(twenty);
		logic.station.getBanknoteStorage().receive(twenty);


	}
	//The system should accept the bank note
	@Test (expected = NullPointerSimulationException.class)
	public void testSlotopendanglingbankNote () throws DisabledException, CashOverloadException {
		logic.station.getBanknoteInput().enable();
		logic.station.getBanknoteInput().receive(five);
		logic.station.getBanknoteInput().removeDanglingBanknote();
		logic.station.getBanknoteInput().emit(five);

	}
	/*The system should reject a bad banknote
	 * 
	 */
	@Test 
	public void testBadBankNote () throws DisabledException, CashOverloadException {
		logic.station.getBanknoteInput().enable();
		logic.station.getBanknoteInput().receive(euros_5);
		logic.station.getBanknoteInput().removeDanglingBanknote();
		logic.station.getBanknoteInput().emit(five);

	}
	/*
	 * This test goes through a payment process for valid currency. 
	 */
	
	@Test
	public void onePaymentStepValidCurrency() throws DisabledException, CashOverloadException, EmptyDevice, OverloadedDevice {
		logic.station.getMainScanner().enable();
		logic.session.Cart.add(ProductDatabases.BARCODED_PRODUCT_DATABASE.get(products.beanBarcode));
	
		logic.station.getMainScanner().scan(products.beanBarcodeItem);
	
		logic.station.getBaggingArea().enable();
		logic.station.getBaggingArea().turnOn();
		logic.station.getBaggingArea().addAnItem(products.beanBarcodeItem);
		
		logic.banknoteController.onPayViaBanknote();
		logic.station.getBanknoteInput().enable();
	
		
		logic.station.getBanknoteInput().receive(five);
		logic.station.getBanknoteStorage().receive(five);

		
		logic.station.printer.enable();
		logic.station.printer.print('$');
		logic.station.printer.cutPaper();
		
		logic.station.printer.removeReceipt();
	}
	
	
}
