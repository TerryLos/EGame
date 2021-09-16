package market;

import java.util.PriorityQueue;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import people.BankAccount;

public class Market{

	protected PriorityQueue<Tradable> marketOffers;

	/*
		Meta-data computed in other functions to spare time.
	 */
	private int totalVolume;

	public Market(){
		marketOffers = new PriorityQueue<Tradable>();
		totalVolume = -1;
	}

	synchronized public int getTotalSupply(){
		int totalVolume = 0;
		if(this.totalVolume != -1)
			return  this.totalVolume;
		Iterator<Tradable> item = marketOffers.iterator();
		Tradable tmp;

		while (item.hasNext()){
			tmp = item.next();
			totalVolume += tmp.getVolume();
		}
		this.totalVolume = totalVolume;
		return totalVolume;
	}

	synchronized public int getSupply(){ 
		Tradable tmp = marketOffers.peek();

		if(tmp == null)
			return 0;

		return tmp.getVolume(); 
	}
	synchronized public void addSupply(Tradable offer){
		//Tries to regroup already existing offers.
		Iterator<Tradable> offerIt = marketOffers.iterator();
		Tradable tmp;
		boolean found = false;

		while(offerIt.hasNext() && !found){
			tmp = (Tradable)offerIt.next();
			if(tmp.getAskedPrice() == offer.getAskedPrice() && tmp.getReceiverAccount() == offer.getReceiverAccount()){
				found = true;
				tmp.setVolume(tmp.getVolume()+offer.getVolume());
			}
		}

		if(!found)
        	marketOffers.add(offer);
    }
    synchronized public Tradable getBestOffer(){
    	return marketOffers.peek();
    }
	/*
		Mean item price returns the mean price of the item. The mean isn't done on the number of offers but on the volume instead.
	 */
	synchronized public float meanItemPrice(){
		int totalVolume = 0;
		float meanPrice = 0;

		Iterator<Tradable> item = marketOffers.iterator();
		Tradable tmp;
		while (item.hasNext()){
			tmp = item.next();
			totalVolume += tmp.getVolume();
			meanPrice += tmp.getAskedPrice()*tmp.getVolume();
		}
		this.totalVolume = totalVolume;

		return meanPrice/totalVolume;
	}
    synchronized public List<Tradable> peekSupply(int amount,int maxBudget){
    	Tradable tmp = marketOffers.peek();
		int remainingAmount = amount;
		int remainingBudget = maxBudget;
		List<Tradable> sellerList = new ArrayList<>();
		PriorityQueue<Tradable> toRestore = Tradable.cloneQueue(marketOffers);
		int i =0;
		while(remainingAmount > 0 && remainingBudget > 0 && tmp!=null){
			
			if(tmp.getVolume() == 0){
				marketOffers.poll();
				tmp = marketOffers.peek();
				continue;
			}
			if(tmp.getVolume() - remainingAmount >= 0 && remainingAmount*tmp.getAskedPrice()<=remainingBudget){
		    	sellerList.add(new Tradable(tmp.getAskedPrice(),remainingAmount,tmp.getReceiverAccount()));
		    	tmp.setVolume(tmp.getVolume()-remainingAmount);
				remainingBudget -= remainingAmount*tmp.getAskedPrice();
		    	remainingAmount = 0;
			}

			else if(tmp.getVolume() - remainingAmount < 0 && tmp.getVolume()*tmp.getAskedPrice()<=remainingBudget){
				sellerList.add(new Tradable(tmp.getAskedPrice(),tmp.getVolume(),tmp.getReceiverAccount()));
				remainingAmount -= tmp.getVolume();
				remainingBudget -= (tmp.getVolume()*tmp.getAskedPrice());
				tmp.setVolume(0);
			}
			else
				break;
		}

		marketOffers = Tradable.cloneQueue(toRestore);
		return sellerList;
    }
	synchronized public List<Tradable> consumeSupply(int amount,int maxBudget){
		Tradable tmp = marketOffers.peek();
		int remainingAmount = amount;
		int remainingBudget = maxBudget;
		List<Tradable> sellerList = new ArrayList<>();

		while(remainingAmount > 0 && remainingBudget > 0 && tmp != null){

			if(tmp.getVolume() == 0){
				tmp = marketOffers.peek();
				marketOffers.poll();
				continue;
			}
					
			if(tmp.getVolume() - remainingAmount >= 0 && remainingAmount*tmp.getAskedPrice()<=remainingBudget){
		    	sellerList.add(new Tradable(tmp.getAskedPrice(),remainingAmount,tmp.getReceiverAccount()));
		    	tmp.setVolume(tmp.getVolume()-remainingAmount);
				remainingBudget -= remainingAmount*tmp.getAskedPrice();
		    	remainingAmount = 0;
			}

			else if(tmp.getVolume() - remainingAmount < 0 && tmp.getVolume()*tmp.getAskedPrice()<=remainingBudget){
				sellerList.add(new Tradable(tmp.getAskedPrice(),tmp.getVolume(),tmp.getReceiverAccount()));
				remainingAmount -= tmp.getVolume();
				remainingBudget -= (tmp.getVolume()*tmp.getAskedPrice());
				tmp.setVolume(0);
			}
			else
				break;
		}

		return sellerList;
	}
	synchronized public void deleteOffer(BankAccount account){
		PriorityQueue<Tradable> toKeep = new PriorityQueue<>();
		Tradable tmp;
		while(marketOffers.size()>0){
			tmp = marketOffers.poll();
			if(tmp.getReceiverAccount() != account)
				toKeep.add(tmp);
		}

		marketOffers = toKeep;
	}
}
