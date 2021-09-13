package market;

import java.lang.Comparable;
import people.BankAccount;
import java.util.PriorityQueue;

public class Tradable implements Comparable<Tradable>{

	private int askedPrice;
	private int volume;
	private BankAccount receiverAccount;

	public Tradable(int askedPrice,int volume,BankAccount receiverAccount){
		this.askedPrice = askedPrice;
		this.volume = volume;
		this.receiverAccount = receiverAccount;
	}
	public BankAccount getReceiverAccount(){
		return receiverAccount;
	}
	public int getVolume(){
		return volume;
	}
	public int getAskedPrice(){
		return askedPrice;
	}
	public void setVolume(int volume){
		this.volume = volume;
	}
	@Override
	public boolean equals(Object o){
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		Tradable tradable = (Tradable) o;
		return askedPrice == tradable.getAskedPrice() && volume == tradable.getVolume() && 
			receiverAccount == tradable.getReceiverAccount();
	}
	@Override
	public String toString(){
		return " Price :"+Integer.toString(askedPrice)+" volume :"+Integer.toString(volume)+
			" Owner :"+Integer.toString(receiverAccount.getOwner());
	}
	@Override
	public int compareTo(Tradable tradable){
		if(this.getAskedPrice() > tradable.getAskedPrice())
			return 1;
		else if(this.getAskedPrice() < tradable.getAskedPrice())
			return -1;

		return 0;
	}
	@Override
	public Tradable clone(){
		return new Tradable(askedPrice,volume,receiverAccount);
	}
	public static PriorityQueue<Tradable> cloneQueue(PriorityQueue<Tradable> toCpy){
		PriorityQueue<Tradable> tmp = new PriorityQueue<>();

		for(Tradable t:toCpy)
			tmp.add(t.clone());

		return tmp;
	}
}