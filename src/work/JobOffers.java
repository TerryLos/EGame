package work;

import config.SocietyConfig;
import market.FoodMarket;
import market.HouseMarket;
import market.LandMarket;
import people.BankAccount;
import java.util.HashMap;
import logger.Logger;
import logger.LoggerException;

public class JobOffers{

	private int farmerNbr;
	private int builderNbr;

	private FoodMarket foodsupply;
	private HouseMarket houseSupply;

	public JobOffers(FoodMarket foodsupply,HouseMarket houseSupply){
		farmerNbr = SocietyConfig.MAX_FARMER_NBR;
		builderNbr = SocietyConfig.MAX_BUILDER_NBR;
		
		this.foodsupply = foodsupply;
		this.houseSupply = houseSupply;
		//Inits the list of jobs
		SocietyConfig.AVAILABLE_JOBS_DIC = new HashMap<>();
		SocietyConfig.AVAILABLE_JOBS_DIC.put("Unemployed",0);
        SocietyConfig.AVAILABLE_JOBS_DIC.put("Farmer",1);
        SocietyConfig.AVAILABLE_JOBS_DIC.put("Builder",2);
	}

	/*Gets the job associated to the name.
	and returns the income associated to it.
	People should check if the job is available before submitting to the offer.
	*/
	public synchronized Work takeOffer(String workName,Work previousJob,BankAccount account) throws LoggerException{
		//Takes the offer
		switch(SocietyConfig.AVAILABLE_JOBS_DIC.get(workName)){
			case 0:
				return new Unemployed();
			case 1:
				if(farmerNbr > 0){
					Farmer job = new Farmer(foodsupply,account);
					farmerNbr--;
					return job;
				}
			case 2:
				if(builderNbr>0)
				{
					Builder job = new Builder(houseSupply);
					builderNbr--;
					return job;
				}
		}

		//Gives back his previous job.
		if(previousJob instanceof Farmer)
			farmerNbr++;
		if(previousJob instanceof Builder)
			builderNbr++;

		Logger.WARN("No "+workName+" available ! A person took it before.");
		return new Unemployed();
	}
	public synchronized int getIncome(String workName){
		switch(SocietyConfig.AVAILABLE_JOBS_DIC.get(workName)){
			case 0:
				return 0;
			case 1:
				return computeIncome(SocietyConfig.BASE_INCOME_FARMER,SocietyConfig.MAX_FARMER_NBR,SocietyConfig.MAX_FARMER_NBR-farmerNbr);
			case 2:
				return computeIncome(SocietyConfig.BASE_INCOME_BUILDER,SocietyConfig.MAX_BUILDER_NBR,SocietyConfig.MAX_BUILDER_NBR-builderNbr);
		}
		return 0;
	}
	public synchronized boolean isFarmerAvailable(){
		if(farmerNbr>=0)
			return true;

		return false;
	}

	private synchronized int computeIncome(int minIncome,int maxWorkerNbr,int currentWorkerNbr){
		if(currentWorkerNbr >= maxWorkerNbr)
			return 0;
		return (int)(minIncome + (2*minIncome/(maxWorkerNbr-currentWorkerNbr)));
	}
}