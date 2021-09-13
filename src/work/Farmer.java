package work;

import market.FoodMarket;
import market.Tradable;
import people.BankAccount;
import people.PeoplePossessions;
import config.SocietyConfig;
import java.lang.Math;
import java.util.concurrent.ThreadLocalRandom;

import config.SocietyConfig;

public class Farmer extends Work{

	FoodMarket foodSupply;
	
	public Farmer(FoodMarket foodSupply,BankAccount account){
		super();
		//So it allows to produce food since the start.
		this.workedTime = SocietyConfig.FARMER_WORK_GATHERING;
		this.foodSupply = foodSupply;
		this.income = SocietyConfig.BASE_INCOME_FARMER;
		this.account = account;
	}
	@Override
	public void work(){
		//Can only work if he has lands
		if(possession.getLandSurface()>0){
			if(workedTime>=SocietyConfig.FARMER_WORK_GATHERING){
				foodSupply.addSupply(new Tradable(priceEstimate(),yieldEstimate(),account));
				System.out.println("Farmer produced :"+Integer.toString(yieldEstimate())+ " for "+Integer.toString(priceEstimate()));
				workedTime = 0;
				
			}
			else
				workedTime++;
			gainExperience();
		}
	}

	@Override
	public String toString(){
		return "job : Farmer\t Income : "+Integer.toString(this.income)+"\t Experience : "+getExperience()+"\n"
			+"Production at :"+Integer.toString(workedTime)+"\t on :"+Integer.toString(SocietyConfig.FARMER_WORK_GATHERING)+"\n";
	}
	@Override
	public void deleteOffer(){
		foodSupply.deleteOffer(account);
	}
	@Override
	public int yieldEstimate(){
		return 1000+(int)(Math.log(1+getExperience()/Math.log(2))* SocietyConfig.FARMER_YIELD*possession.getLandSurface());
	}
	@Override
	public int priceEstimate(){
		Tradable bestOffer = foodSupply.getBestOffer();
		int basePrice = (int)((1+ ThreadLocalRandom.current().nextGaussian())*possession.getLandPrice() + account.getAverageExpenses())/yieldEstimate();
		int lowestMarketPrice = 0;
		if(bestOffer != null)
			lowestMarketPrice = bestOffer.getAskedPrice();

		//Wants to at least cover its expenses.
		if(basePrice>= lowestMarketPrice)
			return basePrice;
		//Wants to maximise its revenues therefore place himself as first on the market
		else{
			if(lowestMarketPrice - 1 <= 0)
				return 1;
			else
				return lowestMarketPrice-1;
			}
	}
}