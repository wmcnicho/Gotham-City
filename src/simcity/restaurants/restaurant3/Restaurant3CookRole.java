package simcity.restaurants.restaurant3;

import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;

import simcity.PersonAgent;
import simcity.TheCity;
import Gui.RoleGui;
import Gui.ScreenFactory;
import agent.Role;
import simcity.Home.ResidentRole.HomeEvent;
import simcity.Market.MarketGui.MarketAnimationPanel;
import simcity.Market.interfaces.MarketCashier;
import simcity.Market.*;
import simcity.Market.Market;
import simcity.restaurants.restaurant3.Order.OrderState;
import simcity.restaurants.restaurant3.Restaurant3CustomerRole.AgentEvent;
import simcity.restaurants.restaurant3.gui.CookGui;
import simcity.restaurants.restaurant3.gui.HostGui;
import simcity.restaurants.restaurant3.interfaces.*;
import trace.AlertLog;
import trace.AlertTag;
import agent.Agent;
import simcity.Item;
//import restaurant.WaiterAgent.myCustomer;

/**
 * Restaurant Host Agent
 */
public class Restaurant3CookRole extends Role implements Cook{

	public List<Order> orders = Collections.synchronizedList(new Vector<Order>()); 
	public Map<String, Food> foods = Collections.synchronizedMap(new HashMap<String, Food>());
	public Map<String, Integer> neededFood = Collections.synchronizedMap(new HashMap<String, Integer>());
	public List<MarketRole> markets = Collections.synchronizedList(new ArrayList<MarketRole>());
	private String name;
	public HostGui hostGui = null;
	private CookGui cookGui;
	public Restaurant3CashierRole restCashier;
	public MarketCashier cashier;
	Timer timer = new Timer();
	private Timer cookingTimer = new Timer();
	Restaurant3 restaurant3;
	boolean needFood = false;
	boolean order = false;
	
	//private WaiterAgent waiter;


	public Restaurant3CookRole(PersonAgent p) {
		super(p);

		Food f = new Food ("Chicken");
		foods.put("Chicken", f);

		f = new Food ("Steak");
		foods.put("Steak", f);

		f = new Food ("Pizza");
		foods.put("Pizza", f);

		f = new Food ("Salad");
		foods.put("Salad", f);

		MarketRole market = new MarketRole("restaurant1");
		markets.add(market);
		market = new MarketRole("restaurant2");
		markets.add(market);
		market = new MarketRole("restaurant3");
		markets.add(market);

	}
	public Restaurant3CookRole(){
		//super();
		Food f = new Food ("Chicken");
		foods.put("Chicken", f);

		f = new Food ("Steak");
		foods.put("Steak", f);

		f = new Food ("Pizza");
		foods.put("Pizza", f);

		f = new Food ("Salad");
		foods.put("Salad", f);

		MarketRole market = new MarketRole("restaurant1");
		markets.add(market);
		market = new MarketRole("restaurant2");
		markets.add(market);
		market = new MarketRole("restaurant3");
		markets.add(market);
	}

	public String getName() {
		return name;
	}


	// Messages
	 public Vector<Item> getInventory(){
         Vector<Item> inventory = new Vector<Item>();
         for (Map.Entry<String, Food> f : foods.entrySet())//check food amount
         {
                 inventory.add(new Item(f.getKey(), f.getValue().amount));
                 /*AlertLog.getInstance().logInfo(AlertTag.GUI, "CookRole",
                                 "Key: " + f.getKey() + " Value: " + f.getValue());*/
         }
        /* AlertLog.getInstance().logInfo(AlertTag.GUI, "CookRole",
                         inventory.toString());*/
         return inventory;
	 }
 
	public void updateItem(String s, int hashCode) {
         // TODO Auto-generated method stub
         Food f = foods.get(s);
         foods.get(s).amount = hashCode;
	}
	
	public void orderIsOnStand() {
		stateChanged();
	}
	
	public void msgHereIsOrder(Order o) {
		AlertLog.getInstance().logInfo(AlertTag.REST3, this.getName(),
				"waiter gives cook an order from customer");
		//System.out.println("waiter gives " + this.getName() + " an order of from customer" );
		orders.add(o);
		o.os = OrderState.pending;
		stateChanged();

		//orders.add(new Order(w, choice, table, OrderState.pending))
	}

