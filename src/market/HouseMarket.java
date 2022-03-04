package market;

import java.util.Iterator;

import config.SocietyConfig;
import people.BankAccount;

public class HouseMarket extends Market{

	int houseLand;

	public HouseMarket(BankAccount state,float price,int volume,BankAccount owner){
		super(state);
		houseLand = volume*SocietyConfig.BUILDER_LAND_QTITY;
		for(int i =0;i < volume;i++)
			marketOffers.add(new Tradable(price, SocietyConfig.BUILDER_LAND_QTITY,owner));
	}
	@Override
	synchronized public boolean takeBestOffer(Tradable house){
		int tmp = marketOffers.size();
		if(super.takeBestOffer(house)){
			houseLand -= house.getVolume();
			//System.out.println("del " + tmp + " " +marketOffers.size());
			return true;
		}
		return false;
	}
	@Override
	synchronized public void addSupply(Tradable house){
		int tmp = marketOffers.size();
		marketOffers.add(house);
		houseLand += house.getVolume();
		//System.out.println("add " + tmp + " " +marketOffers.size());
	}
	synchronized public int getHouseLand(){
		return houseLand;
	}
	synchronized public String toString(){
		Iterator it = marketOffers.iterator();
		String tmp = "";
		while(it.hasNext())
			tmp += it.next().toString()+"\n";

		return "Current HouseMarket state : \n" + tmp;
	}
}