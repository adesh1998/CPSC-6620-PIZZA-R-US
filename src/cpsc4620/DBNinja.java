package cpsc4620;

import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.Date;

/*
 * This file is where most of your code changes will occur You will write the code to retrieve
 * information from the database, or save information to the database
 * 
 * The class has several hard coded static variables used for the connection, you will need to
 * change those to your connection information
 * 
 * This class also has static string variables for pickup, delivery and dine-in. If your database
 * stores the strings differently (i.e "pick-up" vs "pickup") changing these static variables will
 * ensure that the comparison is checking for the right string in other places in the program. You
 * will also need to use these strings if you store this as boolean fields or an integer.
 * 
 * 
 */

/**
 * A utility class to help add and retrieve information from the database
 */

public final class DBNinja {
	private static Connection conn;

	// Change these variables to however you record dine-in, pick-up and delivery, and sizes and crusts
	public final static String pickup = "pickup";
	public final static String delivery = "delivery";
	public final static String dine_in = "dinein";

	public final static String size_s = "small";
	public final static String size_m = "medium";
	public final static String size_l = "Large";
	public final static String size_xl = "XLarge";

	public final static String crust_thin = "Thin";
	public final static String crust_orig = "Original";
	public final static String crust_pan = "Pan";
	public final static String crust_gf = "Gluten-Free";


	private static boolean connect_to_db() throws SQLException, IOException {

		try {
			conn = DBConnector.make_connection();
			return true;
		} catch (SQLException e) {
			return false;
		} catch (IOException e) {
			return false;
		}

	}


	public static void addOrder(Order o) throws SQLException, IOException {
		connect_to_db();
		String sql = "insert into ordert(OrdertCustomerID, OrdertTimeStamp, OrdertPriceToCustomer,OrdertPriceToBusiness,OrdertType,IsCompleted) values(?, ?, ?,?,?,?)";

		PreparedStatement preparedStatement = conn.prepareStatement(sql);
		preparedStatement.setInt(1,o.getCustID());
		preparedStatement.setString(2,o.getDate());
		preparedStatement.setDouble(3, o.getCustPrice());
		preparedStatement.setDouble(4, o.getBusPrice());
		preparedStatement.setString(5, o.getOrderType());
		preparedStatement.setInt(6, o.getIsComplete());
		preparedStatement.executeUpdate();

		/*
		 * add code to add the order to the DB. Remember that we're not just
		 * adding the order to the order DB table, but we're also recording
		 * the necessary data for the delivery, dinein, and pickup tables
		 */

        conn.close();
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
	}

	public static void mapNames(int orderID, int size, int crust, int ordertype, ArrayList<Integer> discountList, ArrayList<Integer> toppingList, boolean[] isToppingDouble) throws SQLException, IOException {
		System.out.println("Entered Map function");
		String nsize = "";
		String ncrust = "";
		String nordertype = "";

		if(size==1)
			nsize = "small";
		else if(size==2)
			nsize = "medium";
		else if(size==3)
			nsize = "large";
		else if(size==4)
			nsize = "x-large";

		if(crust==1)
			ncrust = "Thin";
		else if(crust==2)
			ncrust = "Original";
		else if(crust==3)
			ncrust = "Pan";
		else if(crust==4)
			ncrust = "Gluten-Free";

		if(ordertype==1)
			nordertype = "pickup";
		else if(ordertype==2)
			nordertype = "delivery";
		else if(ordertype==3)
			nordertype = "dinein";

		Date curDate = new Date();
		String strdt = curDate.toString();

		System.out.println("Preparing pizza object");
		Pizza newpizza = new Pizza(0, nsize, ncrust, orderID, "processing", strdt, 0.0, 0.0);
		System.out.println("Calling add function");
//		addPizza(newpizza, discountList, toppingList, isToppingDouble);
	}

