package work;

import config.SocietyConfig;
import market.StoneMarket;
import market.Tradable;
import people.BankAccount;
import people.PeoplePossessions;

import java.util.concurrent.ThreadLocalRandom;

public class Miner extends Work{

	private float yieldPrice;
	StoneMarket stoneSupply;
	private int difficulty;
	private int lastLandSize;

	public Miner(StoneMarket stoneSupply, BankAccount account, PeoplePossessions possessions){
		super();
		this.stoneSupply = stoneSupply;
		this.account = account;
		this.possession = possessions;
		this.yieldPrice = 0;
		this.difficulty = 0;
		this.lastLandSize = 0;
	}
	/*
		Input : greediness (int)
		Does : executes an iteration of work cycle if the conditions are met.
	 */
	@Override
	public void work(int greediness){
		//Can only work if he has lands
		if(possession.getLandSurface()>0){
			if(workedTime>=(SocietyConfig.MINER_WORK_GATHERING+SocietyConfig.MINER_DIFFICULTY)){
				stoneSupply.addSupply(new Tradable(priceEstimate(greediness),yieldEstimate(),account));
				workedTime = 0;
				this.yieldPrice=0;
				gainExperience();
				updateDifficulty();
			}
			else
				workedTime += getExperience();
		}
	}
	/*
		Does : returns miner as a formatted string
	 */
	@Override
	public String toString(){
		return "job : Miner\t Income : "+Integer.toString(this.income)+"\t Experience : "+getExperience()+"\n"
			+"Production at :"+Integer.toString(workedTime)+"\t on :"+Integer.toString(SocietyConfig.MINER_WORK_GATHERING)+"\n";
	}
	@Override
	public String jobString(){
		return "Miner";
	}
	/*
		Does : returns the miner yield
	 */
	@Override
	public int yieldEstimate(){
		return  Math.round(possession.getLandSurface()*SocietyConfig.MINER_YIELD);
	}
	/*
		Input : greediness (int)
		Does : returns the price of the next harvest based on the greediness coefficient
	 */
	@Override
	public float priceEstimate(int greediness){
		Tradable bestOffer = stoneSupply.getBestOffer();
		int commodity = 0;
		float avgLandPrice = possession.getLandPrice()/ (float)possession.getLandSurface();
		if(possession.getLandSurface() == 0)
			return 0;
		if (this.yieldPrice != 0)
			return this.yieldPrice;
		if(account.getSpentMoney()>= possession.getLandPrice())
			commodity = account.getSpentMoney() - possession.getLandPrice();
		float basePrice = (float) (greedinessCoeff(greediness,stoneSupply.offerNbr())*Math.abs(1+ThreadLocalRandom.current().nextGaussian())
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
	/*
		Does : updates the mining difficulty based on the amount of land possessed
	 */
	public void updateDifficulty(){
		int actualSurface = possession.getLandSurface();
		int landDiff = actualSurface - lastLandSize;

		if(landDiff == 0)
			difficulty += SocietyConfig.MINER_DIFFICULTY;
		else
			difficulty = Math.round(difficulty*(lastLandSize/(float)actualSurface));

		lastLandSize = actualSurface;
	}
}