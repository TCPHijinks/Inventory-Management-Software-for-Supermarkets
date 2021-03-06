package GUI;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.*;

import Delivery.OrdinaryTruck;
import Delivery.RefrigeratedTruck;
import Delivery.Truck;
import Execeptions.CSVFormatException;
import Execeptions.DeliveryException;
import Execeptions.StockException;
import Stock.Stock;
import Stock.Store;
import Stock.Item;
import Delivery.Manifest;

public class GUI {

	private static Store supermarket = Store.getStore();
	private static Stock storeInventory = new Stock();
	private static JFrame mainFrame = new JFrame();

	// File path to manifest.
	static String manifestFilePath = null;

	public static void gui() {


		// Store variables




		// Store variables
		Store supermarket = Store.getStore();
		Stock storeInventory = new Stock();
		double storeCapital = supermarket.getCapital();

		// Swing Variables


		// Mainframe
		Dimension mainFrameSize = new Dimension(1000,600);


		mainFrame.setTitle("Inventory");
		mainFrame.setPreferredSize(mainFrameSize);
		mainFrame.setLayout(new BorderLayout());
		mainFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);


		// Table
		DefaultTableModel tableData = new DefaultTableModel(new Object[]{"Name", "Quantity", "Manufactoring Cost in $", "Sell Price in $", "Reorder Point", "Reorder Amount", "Temperature"}, 0);
		JTable inventoryTable = new JTable(tableData);


		// Menu Bar
		JMenuBar menuBar = new JMenuBar();
		menuBar.setLayout(new BorderLayout());

		// Labels

		JLabel capitalLabel = new JLabel("Store Capital: $" + storeCapital);
		double capital = supermarket.getCapital();

		// Menu Buttons
		JMenu fileButton = new JMenu("File");
		JMenuItem importItemProperties = new JMenuItem("Import Item Properties");

		JMenuItem importManifest = new JMenuItem("Import Manifest");
		importManifest.setEnabled(false);

		JMenuItem importSaleLog = new JMenuItem("Import Sales log");
		importSaleLog.setEnabled(false);
		
		JMenuItem exportManifest = new JMenuItem("Export Manifest");
		exportManifest.setEnabled(false);

		// Action Listeners

		
		/*
		 * Imports a csv file which creates item using the properties found within the csv files
		 *
		 * @author Kyle Langton
		 *
		 */
		importItemProperties.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				JFrame csvChooserFrame = new JFrame();
				JFileChooser csvChooser = new JFileChooser();
				csvChooser.setFileFilter(new FileNameExtensionFilter(".csv", "csv"));
				csvChooserFrame.add(csvChooser);
				int status = csvChooser.showOpenDialog(null);

