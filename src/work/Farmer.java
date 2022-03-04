package work;

import calendar.Calendar;
import market.FoodMarket;
import market.Tradable;
import people.BankAccount;
import people.PeopleCharacteristics;
import people.PeoplePossessions;
import config.SocietyConfig;
import java.lang.Math;
import java.util.concurrent.ThreadLocalRandom;

import config.SocietyConfig;

public class Farmer extends Work{

	private float yieldPrice;
	FoodMarket foodSupply;
	
	public Farmer(FoodMarket foodSupply, BankAccount account, PeoplePossessions possessions){
		super();
		this.workedTime = SocietyConfig.FARMER_WORK_GATHERING;
		this.foodSupply = foodSupply;
		this.account = account;
		this.possession = possessions;
		this.yieldPrice = 0;
	}
	@Override
	public void work(int greedyness){
		//Can only work if he has lands
		if(possession.getLandSurface()>0){
			if(workedTime>=SocietyConfig.FARMER_WORK_GATHERING){
				foodSupply.addSupply(new Tradable(priceEstimate(greedyness),yieldEstimate(),account));
				workedTime = 0;
				this.yieldPrice=0;
				gainExperience();
			}
			else
				workedTime++;
		}
	}

	@Override
	public String toString(){
		return "job : Farmer\t Income : "+Integer.toString(this.income)+"\t Experience : "+getExperience()+"\n"
			+"Production at :"+Integer.toString(workedTime)+"\t on :"+Integer.toString(SocietyConfig.FARMER_WORK_GATHERING)+"\n";
	}
	@Override
	public String jobString(){
		return "Farmer";
	}
	@Override
	public void deleteOffer(){
		foodSupply.deleteOffer(account);
	}
	@Override
	public int yieldEstimate(){
		return  possession.getLandSurface()*SocietyConfig.FARMER_YIELD*(int)getExperience();
	}
	@Override
	public float priceEstimate(int greedyness){
		Tradable bestOffer = foodSupply.getBestOffer();
		int commodity = account.getSpentMoney();
		if (this.yieldPrice != 0)
			return this.yieldPrice;
		//Avoids spike at startup because buying a land is an investment and shouldn't
		//Be paid back directly
		if(account.getSpentMoney()>= possession.getLandPrice())
		 	commodity = account.getSpentMoney() - possession.getLandPrice();

		float basePrice = (float)(Math.round(greedyness/4.0)*Math.abs(1+ThreadLocalRandom.current().nextGaussian()) * (possession.getLandPrice()/100.0 + commodity)/ yieldEstimate());
		float lowestMarketPrice = bestOffer.getAskedPrice();

		//Wants to at least cover its expenses.
		if(basePrice>= lowestMarketPrice)
			this.yieldPrice = basePrice;
		//Wants to maximise its revenues therefore place himself as first on the market
		else{
			if(lowestMarketPrice - 1 <= 0)
				this.yieldPrice = 1;
			else
				this.yieldPrice = lowestMarketPrice- (float) ThreadLocalRandom.current().nextDouble(lowestMarketPrice-basePrice);
			}
		return this.yieldPrice;
	}
	@Override
	public float getWorkStatus(){
		return workedTime/(float)SocietyConfig.FARMER_WORK_GATHERING;
	}
}