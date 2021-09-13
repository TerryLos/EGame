package market;

import config.SocietyConfig;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.List;
import java.util.ArrayList;
import people.BankAccount;

public class LandMarket extends Market{

	public LandMarket(int price,int surface,BankAccount owner){
		super();
		int remainingSurface = surface;
		while(remainingSurface>0){
			marketOffers.add(new Tradable(price*(int)(1+(int)(surface/remainingSurface)),SocietyConfig.LAND_PARCEL,owner));
			remainingSurface -= SocietyConfig.LAND_PARCEL;
		}
		
	}

	synchronized public String toString(){
		Iterator it = marketOffers.iterator();
		String tmp = "";
		while(it.hasNext())
			tmp += it.next().toString()+"\n";

		return "Current LandMarket state : \n" + tmp;
	}
}

