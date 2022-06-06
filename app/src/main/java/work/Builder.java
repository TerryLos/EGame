package work;

import calendar.Calendar;
import market.HouseMarket;
import config.SocietyConfig;
import java.lang.Math;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import market.Tradable;
import people.BankAccount;
import people.PeoplePossessions;

public class Builder extends Work{

	HouseMarket market;
	int neededWood;
	int neededStone;

	int stockWood;
	int stockLand;
	int stockStone;

	float priceHouse;
	int materialPrice;
	public Builder(HouseMarket houseSupply, BankAccount account, PeoplePossessions possessions){
		super();
		this.market = houseSupply;
		this.account = account;
		this.possession = possessions;
		this.neededWood = SocietyConfig.BUILDER_WOOD_QTITY;
		this.neededStone = SocietyConfig.BUILDER_STONE_QTITY;
		this.stockWood = 0;
		this.stockLand = 0;
		this.priceHouse = 0;
		this.materialPrice = 0;

	}

	/*
		Input : greediness (int)
		Does : executes an iteration of work cycle if the conditions are met.
	 */
	@Override
	public void work(int greediness){
		if(stockWood >= neededWood && stockLand >= SocietyConfig.BUILDER_LAND_QTITY && stockStone
				>= neededStone){
			if(workedTime>=SocietyConfig.BUILDER_WORK_GATHERING){
				market.addSupply(new Tradable((float) priceEstimate(greediness),SocietyConfig.BUILDER_LAND_QTITY,account));
				workedTime = 0;
				stockWood -= neededWood;
				stockStone -= neededStone;
				stockLand -= SocietyConfig.BUILDER_LAND_QTITY;
				priceHouse = 0;
				materialPrice = 0;

				//updates wood quantity
				gainExperience();
				neededWood = Math.round(SocietyConfig.BUILDER_WOOD_QTITY/getExperience());
				neededStone = Math.round(SocietyConfig.BUILDER_STONE_QTITY/getExperience());
			}
			else
				workedTime += getExperience();
		}
		//Refills the working buffer
		else{
			rawMaterialPrice();
		}
	}
	/*
		Does : extracts the needed materials out of PeoplePossessions and
		computes their price
	 */
	public void rawMaterialPrice(){
		List<Tradable> wood = possession.deleteWood(neededWood-stockWood);
		List<Tradable> land = possession.deleteLandSurface(SocietyConfig.BUILDER_LAND_QTITY-stockLand);
		List<Tradable> stone = possession.deleteStone(neededStone-stockStone);
		for(Tradable wd:wood){
			if(stockWood<=neededWood){
				materialPrice += wd.getVolume()*wd.getAskedPrice();
				stockWood += wd.getVolume();
			}
		}
		for(Tradable ld:land){
			if(stockLand<=SocietyConfig.BUILDER_LAND_QTITY){
				materialPrice += ld.getVolume()*ld.getAskedPrice();
				stockLand += ld.getVolume();
			}
		}
		for(Tradable st:stone){
			if(stockStone<=neededStone){
				materialPrice += st.getVolume()*st.getAskedPrice();
				stockStone += st.getVolume();
			}
		}
	}
	/*
		Input : greediness (int)
		Does : returns the price of the next harvest based on the greediness coefficient
	 */
	@Override
	public float priceEstimate(int greediness){
		Tradable bestOffer = market.getBestOffer();
		int commodity = account.getSpentMoney();
		if (this.priceHouse != 0)
			return this.priceHouse;
		if (this.materialPrice == 0){
			rawMaterialPrice();
			if(materialPrice == 0)
				return 0;
		}

		//Avoids spike at startup because buying a land is an investment and shouldn't
		//Be paid back directly
		if(account.getSpentMoney()>= possession.getLandPrice())
			commodity = account.getSpentMoney() - possession.getLandPrice();

		float basePrice = (float) (greedinessCoeff(greediness,market.offerNbr())*Math.abs(1+ThreadLocalRandom.current().nextGaussian() * (materialPrice + commodity)));
		float lowestMarketPrice = bestOffer.getAskedPrice();

		//Wants to at least cover its expenses.
		if(basePrice>= lowestMarketPrice)
			this.priceHouse = basePrice;
			//Wants to maximise its revenues therefore place himself as first on the market
		else{
			if(lowestMarketPrice - 1 <= 0)
				this.priceHouse = 1;
			else
				this.priceHouse = (float) (lowestMarketPrice-ThreadLocalRandom.current().nextDouble((lowestMarketPrice-basePrice)));
		}
		return this.priceHouse;
	}
	@Override
	public int yieldEstimate(){
		return 1;
	}
	/*
		Does : returns builder as a formatted string
	 */
	@Override
	public String toString(){
		return "job : Builder\t Income : "+Integer.toString(this.income)+"\t Experience : "+getExperience()+"\n"
			+"Production at :"+Integer.toString(workedTime)+"\t on :"+Integer.toString(SocietyConfig.BUILDER_WORK_GATHERING)+"\n"+
				"filled wood : "+stockWood+" and land : "+stockLand;
	}
	@Override
	public String jobString(){
		return "Builder";
	}
	@Override
	public int getNeededWood(){return neededWood;}
	@Override
	public int getNeededStone(){return neededStone;}
	/*
		Does : returns the work cycle status
	 */
	@Override
	public float getWorkStatus(){
		if(stockWood >= neededWood && stockLand >= SocietyConfig.BUILDER_LAND_QTITY)
			return workedTime/(float)SocietyConfig.BUILDER_WORK_GATHERING;
		return 0;
	}
}