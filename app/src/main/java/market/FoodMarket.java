package market;

import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.List;
import java.util.ArrayList;

import config.SocietyConfig;
import people.BankAccount;

public class FoodMarket extends Market{

	
	public FoodMarket(BankAccount state,float price,int volume){
		super(state);
		if(volume != 0)
			marketOffers.add(new Tradable(price,volume,state));
	}
	/*
		Input : amount (int), maxBudget (int)
		Does : returns a list of Tradables, where the total amount of items
		retrieved from the market are either limited by amount of maxBudget.
		Retrieving a tradable doesn't remove it from the priority queue.
		The last tradable is the VAT which should be paid to the society if
		tradables have been retrieved from the queue.
		Note : Thread-safe
	 */
	@Override
    synchronized public List<Tradable> peekSupply(int amount,int maxBudget){
    	Tradable tmp = marketOffers.peek();
		int remainingAmount = amount;
		int remainingBudget = maxBudget;
		List<Tradable> sellerList = new ArrayList<>();
		PriorityQueue<Tradable> toRestore = Tradable.cloneQueue(marketOffers);
		float VATPrice = 0;

		while(remainingAmount > 0 && remainingBudget >0 && tmp!=null){
			VATPrice = (1+ SocietyConfig.VAT/100)*tmp.getAskedPrice();
			if(tmp.getVolume()- remainingAmount >= 0 && remainingAmount*VATPrice >= remainingBudget){
				sellerList.add(new Tradable(VATPrice,(int)(remainingBudget/VATPrice),tmp.getReceiverAccount()));
				tmp.setVolume(tmp.getVolume()-(int)(remainingBudget/VATPrice));
				remainingAmount -= (int)remainingBudget/VATPrice;
				remainingBudget = 0;
			}

			else if(tmp.getVolume() - remainingAmount > 0 && remainingAmount*VATPrice<=remainingBudget){
		    	sellerList.add(new Tradable(VATPrice,remainingAmount,tmp.getReceiverAccount()));
		    	tmp.setVolume(tmp.getVolume()-remainingAmount);
				remainingBudget -= remainingAmount*VATPrice;
		    	remainingAmount = 0;
			}

			else if(tmp.getVolume() - remainingAmount < 0 && tmp.getVolume()*VATPrice<=remainingBudget){
				sellerList.add(new Tradable(VATPrice,tmp.getVolume(),tmp.getReceiverAccount()));
				remainingAmount -= tmp.getVolume();
				remainingBudget -= (tmp.getVolume()*VATPrice);
				tmp.setVolume(0);
			}
			//if we don't enter in any of the following elements. Avoid infinite loop
			else 
				break;

			if(tmp.getVolume() == 0)
					marketOffers.poll();
			tmp = marketOffers.peek();
		}

		marketOffers = Tradable.cloneQueue(toRestore);
		return sellerList;
    }
	/*
		Input : amount (int), maxBudget (int)
		Does : returns a list of Tradables, where the total amount of items
		retrieved from the market are either limited by amount of maxBudget.
		Retrieving a tradable removes it from the priority queue.
		The last tradable is the VAT which should be paid to the society if
		tradables have been retrieved from the queue.
		Note : Thread-safe
	 */
    @Override
	synchronized public List<Tradable> consumeSupply(int amount,int maxBudget){
		Tradable tmp = marketOffers.peek();
		int remainingAmount = amount;
		int remainingBudget = maxBudget;
		float VATPrice = 0;
		List<Tradable> sellerList = new ArrayList<>();

		while(remainingAmount > 0 && remainingBudget >0 && tmp!=null){
			VATPrice = (1+ SocietyConfig.VAT/100)*tmp.getAskedPrice();
			if(tmp.getVolume()- remainingAmount >= 0 && remainingAmount*VATPrice >= remainingBudget){
				sellerList.add(new Tradable(tmp.getAskedPrice(),(int)(remainingBudget/VATPrice),tmp.getReceiverAccount()));
				sellerList.add(new Tradable((VATPrice-tmp.getAskedPrice())*(int)(remainingBudget/VATPrice),0,state));
				tmp.setVolume(tmp.getVolume()- (int)(remainingBudget/VATPrice));
				remainingAmount -= (int)remainingBudget/VATPrice;
				remainingBudget = 0;
			}

			else if(tmp.getVolume() - remainingAmount > 0 && remainingAmount*VATPrice<=remainingBudget){
		    	sellerList.add(new Tradable(tmp.getAskedPrice(),remainingAmount,tmp.getReceiverAccount()));
				sellerList.add(new Tradable((VATPrice-tmp.getAskedPrice())*remainingAmount,0,state));
		    	tmp.setVolume(tmp.getVolume()-remainingAmount);
				remainingBudget -= remainingAmount*VATPrice;
		    	remainingAmount = 0;
			}

			else if(tmp.getVolume() - remainingAmount < 0 && tmp.getVolume()*VATPrice<=remainingBudget){
				sellerList.add(new Tradable(tmp.getAskedPrice(),tmp.getVolume(),tmp.getReceiverAccount()));
				sellerList.add(new Tradable((VATPrice -tmp.getAskedPrice())*tmp.getVolume(),0,state));
				remainingAmount -= tmp.getVolume();
				remainingBudget -= (tmp.getVolume()*VATPrice);
				tmp.setVolume(0);
			}
			//if we don't enter in any of the following elements. Avoid infinite loop
			else 
				break;

			if(tmp.getVolume() == 0)
					marketOffers.poll();
			tmp = marketOffers.peek();
		}
		demand += amount-remainingAmount;
		return sellerList;
	}
	/*
		Does : returns the priority queue in a string format
		Note : Thread-safe
	 */
	synchronized public String toString(){
		Iterator<Tradable> it = marketOffers.iterator();
		String tmp = "";
		while(it.hasNext())
			tmp += it.next().toString()+"\n";

		return "Current FoodMarket state : \n" + tmp;
	}
}

