package cpsc4620;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.Date;
import java.util.stream.Collectors;

import init.DBIniter;
import cpsc4620.DBConnector;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * This file is where the front end magic happens.
 * 
 * You will have to write the functionality of each of these menu options' respective functions.
 * 
 * This file should need to access your DB at all, it should make calls to the DBNinja that will do all the connections.
 * 
 * You can add and remove functions as you see necessary. But you MUST have all 8 menu functions (9 including exit)
 * 
 * Simply removing menu functions because you don't know how to implement it will result in a major error penalty (akin to your program crashing)
 * 
 * Speaking of crashing. Your program shouldn't do it. Use exceptions, or if statements, or whatever it is you need to do to keep your program from breaking.
 * 
 * 
 */

public class Menu {

	public static void main(String[] args) throws SQLException, IOException {
		System.out.println("Welcome to Taylor's Pizzeria!");
		Menu m=new Menu();

		int menu_option = 0;

		// present a menu of options and take their selection

		PrintMenu();
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		DBIniter.init();
		String option = reader.readLine();
		menu_option = Integer.parseInt(option);

		while (menu_option != 9) {
			switch (menu_option) {
				case 1:// enter order
					EnterOrder();
					break;
				case 2:// view customers
					viewCustomers();
					break;
				case 3:// enter customer
					EnterCustomer();
					break;
				case 4:// view order
					// open/closed/date
					ViewOrders();
					break;
				case 5:// mark order as complete
					MarkOrderAsComplete();
					break;
				case 6:// view inventory levels
					ViewInventoryLevels();
					break;
				case 7:// add to inventory
					AddInventory();
					break;
				case 8:// view reports
					PrintReports();
					break;
			}
			PrintMenu();
			option = reader.readLine();
			menu_option = Integer.parseInt(option);
		}

	}

	public static void PrintMenu() {
		System.out.println("\n\nPlease enter a menu option:");
		System.out.println("1. Enter a new order");
		System.out.println("2. View Customers ");
		System.out.println("3. Enter a new Customer ");
		System.out.println("4. View orders");
		System.out.println("5. Mark an order as completed");
		System.out.println("6. View Inventory Levels");
		System.out.println("7. Add Inventory");
		System.out.println("8. View Reports");
		System.out.println("9. Exit\n\n");
		System.out.println("Enter your option: ");
	}

	public static boolean CheckValidInput(String regex, String input) {
		if(input.length()==0)
			return false;
		Pattern r = Pattern.compile(regex);
		Matcher m = r.matcher(input);
		if(m.results().count() != 0)
			return true;
		else
			return false;
	}