	public void msgFoodDone(Order o) {
		AlertLog.getInstance().logInfo(AlertTag.REST3, this.getName(),
				" The food is done cooking");
		//System.out.println("The food is done cooking");
		o.os = OrderState.doneCooking;
		stateChanged();
	}

	public void HereIsYourFood(Map<String, Integer> m, MarketWorkerRole worker){ 	 //from market
		needFood = false;
		order = false;
		worker.Delivered(restaurant3);
		for (Map.Entry<String, Integer> entry: m.entrySet()){
			Food f = foods.get(entry.getKey());
			f.amount +=  entry.getValue();
			foods.put(entry.getKey(), f);
			System.out.println("Got order from market, now I have " + f.type + " " + f.amount);
		}
	}

	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */

	public boolean pickAndExecuteAnAction() {
		if(theManLeavingMe != null && orders.isEmpty()){
			leaveWork();
			return true;
		}
		
		try {
			if (order == false){
			for (Map.Entry<String, Food> entry: foods.entrySet()){
				if (entry.getValue().amount <= 2){
					myPerson.Do("Need " + entry.getKey());
					int needed = entry.getValue().capacity - entry.getValue().amount;
					neededFood.put(entry.getKey(), needed);
					needFood = true;
				}
			}
			}
			if((needFood == true) && (order == false)){
				order = true;
				OrderFoodThatIsLow(neededFood);
				return true;
			}
			for (Order o: orders) {
				if(o.os == OrderState.outOfInventory) {
					messageWaiterOutOfInventory(o);
					AlertLog.getInstance().logInfo(AlertTag.REST3,
							this.getName(), "OUT OF INVENTORY ");
					//System.out.println("OUT OF INVENTORY");
					return true;
				}
				//	}
			}
			//	synchronized (orders){
			for (Order o: orders) {
				if(o.os == OrderState.doneCooking) {
					plateIt(o);
					return true;
				}
			}
			//	}
			//	synchronized(orders) {
			for (Order o: orders) {
				if(o.os == OrderState.pending) {
					tryToCookIt(o); 
					return true;
				}
			}
			if(RevolvingStand.checkStand()) {
				AlertLog.getInstance().logInfo(AlertTag.REST3,
						this.getName(), "Order available on stand ");
				//print("Order available on stand");
				goCheckStand();
				return true;
			}
		

			//	}
		} catch (ConcurrentModificationException e) {
			e.printStackTrace();
		}
		return false;

	}
	// Actions

	private void goCheckStand() {
		AlertLog.getInstance().logInfo(AlertTag.REST3, this.getName(),
				"Checking stand ");
		//print("Checking stand");
		try {
			final Order o = RevolvingStand.popOrder();
			if (o != null) {
				// process order
				cookingTimer.schedule(new TimerTask() {
					public void run() {
						print("Taking order off stand and preparing to cook");
						orders.add(o);
						//          cookGui.removeFromStand(temp);
						//stateChanged();
					}
				},
				1200);
			}	

		}catch(Exception e){

		}
	}

	private void messageWaiterOutOfInventory(Order o) {
		AlertLog.getInstance().logInfo(AlertTag.REST3, this.getName(),
				"Out of inventory!!");
		//System.out.println("Out of inventory!!");
		o.os = OrderState.reordering;
		o.waiter.msgWaiterOutOfFood(o);

	}

	private void plateIt(final Order o) {
		AlertLog.getInstance().logInfo(AlertTag.REST3, this.getName(),
				" cook is plating the food");
		//System.out.println(this.getName() + " is plating the food");
		CookGui.cooking = false;
		CookGui.plating = true;
		timer.schedule(new TimerTask() {
			public void run() {
				o.waiter.msgOrderIsReady(o);
			}
		}, 3000);
		//DoPlating(o);
		orders.remove(o);
	}

