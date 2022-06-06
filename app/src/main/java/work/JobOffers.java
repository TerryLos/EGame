package work;

import calendar.Calendar;
import config.SocietyConfig;
import market.*;
import people.BankAccount;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import logger.Logger;
import logger.LoggerException;
import people.People;
import people.PeoplePossessions;

public class JobOffers{

	private Calendar date;
	//Follows the order of the dic in SocietyConfig
	private List<Integer> workNbrs;
	private List<Integer> workIncomes;
	private List<Integer> registeredIncomes;

	private FoodMarket foodSupply;
	private HouseMarket houseSupply;
	private WoodMarket woodSupply;
	private StoneMarket stoneSupply;

	public JobOffers(FoodMarket foodSupply, HouseMarket houseSupply, WoodMarket woodSupply, StoneMarket stoneSupply, Calendar date){
		this.workIncomes = new ArrayList<>();
		this.workNbrs = new ArrayList<>();
		this.registeredIncomes = new ArrayList<>();

		for (String jobs : SocietyConfig.AVAILABLE_JOBS_DIC.keySet()){
			//No need to check the current job
			if(jobs.equals("Nowork"))
				continue;
			this.workNbrs.add(0);
			this.registeredIncomes.add(0);
			this.workIncomes.add(0);
		}

		this.foodSupply = foodSupply;
		this.houseSupply = houseSupply;
		this.woodSupply = woodSupply;
		this.stoneSupply = stoneSupply;
		this.date = date.copy();

	}