	// allow for a new order to be placed
	public static void EnterOrder() throws SQLException, IOException {
		Connection conn = DBConnector.make_connection();
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		/*
		 * EnterOrder should do the following:
		 * Ask if the order is for an existing customer -> If yes, select the customer. If no -> create the customer (as if the menu option 2 was selected).
		 * 
		 * Ask if the order is delivery, pickup, or dinein (ask for orderType specific information when needed)
		 * 
		 * Build the pizza (there's a function for this)
		 * 
		 * ask if more pizzas should be be created. if yes, go back to building your pizza. 
		 * 
		 * Apply order discounts as needed (including to the DB)
		 * 
		 * apply the pizza to the order (including to the DB)
		 * 
		 * return to menu
		 */


		String choice = "N";
		boolean validInp = false;
		String regex = "^([YyNn]?)$";

		while(!validInp) {
			System.out.println("Is this order for a existing customer? (Y/N):");
			choice = reader.readLine().trim();
			validInp = CheckValidInput(regex, choice);
			if(validInp)
				validInp = true;
			else
				System.out.println("Provide only valid input");
		}

		int customerID=0;
		if(choice.equals("N") || choice.equals("n")) {
			Menu.EnterCustomer();
			ArrayList<Customer> cus=new ArrayList<Customer>();
			cus=DBNinja.getCustomerList();
			customerID=cus.get(cus.size()-1).getCustID();
			// System.out.println(customerID);

		} else {
			System.out.println("Here is the list of existing customers: ");
			viewCustomers();
			System.out.println("Which customer is this order for? Enter customer ID: ");
			customerID = Integer.parseInt(reader.readLine());
		}

		System.out.println("What type of order is this?");
		choice = "1";
		int type = 1;
		validInp = false;
		regex = "^([1-3]?)$";
		while(!validInp) {
			System.out.println("1. Dine-in\n2. Pick-up\n3. Delivery\nEnter Here:");
			choice = reader.readLine();
			validInp = CheckValidInput(regex, choice);
			if(validInp) {
				validInp = true;
				type = Integer.parseInt(choice);
			} else
				System.out.println("Provide only valid input");
		}
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
		LocalDateTime now = LocalDateTime.now();
		String timestamp=dtf.format(now);

		int maxOrderID=DBNinja.getNextOrderID();

		String ordertype=null;
		Order o=new Order(0,customerID,"abc",timestamp,0.0,0.0,0);
		DBNinja.addOrder(o);

		if(type==1){
			System.out.println("Enter the table number  ");
			Integer tableNumber = Integer.parseInt(reader.readLine());

			ordertype="dinein";
			o.setOrderType(ordertype);
			DBNinja.dineIn(maxOrderID, tableNumber);
		} else if(type==2){
			ordertype="pickup";
			o.setOrderType(ordertype);
			DBNinja.updatePickUp(maxOrderID);
		} else {
			System.out.println("Enter the customer address ");
			String customerAddress = reader.readLine();
			ordertype="delivery";
			o.setOrderType(ordertype);
			DBNinja.updateDelivery(maxOrderID, customerAddress);
		}



		int orderId=DBNinja.getNextOrderID();


//		HashMap<Integer, Integer> pizzaDiscountmap = new HashMap<>();
//		HashMap<Integer, Integer> orderDiscountmap = new HashMap<>();
//		HashMap<Integer, List<Integer>> pizzatopping = new HashMap<>();
		ArrayList<Integer[]> pizzaDiscountmap = new ArrayList<Integer[]>();
		ArrayList<Integer[]> orderDiscountmap = new ArrayList<Integer[]>();
		ArrayList<Integer[]> pizzatopping = new ArrayList<Integer[]>();
//		ArrayList<ArrayList<Integer>> discountsArrayList;
//		discountsArrayList = new ArrayList<ArrayList<Integer>>();
//		ArrayList<ArrayList<Integer>> pizzatopping;
//		pizzatopping = new ArrayList<ArrayList<Integer>>();
//		ArrayList<Integer> onpizzatopping = new ArrayList<>();
//		ArrayList<Integer> ordDiscountList = new ArrayList<>();
		ArrayList<Pizza> pizzas=new ArrayList<Pizza>();
        double pricetocustomer=0.0;
		double pricetobusiness=0.0;
		System.out.println("Let's Build a pizza");
		int flag = 1;
		while(flag != -1) {


			ArrayList<Integer> toppingList = new ArrayList<>();
			boolean[] isToppingDouble = new boolean[17];
			System.out.println("What size is the pizza?");
			regex = "^[1-4]?$";
			validInp = false;
			int s=1;
			while(!validInp)
			{
				System.out.println("1. Small\n2.Medium\n3.Large\n4.X-Large\nEnter Here:");
				choice = reader.readLine();
				validInp = CheckValidInput(regex, choice);
				if(validInp)
				{
					validInp = true;
					s = Integer.parseInt(choice);
				}
				else
					System.out.println("Provide only valid input");
			}
			String size=null;
			String crust=null;
			if(s==1)
				size="small";
			else if(s==2)
				size="medium";
			else if(s==3)
				size="large";
			else if(s==4)
				size="x-large";

			System.out.println("What crust for this pizza?");
			regex = "^[1-4]$";
			validInp = false;
			int c = 1;
			while (!validInp)
			{
				System.out.println("1. Thin\n2.Original\n3.Pan\n4.Gluten-Free\nEnter Here:");
				choice = reader.readLine();
				validInp = CheckValidInput(regex, choice);
				if(validInp)
				{
					validInp = true;
					c = Integer.parseInt(choice);
				}
				else
					System.out.println("Provide only valid input");
			}
			if(c==1)
				crust="Thin";
			else if(c==2)
				crust="Original";
			else if(c==3)
				crust="Pan";
			else if(c==4)
				crust="Gluten-Free";

			double basepricecustomer=DBNinja.getBaseCustPrice(size,crust);
			double basepricebusiness=DBNinja.getBaseBusPrice(size,crust);

			int pizzaId=DBNinja.getMaxPizzaID();

			Pizza p=new Pizza(pizzaId+1,size,crust,maxOrderID,"Processing",timestamp,basepricecustomer,basepricebusiness);

			int toppingFlag = 1;
			ArrayList<Topping> toppingsList = new ArrayList<Topping>();
			while (toppingFlag != -1) {
				regex = "^(-1|1[0-7]|[1-9])$";
				validInp = false;
				int toppingID=-1;
				while (!validInp)
				{
					ViewInventoryLevels();
					System.out.println("Select the required topping from the available list. Enter ToppingID. Enter -1 to stop adding toppings:");

					choice = reader.readLine();
					validInp = CheckValidInput(regex, choice);
					if(validInp)
					{
						validInp = true;
						toppingID = Integer.parseInt(choice);
					}
					else
						System.out.println("Provide only valid input");

				}
				if (toppingID != -1) {
					Topping t=DBNinja.getToppingFromId(toppingID);
					toppingsList=DBNinja.getInventory();
					System.out.println("Do you want this topping in double amount? (Y/N)");
					String damt = reader.readLine();
					if(damt.equals("y") || damt.equals("Y")) {
						DBNinja.useTopping(p,t,true);
						p.addToppings(t,true);


						Integer mapping[]={p.getPizzaID(),t.getTopID(),1};
						pizzatopping.add(mapping);

					} else{
						DBNinja.useTopping(p,t,false);
						p.addToppings(t,false);
                        Integer mapping[]={p.getPizzaID(),t.getTopID(),0};
						pizzatopping.add(mapping);

					}

				} else
					toppingFlag = -1;
			}


			ArrayList<Integer> discountList = new ArrayList<Integer>();

			choice = "N";
			validInp = false;
			regex = "^([YyNn]?)$";
			while(!validInp)
			{
				System.out.println("Do you want to add Discount to pizza? Enter y/n");
				choice = reader.readLine();
				validInp = CheckValidInput(regex, choice);
				if(validInp)
					validInp = true;
				else
					System.out.println("Provide only valid input");
			}
			if (choice.equals("Y") || choice.equals("y")) {
//				String getDiscountssql = "SELECT * FROM discount";
//				PreparedStatement dpreparedStatement = conn.prepareStatement(getDiscountssql);
//				ResultSet discounts = dpreparedStatement.executeQuery();
				System.out.println("Getting discount list...");
				int discountflag = 1;
				while (discountflag != -1) {
					ArrayList<Discount> disc=new ArrayList<Discount>();
					Discount d=null;
					disc=DBNinja.getDiscountList();
					for (Discount discount:disc) {
						System.out.println(discount.toString());
					}
					System.out.println("Select the required discount from the available list. Enter DiscountID. Enter -1 to stop adding discounts:");
					int DiscountID = Integer.parseInt(reader.readLine());
					double custPrice1=p.getCustPrice();
					if (DiscountID != -1) {
						for (Discount discount:disc) {
							if(discount.getDiscountID()==DiscountID){
								d=discount;
							}
						}
						if(d.isPercent()) {
							p.setCustPrice(custPrice1-((custPrice1*d.getAmount())/100));
						} else {
							p.setCustPrice(custPrice1 - d.getAmount());;
						}

//						discountList.add(DiscountID);
						Integer mapping[] = {p.getPizzaID(),d.getDiscountID()};
						pizzaDiscountmap.add(mapping);


					} else
						discountflag = -1;
				}
			}

			pricetocustomer=pricetocustomer+p.getCustPrice();
			pricetobusiness=pricetobusiness+p.getBusPrice();
			o.setCustPrice(pricetocustomer);
			o.setBusPrice(pricetobusiness);

            pizzas.add(p);
     		DBNinja.addPizza(p);
//			System.out.println("Calling Map function");
//			DBNinja.mapNames(maxOrderID, s, c, type, discountList, toppingList, isToppingDouble);
			System.out.println("Do you want to add more pizzas? (Y/N):");
			String addPizza = reader.readLine();
			if(addPizza.equals("N") || addPizza.equals("n"))
				flag = -1;
		}

		System.out.println("Do you want to add discounts to this order? Enter (Y/N):");
		String ordDischoice = reader.readLine();
		if (ordDischoice.equals("Y") || ordDischoice.equals("y")) {
//			String getDiscountssql = "SELECT * FROM discount";
//			PreparedStatement dpreparedStatement = conn.prepareStatement(getDiscountssql);
//			ResultSet discounts = dpreparedStatement.executeQuery();
			System.out.println("Getting discount list...");
			int discountflag = 1;
			while (discountflag != -1) {
				ArrayList<Discount> discorder=new ArrayList<Discount>();
				Discount d1=null;
				discorder=DBNinja.getDiscountList();
				for (Discount discount:discorder) {
					System.out.println(discount.toString());
				}
				System.out.println("Select the required discount from the available list. Enter DiscountID. Enter -1 to stop adding discounts:");
				int DiscountID = Integer.parseInt(reader.readLine());
				double custPrice=o.getCustPrice();

				if (DiscountID != -1) {
					for (Discount discount:discorder) {
						if(discount.getDiscountID()==DiscountID){
							d1=discount;
						}
					}
					if(d1.isPercent()) {
						o.setCustPrice(custPrice-((custPrice*d1.getAmount())/100));
					} else {
						o.setCustPrice(custPrice - d1.getAmount());

					}


					Integer mapping[] = {maxOrderID,d1.getDiscountID()};
					orderDiscountmap.add(mapping);

				} else
					discountflag = -1;
			}
//			discountsArrayList.add(ordDiscountList);
		}

		o.setOrderID(maxOrderID);
        DBNinja.addOrder(o);

		for(int i=0;i< pizzatopping.size();i++){
			DBNinja.pizzaToppingConnection(pizzatopping.get(i)[0],pizzatopping.get(i)[1],pizzatopping.get(i)[2]);
		}
		for(int i=0;i< pizzaDiscountmap.size();i++){
			DBNinja.pizzaDiscountConnection(pizzaDiscountmap.get(i)[0],pizzaDiscountmap.get(i)[1]);
		}
		for(int i=0;i< orderDiscountmap.size();i++){
			DBNinja.orderDiscountConnection(orderDiscountmap.get(i)[0],orderDiscountmap.get(i)[1]);
		}
		System.out.println();


		System.out.println("Finished adding order...Returning to menu...");
		conn.close();
	}


