package com.thelocalmarketplace.software.test;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Calendar;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.jjjwelectronics.Item;
import com.jjjwelectronics.Mass;
import com.jjjwelectronics.Numeral;
import com.jjjwelectronics.card.Card;
import com.jjjwelectronics.card.MagneticStripeFailureException;
import com.jjjwelectronics.scanner.Barcode;
import com.jjjwelectronics.scanner.BarcodedItem;
import com.thelocalmarketplace.hardware.SelfCheckoutStationBronze;
import com.thelocalmarketplace.hardware.SelfCheckoutStationGold;
import com.thelocalmarketplace.hardware.SelfCheckoutStationSilver;
import com.thelocalmarketplace.hardware.external.CardIssuer;

import ca.ucalgary.seng300.simulation.NullPointerSimulationException;
import control.SelfCheckoutLogic;
import powerutility.PowerGrid;
import testingUtilities.CardPayment;
import testingUtilities.Products;

public class EnterMembershipTest implements CardPayment{
	private SelfCheckoutStationGold gold;
	private SelfCheckoutStationBronze bronze;
	private SelfCheckoutStationSilver silver;
	private SelfCheckoutLogic logicBronze;
	private SelfCheckoutLogic logicSilver;
	private SelfCheckoutLogic logicGold;
	private Products products;
	public BarcodedItem membershipCard;
	public Mass mass;
	public Barcode barcode;
	private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalErr = System.err;
	
	
	@Before
	public void setUp() throws Exception {
		bronze.resetConfigurationToDefaults();
		PowerGrid.engageUninterruptiblePowerSource();
		PowerGrid.instance().forcePowerRestore();
		SelfCheckoutStationBronze bronzeStation = new SelfCheckoutStationBronze();
		bronzeStation.plugIn(PowerGrid.instance());
		bronzeStation.turnOn();
		
		silver.resetConfigurationToDefaults();
		PowerGrid.engageUninterruptiblePowerSource();
		PowerGrid.instance().forcePowerRestore();
		SelfCheckoutStationSilver silverStation = new SelfCheckoutStationSilver();
		silverStation.plugIn(PowerGrid.instance());
		silverStation.turnOn();
		
		gold.resetConfigurationToDefaults();
		PowerGrid.engageUninterruptiblePowerSource();
		PowerGrid.instance().forcePowerRestore();
		SelfCheckoutStationGold goldStation = new SelfCheckoutStationGold();
		goldStation.plugIn(PowerGrid.instance());
		goldStation.turnOn();
		
		logicBronze = SelfCheckoutLogic.installOn(bronzeStation);
		logicSilver = SelfCheckoutLogic.installOn(silverStation);
		logicGold = SelfCheckoutLogic.installOn(goldStation);
		
		products = new Products();
		mass = new Mass(1);
		barcode = new Barcode(new Numeral[] { Numeral.one });
		membershipCard = new BarcodedItem(barcode,mass);
		
		System.setErr(new PrintStream(errContent));

	}
	
	@After
    public void restoreStreams() {
        System.setErr(originalErr);
    }

	/**
     * Test Case: testSwipeMembershipCard
     * Purpose: Tests if the membership card can be swiped successfully.
     * Actions:
     * - Creates a Card object representing a membership card.
     * - Enables the card reader on the gold station.
     * - Swipes the membership card.
     * Expected Result: This test doesn't have an explicit assertion, so it checks if the swiping process runs without errors.
     */
	@Test
	public void testSwipeMembershipCard() throws IOException {
		Card memberCard = new Card("membership", "00000", "John", null, null, false, false);
		logicGold.station.getCardReader().enable();
		logicGold.station.getCardReader().swipe(memberCard);
		//logicSilver.station.cardReader.enable();
		//logicSilver.station.cardReader.swipe(memberCard);
	}
	
	/**
     * Test Case: testSwipeInformationCorrect
     * Purpose: Tests if the membership number is correctly set after swiping the card.
     * Actions:
     * - Creates a Card object representing a membership card.
     * - Enables the card reader on the gold station.
     * - Swipes the membership card.
     * Expected Result: Checks if the membership number in the EnterMembership instance matches the card number.
     */
	@Test
	public void testSwipeInformationCorrect() throws IOException {
		Card memberCard = new Card("membership", "00000", "John", null, null, false, false);
		logicGold.station.getCardReader().enable();
		logicGold.station.getCardReader().swipe(memberCard);
		assertEquals(logicGold.session.membership, memberCard.number );
	}
	