	/*
		Input : workName (String) : the name of the work to take
				previousJob (Work) : the instance of the current work
				account (BankAccount) : reference to the agent's bank account
				possessions (PeoplePossessions) : reference to the agent's possessions
		Does : returns the work instance of "workName" if available, returns an instance of unemployed otherwise.
		Note : Thread-safe
	 */
	public synchronized Work takeOffer(String workName,Work previousJob,BankAccount account,PeoplePossessions possessions) throws LoggerException{
		//Gives back his previous job.
		if(previousJob instanceof Farmer)
			workNbrs.set(1,workNbrs.get(1)-1);
		if(previousJob instanceof Builder)
			workNbrs.set(2,workNbrs.get(2)-1);
		if(previousJob instanceof WoodCutter)
			workNbrs.set(3,workNbrs.get(3)-1);
		if(previousJob instanceof Miner)
			workNbrs.set(4,workNbrs.get(4)-1);
		if(previousJob instanceof Unemployed){
			workNbrs.set(0,workNbrs.get(0)-1);
		}
		//Takes the offer
		switch(SocietyConfig.AVAILABLE_JOBS_DIC.get(workName)){
			case -1:
				return new Work();
			case 0:
				workNbrs.set(0,workNbrs.get(0)+1);
				return new Unemployed();
			case 1:
				if(workNbrs.get(1) < SocietyConfig.MAX_FARMER_NBR){
					Farmer job = new Farmer(foodSupply,account,possessions);
					workNbrs.set(1,workNbrs.get(1)+1);
					return job;
				}
				break;
			case 2:
				if(workNbrs.get(2) < SocietyConfig.MAX_BUILDER_NBR)
				{
					Builder job = new Builder(houseSupply,account,possessions);
					workNbrs.set(2,workNbrs.get(2)+1);
					return job;
				}
				break;
			case 3:
				if(workNbrs.get(3) < SocietyConfig.MAX_WOODCUTTER_NBR)
				{
					WoodCutter job = new WoodCutter(woodSupply,account,possessions);
					workNbrs.set(3,workNbrs.get(3)+1);
					return job;
				}
				break;
			case 4:
				if(workNbrs.get(4) < SocietyConfig.MAX_MINER_NBR)
				{
					Miner job = new Miner(stoneSupply,account,possessions);
					workNbrs.set(4,workNbrs.get(4)+1);
					return job;
				}
				break;
		}

		Logger.WARN("No "+workName+" available ! A person took it before.");
		workNbrs.set(0,workNbrs.get(0)+1);
		return new Unemployed();
	}
	/*
		Input : account (bankAccount), current (Calendar)
		Does : updates the income of the different works based on the configurations picked.
		Note : Thread-safe
	 */
	public synchronized void setIncome(BankAccount account,Calendar current){
		if (date.getMonth() != current.getMonth()){
			for(int i=0;i<this.workIncomes.size();i++){
				this.registeredIncomes.set(i,0);
				this.workIncomes.set(i,0);
			}
			date = current.copy();
		}
		//Job income is computed as the average

		List<Float> tmp = account.getIncomeArray();
		for(int i=0;i<this.workIncomes.size();i++){
			if(SocietyConfig.AVRG_INCOME){
				if(Math.round(tmp.get(i)) != 0){
					this.workIncomes.set(i,workIncomes.get(i)+Math.round(tmp.get(i)));
					this.registeredIncomes.set(i,registeredIncomes.get(i)+1);
				}
			}
			//Job income is the best income found
			else{
				if(this.workIncomes.get(i) < tmp.get(i)){
					this.workIncomes.set(i,Math.round(tmp.get(i)));
				}
			}
		}
	}
	/*
		Input : workName (String)
		Does : returns the income related to "workName"
		Note : Thread-safe
	 */
	public synchronized int getIncome(String workName){
		int index = SocietyConfig.AVAILABLE_JOBS_DIC.get(workName);

		if(this.registeredIncomes.get(index) > 0 && SocietyConfig.AVRG_INCOME)
			return this.workIncomes.get(index)/this.registeredIncomes.get(index);
		else
			return this.workIncomes.get(index);
	}
	/*
		Input : workName (String)
		Does : returns the cycle's length related to "workName"
	 */
	public int getWorkTime(String workName){
		switch (SocietyConfig.AVAILABLE_JOBS_DIC.get(workName)){
			case 0:
				return 30;
			case 1:
				return SocietyConfig.FARMER_WORK_GATHERING;
			case 2:
				return SocietyConfig.BUILDER_WORK_GATHERING;
			case 3:
				return SocietyConfig.WOODCUTTER_WORK_GATHERING;
			case 4:
				return SocietyConfig.MINER_WORK_GATHERING;
		}
		return 0;
	}
	/*
		Input : workName (String), account (BankAccount), possessions (PeoplePossessions), greediness (int)
		Does : returns an estimate of the income that an agent could perceive from a market.
		The computation is based on the greediness and the possessions of the agent.
	 */
	public synchronized int getIncomeByMarket(String workName,BankAccount account, PeoplePossessions possessions,int greediness){
		Work tmp;
		PeoplePossessions save;
		int value;
		switch(SocietyConfig.AVAILABLE_JOBS_DIC.get(workName)){
			case 0:
				return 0;
			case 1:
				tmp = new Farmer(foodSupply,account,possessions);
				return (int)(tmp.priceEstimate(greediness)*tmp.yieldEstimate());
			case 2:
				tmp = new Builder(houseSupply,account,possessions);
				//Saves because it deletes material
				save = possessions.copy();
				value = (int)(tmp.priceEstimate(greediness)*possessions.getLandSurface());
				possessions.resume(save);
				return value;
			case 3:
				tmp = new WoodCutter(woodSupply,account,possessions);
				return (int)(tmp.priceEstimate(greediness)*tmp.yieldEstimate());
			case 4:
				tmp = new Miner(stoneSupply,account,possessions);
				return (int)(tmp.priceEstimate(greediness)*tmp.yieldEstimate());
		}
		return 0;
	}
	/*
		Does : returns the job offers formatted as string
	 */
	public synchronized String toString(){
		return "Farmer left : "+ this.workNbrs.get(1) + " Builder left " + this.workNbrs.get(2) + " Farmer init "+ SocietyConfig.MAX_FARMER_NBR + " Builder init " + SocietyConfig.MAX_BUILDER_NBR;
	}
	public synchronized void deleteOffer(BankAccount account){
		foodSupply.deleteOffer(account);
		houseSupply.deleteOffer(account);
		woodSupply.deleteOffer(account);
	}
	public synchronized int getFarmerNbr(){
		return workNbrs.get(1);
	}
	public synchronized int getWoodcutterNbr(){
		return workNbrs.get(3);
	}
	public synchronized int getBuilderNbr(){
		return this.workNbrs.get(2);
	}
	public synchronized int getUnemployedNbr(){
		return this.workNbrs.get(0);
	}
	public synchronized int getMinerNbr(){
		return this.workNbrs.get(4);
	}
}