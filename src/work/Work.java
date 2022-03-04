package work;

import java.lang.Math;
import calendar.Calendar;
import config.SocietyConfig;
import people.BankAccount;
import people.PeopleCharacteristics;
import people.PeoplePossessions;
public class Work{

	protected BankAccount account;
	protected PeoplePossessions possession;
	protected int income;
	protected int workedTime;
	protected Calendar lastWorkedTime;
	protected Calendar lastWorkSwitch;
	private float experience;

	public Work(){
		this.income = 0;
		this.experience = 1;
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
		experience += 1.0/Math.exp((int)experience);
	}
	public float getExperience(){
		return experience;
	}
	public void deleteOffer(){}
	public String toString(){return "Job : None\t";}
	public String jobString() {return "Nowork";}
	public Calendar getLastWorkedTime(){
		return lastWorkedTime;
	}
	public void setWorkingTime(Calendar toSet){
		lastWorkedTime = toSet.copy();
	}
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
	public float getWorkStatus() {return 0.0F;}
	public int getNeededWood(){return 0;}
	public int getNeededStone(){return 0;}

}