				if (status == JFileChooser.CANCEL_OPTION) {
					csvChooserFrame.dispose();
				}
				if (status == JFileChooser.OPEN_DIALOG) {
					String fileName = csvChooser.getSelectedFile().getName();
					if (fileName.matches(".*.csv")) {
						ArrayList<String[]> readValues = new ArrayList<String[]>();

						try {
							readValues = CSVReader.readCSV(csvChooser.getSelectedFile());
							manifestFilePath = csvChooser.getSelectedFile().getParentFile().getPath()+"\\";
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						
						// Scans through the csv documents creating an item out of the CSV values
						for (int stringcount = 0; stringcount < readValues.size(); stringcount++) {
							try
							{
								if (readValues.get(stringcount).length == 5) {

									Item foodItem = new Item(readValues.get(stringcount)[0],Double.parseDouble(readValues.get(stringcount)[1]),Double.parseDouble(readValues.get(stringcount)[2])
											,Integer.parseInt(readValues.get(stringcount)[3]),Integer.parseInt(readValues.get(stringcount)[4]));



									Object[] tempData = { foodItem.getName(), foodItem.getQuantity(), foodItem.getManufacturePrice(),
											foodItem.getSellPrice(), foodItem.getReorderPoint(), foodItem.getReorderAmount(), null };
									tableData.addRow(tempData);

									// ---- TEMP STORE STUFF ----
									storeInventory.addItem(foodItem);
									}

									if (readValues.get(stringcount).length == 6){

										Item foodItem = new Item(readValues.get(stringcount)[0],Double.parseDouble(readValues.get(stringcount)[1]),Double.parseDouble(readValues.get(stringcount)[2])
												,Integer.parseInt(readValues.get(stringcount)[3]),Integer.parseInt(readValues.get(stringcount)[4]),
												Double.parseDouble(readValues.get(stringcount)[5]));

									Object[] tempData = { foodItem.getName(), foodItem.getQuantity(), foodItem.getManufacturePrice(),
											foodItem.getSellPrice(), foodItem.getReorderPoint(), foodItem.getReorderAmount() , foodItem.getTemperature() };
									tableData.addRow(tempData);

									// ---- TEMP STORE STUFF ----
									storeInventory.addItem(foodItem);
									}
							}
							catch(Exception e1)
							{
								try {
									throw new CSVFormatException("CSV Format Error - check manifest format - field values");
								} catch (CSVFormatException e2) {									
									e2.printStackTrace();
								}
							}
							
							if(readValues.get(stringcount).length > 6 || readValues.get(stringcount).length < 5)
							{								
								try 
								{
									throw new CSVFormatException("CSV Format Error - check manifest format - number of fields");
								} 
								catch (CSVFormatException e1) 
								{									
									e1.printStackTrace();
								}
							}
						}

						// Finally Configure The Table //
						inventoryTable.disable();
						menuBar.add(inventoryTable.getTableHeader(), BorderLayout.SOUTH);
						mainFrame.add(inventoryTable, BorderLayout.CENTER);
						JOptionPane.showMessageDialog(csvChooserFrame, "Loaded Item Properties");
						mainFrame.repaint();
						mainFrame.setVisible(true);
					}
					else {
						JOptionPane.showMessageDialog(csvChooserFrame, "Error: Input was not a CSV File");
					}

				}
				// Update store inventory with new one.
				supermarket.updateInventory(storeInventory);
				// Display pop-up message after completing task.

				importSaleLog.setEnabled(true);
				importManifest.setEnabled(true);
				exportManifest.setEnabled(true);
				csvChooserFrame.dispose();
			}
		});


		
		/*
		 * Imports manifest and updates the store capital based on the quanity of items being passed.
		 *
		 * @author Kyle Langton
		 *
		 */
		importManifest.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFrame manifestFrame = new JFrame();
				JFileChooser csvChooser = new JFileChooser();
				ArrayList<String[]> manifestContent = new ArrayList<String[]>();
				csvChooser.setFileFilter(new FileNameExtensionFilter(".csv", "csv"));
				manifestFrame.add(csvChooser);
				int status = csvChooser.showOpenDialog(null);
				double reduceValue = 0.0;


				if (status == JFileChooser.CANCEL_OPTION) {
					manifestFrame.dispose();
				}
				if (status == JFileChooser.OPEN_DIALOG) {
					tableData.setRowCount(0);
					String fileName =  csvChooser.getSelectedFile().getName();

					if (fileName.matches(".*.csv")) {
						JOptionPane.showMessageDialog(manifestFrame, "Loaded Manifest");
						try {
							manifestContent = CSVReader.readCSV(csvChooser.getSelectedFile());
						} catch (IOException e1) {
							try {
								throw new CSVFormatException("CSV Format Error - not CSV file");
							} catch (CSVFormatException e2) {
								// TODO Auto-generated catch block
								e2.printStackTrace();
							}
						}
						
						// Trys to create a cold truck and runs through all the values inside the csv file trying to match it to an item
						// Only runs for Refrigerated trucks
						for (int manifestCount = 0; manifestCount < manifestContent.size();  manifestCount++) {
							if (manifestContent.get(manifestCount)[0].matches(">Refrigerated")) {
								Stock truckStock = new Stock();
								RefrigeratedTruck coldtruck;
								try 
								{
									coldtruck = new RefrigeratedTruck(truckStock);
								}
								catch(Exception e1)
								{
									try 
									{
										throw new DeliveryException("Delivery Exception - Cargo dosen't fit inside refrigerated truck.");
									} 
									catch (DeliveryException e2) 
									{										
										e2.printStackTrace();
									}
								}
								
								
								int count = manifestCount + 1;
								while (!(manifestContent.get(count)[0].matches(">Refrigerated")) && !(manifestContent.get(count)[0].matches(">Ordinary")) && count <= manifestContent.size() ) {
									for (int inventoryCount = 0; inventoryCount < storeInventory.getLength(); inventoryCount++) {
										if (manifestContent.get(count)[0].matches(storeInventory.getItem(inventoryCount).getName())) {

											Item oldItem;
											try
											{
												oldItem = storeInventory.getItem(inventoryCount);
												if (oldItem.getTemperature() != null) {
													Item newItem = new Item(oldItem.getName(), oldItem.getManufacturePrice(), oldItem.getSellPrice(), oldItem.getReorderPoint(), oldItem.getReorderAmount(), oldItem.getTemperature());
													newItem.setQuantity((int) Double.parseDouble(manifestContent.get(count)[1]));
													truckStock.addItem(newItem);

													}
													if (oldItem.getTemperature() == null) {
													Item newItem = new Item(oldItem.getName(), oldItem.getManufacturePrice(), oldItem.getSellPrice(), oldItem.getReorderPoint(), oldItem.getReorderAmount());
													newItem.setQuantity((int) Double.parseDouble(manifestContent.get(count)[1]));
													truckStock.addItem(newItem);
													}
											}
											catch(Exception e1)
											{
												try {
													throw new StockException("Stock Exception - Couldn't create item copy from store.");
												} catch (StockException e2) {													
													e2.printStackTrace();
												}
											}
											

											reduceValue = reduceValue + storeInventory.getItem(inventoryCount).getManufacturePrice() * Double.parseDouble(manifestContent.get(count)[1]);
											storeInventory.getItem(inventoryCount).setQuantity((int) (storeInventory.getItem(inventoryCount).getQuantity() + Double.parseDouble((manifestContent.get(count)[1]))));
										}
									}
									count++;
									if (count == manifestContent.size()) {
										break;
									}
								}
								try 
								{
									coldtruck = new RefrigeratedTruck(truckStock);
									
									for (int i = 0; i < coldtruck.getCargo().getLength(); i++) 
									{
										System.out.println(coldtruck.getCargo().getItem(i).getName());
										System.out.println(coldtruck.getCargo().getItem(i).getQuantity());
									}
									System.out.println(coldtruck.getTemperature());
									System.out.println("\n");
									reduceValue = reduceValue +  coldtruck.getCost();										
								}
								catch(Exception e1)
								{
									try
									{
										throw new DeliveryException("Delivery Exception - Couldn't create cold truck.");
									}
									catch(Exception e11)
									{
										e11.printStackTrace();
									}
								}	
							}
						}
						
						
						// Trys to create a cold truck and runs through all the values inside the csv file trying to match it to an item
						// Only runs for Ordinary trucks
						for (int manifestCount = 0; manifestCount < manifestContent.size();  manifestCount++) {
							if (manifestContent.get(manifestCount)[0].matches(">Ordinary")) {
								Stock ordStock = new Stock();
								OrdinaryTruck ordTruck;
								try 
								{
									ordTruck = new OrdinaryTruck(ordStock);
								}
								catch(Exception e1)
								{
									try 
									{
										throw new DeliveryException("Delivery Exception - Cargo dosen't fit inside truck.");
									} 
									catch (DeliveryException e2) 
									{										
										e2.printStackTrace();
									}
								}
								
								int count = manifestCount + 1;
								while (!(manifestContent.get(count)[0].matches(">Refrigerated")) && !(manifestContent.get(count)[0].matches(">Ordinary")) && count <= manifestContent.size() ) {
									for (int inventoryCount = 0; inventoryCount < storeInventory.getLength(); inventoryCount++) {
										if (manifestContent.get(count)[0].matches(storeInventory.getItem(inventoryCount).getName())) {

											Item oldItem = storeInventory.getItem(inventoryCount);
											if (oldItem.getTemperature() != null){
												Item newItem = new Item(oldItem.getName(), oldItem.getManufacturePrice(), oldItem.getSellPrice(),
														oldItem.getReorderPoint(),  oldItem.getReorderAmount(), oldItem.getTemperature());
												newItem.setQuantity((int) Double.parseDouble(manifestContent.get(count)[1]));
												ordStock.addItem(newItem);
												}
											if (oldItem.getTemperature() == null){
												Item newItem = new Item(oldItem.getName(), oldItem.getManufacturePrice(), oldItem.getSellPrice(),oldItem.getReorderPoint(),  oldItem.getReorderAmount());
												newItem.setQuantity((int) Double.parseDouble(manifestContent.get(count)[1]));
												ordStock.addItem(newItem);
											}


											reduceValue = reduceValue + storeInventory.getItem(inventoryCount).getManufacturePrice() * Double.parseDouble(manifestContent.get(count)[1]);
											storeInventory.getItem(inventoryCount).setQuantity((int) (storeInventory.getItem(inventoryCount).getQuantity() + Double.parseDouble((manifestContent.get(count)[1]))));
										}
									}
									count++;
									if (count == manifestContent.size()) {
										break;
									}
								}
									
								try 
								{
									ordTruck = new OrdinaryTruck(ordStock);
									for (int i = 0; i < ordTruck.getCargo().getLength(); i++) 
									{
										System.out.println(ordTruck.getCargo().getItem(i).getName());
										System.out.println(ordTruck.getCargo().getItem(i).getQuantity());										
									}
									System.out.println(ordTruck.getCost());
									System.out.println("\n");
									reduceValue = reduceValue +  ordTruck.getCost();										
								}
								catch(Exception e1)
								{
									try 
									{
										throw new DeliveryException("Delivery Exception - Cargo dosen't fit inside truck.");
									} 
									catch (DeliveryException e2) 
									{										
										e2.printStackTrace();
									}
								}									
							}
						}
						// Updates the data inside the JTable
							for (int inventCount = 0; inventCount < storeInventory.getLength(); inventCount++) {
								Object[] tempData = { storeInventory.getItem(inventCount).getName(), storeInventory.getItem(inventCount).getQuantity(), storeInventory.getItem(inventCount).getManufacturePrice(),
										storeInventory.getItem(inventCount).getSellPrice(), storeInventory.getItem(inventCount).getReorderPoint(),
										storeInventory.getItem(inventCount).getReorderAmount(), storeInventory.getItem(inventCount).getTemperature() };
								tableData.addRow(tempData);
							}

							// Updates the capital label and refreshes the frame.
							supermarket.updateCapital(supermarket.getCapital() - reduceValue);
							capitalLabel.setText("Store Capital: " + supermarket.getCapital());
							importItemProperties.setEnabled(false);
							inventoryTable.disable();
							inventoryTable.repaint();
							mainFrame.repaint();
						}

					}
					else {
						JOptionPane.showMessageDialog(manifestFrame, "Error: Input was not a CSV File");
					}
				}

			}

		);




		/*
		 * exports manifest file.
		 *
		 * @author Clinton Hodge
		 *
		 */
		exportManifest.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				PrintWriter pw = null;

				try
				{
					// Set output manifest file.
					pw = new PrintWriter(new File(manifestFilePath+"manifest.csv"));
				}
				catch (FileNotFoundException e1)
				{
					e1.printStackTrace();
				}

				double cOrderQuantity = 0;		// Quantity of all cold items.
				Stock manifest = new Stock();	// Items to be ordered.
				Stock cItems = new Stock();		// Cold items.
				Stock oItems = new Stock();		// Ordinary non-cold items.


				// Check all items in store inventory.
				// Add under-stocked items to order.
				Stock g = supermarket.getInventory();
				for(Item i : g.getStock())
				{
					if((int)i.getQuantity() <= (int)i.getReorderPoint())
					{
						Item oldItem = i;
						if (oldItem.getTemperature() != null) {
							Item newItem = new Item(i.getName(),i.getManufacturePrice(), i.getSellPrice(), i.getReorderPoint(), i.getReorderAmount(), i.getTemperature());
							newItem.setQuantity(newItem.getReorderAmount());
							manifest.addItem(newItem);
						}
						if (oldItem.getTemperature() == null) {
							Item newItem = new Item(i.getName(),i.getManufacturePrice(), i.getSellPrice(), i.getReorderPoint(), i.getReorderAmount());
							newItem.setQuantity(newItem.getReorderAmount());
							manifest.addItem(newItem);
						}
					}
				}

				// Check all items inside order.
				// Get, sort and count all items.
				for(Item i : manifest.getStock())
				{
					if(i.getTemperature() != null)
					{
						cItems.addItem(i);
						cOrderQuantity += i.getQuantity();
					}
					else
					{
						oItems.addItem(i);
					}
				}



				// Cold Trucks Needed.
				int numColdTrucks = (int) Math.ceil(cOrderQuantity / 800.0);
				int curNumItems = 0, allNumItems = 0;

				double capacity = 800.0;
				String truckType = ">Refrigerated";
				String newline = System.getProperty("line.separator");



				int count = cItems.getLength() - 1;
				boolean sFlag = true;

				// Sort cold cargo.
				while(sFlag)
				{
					sFlag = false;
					for(int i = 0; i < count; i++)
					{
						if(cItems.getItem(i+1).getTemperature() < cItems.getItem(i).getTemperature())
						{
							Item c = cItems.getItem(i);
							cItems.getStock().set(i, cItems.getItem(i+1));
							cItems.getStock().set(i+1, c);
							sFlag = true;
						}
					}
				}

				manifest = cItems;

				// CSV file builder.
				StringBuilder sb = new StringBuilder();


				// Set heading, add stock.
				sb.append(truckType);
				for(int j = 0; j < manifest.getLength(); j++)
				{
					Item c = manifest.getItem(j);

					// Check if item quantity > max capacity.
					if((curNumItems + c.getQuantity()) > capacity)
					{

						// Fill item remaining space.
						c.setQuantity(c.getQuantity() - (int)(capacity - curNumItems));

						// Print only if item fits.
						if(capacity - curNumItems != 0) {
							sb.append(newline+c.getName() + ",");
							sb.append(capacity - curNumItems);
						}
						allNumItems += (capacity - curNumItems);
						curNumItems = 0;

						// Calculate and loop by number of needed trucks.
						for(int x = 0; x < Math.ceil(c.getQuantity() /  capacity); x++)
						{
							// Set to ordinary trucks.
							if(manifest == oItems && capacity != 1000 && allNumItems >= (800 * numColdTrucks))
							{
								capacity = 1000.0;
								truckType = ">Ordinary";
							}

							// Parse CSV data.

							sb.append(newline+truckType);
							sb.append(newline+c.getName() + ",");

							// Check if fits and parse.
							if((int)c.getQuantity() > capacity)
							{
								c.setQuantity(c.getQuantity() - (int)(capacity - curNumItems));
								sb.append((int)(capacity - curNumItems)+newline);
								allNumItems += (capacity - curNumItems);
								curNumItems = 0;
							}
							else
							{
								sb.append((int)c.getQuantity());
								allNumItems += c.getQuantity();
								curNumItems += c.getQuantity();
								c.setQuantity(0);
							}
						}
					}
					else
					{
						// Parse items that fit.
						sb.append(newline+c.getName() + ",");
						sb.append((int)c.getQuantity());
						allNumItems += c.getQuantity();
						curNumItems += c.getQuantity();
						c.setQuantity(0);
					}

					// Set ordinary truck cargo to add to manifest.
					if(j >= manifest.getLength() - 1 && manifest == cItems) {
						manifest = oItems;
						j = -1;
					}
				}


				pw.write(sb.toString());
				pw.close();

				// Display message.
				JOptionPane.showMessageDialog(exportManifest, "Truck Manifest Exported");
			}


		});


		/*
		 * imports sales log file.
		 *
		 * @author Clinton Hodge
		 *
		 */
		importSaleLog.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFrame salesChooserFrame = new JFrame();
				JFileChooser salesChooser = new JFileChooser();
				salesChooser.setFileFilter(new FileNameExtensionFilter(".csv", "csv"));
				salesChooserFrame.add(salesChooser);

				int status = salesChooser.showOpenDialog(null);
				if (status == JFileChooser.CANCEL_OPTION) {
					salesChooserFrame.dispose();
				}
				// Open file chooser menu.
				if (status == JFileChooser.OPEN_DIALOG) {
					// Name of selected file.
					String fileName =  salesChooser.getSelectedFile().getName();
					if (fileName.matches(".*.csv")) {

						// File value array.
						ArrayList<String[]> readValues = new ArrayList<String[]>();

						try 
						{
							// Values of file saved to an array.
							readValues = CSVReader.readCSV(salesChooser.getSelectedFile());

							for(int i = 0; i < storeInventory.getStock().size(); i++) {
								// Update store capital and inventory stock.
								if(storeInventory.getItem(i).getName().contains(readValues.get(i)[0])) {
									// Remove sold items form store stock.
									// Add net profit (sale price - manufacture cost) to capital.
									storeInventory.getItem(i).setQuantity(storeInventory.getItem(i).getQuantity() - (int)Double.parseDouble(readValues.get(i)[1]));
									supermarket.updateCapital(supermarket.getCapital() + (storeInventory.getItem(i).getSellPrice() * Double.parseDouble(readValues.get(i)[1])));

									// Update table
									Object temp = tableData.getValueAt(i, 1);
									temp = (int) (Double.parseDouble(temp.toString()) - Double.parseDouble(readValues.get(i)[1]));
									tableData.setValueAt(temp, i, 1);
								}
							}
							capitalLabel.setText("Store Capital: " + supermarket.getCapital());

						} 
						catch (Exception e1) 
						{
							try
							{
								throw new CSVFormatException("CSV Format Exception - check CSV formatting - couldn't get quantity and/or item name.");
							}
							catch(Exception e11)
							{
								e11.printStackTrace();
							}
						}

						// Display completion message pop-up.
						JOptionPane.showMessageDialog(salesChooserFrame, "Updated Store Inventory and Capital");
					}
					else {
						JOptionPane.showMessageDialog(salesChooserFrame, "Error: Input was not a CSV File");
					}
				}
			}
		});




		// Adding Components to the menu bar
		menuBar.add(fileButton, BorderLayout.WEST);
		menuBar.add(capitalLabel, BorderLayout.EAST);

		// Adding components to the file Menu
		fileButton.add(importItemProperties);
		fileButton.add(importManifest);
		fileButton.add(importSaleLog);
		fileButton.add(exportManifest);

		// Adding components to the mainframe
		mainFrame.add(menuBar, BorderLayout.NORTH);

		// Run the GUI
		mainFrame.pack();
		mainFrame.setVisible(true);
	}
}