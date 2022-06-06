package market;

import java.util.PriorityQueue;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

import config.SocietyConfig;
import people.BankAccount;

public class Market{

	protected PriorityQueue<Tradable> marketOffers;
	protected BankAccount state;
	protected int demand;
	/*
		Meta-data computed in other functions to spare time.
	 */
	private float meanPrice;

	public Market(BankAccount state){
		marketOffers = new PriorityQueue<Tradable>();
		this.state = state;
		meanPrice = -1;
		demand = 0;
	}

	synchronized public int getTotalSupply(){
		int totalVolume = 0;
		float meanPrice = 0;

		Iterator<Tradable> item = marketOffers.iterator();
		Tradable tmp;
		while (item.hasNext()){
			tmp = item.next();
			totalVolume += tmp.getVolume();
			meanPrice += tmp.getAskedPrice()*tmp.getVolume();
		}
		//Computes the total volume at the same time -> optimisation

		if(totalVolume > 0)
			this.meanPrice = meanPrice/totalVolume;
		else
			this.meanPrice = 0;

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
    		Tradable toReturn = marketOffers.peek();

			if(toReturn == null)
				return new Tradable((float)0,0,null);
			else
				return toReturn;
    }
	synchronized public boolean takeBestOffer(Tradable offer){
		return marketOffers.remove(offer);
	}
	/*
		Mean item price returns the mean price of the item. The mean isn't done on the number of offers but on the volume instead.
	 */
	synchronized public float meanItemPrice(){
		int totalVolume = 0;
		float meanPrice = 0;

		if(this.meanPrice > -1)
			return this.meanPrice;

		Iterator<Tradable> item = marketOffers.iterator();
		Tradable tmp;
		while (item.hasNext()){
			tmp = item.next();
			totalVolume += tmp.getVolume();
			meanPrice += tmp.getAskedPrice()*tmp.getVolume();
		}

		if(totalVolume > 0)
			return meanPrice/totalVolume;


		return 0;
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
		float VATPrice = 0;
		List<Tradable> sellerList = new ArrayList<>();

		while(remainingAmount > 0 && remainingBudget > 0 && tmp != null){

			if(tmp.getVolume() == 0){
				tmp = marketOffers.peek();
				marketOffers.poll();
				continue;
			}
			VATPrice = tmp.getAskedPrice()*(1+SocietyConfig.VAT/100);
			if(tmp.getVolume() - remainingAmount >= 0 && remainingAmount*VATPrice<=remainingBudget){
		    	sellerList.add(new Tradable(tmp.getAskedPrice(),remainingAmount,tmp.getReceiverAccount()));
				sellerList.add(new Tradable((VATPrice-tmp.getAskedPrice())*remainingAmount,0,state));
		    	tmp.setVolume(tmp.getVolume()-remainingAmount);
				remainingBudget -= remainingAmount*tmp.getAskedPrice();
		    	remainingAmount = 0;
			}

			else if(tmp.getVolume() - remainingAmount < 0 && tmp.getVolume()*VATPrice<=remainingBudget){
				sellerList.add(new Tradable(tmp.getAskedPrice(),tmp.getVolume(),tmp.getReceiverAccount()));
				sellerList.add(new Tradable((VATPrice-tmp.getAskedPrice())*tmp.getVolume(),0,state));
				remainingAmount -= tmp.getVolume();
				remainingBudget -= (tmp.getVolume()*tmp.getAskedPrice());
				tmp.setVolume(0);
			}
			else
				break;
		}
		demand += amount - remainingAmount;
		return sellerList;
	}
	synchronized public int getDemand(){
		int tmp = demand;
		demand = 0;
		return tmp;
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
	synchronized public int offerNbr(){
		return marketOffers.size();
	}
}
