package market;

import config.SocietyConfig;
import java.util.Iterator;
import people.BankAccount;

public class LandMarket extends Market{

	public LandMarket(BankAccount state,float price,int surface,BankAccount owner){
		super(state);
		int remainingSurface = surface;
		while(remainingSurface>0){
			marketOffers.add(new Tradable(surfacePricing(price,surface,remainingSurface),SocietyConfig.LAND_PARCEL,owner));
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
	public int getSocietySurface(){
		int surface = 0;
		for(Tradable item:marketOffers){
			if(item.getReceiverAccount().getOwner() == -1)
				surface += item.getVolume();
		}
		return surface;
	}
	public float surfacePricing(float price,int surface,int remainingSurface){
		return price*(1+(int)(surface/(0.25*remainingSurface)));
	}
}

