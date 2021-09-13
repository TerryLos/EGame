package work;

import market.HouseMarket;
import config.SocietyConfig;
import java.lang.Math;
import config.SocietyConfig;

public class Builder extends Work{

	HouseMarket houseSupply;

	public Builder(HouseMarket houseSupply){
		super();
		this.houseSupply = houseSupply;
		this.income = SocietyConfig.BASE_INCOME_BUILDER;
	}

	@Override
	public void work(){
		if(workedTime>=SocietyConfig.BUILDER_WORK_GATHERING){
			workedTime = 0;
		}
		else
			workedTime += getExperience()/2;

		gainExperience();
	}

	@Override
	public String toString(){
		return "job : Builder\t Income : "+Integer.toString(this.income)+"\t Experience : "+getExperience()+"\n"
			+"Production at :"+Integer.toString(workedTime)+"\t on :"+Integer.toString(SocietyConfig.BUILDER_WORK_GATHERING)+"\n";
	}
}