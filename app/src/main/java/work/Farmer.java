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
		this.foodSupply = foodSupply;
		this.account = account;
		this.possession = possessions;
		this.yieldPrice = 0;
	}
	/*
		Input : greediness (int)
		Does : executes an iteration of work cycle if the conditions are met.
	 */
	@Override
	public void work(int greediness){
		//Can only work if he has lands
		if(possession.getLandSurface()>0){
			if(workedTime>=SocietyConfig.FARMER_WORK_GATHERING){
				foodSupply.addSupply(new Tradable(priceEstimate(greediness),yieldEstimate(),account));
				workedTime = 0;
				this.yieldPrice=0;
				gainExperience();
			}
			else
				workedTime++;
		}
	}
	/*
		Does : returns farmer as a formatted string
	 */
	@Override
	public String toString(){
		return "job : Farmer\t Income : "+Integer.toString(this.income)+"\t Experience : "+getExperience()+"\n"
			+"Production at :"+Integer.toString(workedTime)+"\t on :"+Integer.toString(SocietyConfig.FARMER_WORK_GATHERING)+"\n";
	}
	@Override
	public String jobString(){
		return "Farmer";
	}
	/*
		Does : returns the farmer yield
	 */
	@Override
	public int yieldEstimate(){
		return (int) (possession.getLandSurface()*SocietyConfig.FARMER_YIELD*getExperience());
	}
	/*
		Input : greediness (int)
		Does : returns the price of the next harvest based on the greediness coefficient
	 */
	@Override
	public float priceEstimate(int greediness){
		Tradable bestOffer = foodSupply.getBestOffer();
		int commodity = 0;
		float avgLandPrice = possession.getLandPrice()/ (float)possession.getLandSurface();
		if(possession.getLandSurface() == 0)
			return 0;
		if (this.yieldPrice != 0)
			return this.yieldPrice;
		if(account.getSpentMoney()>= possession.getLandPrice())
			commodity = account.getSpentMoney() - possession.getLandPrice();
		float basePrice = (float) (greedinessCoeff(greediness,foodSupply.offerNbr())*Math.abs(1+ThreadLocalRandom.current().nextGaussian())
				* ((avgLandPrice + commodity)/ yieldEstimate()));
		float lowestMarketPrice = bestOffer.getAskedPrice();

		//Wants to at least cover its expenses.
		if(basePrice>= lowestMarketPrice)
			this.yieldPrice = basePrice;
		//Wants to maximise its revenues therefore place himself as first on the market
		else
			this.yieldPrice = lowestMarketPrice - (float) ThreadLocalRandom.current().nextDouble(lowestMarketPrice-basePrice);

		return this.yieldPrice;
	}
	/*
		Does : returns the work cycle status
	 */
	@Override
	public float getWorkStatus(){
		if(possession.getLandSurface()>0)
			return workedTime/(float)SocietyConfig.FARMER_WORK_GATHERING;
		return 0;
	}
}