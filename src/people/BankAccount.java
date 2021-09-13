package people;

import java.util.List;
import java.util.LinkedList;

public class BankAccount{

	private BankAccount stateAccount;
	private LinkedList<Integer> spentMoney;
	private LinkedList<Integer> receivedMoney;
	private int bankAccount;
	private int owner;

	public BankAccount(int bankAccount,int owner){
		this.bankAccount = bankAccount;
		this.owner = owner;
		this.spentMoney = new LinkedList<Integer>();
		this.receivedMoney = new LinkedList<Integer>();
		
		for(int i = 0 ; i<3 ; i++){
			spentMoney.addFirst(0);
			receivedMoney.addFirst(0);
		}
	}
	public BankAccount(int bankAccount,int owner,LinkedList<Integer> spentMoney,LinkedList<Integer> receivedMoney){
		this.bankAccount = bankAccount;
		this.owner = owner;
		this.spentMoney = spentMoney;
		this.receivedMoney = receivedMoney;
	}
	public void setStateAccount(BankAccount account){
		stateAccount = account;
	}
	public BankAccount getStateAccount(){
		return stateAccount;
	}
	public synchronized int getBankAccount(){
		return bankAccount;
	}
	public int getOwner(){
		return owner;
	}
	public synchronized void addMoney(int amount){
		bankAccount += amount;
		receivedMoney.set(0,receivedMoney.getFirst() + amount);
	}
	public synchronized boolean withdrawMoney(int amount){
		if(amount > bankAccount)
			return false;

		bankAccount -= amount;
		spentMoney.set(0,spentMoney.getFirst() + amount);

		return true;
	}

	public synchronized String toString(){
		return "Bank account : "+Integer.toString(bankAccount)+"\n";
	}
	public synchronized BankAccount copy(){
		return new BankAccount(bankAccount,owner,new LinkedList<Integer>(spentMoney),new LinkedList<Integer>(receivedMoney));
	}
	public synchronized void resume(BankAccount money){
		bankAccount = money.getBankAccount();
		owner = money.getOwner();
		spentMoney = money.getSpentMoney();
		receivedMoney = money.getReceivedMoney();
	}
	public synchronized LinkedList<Integer> getSpentMoney(){
		return spentMoney;
	}
	public synchronized LinkedList<Integer> getReceivedMoney(){
		return receivedMoney;
	}
	public synchronized void resetMoneyHistoric(){
		spentMoney.addFirst(0);
		spentMoney.pollLast();
		receivedMoney.addFirst(0);
		receivedMoney.pollLast();
	}
	public synchronized int getAverageIncome(){
		int tmp = 0;
		for(int i:receivedMoney)
			tmp += i;

		return (int)tmp/receivedMoney.size();
	}
	public synchronized int getAverageExpenses(){
		int tmp = 0;
		for(int i:spentMoney)
			tmp += i;

		return (int)tmp/spentMoney.size();
	}
}