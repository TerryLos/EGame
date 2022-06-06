package work;


import calendar.Calendar;
import config.SocietyConfig;

public class Unemployed extends Work{

	public Unemployed(){
		super();
		this.income = SocietyConfig.BASE_INCOME_UNEMPLOYED;
	}
	@Override
	public String jobString() {return "Unemployed";}
	@Override
	public String toString(){
		return "job : Unemployed\t Income : "+Integer.toString(this.income)+"\n";
	}
	@Override
	public int getNeededLand() { return 0;}
}