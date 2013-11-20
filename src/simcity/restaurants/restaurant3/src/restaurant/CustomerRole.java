package simcity.restaurant_evan.src.restaurant;

import java.util.Random;
import java.util.Timer;

import simcity.PersonAgent;
import simcity.agent.Role;
import simcity.restaurant_evan.src.restaurant.gui.CustomerGui;
import simcity.restaurant_evan.src.restaurant.interfaces.Customer;
import agent.Agent;
//import restaurant.WaiterAgent.Menu;

/**
 * Restaurant customer agent.
 */
public class CustomerRole extends Role implements Customer {
	private String name;
	private int hungerLevel = 10;        // determines length of meal
	Timer timer = new Timer();
	private CustomerGui customerGui;
	//List<AgentEvent> events;
	// agent correspondents
	private HostRole host;
	private WaiterRole waiter;
	public Menu menu;
	private CashierRole cashier;
	public int tableNum;
	private double wallet;
	public Order order;
	public boolean leaveIfOutOfStock = false;
	public Random random = new Random();
	public boolean leaveRestaurant = random.nextBoolean();
	
	//private String choice;

	 //hack for gui
	public enum AgentState
	{DoingNothing, WaitingInRestaurant, Seated, Waiting, Eating, Paying, Leaving}; //Seated
	private AgentState state = AgentState.DoingNothing;//The start state

	//other agentstates:Seated, askedToOrder, ordered, DoneEating

	public enum AgentEvent 
	{none, gotHungry, followHost, seated, AskedToOrder, ordered, reordering, 
		foodDelivered, checkDelivered, doneEating, donePaying, doneLeaving};
	AgentEvent event = AgentEvent.none;
 

	/**
	 * Constructor for CustomerAgent class
	 *
	 * @param name name of the customer
	 * @param gui  reference to the customergui so the customer can send it messages
	 */
	public CustomerRole(){
		
		//hello
		this.name = name;
		Random random = new Random();
		this.wallet = random.nextInt(20); //random generated amount of money in wallet.
		//this.wallet = 7;
		//this.wallet = 0;
		System.out.println(" initially has $" + wallet + " .");
	}

	
	/**
	 * hack to establish connection to Host agent.
	 */
	public void setHost(HostRole host) {
		this.host = host;
	}
	public void setCashier(CashierRole cashier) {
		this.cashier = cashier;
	}

	public String getCustomerName() {
		return name;
	}
	
	public double getWallet() {  
		return wallet;
	}
	
	public void setWallet(double newWallet) {
		this.wallet = newWallet;
	}
	
	// Messages

	public void gotHungry() {//from animation
		System.out.println("I'm hungry");
		event = AgentEvent.gotHungry;
		stateChanged();
	}

	public void msgFollowMeToTable(Menu menu, int tnum) { //tnum
		System.out.println("Follow me to the table");
		this.menu = menu;
		event = AgentEvent.followHost;
		stateChanged();
		tableNum = tnum;
	}

	public void msgWhatWouldYouLike() {
		System.out.println(this.getName() + " is asked by the waiter what he/she would like to eat.");
		event = AgentEvent.AskedToOrder;
		stateChanged();
	}

	public void msgHereIsYourFood() {
		System.out.println(this.getName() + " received his/her order from the waiter.");
		//state = AgentState.Eating;
		event = AgentEvent.foodDelivered;
		stateChanged();
	}
	public void msgHereIsYourCheck(Order o) {
		System.out.println(this.getName() + " received the check from the waiter.");
		this.order = o;
		event = AgentEvent.checkDelivered;
		stateChanged();
	}
	
	public void msgAnimationFinishedGoToSeat() {
		//from animation
		//System.out.println("msgFinishedGoToSeat");
		event = AgentEvent.seated;
		stateChanged();
	}
	public void msgAnimationFinishedGoToCashier() {
		event = AgentEvent.donePaying;
		stateChanged();
	}

