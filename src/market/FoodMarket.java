package market;

import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.List;
import java.util.ArrayList;
import people.BankAccount;

public class FoodMarket extends Market{

	
	public FoodMarket(int price,int volume,BankAccount owner){
		super();
		marketOffers.add(new Tradable(price,volume,owner));
	}
	@Override
    synchronized public List<Tradable> peekSupply(int amount,int maxBudget){
    	Tradable tmp = marketOffers.peek();
		int remainingAmount = amount;
		int remainingBudget = maxBudget;
		List<Tradable> sellerList = new ArrayList<>();
		PriorityQueue<Tradable> toRestore = Tradable.cloneQueue(marketOffers);

		while(remainingAmount > 0 && remainingBudget >0 && tmp!=null){
			
			if(tmp.getVolume()- remainingAmount >= 0 && remainingAmount*tmp.getAskedPrice() >= remainingBudget){
				sellerList.add(new Tradable(tmp.getAskedPrice(),(int)remainingBudget/tmp.getAskedPrice(),tmp.getReceiverAccount()));
				tmp.setVolume(tmp.getVolume()-(int)remainingBudget/tmp.getAskedPrice());
				remainingAmount -= (int)remainingBudget/tmp.getAskedPrice();
				remainingBudget = 0;
			}

			else if(tmp.getVolume() - remainingAmount > 0 && remainingAmount*tmp.getAskedPrice()<=remainingBudget){
		    	sellerList.add(new Tradable(tmp.getAskedPrice(),remainingAmount,tmp.getReceiverAccount()));
		    	tmp.setVolume(tmp.getVolume()-remainingAmount);
		    	remainingAmount = 0;
		    	remainingBudget -= remainingAmount*tmp.getAskedPrice();
			}

			else if(tmp.getVolume() - remainingAmount < 0 && tmp.getVolume()*tmp.getAskedPrice()<=remainingBudget){
				sellerList.add(new Tradable(tmp.getAskedPrice(),tmp.getVolume(),tmp.getReceiverAccount()));
				remainingAmount -= tmp.getVolume();
				remainingBudget -= (tmp.getVolume()*tmp.getAskedPrice());
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
    @Override
	synchronized public List<Tradable> consumeSupply(int amount,int maxBudget){
		Tradable tmp = marketOffers.peek();
		int remainingAmount = amount;
		int remainingBudget = maxBudget;
		List<Tradable> sellerList = new ArrayList<>();

		while(remainingAmount > 0 && remainingBudget >0 && tmp!=null){

			if(tmp.getVolume()- remainingAmount >= 0 && remainingAmount*tmp.getAskedPrice() >= remainingBudget){
				sellerList.add(new Tradable(tmp.getAskedPrice(),(int)remainingBudget/tmp.getAskedPrice(),tmp.getReceiverAccount()));
				tmp.setVolume(tmp.getVolume()-remainingBudget/tmp.getAskedPrice());
				remainingAmount -= (int)remainingBudget/tmp.getAskedPrice();
				remainingBudget = 0;
			}

			else if(tmp.getVolume() - remainingAmount > 0 && remainingAmount*tmp.getAskedPrice()<=remainingBudget){
		    	sellerList.add(new Tradable(tmp.getAskedPrice(),remainingAmount,tmp.getReceiverAccount()));
		    	tmp.setVolume(tmp.getVolume()-remainingAmount);
		    	remainingAmount = 0;
		    	remainingBudget -= remainingAmount*tmp.getAskedPrice();
			}

			else if(tmp.getVolume() - remainingAmount < 0 && tmp.getVolume()*tmp.getAskedPrice()<=remainingBudget){
				sellerList.add(new Tradable(tmp.getAskedPrice(),tmp.getVolume(),tmp.getReceiverAccount()));
				remainingAmount -= tmp.getVolume();
				remainingBudget -= (tmp.getVolume()*tmp.getAskedPrice());
				tmp.setVolume(0);
			}
			//if we don't enter in any of the following elements. Avoid infinite loop
			else 
				break;

			if(tmp.getVolume() == 0)
					marketOffers.poll();
			tmp = marketOffers.peek();
		}
		return sellerList;
	}
	synchronized public String toString(){
		Iterator it = marketOffers.iterator();
		String tmp = "";
		while(it.hasNext())
			tmp += it.next().toString()+"\n";

		return "Current FoodMarket state : \n" + tmp;
	}
}

