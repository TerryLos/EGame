package work;

import calendar.Calendar;
import config.SocietyConfig;
import market.FoodMarket;
import market.HouseMarket;
import market.LandMarket;
import market.WoodMarket;
import people.BankAccount;
import java.util.HashMap;
import logger.Logger;
import logger.LoggerException;
import people.PeoplePossessions;

public class JobOffers{

	private Calendar date;
	private int farmerNbr;
	private int farmerIncome;
	private int builderNbr;
	private int builderIncome;
	private int unemployedNbr;
	private int woodcutterNbr;
	private int woodcutterIncome;

	private FoodMarket foodSupply;
	private HouseMarket houseSupply;
	private WoodMarket woodSupply;

	public JobOffers(FoodMarket foodSupply,HouseMarket houseSupply,WoodMarket woodSupply,Calendar date){
		this.unemployedNbr = SocietyConfig.PEOPLE_NBR;

		this.farmerNbr = SocietyConfig.MAX_FARMER_NBR;
		this.farmerIncome = SocietyConfig.BASE_INCOME_UNEMPLOYED;

		this.builderNbr = SocietyConfig.MAX_BUILDER_NBR;
		this.builderIncome = SocietyConfig.BASE_INCOME_UNEMPLOYED;

		this.woodcutterNbr = SocietyConfig.MAX_WOODCUTTER_NBR;
		this.woodcutterIncome = SocietyConfig.BASE_INCOME_UNEMPLOYED;
		
		this.foodSupply = foodSupply;
		this.houseSupply = houseSupply;
		this.woodSupply = woodSupply;
		this.date = date.copy();
		//Inits the list of jobs
		SocietyConfig.AVAILABLE_JOBS_DIC = new HashMap<>();
		SocietyConfig.AVAILABLE_JOBS_DIC.put("Nowork",-1);
        SocietyConfig.AVAILABLE_JOBS_DIC.put("Unemployed",0);
        SocietyConfig.AVAILABLE_JOBS_DIC.put("Farmer",1);
		SocietyConfig.AVAILABLE_JOBS_DIC.put("Builder",2);
		SocietyConfig.AVAILABLE_JOBS_DIC.put("Woodcutter",3);
	}

	/*Gets the job associated to the name.
	and returns the income associated to it.
	People should check if the job is available before submitting to the offer.
	*/
	public synchronized Work takeOffer(String workName,Work previousJob,BankAccount account,PeoplePossessions possessions) throws LoggerException{
		//Gives back his previous job.
		if(previousJob instanceof Farmer)
			farmerNbr++;
		if(previousJob instanceof Builder)
			builderNbr++;
		if(previousJob instanceof WoodCutter)
			woodcutterNbr++;
		if(previousJob instanceof Unemployed){
			unemployedNbr++;
		}
		//Takes the offer
		switch(SocietyConfig.AVAILABLE_JOBS_DIC.get(workName)){
			case -1:
				return new Work();
			case 0:
				unemployedNbr--;
				return new Unemployed();
			case 1:
				if(farmerNbr > 0){
					Farmer job = new Farmer(foodSupply,account,possessions);
					farmerNbr--;
					return job;
				}
			case 2:
				if(builderNbr>0)
				{
					Builder job = new Builder(houseSupply,account,possessions);
					builderNbr--;
					return job;
				}
			case 3:
				if(woodcutterNbr>0)
				{
					WoodCutter job = new WoodCutter(woodSupply,account,possessions);
					woodcutterNbr--;
					return job;
				}
		}

		Logger.WARN("No "+workName+" available ! A person took it before.");
		unemployedNbr--;
		return new Unemployed();
	}
	public synchronized boolean isAvailable(String workName){
		switch(SocietyConfig.AVAILABLE_JOBS_DIC.get(workName)){
			case -1:
				return false;
			case 0:
				return true;
			case 1:
				if(farmerNbr > 0)
					return true;
			case 2:
				if(builderNbr>0)
					return true;
			case 3:
				if(woodcutterNbr>0)
					return true;
		}
		return  false;
	}
	public synchronized void setIncome(BankAccount account,Calendar current){
		if (date.getMonth() != current.getMonth()){
			this.builderIncome = 0;
			this.farmerIncome = 0;
			this.woodcutterIncome = 0;
			date = current.copy();
		}
		//Job income is computed as the average
		if(SocietyConfig.AVRG_INCOME){
			this.farmerIncome += account.getReceivedMoneyFarmer();
			this.builderIncome += account.getReceivedMoneyBuilder();
			this.woodcutterIncome += account.getReceivedMoneyWC();
		}
		//Job income is the best income found
		else{
			if(farmerIncome < account.getReceivedMoneyFarmer()){
				this.farmerIncome = account.getReceivedMoneyFarmer();
			}
			if(builderIncome < account.getReceivedMoneyBuilder()){
				this.builderIncome = account.getReceivedMoneyBuilder();
			}
			if(woodcutterIncome < account.getReceivedMoneyWC()){
				this.woodcutterIncome = account.getReceivedMoneyWC();
			}
		}

	}
	public synchronized int getIncome(String workName){
		switch(SocietyConfig.AVAILABLE_JOBS_DIC.get(workName)){
			case 0:
				return 0;
			case 1:
				if(getFarmerNbr() > 0)
					return this.farmerIncome/getFarmerNbr();
				break;
			case 2:
				if(getBuilderNbr() > 0)
					return this.builderIncome/getBuilderNbr();
				break;
			case 3:
				if(getWoodcutterNbr() > 0)
					return this.woodcutterIncome/getWoodcutterNbr();
				break;
		}
		return 0;
	}
	public synchronized int getIncomeByMarket(String workName, PeoplePossessions possessions){
		switch(SocietyConfig.AVAILABLE_JOBS_DIC.get(workName)){
			case 0:
				return SocietyConfig.BASE_INCOME_UNEMPLOYED;
			case 1:
				if(foodSupply.offerNbr() == 0 && getFarmerNbr() == 0)
					return Integer.MAX_VALUE;
				return (int)(foodSupply.meanItemPrice()*possessions.getLandSurface()*SocietyConfig.FARMER_YIELD/foodSupply.offerNbr());
			case 2:
				if(houseSupply.offerNbr() == 0 && getBuilderNbr() == 0)
					return Integer.MAX_VALUE;
				return 0;
				//return (int)(houseSupply.meanItemPrice()*possessions.getLandSurface()/houseSupply.offerNbr());
			case 3:
				if(woodSupply.offerNbr() == 0 && getWoodcutterNbr() == 0)
					return Integer.MAX_VALUE;
				return (int)(woodSupply.meanItemPrice()*possessions.getLandSurface()*SocietyConfig.WOODCUTTER_YIELD/woodSupply.offerNbr());
		}
		return 0;
	}
	public synchronized String toString(){
		return "Farmer left : "+ farmerNbr + " Builder left " + builderNbr + " Farmer init "+ SocietyConfig.MAX_FARMER_NBR + " Builder init " + SocietyConfig.MAX_BUILDER_NBR;
	}

	public synchronized int getFarmerNbr(){
		return SocietyConfig.MAX_FARMER_NBR-farmerNbr;
	}
	public synchronized int getWoodcutterNbr(){
		return SocietyConfig.MAX_WOODCUTTER_NBR-woodcutterNbr;
	}
	public synchronized int getBuilderNbr(){
		return SocietyConfig.MAX_BUILDER_NBR-builderNbr;
	}
	public synchronized int getUnemployedNbr(){
		return SocietyConfig.PEOPLE_NBR-unemployedNbr;
	}
}