	public static void addPizza(Pizza p) throws SQLException, IOException {
		connect_to_db();
		String sql = "insert into pizza(PizzaOrderID, PizzaPriceToBusiness, PizzaPriceToCustomer,PizzaState,PizzaCrustType,PizzaSize) values(?, ?, ?,?,?,?)";

		PreparedStatement preparedStatement = conn.prepareStatement(sql);
		preparedStatement.setInt(1,p.getOrderID());
		preparedStatement.setDouble(2,p.getBusPrice());
		preparedStatement.setDouble(3, p.getCustPrice());
		preparedStatement.setString(4, p.getPizzaState());
		preparedStatement.setString(5, p.getCrustType());
		preparedStatement.setString(6, p.getSize());
		preparedStatement.executeUpdate();


		conn.close();
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
	}

	public static int getMaxPizzaID() throws SQLException, IOException {
		connect_to_db();
		String maxOrdSql = "SELECT * FROM pizza where PizzaID = (SELECT MAX(PizzaID) from pizza)";
		PreparedStatement maxOrderstmt = conn.prepareStatement(maxOrdSql);
		ResultSet maxOrder = maxOrderstmt.executeQuery();
		int maxOrderID=-1;
		while(maxOrder.next())
			maxOrderID = Integer.parseInt(maxOrder.getString("PizzaID"));


		//DO NOT FORGET TO CLOSE YOUR CONNECTION
		return maxOrderID;

		/*
		 * A function I needed because I forgot to make my pizzas auto increment in my DB.
		 * It goes and fetches the largest PizzaID in the pizza table.
		 * You wont need to implement this function if you didn't forget to do that
		 */


		//DO NOT FORGET TO CLOSE YOUR CONNECTION

	}

	public static void useTopping(Pizza p, Topping t, boolean isDoubled) throws SQLException, IOException //this function will update toppings inventory in SQL and add entities to the Pizzatops table. Pass in the p pizza that is using t topping
	{
		try {
			connect_to_db();
			double sizeAmount = 0.0;
			if (p.getSize() == "small") {
				sizeAmount = t.getPerAMT();
			} else if (p.getSize() == "medium") {
				sizeAmount = t.getMedAMT();
			} else if (p.getSize() == "large") {
				sizeAmount = t.getLgAMT();
			} else if (p.getSize() == "x-large") {
				sizeAmount = t.getXLAMT();
			}
			String updateStatement=null;
           if (isDoubled==true) {
			   System.out.println(sizeAmount);
			   updateStatement = "update topping set ToppingCurrentInvLvl=ToppingCurrentInvLvl- " + 2*sizeAmount + " where ToppingID= " + t.getTopID();
		   }
		   else{
				updateStatement = "update topping set ToppingCurrentInvLvl=ToppingCurrentInvLvl-" + sizeAmount + " where ToppingID= " + t.getTopID();
			}
			PreparedStatement preparedStatement = conn.prepareStatement(updateStatement);

			preparedStatement.executeUpdate();
//
//			String toppingpizza = "insert into pizzatopping(PizzatoppingPizzaID,PizzatoppingToppingID,PizzatoppingIsDouble) values(?,?,?)";
//
//
//			PreparedStatement pizzaPreparedStatement = conn.prepareStatement(toppingpizza);
//			pizzaPreparedStatement.setInt(1,p.getPizzaID());
//			pizzaPreparedStatement.setInt(2,t.getTopID());
//			pizzaPreparedStatement.setBoolean(3,isDoubled);
//
//			pizzaPreparedStatement.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}

			/*
			 * This function should 2 two things.
			 * We need to update the topping inventory every time we use t topping (accounting for extra toppings as well)
			 * and we need to add that instance of topping usage to the pizza-topping bridge if we haven't done that elsewhere
			 * Ideally, you should't let toppings go negative. If someone tries to use toppings that you don't have, just print
			 * that you've run out of that topping.
			 */


			//DO NOT FORGET TO CLOSE YOUR CONNECTION
		}
	}


