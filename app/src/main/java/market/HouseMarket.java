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
	@Override
	synchronized public void addSupply(Tradable house){
		marketOffers.add(house);
		houseLand += house.getVolume();
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