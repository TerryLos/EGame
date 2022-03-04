package people;


import config.SocietyConfig;

public class BankAccount{

	private BankAccount stateAccount;
	private float spentMoney;
	private float receivedMoneyFarmer;
	private float receivedMoneyBuilder;
	private float receivedMoneyWC;
	private float receivedMoney;
	private float bankAccount;
	private int owner;

	public BankAccount(int bankAccount,int owner){
		this.bankAccount = bankAccount;
		this.owner = owner;
		this.spentMoney = 0;
		this.receivedMoney = 0;
		this.receivedMoneyFarmer = 0;
		this.receivedMoneyBuilder = 0;
		this.receivedMoneyWC = 0;
	}
	public BankAccount(float bankAccount,int owner,float spentMoney,float receivedMoney,float receivedMoneyFarmer,float receivedMoneyBuilder,float receivedMoneyWC){
		this.bankAccount = bankAccount;
		this.owner = owner;
		this.spentMoney = spentMoney;
		this.receivedMoney = receivedMoney;
		this.receivedMoneyFarmer = receivedMoneyFarmer;
		this.receivedMoneyBuilder = receivedMoneyBuilder;
		this.receivedMoneyWC = receivedMoneyWC;
	}
	public void setStateAccount(BankAccount account){
		stateAccount = account;
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
	public synchronized void addMoney(float amount,String source){
		bankAccount += amount;
		receivedMoney += 0;
		if(SocietyConfig.AVAILABLE_JOBS_DIC.get(source) == null)
			return;
		switch(SocietyConfig.AVAILABLE_JOBS_DIC.get(source)){
			default:
				break;
			case 1:
				this.receivedMoneyFarmer += amount;
				break;
			case 2:
				this.receivedMoneyBuilder += amount;
				break;
			case 3:
				this.receivedMoneyWC += amount;
				break;
		}
	}
	public synchronized boolean withdrawMoney(float amount){
		if(amount > bankAccount)
			return false;

		bankAccount -= amount;
		spentMoney += amount;

		return true;
	}
	public synchronized void withdrawMoneyDebt(float amount){
		bankAccount -= amount;
		spentMoney += amount;
	}
	public synchronized String toString(){
		return "Bank account : "+Float.toString(bankAccount)+"\n";
	}
	public synchronized BankAccount copy(){
		return new BankAccount(bankAccount,owner,spentMoney,receivedMoney,receivedMoneyFarmer,receivedMoneyBuilder,receivedMoneyWC);
	}
	public synchronized void resume(BankAccount money){
		bankAccount = money.getBankAccount();
		owner = money.getOwner();
		spentMoney = money.getSpentMoney();
		receivedMoneyFarmer = money.getReceivedMoneyFarmer();
		receivedMoneyBuilder = money.getReceivedMoneyBuilder();
		receivedMoneyWC = money.getReceivedMoneyWC();
	}
	public synchronized int getSpentMoney(){
		return (int)spentMoney;
	}
	public synchronized int getReceivedMoneyFarmer(){
		return (int)receivedMoneyFarmer;
	}
	public synchronized int getReceivedMoneyWC(){
		return (int)receivedMoneyWC;
	}
	public synchronized int getReceivedMoneyBuilder(){
		return (int)receivedMoneyBuilder;
	}
	public synchronized int getReceivedMoney(String source){
		switch(SocietyConfig.AVAILABLE_JOBS_DIC.get(source)){
			case -1:
			case 0:
				return (int) (receivedMoney-receivedMoneyFarmer-receivedMoneyWC-receivedMoneyBuilder);
			case 1:
				return (int) receivedMoneyFarmer;
			case 2:
				return (int) receivedMoneyBuilder;
			case 3:
				return (int) receivedMoneyWC;
		}
		return  0;
	}
	public synchronized void setOwner(int id) {owner = id;}
	public synchronized void resetMoneyHistoric(){
		spentMoney = 0;
		receivedMoneyFarmer = 0;
		receivedMoneyWC = 0;
		receivedMoneyBuilder = 0;
		receivedMoney = 0;
	}
	public synchronized int monthBalance(){
		return (int)(receivedMoney-spentMoney);
	}
}