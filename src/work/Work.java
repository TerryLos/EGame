package work;

import java.lang.Math;
import calendar.Calendar;
import people.BankAccount;
import people.PeoplePossessions;
public class Work{

	protected BankAccount account;
	protected PeoplePossessions possession;
	protected int studyLevel;
	protected int income;
	protected int workedTime;
	private Calendar lastWorkedTime;
	private float experience;

	public Work(){
		this.studyLevel = 0;
		this.income = 0;
		this.experience = 0;
		this.workedTime = 0;
		this.lastWorkedTime = null;
		possession=null;
	}
	public void work(){};
	public void setPossession(PeoplePossessions possession){
		this.possession = possession;
	}
	public int getStudyLevel(){
		return studyLevel;
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
	public String toString(){
		return "Job : None\t";
	}
	public Calendar getLastWorkedTime(){
		return lastWorkedTime;
	}
	public void setWorkingTime(Calendar toSet){
		lastWorkedTime = toSet.copy();
	}
	public int yieldEstimate(){
		return 0;
	}
	public int priceEstimate(){
		return 0;
	}

}