	public static void viewCustomers() throws SQLException, IOException {
		/*
		 * Simply print out all of the customers from the database. 
		 */

		Connection conn = DBConnector.make_connection();
		if(conn != null) {
			ArrayList<Customer> customers = null;
			customers=DBNinja.getCustomerList();
			for(Customer customer:customers){
				System.out.println(customer.toString());
			}


		}
		conn.close();
	}


	// Enter a new customer in the database
	public static void EnterCustomer() throws SQLException, IOException {
		/*
		 * Ask what the name of the customer is. YOU MUST TELL ME (the grader) HOW TO FORMAT THE FIRST NAME, LAST NAME, AND PHONE NUMBER.
		 * If you ask for first and last name one at a time, tell me to insert First name <enter> Last Name (or separate them by different print statements)
		 * If you want them in the same line, tell me (First Name <space> Last Name).
		 * 
		 * same with phone number. If there's hyphens, tell me XXX-XXX-XXXX. For spaces, XXX XXX XXXX. For nothing XXXXXXXXXXXX.
		 * 
		 * I don't care what the format is as long as you tell me what it is, but if I have to guess what your input is I will not be a happy grader
		 * 
		 * Once you get the name and phone number (and anything else your design might have) add it to the DB
		 */
		Connection conn = DBConnector.make_connection();
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Please Enter the Customer name (First Name <space> Last Name): ");
		String fullName = reader.readLine();
		String[] names = fullName.trim().split("\\s+");
		System.out.println("Please Enter the Customer Mobile Number (XXXXXXXXXX): ");
		String phone = reader.readLine();
		Customer customer=new Customer(0,names[0],names[1],phone);
		DBNinja.addCustomer(customer);


//			String getIDsql = "SELECT MAX(CustomerID) FROM customer";
//			PreparedStatement preparedStatement1 = conn.prepareStatement(getIDsql);
//			ResultSet maxID = preparedStatement1.executeQuery();
//			while(maxID.next())
//			{
//				System.out.println(maxID.getString("CustomerID"));
//			}
	}