	public static void usePizzaDiscount(Pizza p, Discount d) throws SQLException, IOException {
		connect_to_db();
		String updateStatement=null;
//		updateStatement = "update topping set ToppingCurrentInvLvl=ToppingCurrentInvLvl- " + 2*sizeAmount + " where ToppingID= " + t.getTopID();
//
//
//
//	      PreparedStatement preparedStatement = conn.prepareStatement(updateStatement);
//
//		  preparedStatement.executeUpdate();
		/*
		 * Helper function I used to update the pizza-discount bridge table. 
		 * You might use this, you might not depending on where / how to want to update
		 * this table
		 */


		//DO NOT FORGET TO CLOSE YOUR CONNECTION
	}

	public static void useOrderDiscount(Order o, Discount d) throws SQLException, IOException {
		connect_to_db();
		/*
		 * Helper function I used to update the pizza-discount bridge table. 
		 * You might use this, you might not depending on where / how to want to update
		 * this table
		 */


		//DO NOT FORGET TO CLOSE YOUR CONNECTION
	}


	public static void addCustomer(Customer c) throws SQLException, IOException {
		connect_to_db();
		String sql = "insert into customer(CustomerFirstName, CustomerLastName, CustomerPhone) values(?, ?, ?)";

		PreparedStatement preparedStatement = conn.prepareStatement(sql);
		preparedStatement.setString(1, c.getFName());
		preparedStatement.setString(2, c.getLName());
		preparedStatement.setString(3, c.getPhone());
		preparedStatement.executeUpdate();
		/*
		 * This should add a customer to the database
		 */

		conn.close();
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
	}


