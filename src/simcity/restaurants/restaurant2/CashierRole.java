package simcity.restaurants.restaurant2;

import Gui.RoleGui;
import agent.Role;

import simcity.Market.interfaces.MarketCashier;
import simcity.restaurants.Restaurant;

import simcity.restaurants.restaurant2.gui.CashierGui;

import simcity.restaurants.restaurant2.gui.Restaurant2CashierGui;
//import restaurant.Customer.AgentEvent;
//import restaurant.WaiterAgent;

import simcity.restaurants.restaurant2.interfaces.Cashier;
import simcity.restaurants.restaurant2.interfaces.Customer;
import simcity.restaurants.restaurant2.interfaces.Waiter;
import simcity.restaurants.restaurant4.Restaurant4CashierRole.Payment;
import simcity.PersonAgent;
import simcity.TheCity;

import java.util.*;

/**
 * Restaurant Cashier Agent
 */
public class CashierRole extends Role implements Cashier {
	public List<Waiter> waiters = Collections.synchronizedList(new LinkedList<Waiter>());
	public List<Check> checks = Collections.synchronizedList(new LinkedList<Check>());
	public List<checkOrder> checkOrders = Collections.synchronizedList(new LinkedList<checkOrder>());
	
	Map <String, Double> foodPrices = Collections.synchronizedMap(new HashMap<String, Double>());
	
	int marketIndex = 0;
	public double restaurantCash;
	private String name;
	//Timer timer = new Timer();
	public CashierGui gui = null;

	public CashierRole(PersonAgent person) {
		super(person);
		restaurantCash = 50000;
	}
	
	public CashierRole() {
		super();
		restaurantCash = 50000;
	}
	
	public void addFoodPrice(String food, double price) {
		foodPrices.put(food, price);
	}
	
	public void addWaiter(Waiter w) {
		waiters.add(w);
	}
	
	public String getName() {
		return name;
	}
	
	// Messages
	
	/* (non-Javadoc)
	 * @see restaurant.Cashier#msgNeedCheck(restaurant.WaiterAgent, double, int)
	 */
	@Override
	public void msgNeedCheck(Waiter w, double price, int table) {
		System.out.println(getName() + ": Received Request for check.");
		checkOrders.add(new checkOrder(price, w, table));
		stateChanged();
	}
	
	/* (non-Javadoc)
	 * @see restaurant.Cashier#msgHeresMyCheck(restaurant.CustomerAgent, restaurant.Check, double)
	 */
	@Override
	public void msgHeresMyCheck(Customer c, Check ch, double debt) {
		System.out.println(getName() + ": Got check from " + c.getName());
		c.setCash(c.getCash() - ch.moneyOwed - debt);
		if(c.getCash() < 0) {
			System.out.println(getName() + ": You don't have enough money. You owe $" + Math.abs(c.getCash()));
			c.addDebt(Math.abs(ch.moneyOwed - c.getCash()));
		}
	}
	
	@Override
	public void msgHereIsDeliveryCheck(Check ch, Market m) {
		System.out.println(getName() + ": Got check for delivery from " + m.getName() + " for $" + ch.moneyOwed);
		if(restaurantCash < ch.moneyOwed) {
			System.out.println(getName() + ": We are short by $" + (ch.moneyOwed - Math.abs(restaurantCash)));
		}
		else {
			System.out.println(getName() + ": Paid check for $" + ch.moneyOwed);
			restaurantCash -= ch.moneyOwed;
		}
	}
	
	public void amountDue(double a, MarketCashier c){  // from market Cashier 
		myPerson.Do("Got check, about to pay " + a);
		restaurantCash = restaurantCash-a;
		Restaurant r2 = (Restaurant) TheCity.getBuildingFromString("Restaurant 2");
		c.hereIsMoneyRestaurant(r2, a);
	}
	
	public void HereIsYourChange(double d, MarketCashier c ){
		restaurantCash = restaurantCash+ d ;
		myPerson.Do("Got change from market cashier " + d);
	}
	
	
	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	public boolean pickAndExecuteAnAction() {
		/* Think of this next rule as:
            Does there exist a table and customer,
            so that table is unoccupied and customer is waiting.
            If so seat him at the table.
		 */
		if(theManLeavingMe != null && checks.isEmpty() && checkOrders.isEmpty()) {
			leaveWork();
			return true;
		}
		
		synchronized(checkOrders) {
			for (int x = 0; x < checkOrders.size(); x++) {
				if (checkOrders.get(x).s == checkOrderState.Pending) {
					makeCheck(x);
				}
			}
		}
		
		
		
		return false;
		//we have tried all our rules and found
		//nothing to do. So return false to main loop of abstract agent
		//and wait.
	}
	
	// Actions
	
	public void makeCheck(int index) {
		checks.add(new Check(checkOrders.get(index).price, checkOrders.get(index).table));
		
		final int ind = index;
		checkOrders.get(ind).s = checkOrderState.checkMade;
		checkOrders.get(index).timer.schedule(new TimerTask() {
			Object cookie = 1;
			public void run() {
				System.out.println(getName() + ": Done making check for " + checkOrders.get(ind).waiter.getName() + " at table " + checkOrders.get(ind).table + ", cookie=" + cookie);
				
				checkOrders.get(ind).waiter.msgCheckReady(checks.get(ind));
				checkOrders.remove(ind);
				//stateChanged();
			}
		},
		4000);
		}
	
	
	//utilities
	
	public class checkOrder {
		Waiter waiter;
		Timer timer = new Timer();
		checkOrderState s;
		public double price;
		int table;
		
		public checkOrder(double amount, Waiter w, int t) {
			price = amount;
			waiter = w;
			table = t;
			
			s = checkOrderState.Pending;
		}
	}
	
	public enum checkOrderState {Pending, checkMade, withCustomer, customerPaying}

	public void setGui(RoleGui g) {
		super.setGui(g);
		gui = (CashierGui)g;
	}

	public CashierGui getGui() {
		return gui;
	}

}