	// View any orders that are not marked as completed
	public static void ViewOrders() throws SQLException, IOException {
		/*
		 * This should be subdivided into two options: print all orders (using simplified view) and print all orders (using simplified view) since a specific date.
		 *
		 * Once you print the orders (using either sub option) you should then ask which order I want to see in detail
		 *
		 * When I enter the order, print out all the information about that order, not just the simplified view.
		 *
		 */
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Would you like to:");
		System.out.println("(a) Display all orders");
		System.out.println("(b) Display orders since a specific date");
		System.out.println("Enter your option: ");
		char choice = (char) reader.read();
		try  {
			ArrayList<Order> orders = null;
			if (choice == 'a' || choice == 'A') {

				orders = DBNinja.getCurrentOrders(null);
				for (Order order : orders) {
					System.out.println(order.toSimplePrint());
				}


				System.out.print("Which order do you wish to see in detail? Enter the order ID:\n ");
				reader.readLine();
				int orderID = Integer.parseInt(reader.readLine());
				Map<Integer, Order> resultMap = orders.stream().collect(Collectors.toMap(x -> x.getOrderID(), x -> x));
				resultMap.get(orderID);

				System.out.println(resultMap.get(orderID).toString());



			}
			if (choice == 'b' || choice == 'B') {

				System.out.print("Enter the date in YYYY-MM-DD format: ");
				reader.readLine();
				String dateString = reader.readLine();
				orders = DBNinja.getCurrentOrders(dateString);
				for (Order order : orders) {
					System.out.println(order.toSimplePrint());
				}

				System.out.print("Which order do you wish to see in detail? Enter the order ID:\n ");
				int orderID = Integer.parseInt(reader.readLine());
				Map<Integer, Order> resultMap = orders.stream().collect(Collectors.toMap(x -> x.getOrderID(), x -> x));
				resultMap.get(orderID);

				System.out.println(resultMap.get(orderID).toString());


			}
		}catch (SQLException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	// When an order is completed, we need to make sure it is marked as complete
	public static void MarkOrderAsComplete() throws SQLException, IOException {


		/*All orders that are created through java (part 3, not the 7 orders from part 2) should start as incomplete
		 * 
		 * When this function is called, you should print all of the orders marked as complete 
		 * and allow the user to choose which of the incomplete orders they wish to mark as complete
		 * 
		 */

		ArrayList<Order> orders = DBNinja.getCurrentOrders(0);
		if(orders.size()!=0) {
			for (Order order : orders) {
				System.out.println(order.toSimplePrint());
			}
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));


			System.out.println("Enter the order Id you wish to mark as completed :");
			Integer orderId = Integer.parseInt(reader.readLine());
			Order order = null;
			int index = 0;
			for (int i = 0; i < orders.size(); i++) {
				if (orders.get(i).getOrderID() == orderId) {
					index = i;
				}
			}
			order = orders.get(index);
			DBNinja.CompleteOrder(order);
		}
		else{
			System.out.println("Every order is already completed");
		}



	}