	public void msgAnimationFinishedLeaveRestaurant() {
		//from animation
		System.out.println("Received msgFinishedLeaveRestaurant");
		event = AgentEvent.doneLeaving;
		stateChanged();
	}

	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	public boolean pickAndExecuteAnAction() {
		//	CustomerAgent is a finite state machine

		if (state == AgentState.DoingNothing && event == AgentEvent.gotHungry ){
			state = AgentState.WaitingInRestaurant;
			goToRestaurant();
			return true;
		}
		if (state == AgentState.WaitingInRestaurant && event == AgentEvent.followHost ){
			state = AgentState.Seated;
			SitDown();
			return true;
		}
		/*if(state == AgentState.BeingSeated && event == AgentEvent.seated){
			//state = AgentState.AskedToOrder;
			
			try {
				System.out.System.out.printlnln("Customer is looking at the menu");
				Thread.sleep(3000);
				System.out.System.out.printlnln("Customer is ready to order");
			
			}catch(Exception e) {
				System.out.System.out.println(e.getMessage());
			}
			return true;
		}*/
		if (state == AgentState.Seated && event == AgentEvent.AskedToOrder){
			state = AgentState.Waiting;
			SayChoice();
			return true;
		}
		
		if (state == AgentState.Waiting && event == AgentEvent.AskedToOrder){

			if (order.outOfStock && leaveIfOutOfStock) {
				state = AgentState.Leaving;
				leaveTable();
				return true;
			}
			else {
				state = AgentState.Waiting;
				SayChoice();
				return true;
			}
		}
		
	//	if (state == AgentState.Seated && event == AgentEvent.AskedToOrder){
	//		state = AgentState.Waiting;
		//	ReOrder();
		//	return true;
	//	}
		//if (state == AgentState.WaitingInRestaurant && event == AgentEvent.ordered){
		//	state = AgentState.Eating;

		//	return true;
		//

		if (state == AgentState.Waiting && event == AgentEvent.foodDelivered){
			state = AgentState.Eating;
			System.out.println("food delivered if");
			EatFood();
			return true;
		}
		if (state == AgentState.Eating && event == AgentEvent.doneEating){
			state = AgentState.Waiting;
			waiter.msgIWantTheCheck(this);
			//PayCheck();
			return true;
		}
		if (state == AgentState.Waiting && event == AgentEvent.checkDelivered){
			state = AgentState.Paying;
			PayCheck();
			return true;
		}
		if (state == AgentState.Paying && customerGui.getXPos() == 600 && customerGui.getYPos() == 200 && event != AgentEvent.donePaying){
			//System.out.println("*************inside if");
			cashier.msgCustomerPayingCheck(order);
			return true;
		}
		if (state == AgentState.Paying && event == AgentEvent.donePaying){
			state = AgentState.Leaving;
			leaveTable();
			return true;
		}
		if (state == AgentState.Leaving && event == AgentEvent.doneLeaving){
			state = AgentState.DoingNothing;
			//no action
			return true;
		}
		return false;
	}

	// Actions

	private void goToRestaurant() {
		System.out.println(this.getName() + " is going to the restaurant");
//		Do("Going to restaurant");
		host.msgIWantToEat(this);//send our instance, so he can respond to us
	}
	/*private void tellHostIWantToEat() {
//		Do("Telling host they are hungry");
		System.out.println("Telling host they are hungry");
	}
	*/
	private void SitDown() {
//		Do("Being seated. Going to table");
		System.out.println(this.getName() + " is being seated and going to a table.");
		customerGui.DoGoToSeat(tableNum);//hack; only one table
		event = AgentEvent.seated;
		stateChanged();
	}

	private void SayChoice() {
//		Do("Customer orders food");
		System.out.println("Customer says what type of food he/she wants.");
		if (wallet < cheapestFood()) {
			//Random random = new Random();
			//boolean leaveRestaurant = random.nextBoolean();
			if (leaveRestaurant) {
				System.out.println("Customer " + getName() + " does not have enough money and chose to leave the restaurant.");
				leaveTable();
				//waiter.setCustomerStateLeaving(this);
				return;
			}
			else if(!leaveRestaurant){
				System.out.println("Customer " + getName() + " does not have enough money and chose to order anyway.");
				//waiter.setCustomerStateReadyToOrder(this);
			}
				
		}
		
		
		String choice = randomizeFoodChoice();
		System.out.println(this.getName() + " orders food.");
		customerGui.order=choice;
		customerGui.waitingForFood=true;
		waiter.msgHereIsMyChoice(this, choice);
		
		event = AgentEvent.ordered;
		stateChanged();
	}
	
