package simcity.Market.test.mock;

import java.util.List;
import java.util.Map;

import agent.Role;
import simcity.Market.Item;
import simcity.Market.MarketCashierRole.Check;
import simcity.Market.MarketCashierRole.RestaurantOrder;
import simcity.Market.MarketCustomerRole;
import simcity.Market.Order;
import simcity.Market.interfaces.MarketCashier;
import simcity.Market.interfaces.MarketCustomer;
import simcity.restaurants.Restaurant;
//import simcity.Restaurant4.Restaurant4CashierRole;
import simcity.restaurants.restaurant4.Restaurant4CashierRole;

public class MockMarketCashier extends Mock implements MarketCashier {

	public MockMarketCashier(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void hereIsMoney(MarketCustomer marketCustomerRole, double payment) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void INeed(List<Order> orders) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Map<String, Item> getInventory() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void needFood(MarketCustomer marketCustomerRole) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void hereIsMoneyRestaurant(
			Restaurant r, double money) {
		// TODO Auto-generated method stub
		
	}

	public List<MarketCustomer> getCustomers() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Check> getChecks() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<RestaurantOrder> getRestaurantOrders() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setRest4Cashier(Restaurant4CashierRole r) {
		// TODO Auto-generated method stub
		
	}




	@Override
	public void INeedFood(Map<String, Integer> food, Restaurant r) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void foodIsDelivered(Restaurant rest) {
		// TODO Auto-generated method stub
		
	}



}