	// See the list of inventory and it's current level
	public static void ViewInventoryLevels() throws SQLException, IOException {
		//print the inventory. I am really just concerned with the ID, the name, and the current inventory
		DBNinja.printInventory();


	}

	// Select an inventory item and add more to the inventory level to re-stock the
	// inventory
	public static void AddInventory() throws SQLException, IOException {
		/*
		 * This should print the current inventory and then ask the user which topping they want to add more to and how much to add
		 */
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Current Inventory Levels:");
		Menu.ViewInventoryLevels();
		System.out.println("Enter ID of the topping you wish to add to the Inventory: ");
		int toppingID = Integer.parseInt(reader.readLine());
		Topping t= DBNinja.getToppingFromId(toppingID);
		System.out.println("Enter the quantity to add to the Inventory: ");
		double quantity = Float.parseFloat(reader.readLine());
		DBNinja.AddToInventory(t,quantity);

	}

	// A function that builds a pizza. Used in our add new order function
	public static Pizza buildPizza(int orderID) throws SQLException, IOException {

		/*
		 * This is a helper function for first menu option.
		 * 
		 * It should ask which size pizza the user wants and the crustType.
		 * 
		 * Once the pizza is created, it should be added to the DB.
		 * 
		 * We also need to add toppings to the pizza. (Which means we not only need to add toppings here, but also our bridge table)
		 * 
		 * We then need to add pizza discounts (again, to here and to the database)
		 * 
		 * Once the discounts are added, we can return the pizza
		 */
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

		Pizza ret = null;


		return ret;
	}

