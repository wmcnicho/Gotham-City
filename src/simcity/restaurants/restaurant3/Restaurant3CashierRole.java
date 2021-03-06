package simcity.restaurants.restaurant3;

import java.util.*;

import simcity.Market.interfaces.MarketCashier;
import simcity.restaurants.Restaurant;
import simcity.restaurants.restaurant3.Order.OrderState;
import simcity.restaurants.restaurant3.gui.*;
import simcity.restaurants.restaurant3.interfaces.*;

import javax.print.attribute.standard.MediaSize.NA;

import simcity.PersonAgent;
import simcity.TheCity;
import trace.AlertLog;
import trace.AlertTag;
import Gui.RoleGui;
import agent.Role;
import agent.Agent;

import java.util.concurrent.Semaphore;
import java.util.Timer;
import java.util.TimerTask;
//import restaurant.WaiterAgent.myCustomer;

/**
 * Restaurant Host Agent
 */
//We only have 2 types of agents in this prototype. A customer and an agent that
//does all the rest. Rather than calling the other agent a waiter, we called him
//the HostAgent. A Host is the manager of a restaurant who sees that all
//is proceeded as he wishes.
public class Restaurant3CashierRole extends Role implements Cashier {
	
	public List<Order> orders = Collections.synchronizedList(new Vector<Order>()); 
	public Map<String, Food> foods = Collections.synchronizedMap(new HashMap<String, Food>());
	public List<Payment> payments = new ArrayList<Payment>();
	private String name;
	private Food f;

	public HostGui hostGui = null;
	public Restaurant3CookRole cook;

	public CashierGui gui = null;
	

	//private WaiterAgent waiter;
	//CashierState cashState;
	public enum CashierState {idle, calculating}
	CashierState cashState;
	public Map<Customer, Double> owed = Collections.synchronizedMap(new HashMap<Customer, Double>());
	private double restaurantRevenue = 1000;
	 
	public Restaurant3CashierRole(PersonAgent p) {
		super(p);

		cashState = CashierState.idle;
	
		
		f = new Food ("Chicken");
		foods.put("Chicken", f);
		
		f = new Food ("Steak");
		foods.put("Steak", f);
		
		f = new Food ("Pizza");
		foods.put("Pizza", f);
		
		f = new Food ("Salad");
		foods.put("Salad", f);
		
	}
	public Restaurant3CashierRole() {
		super();
	}

	public String getMaitreDName() {
		return name;
	}

	public String getName() {
		return name;
	}
	
	public void amountDue(double a, MarketCashier c){
        payments.add(new Payment(c, a));
        stateChanged();
}
	
	// Messages
	public void msgRequestCheck(Customer cust, Order o) {
		AlertLog.getInstance().logInfo(AlertTag.REST3, this.getName(),
				"Waiter needs the check from the cashier for his/her customer ");
		//System.out.println("Waiter needs the check from the cashier for his/her customer");
		orders.add(o);
		o.os = OrderState.requestCheck;
		
		stateChanged();		
	}
	
	public void msgCustomerPayingCheck(Order o) {
		AlertLog.getInstance().logInfo(AlertTag.REST3, this.getName(),
				"The Customer is paying the check");
		//System.out.println("The Customer is paying the check");
		o.os = OrderState.paying;
		
		stateChanged();
		
	}
	public void HereIsYourChange(double d, MarketCashier c ){
        restaurantRevenue = restaurantRevenue + d;
        
        myPerson.Do("Got change from market cashier " + d);
        for (Payment payment: payments){
                if (payment.cashier == c){
                payment.state = Payment.OrderState.gotChange;
                stateChanged();
                }
        }
}
	
	//public void msgPayMarketBill(double bill) {
		
	//	this.restaurantRevenue -= bill;
	//}

	
	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	public boolean pickAndExecuteAnAction() {
		
		/* Think of this next rule as:
            Does there exist a table and customer,
            so that table is unoccupied and customer is waiting.
            If so seat him at the table.
		 */
		if(theManLeavingMe != null && orders.isEmpty()) {
			leaveWork();
			return true;
		}
		
		for (Payment payment: payments){
            if (payment.state == Payment.OrderState.pending){
                    payment.state = Payment.OrderState.paying;
                    Pay(payment);
                    return true;
            }
            if(payment.state == Payment.OrderState.gotChange){
                    payment.state = Payment.OrderState.delete;
                    Remove(payment);
                    return true;
            }
            
    }
		for(Order o:orders) {
			if(o.os == OrderState.requestCheck){
				calculate(o);
				return true;
			}
		}
		
		for(Order o:orders) {
			if(o.os == OrderState.paying){
				receivingCheck(o);
				return true;
			}
		}
		
		return false;
		
		
	}
	
	private void receivingCheck(Order o) {
		o.os = OrderState.idle;
		stateChanged();
		o.customer.setDonePayingState();
		orders.remove(o);
	}
	
	// Actions
	public void Pay(Payment p){
        double money = p.amountDue;
        restaurantRevenue = restaurantRevenue - money;
        Restaurant restaurant3 = (Restaurant) TheCity.getBuildingFromString("Restaurant 3");
        p.cashier.hereIsMoneyRestaurant(restaurant3, money);
	}
	private void Remove(Payment payment) {
        payments.remove(payment);
        stateChanged();
        
	}
	private  void calculate(Order o) {
		AlertLog.getInstance().logInfo(AlertTag.REST3, this.getName(),
				"calculating order");
		//System.out.println("calculating order");
		
		// check if in owed list
		if (owed.get(o.customer) != null) {
			double previousBalance = owed.get(o.customer);
			o.setTotalPrice(o.choice.getFoodPrice() + previousBalance);
		}
		else 
			o.setTotalPrice(o.choice.getFoodPrice());
		
		
		o.waiter.msgWaiterHereIsCheck(o);
		//if(orders.size() == 0) {
		o.os = OrderState.idle;
		stateChanged();
		//}
		
	}

	//utilities

	public void setRestaurantRevenue(double totalPrice) {
		this.restaurantRevenue += totalPrice;
	}
	
	public double getRestaurantRevenue() {
		return restaurantRevenue;
	}
	
	public void setCook(Restaurant3CookRole cook) {
		this.cook = cook;
		
	}
	
	public static class Payment{
        MarketCashier cashier;
        double amountDue;
        public OrderState state;
        public enum OrderState {pending, paying, paid, gotChange, delete};
        
        public Payment(MarketCashier c, double amountdue){
                cashier = c;
                amountDue = amountdue;
                state = OrderState.pending;
        }
        
}
//	private int round(double money){
//        double d = Math.abs(money);
//        int i = (int) d;
//        double result = d - (double) i;
//        if(result<0.5){
//            return money<0 ? -i : i;            
//        }else{
//            return money<0 ? -(i+1) : i+1;          
//        }
//    }
//	
	public void setGui(RoleGui g) {
		super.setGui(g);
		gui = (CashierGui)g;
	}

	public CashierGui getGui() {
		return gui;
	}

	
}