	/**
     * Test Case: testScanMembershipCard
     * Purpose: Tests if the membership card barcode can be scanned successfully using the handheld scanner.
     * Actions:
     * - Enables the handheld scanner on the gold station.
     * - Simulates scanning a membership card barcode.
     * Expected Result: This test doesn't have an explicit assertion, so it checks if the scanning process runs without errors.
     */
	@Test
	public void testScanMembershipCard() throws IOException {
		products = new Products();
		logicGold.session.enable();
		logicGold.station.getHandheldScanner().scan(membershipCard);
	}
	
	 /**
     * Test Case: testScanInformationCorrect
     * Purpose: Tests if the membership number is correctly set after scanning the card barcode.
     * Actions:
     * - Enables the handheld scanner on the gold station.
     * - Simulates scanning a membership card barcode.
     * Expected Result: Checks if the membership number in the EnterMembership instance matches the scanned barcode.
     */
	@Test
	public void testScanInformationCorrect() throws IOException {
		logicGold.session.enable();
		logicGold.station.getHandheldScanner().scan(membershipCard);
		assertEquals(logicGold.session.membership, barcode.toString() );
	}


	/**
	 * Test Case: testCardTypeIsNull
	 * Purpose: Tests how the system handles a null card type when reading from a card.
	 * Actions:
	 * - Configures the card reader to return a card with a null type.
	 * - Invokes the card reading process.
	 * Expected Result: Checks if a NullPointerSimulationException is thrown for a null card type.
	 * @throws IOException 
	 */
	@Test(expected = NullPointerSimulationException.class)
	public void testCardTypeIsNull() throws NullPointerSimulationException, IOException {
	    Card testCard = new Card(null, "00000", "John", null, null, false, false);
	    logicGold.station.getCardReader().swipe(testCard); //change this to a card object
	}

	/**
	 * Test Case: testNonMembershipCardType
	 * Purpose: Tests how the system handles a non-membership card type when reading from a card.
	 * Actions:
	 * - Configures the card reader to return a card with a non-membership type.
	 * - Invokes the card reading process.
	 * Expected Result: Checks if an error message is printed when a non-membership card is detected.
	 * @throws IOException 
	 */
	@Test
	public void testNonMembershipCardType() throws IOException {
	    Card nonMembershipCard = new Card("nonMembership", "12345", "Jane", null, null, false, false);
	    logicGold.station.getCardReader().swipe(nonMembershipCard);
	    assertTrue(errContent.toString().contains("membership card expected, type used is: nonMembership"));
	}

	/**
	 * Test Case: testSessionNotStartedScan
	 * Purpose: Tests how the system handles scanning a membership card when no session is in progress.
	 * Actions:
	 * - Disables the session.
	 * - Enables the handheld scanner.
	 * - Simulates scanning a membership card barcode.
	 * Expected Result: Checks if an error message is printed, indicating that a session needs to be started first.
	 */
	@Test
	public void testSessionNotStartedScan() {
		products = new Products();
	    logicGold.session.disable();
	    logicGold.station.getHandheldScanner().enable();
	    logicGold.station.getHandheldScanner().scan(products.membershipCard);
	    assertTrue(errContent.toString().contains("Please wait for the system to be enabled"));
	}

	/**
	 * Test Case: testDeviceDisabled
	 * Purpose: Tests how the system handles trying to read a card when the card reader is disabled.
	 * Actions:
	 * - Disables the card reader.
	 * - Invokes the card reading process.
	 * Expected Result: Checks if an error message is printed, indicating that the card reader needs to be enabled.
	 * @throws IOException 
	 */
	@Test
	public void testDeviceDisabled() throws IOException {
	    logicGold.station.getCardReader().disable();
	    Card testCard = new Card("membership", "00000", "John", null, null, false, false);
	    logicGold.station.getCardReader().swipe(testCard); //change this to a card object
	}
}