	private static int getTopIndexFromList(int TopID, ArrayList<Topping> tops) {
		/*
		 * This is a helper function I used to get a topping index from a list of toppings
		 * It's very possible you never need to use a function like this
		 * 
		 */
		int ret = -1;


		return ret;
	}


	public static void PrintReports() throws SQLException, NumberFormatException, IOException {
		/*
		 * This function calls the DBNinja functions to print the three reports.
		 * 
		 * You should ask the user which report to print
		 */
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Which report do you wish to print? Enter\n" +
				"1. ToppingPopularity\n" +
				"2. ProfitByPizza\n" +
				"3. ProfitByOrderType");
		Integer option = Integer.parseInt(reader.readLine());

		switch (option) {
			case 1:
				DBNinja.printToppingPopReport();

				break;
			case 2:
				DBNinja.printProfitByPizzaReport();
				break;
			case 3:
				DBNinja.printProfitByOrderType();
				break;
		}
	}

}


//Prompt - NO CODE SHOULD TAKE PLACE BELOW THIS LINE
//DO NOT EDIT ANYTHING BELOW HERE, I NEED IT FOR MY TESTING DIRECTORY. IF YOU EDIT SOMETHING BELOW, IT BREAKS MY TESTER WHICH MEANS YOU DO NOT GET GRADED (0)

