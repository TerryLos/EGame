package work;

import java.lang.Math;
import calendar.Calendar;
import config.PeopleConfig;
import config.SocietyConfig;
import people.BankAccount;
import people.PeopleCharacteristics;
import people.PeoplePossessions;
public class Work{

	protected BankAccount account;
	protected PeoplePossessions possession;
	protected int income;
	protected int workedTime;
	protected float mathCoef;
	protected Calendar lastWorkedTime;
	protected Calendar lastWorkSwitch;
	private float experience;

	public Work(){
		this.income = 0;
		this.experience = 1;
		this.mathCoef = 0;
		this.workedTime = 0;
		this.lastWorkedTime = new Calendar();
		this.lastWorkSwitch = null;
		this.possession= new PeoplePossessions();
	}
	public void work(int greedyness){};
	public void setPossession(PeoplePossessions possession){
		this.possession = possession;
	}
	public void setIncome(int income){
		this.income = income;
	}
	public int getIncome(){
		return income;
	}
	public void gainExperience(){
		if(experience < PeopleConfig.MAX_EXPERIENCE_COEFF)
			experience += 0.1;
	}
	public float getExperience(){
		return experience;
	}
	public float greedinessCoeff(int greediness,int marketSize){
		float base = 2-greediness/10F;
		if(mathCoef == 0){
			mathCoef = (float) (Math.log(1/9.F)/Math.log(base));
		}
		return (float)(1/Math.pow(base,(marketSize+mathCoef)) + 1);
	}
	public String toString(){return "Job : None\t";}
	public String jobString() {return "Nowork";}
	public Calendar getLastWorkedTime(){
		return lastWorkedTime;
	}
	public void setWorkingTime(Calendar toSet){
		lastWorkedTime = toSet.copy();
	}
	public void incWorkedTime() { workedTime +=1; }
	public void decWorkedTime() { if(workedTime>0) workedTime -=1; }
	public void setLastWorkSwitch(Calendar toSet){
		lastWorkedTime = toSet.copy();
		lastWorkSwitch = toSet.copy();
	}
	public Calendar getLastWorkSwitch(){return lastWorkSwitch;}
	public int yieldEstimate(){
		return 0;
	}
	public float priceEstimate(int greedyness){
		return 0;
	}
	public void setWorkedTime(){workedTime = 0;}
	public int getNeededLand(){ return SocietyConfig.LAND_PARCEL;}
	public float getWorkStatus() {return 0.0F;}
	public int getNeededWood(){return 0;}
	public int getNeededStone(){return 0;}

}