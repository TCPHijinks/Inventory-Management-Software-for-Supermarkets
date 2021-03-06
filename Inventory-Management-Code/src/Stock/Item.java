package Stock;

public class Item {
	
	private String itemName;
	private double sellingPrice;
	private double manfCost;
	private int restockPoint;
	private int reorderAmount;
	private int quantity;
	private Double temp;



	/*
	 * Creates an item using a string for the name, doubles to represent a selling price and manufacturing price, 
	 * And integers to represent reorderpoints and reorderAmounts 
	 * 
	 * 
	 * @Author Kyle Langton
	 * @param Doubles: sellprice , selling price of the item
	 * @param double manfprice, price required to manufacture the item
	 * @param integer reorderPoint, the re-order point for the item
	 * @param integer reorderamount, the price of the reorder
	 * @return An Item with the above properties
	 */
	public Item(String name, double manfPrice, double sellPrice, int reorderPoint, int reorderAmount) {
		itemName = name;
		sellingPrice = sellPrice;
		manfCost = manfPrice;
		restockPoint = reorderPoint;
		this.reorderAmount = reorderAmount;
		quantity = 0;
		temp = null;
	}
	
	/*
	 * Overides the constructor by allowing the creation of an item with a temperature
	 * Creates an item using a string for the name, doubles to represent a selling price and manufacturing price, 
	 * And integers to represent reorderpoints and reorderAmounts while adding the functionality for the item to store a 
	 * temperature
	 * 
	 * 
	 * @Author Kyle Langton
	 * @param Doubles: sellprice , selling price of the item
	 * @param double manfprice, price required to manufacture the item
	 * @param integer reorderPoint, the re-order point for the item
	 * @param integer reorderamount, the price of the reorder
	 * @param double temperature, the temperature of the item
	 * @return An Item with the above properties
	 */
	public Item(String name, double manfPrice, double sellPrice, int reorderPoint, int reorderAmount, double temperature) {
		itemName = name;
		sellingPrice = sellPrice;
		manfCost = manfPrice;
		restockPoint = reorderPoint;
		this.reorderAmount = reorderAmount;
		quantity = 0;
		this.temp = temperature;
	}

	
	/*
	 * Simple method which returns the name of the item
	 *
	 * 
	 * @Author Kyle Langton
	 * @return the item name as a string
	 */
	public String getName() {
		// TODO Auto-generated method stub
		return itemName;
	}
	
	/* gets the manufacture price of the item
	 * @Author Kyle Langton
	 * @return the manufacture cost of the item as a double
	 * 
	 */
	public double getManufacturePrice() {
		// TODO Auto-generated method stub
		return manfCost;
	}
	
	/* gets the selling price of the item
	 * @Author Kyle Langton
	 * @return the selling price of the item as a double
	 */
	public double getSellPrice() {
		// TODO Auto-generated method stub
		return sellingPrice;
	}
	
	/* gets the reorderpoint of the item
	 * @Author Kyle Langton
	 * @return returns the reorderpoint of the item as a integer
	 */
	public int getReorderPoint() {
		// TODO Auto-generated method stub
		return restockPoint;
	}
	 
	/* gets the re-order amount of the item
	 * @Author Kyle Langton
	 * @return returns the re-order amount of the item as an integer
	 */
	public int getReorderAmount() {
		// TODO Auto-generated method stub
		return reorderAmount;
	}
	
	/* gets the quantity of the items
	 * @Author Kyle Langton
	 * @return returns the item quantity as an integer
	 */
	public int getQuantity() {
		// TODO Auto-generated method stub
		return quantity;
	}
	
	/*
	 * Gets the temperature of the item as a Double, not to be confused with the 
	 * primitive type double
	 * 
	 * @Author Kyle Langton
	 * @return returns the temperature of the item
	 */
	public Double getTemperature() {
		// TODO Auto-generated method stub
		return temp;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
		
	}
}
