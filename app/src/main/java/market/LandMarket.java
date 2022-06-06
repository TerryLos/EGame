package market;

import config.SocietyConfig;
import java.util.Iterator;
import people.BankAccount;

public class LandMarket extends Market{

	public LandMarket(BankAccount state,float price,int surface){
		super(state);
		int remainingSurface = surface;
		while(remainingSurface>0){
			marketOffers.add(new Tradable(surfacePricing(price,surface,remainingSurface),SocietyConfig.LAND_PARCEL,state));
			remainingSurface -= SocietyConfig.LAND_PARCEL;
		}
		
	}
	/*
		Does : returns the priority queue in a string format
		Note : Thread-safe
	 */
	synchronized public String toString(){
		Iterator it = marketOffers.iterator();
		String tmp = "";
		while(it.hasNext())
			tmp += it.next().toString()+"\n";

		return "Current LandMarket state : \n" + tmp;
	}
	/*
		Does : returns the total amount of land still available in the market
	 */
	public int getSocietySurface(){
		int surface = 0;
		for(Tradable item:marketOffers){
			if(item.getReceiverAccount().getOwner() == -1)
				surface += item.getVolume();
		}
		return surface;
	}
	/*
		Input : price (float), a base price
				surface (int), a land surface
				remainingSurface (int), the amount of remaining land in the society
		Does : returns the price of the surface based on the base price and the amount
		of land remaining in the society
	 */
	public float surfacePricing(float price,int surface,int remainingSurface){
		return price*(1+(int)(surface/(0.5*remainingSurface)));
	}
}

