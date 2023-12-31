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
package GUI;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;

import com.jjjwelectronics.Item;
import com.jjjwelectronics.Mass;
import com.jjjwelectronics.Numeral;
import com.jjjwelectronics.scanner.Barcode;
import com.jjjwelectronics.scanner.BarcodedItem;
import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.hardware.PLUCodedItem;
import com.thelocalmarketplace.hardware.PLUCodedProduct;
import com.thelocalmarketplace.hardware.PriceLookUpCode;
import com.thelocalmarketplace.hardware.external.ProductDatabases;

import control.SelfCheckoutLogic;
import item.AddItemPLU;

public class plu  {
	JFrame mainFrame;
	JPanel mainPanel;
	JPanel bottomPanel;
	JPanel keyboardPanel;
	
	String pluNum = "";
	PriceLookUpCode getter;
	AddItemPLU addItemPlu;
	int pluNumLength = 4; 
	
	JButton okayButton;
	JButton backspaceButton;
	
	JLabel promptLabel;
	JLabel numberLabel;
	String message;
	JLabel test;
	Timer timer;
	SelfCheckoutLogic logic;
	BarcodedProduct product1;

public plu(SelfCheckoutLogic logic) {
	this.logic = logic;
	mainFrame = logic.station.getScreen().getFrame();
	
	timer = new Timer(10000, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            handleTimeout();
        }

		

		
    });
	
	mainPanel = new JPanel();
	mainPanel.setLayout(new GridBagLayout());
	
	promptLabel = new JLabel("Enter a valid PLU number:");
	promptLabel.setFont(promptLabel.getFont().deriveFont(35f));
	promptLabel.setVerticalAlignment(SwingConstants.BOTTOM);
	promptLabel.setHorizontalAlignment(SwingConstants.CENTER);

	numberLabel = new JLabel("-".repeat(pluNumLength));
	numberLabel.setFont(numberLabel.getFont().deriveFont(45f));
	numberLabel.setVerticalAlignment(SwingConstants.TOP);
	numberLabel.setHorizontalAlignment(SwingConstants.CENTER);
	
	bottomPanel = new JPanel();
	bottomPanel.setLayout(new GridLayout(1,3));
	
	keyboardPanel = new JPanel();
	keyboardPanel.setLayout(new GridLayout(4,3));
	
	
	okayButton = new JButton("Done");
	okayButton.addActionListener(e -> {
		if (!timer.isRunning()) {
			if (pluNum.length() != 4) {
				message = "Enter Valid PLU";
				test.setText("Console: " + message);  // Update text
				test.repaint();
				
			}
			else {
				getter = new PriceLookUpCode (pluNum);
				PLUCodedProduct product = ProductDatabases.PLU_PRODUCT_DATABASE.get(getter);
				if (product != null) {
					
					Numeral[] PLUNumeral = new Numeral[getter.toString().length()];
			    	for (int i = 0; i < getter.toString().length(); i++) {
			            char numeralChar = getter.toString().charAt(i);
			            int numeralIndex = Character.getNumericValue(numeralChar) - 1;
			            PLUNumeral[i] = Numeral.values()[numeralIndex];
			        }
			      
			        product1 = ProductDatabases.BARCODED_PRODUCT_DATABASE.get(new Barcode(PLUNumeral));
			        
			        BigDecimal expectedWeight = new BigDecimal(product1.getExpectedWeight());
			        
					AddItemPLU.AddItemFromPLU(logic.session,getter, expectedWeight.multiply(new BigDecimal("1")));
					message = "Item found! Please place item in bagging area within 10 seconds";
					test.setText("Console: " + message);  // Update text
					test.repaint();
					timer.restart();
	           	 	timer.start();
					
				}
				else {
					message = "PLU not found";
					 test.setText("Console: " + message);  // Update text
					test.repaint();
							
				}
				
			}
		}
		else {
			message = "Must Add Item to Proceed!";
    		test.setText("Console: " + message);
			test.repaint();
    	}
		
		
		
	});
	
	backspaceButton = new JButton("Quit");
	backspaceButton.addActionListener(e -> {
		if (!timer.isRunning()) {
		
			if (pluNum.length() == 0) {
				
				mainPanel.setVisible(false);
				MainPanel mainWindow = new MainPanel(logic, "Cancelled PLU number input");
				
			} else if (pluNum.length() > 0) {
				
				pluNum = pluNum.substring(0, pluNum.length() - 1);
				this.numberLabel.setText(pluNum + 
						"-".repeat(pluNumLength - this.pluNum.length()));
				
				if (pluNum.length() == 0) {
					
					backspaceButton.setText("Quit");
					
				}
			}
		}
		else {
			message = "Must Add Item to Proceed!";
    		test.setText("Console: " + message);
			test.repaint();
    	}
		
	});
	
	
	for (int i = 1; i <= 9; i++) {
		
		// Generate two rows of buttons and add them to the grid
		keyboardPanel.add(generateCharacterButton(i));
		
	}
	
	keyboardPanel.add(backspaceButton);
	
	keyboardPanel.add(generateCharacterButton(0));
	
	keyboardPanel.add(okayButton);
	
	JPanel mainPanelSub = new JPanel();
	mainPanelSub.add(promptLabel);
	mainPanelSub.add(numberLabel);
	
	bottomPanel.add(new JLabel());
	bottomPanel.add(keyboardPanel);
	bottomPanel.add(new JLabel());
	
	mainPanelSub.add(bottomPanel);
	GridBagConstraints gbcSubPanel = new GridBagConstraints();
    gbcSubPanel.gridx = 0;
    gbcSubPanel.gridy = 0;
    gbcSubPanel.weighty = 0.8;
    gbcSubPanel.weightx = 1;
    gbcSubPanel.fill = GridBagConstraints.BOTH;
    mainPanel.add(mainPanelSub, gbcSubPanel);
    
	
	message = "Choose PLU";
	test = new JLabel("Console: " + message);
	test.setFont(test.getFont().deriveFont(18f));
    test.setForeground(Color.RED);

    test.setHorizontalAlignment(JLabel.CENTER);
    test.setBorder(BorderFactory.createLineBorder(Color.BLACK));
    JPanel bottomPanel2 = new JPanel();
    bottomPanel2.setLayout(new GridLayout(1, 1));
    bottomPanel2.add(test);
    bottomPanel2.add(test);
  
    GridBagConstraints gbcBottomPanel = new GridBagConstraints();
    gbcBottomPanel.gridx = 0;
    gbcBottomPanel.gridy = 1;
    gbcBottomPanel.weighty = 0.1;
    gbcBottomPanel.weightx = 1;
    gbcBottomPanel.fill = GridBagConstraints.BOTH;
    mainPanel.add(bottomPanel2, gbcBottomPanel);
    
    
	JPanel testPanel = new JPanel();
    testPanel.setBackground(Color.LIGHT_GRAY);

    testPanel.setBorder(BorderFactory.createMatteBorder(5, 0, 0, 0, Color.GRAY));
    
    JButton addedItemButton = new JButton("Click to Place Item on Bagging Area");
    addedItemButton.addActionListener(e -> {
    	timer.stop();
    	
    	logic.station.getBaggingArea().addAnItem(new PLUCodedItem(getter, new Mass(product1.getExpectedWeight()*product1.getPrice())));
    	
    	mainPanel.setVisible(false);
    	new MainPanel(logic, "Item Added");
    	
    	
    });
    testPanel.add(addedItemButton);
    
    GridBagConstraints gbcTestPanel = new GridBagConstraints();
    gbcTestPanel.gridx = 0;
    gbcTestPanel.gridy = 2;
    gbcTestPanel.weighty = 0.2;
    gbcTestPanel.weightx = 1;
    gbcTestPanel.fill = GridBagConstraints.BOTH;

    mainPanel.add(testPanel, gbcTestPanel);
    
	
	mainFrame.getContentPane().add(mainPanel);
}

private JButton generateCharacterButton(int character) {
	JButton button = new JButton(String.valueOf(character));
	button.setFont(button.getFont().deriveFont(25f));
	
	button.addActionListener(e -> {
		
		if (this.pluNum.length() < pluNumLength) {
			
			this.pluNum += String.valueOf(character);
			
			this.numberLabel.setText(this.pluNum + 
					"-".repeat(pluNumLength - this.pluNum.length()));
			
			backspaceButton.setText("<-"); // Replace the quit button to a backspace button
			
			// Use this code to make the membership num all Xs
			// this.numberLabel.setText("X".repeat(this.membershipNum.length()) + 
			//		"-".repeat(membershipNumLength - this.membershipNum.length()));
			
		}
		
	});
	
	return button;
	
}
private void handleTimeout() {
	timer.stop();	
	System.err.println("Timeout: Item not added");
	mainPanel.setVisible(false);
	sessionBlocked sessBlocked = new sessionBlocked(logic);
	
}

}
