package simcity.bank.test;

import simcity.PersonAgent;
import simcity.bank.BankCustomerRole;
import simcity.bank.BankCustomerRole.CustomerState;
import simcity.bank.test.mock.MockBankCustomer;
import simcity.bank.test.mock.MockBankGreeter;
import simcity.bank.test.mock.MockBankTeller;
import junit.framework.*;

/**
 * 
 * This class is a JUnit test class to unit test the CashierAgent's basic interaction
 * with waiters, customers, and the host.
 * It is provided as an example to students in CS201 for their unit testing lab.
 *
 * @author Brice Roland
 */
public class BankCustomerTest extends TestCase
{
	//these are instantiated for each test separately via the setUp() method.
	BankCustomerRole bankCustomer;
	MockBankGreeter mockGreeter;
	MockBankTeller mockTeller;
	
	public void setUp() throws Exception{
		super.setUp();
		bankCustomer = new BankCustomerRole(new PersonAgent("testCustomer"));		
		mockGreeter = new MockBankGreeter("mockGreeter");		
		mockTeller = new MockBankTeller("mockTeller");
		bankCustomer.setGreeter(mockGreeter);
		bankCustomer.setBankTeller(mockTeller);	
	}
	
	/**
	 * This tests the cashier under very simple terms: The cashier waiter list is checked and the mockWaiter is messaged
	 */
	public void testOneNormalCustomerScenario()
	{
		//Check initial conditions
		assertTrue("The transactionList should be empty at the customer's creation. It is not.", 
				bankCustomer.transactionList.size() == 0);
		assertTrue("Customer state to begin with should be nothing. It's currently not.", 
				bankCustomer.state == CustomerState.nothing);
		assertTrue("Name should be testCustomer. It is instead " + bankCustomer.getName(), 
				bankCustomer.getName().equals("testCustomer"));
		assertTrue("No messages were given but the customer should be waiting and send a message to the greeter, "
				+ "meaning pickAndExecute should be true. It is not.", bankCustomer.pickAndExecuteAnAction());
		
		//Initial Setup
		bankCustomer.setTransactions();
		assertTrue("Transaction list should be increased after setTransactions. It is not. Instead the size is: " + 
				bankCustomer.transactionList.size(), bankCustomer.transactionList.size() > 0);
		bankCustomer.setGreeter(mockGreeter);
		assertTrue("Greeter should be set to mockGreeter and not null.", bankCustomer.getBankGreeter().equals(mockGreeter));
		bankCustomer.setBankTeller(mockTeller);
		assertTrue("Teller should be set to mockTeller and not null.", bankCustomer.getBankTeller().equals(mockTeller));
		
		assertTrue("", bankCustomer.getBankTeller() == mockTeller);
		
		//check preconditions
		
	}//end one normal customer scenario
}
