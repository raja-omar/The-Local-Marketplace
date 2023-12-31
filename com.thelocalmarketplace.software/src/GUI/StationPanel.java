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

import javax.swing.*;

import com.jjjwelectronics.Mass;
import com.jjjwelectronics.scanner.Barcode;
import com.jjjwelectronics.scanner.BarcodedItem;
import com.thelocalmarketplace.hardware.BarcodedProduct;

import control.SelfCheckoutLogic;
import attendant.EnableDisable;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class StationPanel extends JPanel {
    private JFrame mainFrame;
    private JPanel stationPanel;
    private SelfCheckoutLogic logic;
    private JTextField addItemTextField;
    private JButton enableStationButton;
    private JButton disableStationButton;
    private JButton backButton;
    
    private EnableDisable enableDisable; // Instance of EnableDisable


    public StationPanel(SelfCheckoutLogic logic) {
        this.logic = logic;
        mainFrame = logic.station.getScreen().getFrame();
        stationPanel = new JPanel(new GridLayout(4, 1));

        JPanel addItemsPanel = new JPanel(new GridBagLayout());
        stationPanel.add(addItemsPanel);
                                GridBagConstraints gbcAddItemsPanel = new GridBagConstraints();
                                gbcAddItemsPanel.anchor = GridBagConstraints.NORTH;
                                gbcAddItemsPanel.insets = new Insets(0, 0, 5, 0);
                                gbcAddItemsPanel.gridx = 0;
                                gbcAddItemsPanel.gridy = 0;
                                gbcAddItemsPanel.gridwidth = 7;
                                gbcAddItemsPanel.fill = GridBagConstraints.HORIZONTAL;
                                
                                
                                        addItemTextField = new JTextField();
                                        addItemTextField.addActionListener(new ActionListener() {
                                            @Override
                                            public void actionPerformed(ActionEvent e) {
                                                String userInput = addItemTextField.getText();
                                                BarcodedProduct product = logic.addItemController.textSearch(userInput, null);
                                                if (product != null) {
                                                	Mass itemMass = new Mass(product.getExpectedWeight());
                                                	BarcodedItem item = new BarcodedItem(product.getBarcode(), itemMass); 
                                                	logic.station.getBaggingArea().addAnItem(item);
                                                	JOptionPane.showMessageDialog(mainFrame, "Success: Product "+product.getDescription()+" added to cart");
                                                } else {
                                                	JOptionPane.showMessageDialog(mainFrame, "Error: Product not found");
                                                }
                                                addItemTextField.setText("");
                                            }
                                        });
                                        
        addItemsPanel.add(new JLabel("Add Item:"), gbcAddItemsPanel);
                                        gbcAddItemsPanel.gridy = 1;
                                        addItemsPanel.add(addItemTextField, gbcAddItemsPanel);

                                        
        // Initialize EnableDisable with the station and a password
        enableDisable = new EnableDisable("1234", logic.station);
                                        
        enableStationButton = new JButton("Enable Station");
        enableStationButton.setBackground(new Color(255, 128, 128));
        enableStationButton.setFont(new Font("Tahoma", Font.PLAIN, 16));
        enableStationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	boolean enabled = enableDisable.enableStation("1234");
                if (enabled) {
                    JOptionPane.showMessageDialog(mainFrame, "Station Enabled Successfully");
                } else {
                    //JOptionPane.showMessageDialog(mainFrame, "Failed to Enable Station");
                }
            }
        });
        stationPanel.add(enableStationButton);

        disableStationButton = new JButton("Disable Station");
        disableStationButton.setForeground(new Color(0, 0, 0));
        disableStationButton.setBackground(new Color(181, 255, 181));
        disableStationButton.setFont(new Font("Tahoma", Font.PLAIN, 16));
        disableStationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	boolean disabled = enableDisable.disableStation("1234");
                if (disabled) {
                    JOptionPane.showMessageDialog(mainFrame, "Station Disabled Successfully");
                } else {
                    //JOptionPane.showMessageDialog(mainFrame, "Failed to Disable Station");
                }
            }
        });
        stationPanel.add(disableStationButton);

        backButton = new JButton("<< Back");
        backButton.setFont(new Font("Tahoma", Font.PLAIN, 16));
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stationPanel.setVisible(false);
                MainAttendantScreen sPanel = new MainAttendantScreen(logic);
            }
        });
        stationPanel.add(backButton);

        mainFrame.getContentPane().add(stationPanel);
    }
}