	private void tryToCookIt(final Order o) {
		//print("cookIt action");

		Food f = foods.get(o.choice.getType());

		if (checkInventory(f)) {
			//print("cook is cooking the food");
			int amount = f.getAmount() - 1;
			f.setAmount (amount);			
			//DoCooking(o);
			AlertLog.getInstance().logInfo(AlertTag.REST3,
					this.getName(), "cook is cooking the food");
			//System.out.println(this.getName() + " is cooking the food");
			o.os = OrderState.cooking;
			CookGui.order = o.choice.getType();
			CookGui.tableNumber = o.tableNumber;
			CookGui.cooking = true;
			CookGui.plating = false;
			int cookingTime = o.choice.getCookingTime();
			timer.schedule(new TimerTask() {
				public void run() {
					msgFoodDone(o);
				}
			}, cookingTime * 250);
			//DoCooking(o); 


			// check low threshold
			for (Map.Entry<String, Food> entry: foods.entrySet()){
				if (entry.getValue().amount <= 2){
					//person.Do("Need " + entry.getKey());
					int needed = entry.getValue().capacity - entry.getValue().amount;
					neededFood.put(entry.getKey(), needed);
					needFood = true;
				}
			}
			if((needFood == true) && (order == false)){
				order = true;
				OrderFoodThatIsLow(neededFood);
			}
			
		}
		else {
			o.os = OrderState.outOfInventory;
			o.outOfStock = true;
			System.out.println(this.getName() + " has run out of " + o.choice);
			stateChanged();
		}


		//if(f.amount == 0) {
		//o.waiter.outOfFood(o.table, o.choice);
		//orders.remove(o);
		//}

		//stateChanged();

		//timer.start(run(foodDone(o)));
		//foods.get(o.choice);
	}
	/*
	private void DoCooking(Order o) {
		System.out.println("Do Cooking");
		int cookingTime = o.choice.getCookingTime();
		try {
			Thread.currentThread().sleep(cookingTime*250);
		} catch(Exception e) {
			System.out.println(e.getMessage());
		}

	}
	 */


	private void OrderFoodThatIsLow(Map<String, Integer> neededFood) {
		AlertLog.getInstance().logInfo(AlertTag.REST3, this.getName(),
				"ordering food that is low");
		//print("ordering food that is low");
		//Market m = ((MarketAnimationPanel) ScreenFactory.getMeScreen("Market")).getMarket();
		//Market m = (Market) ((MarketAnimationPanel) ScreenFactory.getMeScreen("Market")).getMarket();
		//Market m = (Market) ScreenFactory.getMeScreen("Market")).getMarket();

		//cashier =  ((simcity.Market.Market) m).getCashier();
		//cashier.INeedFood(neededFood, this, restCashier);
		/*
		System.out.println(this.getName() + " is ordering from the market.");
		int amountNeeded = f.getCapacity() - f.getAmount();
		double bill = 0;

		// Market Order
		MarketOrder mo = new MarketOrder(f.getType(),amountNeeded);

		for (int i = 0; i < 3; ++i) {

			mo = markets.get(i).msgOrderRestock(mo);

			if (mo == null) {
				System.out.println("NO MORE FOOD LEFT IN THE TOWN");
				return;
			}

			else if (mo.amountNeeded == mo.amountProvided) {
				bill += mo.bill; 
				break;
			}

			// go to the next market
			else if (mo.amountNeeded > mo.amountProvided) {
				mo.amountNeeded -= mo.amountProvided;
				bill += mo.bill; 
			}
		}
		restCashier.msgPayMarketBill(bill);
		 */
		Market market = (Market) TheCity.getBuildingFromString("Market");
		restaurant3 = (Restaurant3) TheCity.getBuildingFromString("Restaurant 3");
		market.getCashier().INeedFood(neededFood, restaurant3);
		
	}

	private boolean checkInventory(Food f) {
		//print("checking Inventory");

		int amount = f.getAmount(); 
		if (amount > 0) {
			return true;
		}
		return false;
	}

	//utilities


	private void DoPlating(Order o) {
		//int cookingTime = o.choice.getCookingTime();

	}
	/*public void setGui(HostGui gui) {
		hostGui = gui;
	}*/
	public void setGui(RoleGui g) {
		super.setGui(g);
		cookGui = (CookGui)g;
	}

	public CookGui getGui() {
		return cookGui;
	}

	
	public void setMarketCashier(MarketCashier m){
		
		cashier = m;
	}

	public void setCashier(Restaurant3CashierRole cashier) {
		restCashier = cashier;

	}

}

