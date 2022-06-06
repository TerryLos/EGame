package people;


import config.SocietyConfig;

import java.util.ArrayList;
import java.util.List;

public class BankAccount{

	private BankAccount stateAccount;
	private float spentMoney;
	/*
		0 Unemployed
		1 Farmer
		2 Builder
		3 Woodcutter
	 */
	private List<Float> incomeArray;
	private float receivedMoney;
	private float bankAccount;
	private int owner;

	public BankAccount(int bankAccount,int owner){
		this.bankAccount = bankAccount;
		this.owner = owner;
		this.spentMoney = 0;
		this.incomeArray = new ArrayList<>();

		for (String jobs : SocietyConfig.AVAILABLE_JOBS_DIC.keySet()){
			if (jobs.equals("Nowork"))
				continue;
			this.incomeArray.add(0.0F);
		}
	}
	public BankAccount(float bankAccount,int owner,float spentMoney,float receivedMoney,List<Float> incomeArray){
		this.bankAccount = bankAccount;
		this.owner = owner;
		this.spentMoney = spentMoney;
		this.receivedMoney = receivedMoney;
		this.incomeArray = new ArrayList<>(incomeArray);
	}
	/*
		Input : amount (float), source (String)
		Does : adds the money "amount" in the income array based on 'source'.
		It also adds that amount of money ot receivedMoney
		Note : Thread-safe
	 */
	public synchronized void addMoney(float amount,String source){
		bankAccount += amount;
		receivedMoney += 0;
		if(SocietyConfig.AVAILABLE_JOBS_DIC.get(source) == null)
			return;
		switch(SocietyConfig.AVAILABLE_JOBS_DIC.get(source)){
			default:
				break;
			case 0:
				this.incomeArray.set(0,this.incomeArray.get(0)+amount);
				break;
			case 1:
				this.incomeArray.set(1,this.incomeArray.get(1)+amount);
				break;
			case 2:
				this.incomeArray.set(2,this.incomeArray.get(2)+amount);
				break;
			case 3:
				this.incomeArray.set(3,this.incomeArray.get(3)+amount);
				break;
		}
	}
	/*
		Input : amount (float), source (String)
		Does : adds the money "amount" in the income array based on 'source'.
		It also adds that amount of money ot receivedMoney
		Note : Thread-safe
	 */
	public synchronized boolean withdrawMoney(float amount){
		if(amount > bankAccount)
			return false;

		bankAccount -= amount;
		spentMoney += amount;

		return true;
	}
	/*
		Input : amount (float)
		Does : withdraw the quantity "amount" from the bank account.
		Note : Thread-safe
	 */
	public synchronized void withdrawMoneyDebt(float amount){
		bankAccount -= amount;
		spentMoney += amount;
	}
	/*
		Does : sets the received money, spent money and income array to 0
		Note : Thread-safe
	 */
	public synchronized void resetMoneyHistoric(){
		spentMoney = 0;
		for (int i=0;i<incomeArray.size();i++){
			this.incomeArray.set(i,0.0F);
		}
		receivedMoney = 0;
	}
	/*
		Does : returns the bank account in a string format
		Note : Thread-safe
	 */
	public synchronized String toString(){
		return "Bank account : "+Float.toString(bankAccount)+"\n";
	}
	/*
		Does : returns a deep copy of this BankAccount instance
		Note : Thread-safe
	 */
	public synchronized BankAccount copy(){
		return new BankAccount(bankAccount,owner,spentMoney,receivedMoney,incomeArray);
	}
	/*
		Input : money (BankAccount)
		Does : sets the state of this instance of BankAccount to the same
		state as "money"
		Note : Thread-safe
	 */
	public synchronized void resume(BankAccount money){
		bankAccount = money.getBankAccount();
		owner = money.getOwner();
		spentMoney = money.getSpentMoney();
		incomeArray = new ArrayList<>(money.getIncomeArray());
	}

	//Getter & setter methods
	public void setStateAccount(BankAccount account){
		stateAccount = account;
	}
	public synchronized int getSpentMoney(){
		return (int)spentMoney;
	}
	public synchronized List<Float> getIncomeArray(){
		return incomeArray;
	}
	public synchronized int getReceivedMoney(String source){
		switch(SocietyConfig.AVAILABLE_JOBS_DIC.get(source)){
			case -1:
			case 0:
				return Math.round(incomeArray.get(0));
			case 1:
				return Math.round(incomeArray.get(1));
			case 2:
				return Math.round(incomeArray.get(2));
			case 3:
				return Math.round(incomeArray.get(3));
		}
		return  0;
	}
	public BankAccount getStateAccount(){
		return stateAccount;
	}
	public synchronized int getBankAccount(){
		return (int)bankAccount;
	}
	public int getOwner(){
		return owner;
	}
	public synchronized void setOwner(int id) {owner = id;}
	public synchronized int monthBalance(){
		return (int)(receivedMoney-spentMoney);
	}
}