	public static void CompleteOrder(Order o) throws SQLException, IOException {

		try {
			connect_to_db();

			String updateStatement = "update ordert set IsCompleted = 1 where OrdertID = " + o.getOrderID() + " ;";

			PreparedStatement preparedStatement = conn.prepareStatement(updateStatement);

			preparedStatement.executeUpdate();
            String completed="Completed";
			String updatePizzaStatement = "update pizza set PizzaState = ?  where PizzaOrderID = ?" ;

			PreparedStatement pizzaPreparedStatement = conn.prepareStatement(updatePizzaStatement);
			pizzaPreparedStatement.setString(1,"Completed");
			pizzaPreparedStatement.setInt(2,o.getOrderID());

			pizzaPreparedStatement.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		/*
		 * add code to mark an order as complete in the DB. You may have a boolean field
		 * for this, or maybe a completed time timestamp. However you have it.
		 */


		//DO NOT FORGET TO CLOSE YOUR CONNECTION
	}


	public static void AddToInventory(Topping t, double toAdd) throws SQLException, IOException {
		connect_to_db();
		/*
		 * Adds toAdd amount of topping to topping t.
		 */


		//DO NOT FORGET TO CLOSE YOUR CONNECTION
	}


	public static void printInventory() throws SQLException, IOException {
		connect_to_db();

			String sql = "SELECT ToppingID, ToppingName, ToppingCurrentInvLvl FROM topping";
			PreparedStatement preparedStatement = conn.prepareStatement(sql);
			ResultSet results = preparedStatement.executeQuery();

			while(results.next()) {
				System.out.println("ToppingID: "+ results.getString("ToppingID")+" | Name: "+ results.getString("ToppingName") + " | CurrentInvLvl: " + results.getString("ToppingCurrentInvLvl"));
			}

			conn.close();

		/*
		 * I used this function to PRINT (not return) the inventory list.
		 * When you print the inventory (either here or somewhere else)
		 * be sure that you print it in a way that is readable.
		 * 
		 * 
		 * 
		 * The topping list should also print in alphabetical order
		 */


		//DO NOT FORGET TO CLOSE YOUR CONNECTION
	}


	public static ArrayList<Topping> getInventory() throws SQLException, IOException {
		connect_to_db();
		/*
		 * This function actually returns the toppings. The toppings
		 * should be returned in alphabetical order if you don't
		 * plan on using a printInventory function
		 */
		ArrayList<Topping> toppings=new ArrayList<Topping>();
		String sql = "SELECT * FROM topping";
		PreparedStatement preparedStatement = conn.prepareStatement(sql);
		ResultSet results = preparedStatement.executeQuery();

		while(results.next()) {
		int toppingID=results.getInt("ToppingID");
		String toppingName=results.getString("ToppingName");
		double toppingPriceToCustomer=results.getDouble("ToppingPriceToCustomer");
		double toppingPriceToBusiness=results.getDouble("ToppingPriceToBusiness");
		double	toppingQuantityForPersonal =results.getDouble("ToppingQuantityForPersonal");
		double	toppingQuantityForMedium =results.getDouble("ToppingQuantityForMedium");
		double	toppingQuantityForLarge =results.getDouble("ToppingQuantityForLarge");
		double	toppingQuantityForXLarge =results.getDouble("ToppingQuantityForXLarge");
		int	toppingCurrentInvLvl=results.getInt("ToppingCurrentInvLvl");
		int	toppingMinInvLvl =results.getInt("ToppingMinInvLvl");

		toppings.add(new Topping(toppingID,toppingName,toppingQuantityForPersonal,
				toppingQuantityForMedium,toppingQuantityForLarge,toppingQuantityForXLarge,
				toppingPriceToCustomer,toppingPriceToBusiness,toppingMinInvLvl,toppingCurrentInvLvl));
			}

		//DO NOT FORGET TO CLOSE YOUR CONNECTION
		return toppings;
	}


	public static ArrayList<Order> getCurrentOrders(String date) throws SQLException, IOException {
		ArrayList<Order> orders = new ArrayList<Order>();

		try {
			connect_to_db();

			String selectQuery = "select * from ordert";
			if (date != null) {
				selectQuery += " where (OrdertTimeStamp >= '" + date + " 00:00:00')";
			}
			selectQuery += " order by OrdertTimeStamp desc;";

			Statement statement = conn.createStatement();

			ResultSet record = statement.executeQuery(selectQuery);

			while (record.next()) {
				Integer orderId = record.getInt("OrdertID");
				String orderType = record.getString("OrdertType");
				Integer customerId = record.getInt("OrdertCustomerID");
				Double orderCost = record.getDouble("OrdertPriceToCustomer");
				Double orderPrice = record.getDouble("OrdertPriceToBusiness");
				String orderTimeStamp = record.getString("OrdertTimeStamp");
				Integer OrderCompleteState = record.getInt("IsCompleted");

				orders.add(
						new Order(orderId, customerId, orderType, orderTimeStamp, orderCost, orderPrice, OrderCompleteState));

			}
		} catch (Exception e) {
			e.printStackTrace();
		}






		/*
		 * This function should return an arraylist of all of the orders.
		 * Remember that in Java, we account for supertypes and subtypes
		 * which means that when we create an arrayList of orders, that really
		 * means we have an arrayList of dineinOrders, deliveryOrders, and pickupOrders.
		 * 
		 * Also, like toppings, whenever we print out the orders using menu function 4 and 5
		 * these orders should print in order from newest to oldest.
		 */


		//DO NOT FORGET TO CLOSE YOUR CONNECTION
		conn.close();
		return orders;
	}

	public static ArrayList<Order> getCurrentOrders(int status) throws SQLException, IOException {
		ArrayList<Order> orders = new ArrayList<Order>();

		try {
			connect_to_db();

			String selectQuery = "select * from ordert";
			if (status==0) {
				selectQuery += " where IsCompleted = " + status;
			}
			selectQuery += " order by OrdertTimeStamp desc;";

			Statement statement = conn.createStatement();

			ResultSet record = statement.executeQuery(selectQuery);

			while (record.next()) {
				Integer orderId = record.getInt("OrdertID");
				String orderType = record.getString("OrdertType");
				Integer customerId = record.getInt("OrdertCustomerID");
				Double orderCost = record.getDouble("OrdertPriceToCustomer");
				Double orderPrice = record.getDouble("OrdertPriceToBusiness");
				String orderTimeStamp = record.getString("OrdertTimeStamp");
				Integer OrderCompleteState = record.getInt("IsCompleted");

				orders.add(
						new Order(orderId, customerId, orderType, orderTimeStamp, orderCost, orderPrice, OrderCompleteState));

			}
		} catch (Exception e) {
			e.printStackTrace();
		}






		/*
		 * This function should return an arraylist of all of the orders.
		 * Remember that in Java, we account for supertypes and subtypes
		 * which means that when we create an arrayList of orders, that really
		 * means we have an arrayList of dineinOrders, deliveryOrders, and pickupOrders.
		 *
		 * Also, like toppings, whenever we print out the orders using menu function 4 and 5
		 * these orders should print in order from newest to oldest.
		 */


		//DO NOT FORGET TO CLOSE YOUR CONNECTION
		conn.close();
		return orders;
	}

	public static ArrayList<Order> sortOrders(ArrayList<Order> list) {
		/*
		 * This was a function that I used to sort my arraylist based on date.
		 * You may or may not need this function depending on how you fetch
		 * your orders from the DB in the getCurrentOrders function.
		 */


		//DO NOT FORGET TO CLOSE YOUR CONNECTION
		return null;

	}

	public static boolean checkDate(int year, int month, int day, String dateOfOrder) {
		//Helper function I used to help sort my dates. You likely wont need these


		return false;
	}


	/*
	 * The next 3 private functions help get the individual components of a SQL datetime object. 
	 * You're welcome to keep them or remove them.
	 */
	private static int getYear(String date)// assumes date format 'YYYY-MM-DD HH:mm:ss'
	{
		return Integer.parseInt(date.substring(0,4));
	}

	private static int getMonth(String date)// assumes date format 'YYYY-MM-DD HH:mm:ss'
	{
		return Integer.parseInt(date.substring(5, 7));
	}

	private static int getDay(String date)// assumes date format 'YYYY-MM-DD HH:mm:ss'
	{
		return Integer.parseInt(date.substring(8, 10));
	}


	public static double getBaseCustPrice(String size, String crust) throws SQLException, IOException {
		connect_to_db();
		double bp = 0.0;
		// add code to get the base price (for the customer) for that size and crust pizza Depending on how
		// you store size & crust in your database, you may have to do a conversion
		String selectQuery = "select * from baseprice;";



		PreparedStatement statement = conn.prepareStatement(selectQuery);
		ResultSet record = statement.executeQuery(selectQuery);
        while(record.next()){
			String crusttype=record.getString("BasepriceCrustType");
			String sizebase=record.getString("BasepriceSize");


			if(crusttype.equals(crust) && sizebase.equals(size)) {
				System.out.println(crusttype);
				bp = record.getDouble("BasepriceToCustomer");
			}
		}

		//DO NOT FORGET TO CLOSE YOUR CONNECTION
		return bp;
	}

	public static String getCustomerName(int CustID) throws SQLException, IOException {
		/*
		 *This is a helper function I used to fetch the name of a customer
		 *based on a customer ID. It actually gets called in the Order class
		 *so I'll keep the implementation here. You're welcome to change
		 *how the order print statements work so that you don't need this function.
		 */
		connect_to_db();
		String ret = "";
		String query = "Select CustomerFirstName, CustomerLastName From customer WHERE CustomerID=" + CustID + ";";
		Statement stmt = conn.createStatement();
		ResultSet rset = stmt.executeQuery(query);

		while(rset.next()) {
			ret = rset.getString(1) + " " + rset.getString(2);
		}
		conn.close();
		return ret;
	}

	public static double getBaseBusPrice(String size, String crust) throws SQLException, IOException {
		connect_to_db();
		double bp = 0.0;
		// add code to get the base cost (for the business) for that size and crust pizza Depending on how
		// you store size and crust in your database, you may have to do a conversion

		String selectQuery = "select * from baseprice;";



		PreparedStatement statement = conn.prepareStatement(selectQuery);
		ResultSet record = statement.executeQuery(selectQuery);
		while(record.next()){
			String crusttype=record.getString("BasepriceCrustType");
			String sizebase=record.getString("BasepriceSize");


			if(crusttype.equals(crust) && sizebase.equals(size)) {
				System.out.println(size);
				bp = record.getDouble("BasepriceToBusiness");
			}
		}
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
		return bp;
	}


	public static ArrayList<Discount> getDiscountList() throws SQLException, IOException {
		ArrayList<Discount> discs = new ArrayList<Discount>();
		connect_to_db();
		//returns a list of all the discounts.
		String getDiscountssql = "SELECT * FROM discount";
		PreparedStatement dpreparedStatement = conn.prepareStatement(getDiscountssql);
		ResultSet discount = dpreparedStatement.executeQuery();
		while(discount.next()){
			int discountID=discount.getInt("DiscountID");
			String discountName=discount.getString("DiscountName");
			boolean isPercent=discount.getBoolean("IsPercent");
			double amount=discount.getDouble("DiscountValue");
			discs.add(new Discount(discountID,discountName,amount,isPercent));
		}

		//DO NOT FORGET TO CLOSE YOUR CONNECTION
		return discs;
	}


	public static ArrayList<Customer> getCustomerList() throws SQLException, IOException {
		ArrayList<Customer> custs = new ArrayList<Customer>();
		connect_to_db();
		String sql = "SELECT * FROM customer";
		PreparedStatement preparedStatement = conn.prepareStatement(sql);

		ResultSet records = preparedStatement.executeQuery();
		while (records.next()) {
			int custID=records.getInt("CustomerID");
			String fName=records.getString("CustomerFirstName");
			String lName=records.getString("CustomerLastName");
			String phone=records.getString("CustomerPhone");


			custs.add(
					new Customer(custID,fName,lName, phone));

		}


		/*
		 * return an arrayList of all the customers. These customers should
		 *print in alphabetical order, so account for that as you see fit.
		*/


		//DO NOT FORGET TO CLOSE YOUR CONNECTION
		conn.close();
		return custs;
	}

	public static int getNextOrderID() throws SQLException, IOException {
		/*
		 * A helper function I had to use because I forgot to make
		 * my OrderID auto increment...You can remove it if you
		 * did not forget to auto increment your orderID.
		 */
		connect_to_db();
		String maxOrdSql = "SELECT * FROM ordert where OrdertID = (SELECT MAX(OrdertID) from ordert)";
		PreparedStatement maxOrderstmt = conn.prepareStatement(maxOrdSql);
		ResultSet maxOrder = maxOrderstmt.executeQuery();
		int maxOrderID=-1;
		while(maxOrder.next())
			maxOrderID = Integer.parseInt(maxOrder.getString("OrdertID"));


		//DO NOT FORGET TO CLOSE YOUR CONNECTION
		return maxOrderID+1;
	}

	public static void printToppingPopReport() throws SQLException, IOException {
		connect_to_db();
		/*
		 * Prints the ToppingPopularity view. Remember that these views
		 * need to exist in your DB, so be sure you've run your createViews.sql
		 * files on your testing DB if you haven't already.
		 * 
		 * I'm not picky about how they print (other than that it should
		 * be in alphabetical order by name), just make sure it's readable.
		 */


		//DO NOT FORGET TO CLOSE YOUR CONNECTION
	}

	public static void printProfitByPizzaReport() throws SQLException, IOException {
		connect_to_db();
		/*
		 * Prints the ProfitByPizza view. Remember that these views
		 * need to exist in your DB, so be sure you've run your createViews.sql
		 * files on your testing DB if you haven't already.
		 * 
		 * I'm not picky about how they print, just make sure it's readable.
		 */


		//DO NOT FORGET TO CLOSE YOUR CONNECTION
	}

	public static void printProfitByOrderType() throws SQLException, IOException {
		connect_to_db();
		/*
		 * Prints the ProfitByOrderType view. Remember that these views
		 * need to exist in your DB, so be sure you've run your createViews.sql
		 * files on your testing DB if you haven't already.
		 * 
		 * I'm not picky about how they print, just make sure it's readable.
		 */


		//DO NOT FORGET TO CLOSE YOUR CONNECTION
	}


	public static void dineIn(int i, Integer tableNumber) {
		
	}

	public static void updatePickUp(int i) {
	}

	public static void updateDelivery(int i, String customerAddress) {
//		connect_to_db();
	}

	public static Topping getToppingFromId(int toppingID) throws SQLException, IOException {

		connect_to_db();
		Topping t=null;
		String maxOrdSql = "SELECT * FROM topping where toppingId = "+toppingID;
		PreparedStatement maxOrderstmt = conn.prepareStatement(maxOrdSql);
		ResultSet results = maxOrderstmt.executeQuery();

		while(results.next())
		{
			int tID=results.getInt("ToppingID");
			String toppingName=results.getString("ToppingName");
			double toppingPriceToCustomer=results.getDouble("ToppingPriceToCustomer");
			double toppingPriceToBusiness=results.getDouble("ToppingPriceToBusiness");
			double	toppingQuantityForPersonal =results.getDouble("ToppingQuantityForPersonal");
			double	toppingQuantityForMedium =results.getDouble("ToppingQuantityForMedium");
			double	toppingQuantityForLarge =results.getDouble("ToppingQuantityForLarge");
			double	toppingQuantityForXLarge =results.getDouble("ToppingQuantityForXLarge");
			int	toppingCurrentInvLvl=results.getInt("ToppingCurrentInvLvl");
			int	toppingMinInvLvl =results.getInt("ToppingMinInvLvl");

			t=new Topping(tID,toppingName,toppingQuantityForPersonal,
					toppingQuantityForMedium,toppingQuantityForLarge,toppingQuantityForXLarge,
					toppingPriceToCustomer,toppingPriceToBusiness,toppingMinInvLvl,toppingCurrentInvLvl);
		}
		return t;
		}

	public static void pizzaToppingConnection(Integer pizzaId, Integer toppingId, Integer isComplete) throws SQLException, IOException {
		connect_to_db();
		String sql = "insert into pizzatopping(PizzatoppingPizzaID, PizzatoppingToppingID, PizzatoppingIsDouble) values(?, ?, ?)";

		PreparedStatement preparedStatement = conn.prepareStatement(sql);
		preparedStatement.setInt(1,pizzaId);
		preparedStatement.setInt(2, toppingId);
		preparedStatement.setInt(3, isComplete);
		preparedStatement.executeUpdate();
       conn.close();

	}

	public static void pizzaDiscountConnection(Integer pizzaId, Integer discountId)throws SQLException, IOException {

		connect_to_db();
		String sql = "insert into pizzadiscount(PizzadiscountPizzaId, PizzadiscountDiscountID) values(?, ?)";

		PreparedStatement preparedStatement = conn.prepareStatement(sql);
		preparedStatement.setInt(1, pizzaId);
		preparedStatement.setInt(2, discountId);

		preparedStatement.executeUpdate();
		conn.close();
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
	}

	public static void orderDiscountConnection(Integer orderId, Integer discountId) throws SQLException, IOException {

		connect_to_db();
		String sql = "insert into discountorder(DiscountorderOrdertID, DiscountorderDiscountID) values(?, ?)";

		PreparedStatement preparedStatement = conn.prepareStatement(sql);
		preparedStatement.setInt(1, orderId);
		preparedStatement.setInt(2, discountId);

		preparedStatement.executeUpdate();
		conn.close();
	}
	}