/*
CPSC 4620 Project: Part 3 â€“ Java Application Due: Thursday 11/30 @ 11:59 pm 125 pts

For this part of the project you will complete an application that will interact with your database. Much of the code is already completed, you will just need to handle the functionality to retrieve information from your database and save information to the database.
Note, this program does little to no verification of the input that is entered in the interface or passed to the objects in constructors or setters. This means that any junk data you enter will not be caught and will propagate to your database, if it does not cause an exception. Be careful with the data you enter! In the real world, this program would be much more robust and much more complex.

Program Requirements:

Add a new order to the database: You must be able to add a new order with pizzas to the database. The user interface is there to handle all of the input, and it creates the Order object in the code. It then calls DBNinja.addOrder(order) to save the order to the database. You will need to complete addOrder. Remember addOrder will include adding the order as well as the pizzas and their toppings. Since you are adding a new order, the inventory level for any toppings used will need to be updated. You need to check to see if there is inventory available for each topping as it is added to the pizza. You can not let the inventory level go negative for this project. To complete this operation, DBNinja must also be able to return a list of the available toppings and the list of known customers, both of which must be ordered appropropriately.

View Customers: This option will display each customer and their associated information. The customer information must be ordered by last name, first name and phone number. The user interface exists for this, it just needs the functionality in DBNinja

Enter a new customer: The program must be able to add the information for a new customer in the database. Again, the user interface for this exists, and it creates the Customer object and passes it to DBNinja to be saved to the database. You need to write the code to add this customer to the database. You do need to edit the prompt for the user interface in Menu.java to specify the format for the phone number, to make sure it matches the format in your database.

View orders: The program must be able to display orders and be sorted by order date/time from most recent to oldest. The program should be able to display open orders, all the completed orders or just the completed order since a specific date (inclusive) The user interface exists for this, it just needs the functionality in DBNinja

Mark an order as completed: Once the kitchen has finished prepping an order, they need to be able to mark it as completed. When an order is marked as completed, all of the pizzas should be marked as completed in the database. Open orders should be sorted as described above for option #4. Again, the user interface exists for this, it just needs the functionality in DBNinja

View Inventory Levels: This option will display each topping and its current inventory level. The toppings should be sorted in alphabetical order. Again, the user interface exists for this, it just needs the functionality in DBNinja

Add Inventory: When the inventory level of an item runs low, the restaurant will restock that item. When they do so, they need to enter into the inventory how much of that item was added. They will select a topping and then say how many units were added. Note: this is not creating a new topping, just updating the inventory level. Make sure that the inventory list is sorted as described in option #6. Again, the user interface exists for this, it just needs the functionality in DBNinja

View Reports: The program must be able to run the 3 profitability reports using the views you created in Part 2. Again, the user interface exists for this, it just needs the functionality in DBNinja

Modify the package DBConnector to contain your database connection information, this is the same information you use to connect to the database via MySQL Workbench. You will use DBNinja.connect_to_db to open a connection to the database. Be aware of how many open database connections you make and make sure the database is properly closed!
Your code needs to be secure, so any time you are adding any sort of parameter to your query that is a String, you need to use PreparedStatements to prevent against SQL injections attacks. If your query does not involve any parameters, or if your queries parameters are not coming from a String variable, then you can use a regular Statement instead.

The Files: Start by downloading the starter code files from Canvas. You will see that the user interface and the java interfaces and classes that you need for the assignment are already completed. Review all these files to familiarize yourself with them. They contain comments with instructions for what to complete. You should not need to change the user interface except to change prompts to the user to specify data formats (i.e. dashes in phone number) so it matches your database. You also should not need to change the entity object code, unless you want to remove any ID fields that you did not add to your database.

You could also leave the ID fields in place and just ignore them. If you have any data types that donâ€™t match (i.e. string size options as integers instead of strings), make the conversion when you pull the information from the database or add it to the database. You need to handle data type differences at that time anyway, so it makes sense to do it then instead of making changes to all of the files to handle the different data type or format.

The Menu.java class contains the actual user interface. This code will present the user with a menu of options, gather the necessary inputs, create the objects, and call the necessary functions in DBNinja. Again, you will not need to make changes to this file except to change the prompt to tell me what format you expect the phone number in (with or without dashes).

There is also a static class called DBNinja. This will be the actual class that connects to the database. This is where most of the work will be done. You will need to complete the methods to accomplish the tasks specified.

Also in DBNinja, there are several public static strings for different crusts, sizes and order types. By defining these in one place and always using those strings we can ensure consistency in our data and in our comparisons. You donâ€™t want to have â€œSMALLâ€� â€œsmallâ€� â€œSmallâ€� and â€œPersonalâ€� in your database so it is important to stay consistent. These strings will help with that. You can change what these strings say in DBNinja to match your database, as all other code refers to these public static strings.

Start by changing the class attributes in DBConnector that contain the data to connect to the database. You will need to provide your database name, username and password. All of this is available is available in the Chapter 15 lecture materials. Once you have that done, you can begin to build the functions that will interact with the database.

The methods you need to complete are already defined in the DBNinja class and are called by Menu.java, they just need the code. Two functions are completed (getInventory and getTopping), although for a different database design, and are included to show an example of connecting and using a database. You will need to make changes to these methods to get them to work for your database.

Several extra functions are suggested in the DBNinja class. Their functionality will be needed in other methods. By separating them out you can keep your code modular and reduce repeated code. I recommend completing your code with these small individual methods and queries. There are also additional methods suggested in the comments, but without the method template that could be helpful for your program. HINT, make sure you test your SQL queries in MySQL Workbench BEFORE implementing them in codeâ€¦it will save you a lot of debugging time!

If the code in the DBNinja class is completed correctly, then the program should function as intended. Make sure to TEST, to ensure your code works! Remember that you will need to include the MySQL JDBC libraries when building this application. Otherwise you will NOT be able to connect to your database.

Compiling and running your code: The starter code that will compile and â€œrunâ€�, but it will not do anything useful without your additions. Because so much code is being provided, there is no excuse for submitting code that does not compile. Code that does not compile and run will receive a 0, even if the issue is minor and easy to correct.

Help: Use MS Teams to ask questions. Do not wait until the last day to ask questions or get started!

Submission You will submit your assignment on Canvas. Your submission must include: â€¢ Updated DB scripts from Part 2 (all 5 scripts, in a folder, even if some of them are unchanged). â€¢ All of the class code files along with a README file identifying which class files in the starter code you changed. Include the README even if it says â€œI have no special instructions to shareâ€�. â€¢ Zip the DB Scripts, the class files (i.e. the application), and the README file(s) into one compressed ZIP file. No other formats will be accepted. Do not submit the lib directory or an IntellJ or other IDE project, just the code.

Testing your submission Your project will be tested by replacing your DBconnector class with one that connects to a special test server. Then your final SQL files will be run to recreate your database and populate the tables with data. The Java application will then be built with the new DBconnector class and tested.

No late submissions will be accepted for this assignment.*/

