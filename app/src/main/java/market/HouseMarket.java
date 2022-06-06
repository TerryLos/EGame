package market;

import java.util.Iterator;

import config.SocietyConfig;
import people.BankAccount;

public class HouseMarket extends Market{

	int houseLand;

	public HouseMarket(BankAccount state,float price,int volume){
		super(state);
		houseLand = volume*SocietyConfig.BUILDER_LAND_QTITY;
		for(int i =0;i < volume;i++)
			marketOffers.add(new Tradable(price, SocietyConfig.BUILDER_LAND_QTITY,state));
	}
	/*
		Input : house (Tradable)
		Does : removes the tradable given in output from the priority queue.
		Returns true if that tradable was found, false otherwise.
		Note : Thread-safe
	 */
	@Override
	synchronized public boolean takeBestOffer(Tradable house){
		int tmp = marketOffers.size();
		if(super.takeBestOffer(house)){
			houseLand -= house.getVolume();
			demand++;
			return true;
		}
		return false;
	}
	/*
		Input : house (Tradable)
		Does : adds the tradable given as argument into the priority queue.
		Note : Thread-safe
	 */
	@Override
	synchronized public void addSupply(Tradable house){
		marketOffers.add(house);
		houseLand += house.getVolume();
	}
	/*
		Does : returns the amount of land associated to houses.
		Note : Thread-safe
	 */
	synchronized public int getHouseLand(){
		return houseLand;
	}
	/*
		Does : returns the priority queue in a string format.
		Note : Thread-safe
	 */
	synchronized public String toString(){
		Iterator it = marketOffers.iterator();
		String tmp = "";
		while(it.hasNext())
			tmp += it.next().toString()+"\n";

		return "Current HouseMarket state : \n" + tmp;
	}
}