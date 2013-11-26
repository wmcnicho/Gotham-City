package simcity.Market.MarketGui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.List;
import java.util.Map;

import agent.Role;
import Gui.RoleGui;
import simcity.Market.Order;
import simcity.Market.interfaces.MarketCustomer;
import simcity.Market.interfaces.MarketWorker;

public class MarketWorkerGui extends RoleGui{
	private int xPos;
	private int yPos;
	private int xDestination;
	private int yDestination;
	private MarketWorker worker;
	private MarketCustomer customer;
	private Command command;
	private enum Command {none, getting, delivering, restGetting, truck};
	private List <Order> orders;
	private boolean drawString = false;
	private Role role;
	
	
	public MarketWorkerGui(MarketWorker mw){
		worker = mw;
		xPos = 430;
		yPos = 360;
		xDestination = 430;
		yDestination = 360;
	}
	

	public void updatePosition() {
		 if (xPos < xDestination)
	            xPos++;
	        else if (xPos > xDestination)
	            xPos--;

	        if (yPos < yDestination)
	            yPos++;
	        else if (yPos > yDestination)
	            yPos--;
	        if (xPos == 550 && yPos == 250   && command == Command.getting){
	        	command = Command.none;
	        	Deliver();
	        }
	        if (xPos == 580 && yPos == 410 && command == Command.delivering){
	        	command = Command.none;
	        	worker.Brought(customer);
	        }
	        if (xPos == 130 && yPos == 250 && command == Command.restGetting){
	        	System.out.println("Got things");
	        	command = Command.none;
	        	LoadToTruck();
	        }
	        if (xPos == 100 && yPos == 250 && command == Command.truck){
	        	command = Command.none;
	        	System.out.println("Loaded to truck");
	        	worker.Sent(role);
	        }
	}


	public void draw(Graphics g) {
		g.setColor(Color.RED);
        g.fillRect(xPos, yPos, 20, 20);
		if (drawString == true){
			//drawOrder(g);
		}
        
	}

	public boolean isPresent() {
		return true;
	}
	
	public void DoSend(Map<String, Integer> m, Role role){
		System.out.println("Going to truck");
		this.role = role;
		xDestination = 130;
		yDestination = 250;
		command = Command.restGetting;
	}

	public void LoadToTruck(){
		xDestination = 100;
		yDestination = 250;
		command = Command.truck;
	}
	public void drawOrder(Graphics g){
		int x = xPos;
		/*for (Order order : orders){
			g.drawString(order.getChoice(),x, yPos+20);
			x+=10;
		}*/
		
	}
	
	
	public void DoBring(MarketCustomer m){
		customer = m;
		xDestination = 550;
		yDestination = 250;
		command = Command.getting;
	}
	
	public void Deliver(){
		drawString = true; 
		xDestination = 580;
		yDestination = 410;
		command = Command.delivering;
	}
	
	public void DefaultPos(){
		xDestination = 430;
		yDestination = 360;
	}
}
