package market;

import java.util.PriorityQueue;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import people.BankAccount;
//Todo create an uper class called market from which it gets the main methods.
public class HouseMarket extends Market{

	
	public HouseMarket(int price,int volume,BankAccount owner){
		super();
		marketOffers.add(new Tradable(price,volume,owner));
	}
	//TODO
	synchronized public String toString(){
		Iterator it = marketOffers.iterator();
		String tmp = "";
		while(it.hasNext())
			tmp += it.next().toString()+"\n";

		return "Current LandMarket state : \n" + tmp;
	}
}