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

	int stockWood;
	int stockLand;

	float priceHouse;
	int materialPrice;
	public Builder(HouseMarket houseSupply, BankAccount account, PeoplePossessions possessions){
		super();
		this.market = houseSupply;
		this.account = account;
		this.possession = possessions;
		this.neededWood = SocietyConfig.BUILDER_WOOD_QTITY;
		this.stockWood = 0;
		this.workedTime = SocietyConfig.BUILDER_WORK_GATHERING;
		this.stockLand = 0;
		this.priceHouse = 0;
		this.materialPrice = 0;

	}

	@Override
	public void work(int greedyness){
		if(stockWood >= neededWood && stockLand >= SocietyConfig.BUILDER_LAND_QTITY){
			if(workedTime>=SocietyConfig.BUILDER_WORK_GATHERING){;
				market.addSupply(new Tradable((float) priceEstimate(greedyness),SocietyConfig.BUILDER_LAND_QTITY,account));
				workedTime = 0;
				stockWood -= neededWood;
				stockLand -= SocietyConfig.BUILDER_LAND_QTITY;
				priceHouse = 0;
				materialPrice = 0;
				//updates wood quantity
				neededWood = (int) (SocietyConfig.BUILDER_WOOD_QTITY/(Math.log(getExperience())/(float)Math.log(2)));
				gainExperience();
			}
			else
				workedTime += getExperience();
		}
		//Refills the working buffer
		else{
			rawMaterialPrice();
		}
	}
	@Override
	public int getNeededWood(){return neededWood;}
	public void rawMaterialPrice(){
		List<Tradable> wood = possession.getWood();
		List<Tradable> woodToRem = new ArrayList<>();
		List<Tradable> land = possession.getLand();
		List<Tradable> landToRem = new ArrayList<>();

		for(Tradable wd:wood){
			if(stockWood<neededWood){
				materialPrice += wd.getVolume()*wd.getAskedPrice();
				stockWood += wd.getVolume();
				woodToRem.add(wd);
			}
		}
		for(Tradable wd:land){
			if(stockLand<SocietyConfig.BUILDER_LAND_QTITY){
				materialPrice += wd.getVolume()*wd.getAskedPrice();

				stockLand += wd.getVolume();
				landToRem.add(wd);
			}
		}
		possession.deleteWood(woodToRem);
		possession.deleteLandSurface(landToRem);
	}
	@Override
	public float priceEstimate(int greedyness){
		Tradable bestOffer = market.getBestOffer();
		int commodity = account.getSpentMoney();
		if (this.priceHouse != 0)
			return this.priceHouse;
		if (this.materialPrice == 0)
			rawMaterialPrice();
		//Avoids spike at startup because buying a land is an investment and shouldn't
		//Be paid back directly
		if(account.getSpentMoney()>= possession.getLandPrice())
			commodity = account.getSpentMoney() - possession.getLandPrice();

		int basePrice = (int) (Math.round(greedyness/2.0)*Math.abs(1+ThreadLocalRandom.current().nextGaussian() * (materialPrice + commodity)));
		float lowestMarketPrice = bestOffer.getAskedPrice();

		//Wants to at least cover its expenses.
		if(basePrice>= lowestMarketPrice)
			this.priceHouse = basePrice;
			//Wants to maximise its revenues therefore place himself as first on the market
		else{
			if(lowestMarketPrice - 1 <= 0)
				this.priceHouse = 1;
			else
				this.priceHouse = (int) (lowestMarketPrice-ThreadLocalRandom.current().nextInt((int)(lowestMarketPrice-basePrice)));
		}
		return this.priceHouse;
	}
	@Override
	public int yieldEstimate(){
		return 1;
	}
	@Override
	public String toString(){
		return "job : Builder\t Income : "+Integer.toString(this.income)+"\t Experience : "+getExperience()+"\n"
			+"Production at :"+Integer.toString(workedTime)+"\t on :"+Integer.toString(SocietyConfig.BUILDER_WORK_GATHERING)+"\n";
	}
	@Override
	public String jobString(){
		return "Builder";
	}
	@Override
	public float getWorkStatus(){
		return workedTime/(float)SocietyConfig.BUILDER_WORK_GATHERING;
	}
}