	private double cheapestFood() {
		double min = Double.MAX_VALUE;
		
		for (int i = 0; i < this.menu.foods.size(); ++i) {
			if (this.menu.foods.get(i).getFoodPrice() < min)
				min = this.menu.foods.get(i).getFoodPrice();
		}
		
		return min;
	}

	private void EatFood() {
		customerGui.waitingForFood=false;
		customerGui.receivedFood=true;
		System.out.println(this.getName() + " is eating his/her food");
		//This next complicated line creates and starts a timer thread.
		//We schedule a deadline of getHungerLevel()*1000 milliseconds.
		//When that time elapses, it will call back to the run routine
		//located in the anonymous class created right there inline:
		//TimerTask is an interface that we implement right there inline.
		//Since Java does not all us to pass functions, only objects.
		//So, we use Java syntactic mechanism to create an
		//anonymous inner class that has the public method run() in it.
		/*timer.schedule(new TimerTask() {
			//Object cookie = 1;
			public void run() {
				
			}
		},
		5000);//getHungerLevel() * 1000);//how long to wait before running task*/
		try {
			Thread.currentThread().sleep(5000);
		}catch(Exception e) {
			System.out.print(e.getMessage());
		}
		
		System.out.println("Done eating");
		event = AgentEvent.doneEating;
		customerGui.receivedFood=false;
		//isHungry = false;
		stateChanged();
	}
	
	private void PayCheck() {
		System.out.println(this.getName() + " is going to the cashier and paying the check");
		customerGui.DoGoToCashier();
		if (wallet >= order.totalPrice) {
			this.setWallet(wallet - order.totalPrice);
			cashier.setRestaurantRevenue(order.totalPrice);
		}
		else {
			System.out.println("Customer owes $" + order.totalPrice + " next time.");
			cashier.owed.put(this, order.totalPrice);
		}
			
		
		//event = AgentEvent.donePaying;
		
		stateChanged();
	}
	private void leaveTable() {
		System.out.println(this.getName() + " is leaving the restaurant");
		waiter.msgDoneEatingAndLeavingTable(this);
		//host.msgLeavingTable(this);
		customerGui.DoExitRestaurant();
		event = AgentEvent.doneLeaving;
		stateChanged();
	}

	// Accessors, etc.

	public String getName() {
		return name;
	}

	public int getHungerLevel() {
		return hungerLevel;
	}
	public void setDonePayingState() {
		event = AgentEvent.donePaying;
		stateChanged();
	}

	private String randomizeFoodChoice() {
		String choice = null;
		
		Random r = new Random();
		int x;
		
		if (wallet < cheapestFood()) {
			x = r.nextInt(4) + 1;
		} 
		else {
		
			int i =0;
			for ( ; i < 4 && wallet > menu.foods.get(i).getFoodPrice(); ++i);
			
			if (i == 1) { //only can afford the cheapest food
				leaveIfOutOfStock = true;
			}
			x = r.nextInt(i) + 1;
		}
		
		if(x == 1) {
			choice = "Salad";
			System.out.println("choice = Salad");
		}
		if(x == 2) {
			choice = "Pizza";
			System.out.println("choice = pizza");
		}
		if(x == 3){
			choice = "Chicken";
			System.out.println("choice = Chicken");
		}
		if(x == 4){
			choice = "Steak";
			System.out.println("choice = Steak");
		}
		

		return choice;
	}

	public void setHungerLevel(int hungerLevel) {
		this.hungerLevel = hungerLevel;
		//could be a state change. Maybe you don't
		//need to eat until hunger lever is > 5?
	}

	public String toString() {
		return "customer " + getName();
	}

	public void setGui(CustomerGui g) {
		customerGui = g;
	}

	public void setWaiter(WaiterRole waiter) {
		this.waiter = waiter;
	}

	public CustomerGui getGui() {
		return customerGui;
	}
	public double getWalletAmount() {
		return wallet;
	}
}
