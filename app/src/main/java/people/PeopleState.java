package people;

import java.util.concurrent.ThreadLocalRandom;
import config.PeopleConfig;

import calendar.Calendar;

/*
PeopleState encapsulates all the variables that take care of a person state 
at a certain time.

It contains its hungriness,sleepiness,sickness and others elements.

*/
public class PeopleState{
    private int aging;
    private int health;
    private int hungriness;
    private int sleepiness;
    private int libido;
    private ThreadLocalRandom threadLocalRandom;
    //When creating a people it's full

	private boolean pregnant;
	private Calendar pregnantDate;

    public PeopleState(){
    	this.threadLocalRandom = ThreadLocalRandom.current();
    	this.hungriness = PeopleConfig.PEOPLE_FOOD_ROOF;
    	this.sleepiness =  PeopleConfig.PEOPLE_SLEEP_ROOF;
    	this.libido = 0;
    	this.health = PeopleConfig.PEOPLE_HEALTH_ROOF;
    	this.pregnant = false;
    }

    //Copy constructor
    public PeopleState(ThreadLocalRandom threadLocalRandom,int hungriness,int sleepiness,int libido,
        int health,boolean pregnant,Calendar pregnantDate){
        this.threadLocalRandom = threadLocalRandom;
        this.hungriness = hungriness;
        this.sleepiness = sleepiness;
        this.libido = libido;
        this.health = health;
        this.pregnant = pregnant;
        this.pregnantDate = pregnantDate;
    }
    //Updates the state and returns the probability vector of the states
    public void updateState(){

        //The more it's hungry/sleepy the more it takes damages from it
        if(hungriness>= -PeopleConfig.PEOPLE_FOOD_ROOF)
            hungriness = hungriness - threadLocalRandom.nextInt(PeopleConfig.PEOPLE_EAT_RATE+1);
        if(sleepiness>= -PeopleConfig.PEOPLE_SLEEP_ROOF)
            sleepiness = sleepiness - threadLocalRandom.nextInt(PeopleConfig.PEOPLE_SLEEP_RATE+1);
    	if(libido < PeopleConfig.PEOPLE_LIBIDO_ROOF && !pregnant)
    		libido = libido + threadLocalRandom.nextInt(PeopleConfig.PEOPLE_LIBIDO_RATE+1);

    	if(sleepiness <=0)
    		health = health - threadLocalRandom.nextInt(((int)-sleepiness/10)*PeopleConfig.PEOPLE_DYING_RATE+1);
        if(hungriness <=0)
            health = health - threadLocalRandom.nextInt(((int)-hungriness/10)*PeopleConfig.PEOPLE_DYING_RATE+1);


    }
    public void eat(int value){

        if(value > PeopleConfig.PEOPLE_FOOD_ROOF+1 - hungriness)
            hungriness = PeopleConfig.PEOPLE_FOOD_ROOF;
        else
            hungriness = hungriness + value;

        if(health < (PeopleConfig.PEOPLE_HEALTH_ROOF-Math.exp(aging-4)))
            restoreHealth();

    }
    public void sleep(){
        
        if(sleepiness  + PeopleConfig.PEOPLE_SLEEP_RECOVERY > PeopleConfig.PEOPLE_SLEEP_ROOF)
            sleepiness = sleepiness + (PeopleConfig.PEOPLE_SLEEP_ROOF - sleepiness);

        else
            sleepiness = sleepiness + PeopleConfig.PEOPLE_SLEEP_RECOVERY;

        if(health < (PeopleConfig.PEOPLE_HEALTH_ROOF-Math.exp(aging-4)))
    		restoreHealth();
    		
    }
    private void restoreHealth(){
    	int nbr1 = threadLocalRandom.nextInt(2);
    	
    	if(nbr1 == 0)
    		health = health + threadLocalRandom.nextInt((int)(PeopleConfig.PEOPLE_HEALTH_ROOF-Math.exp(aging-4))+1-health);
    		
    }
    public int getHealth(){
    	return health;
    }
    public int getSleep(){
        return sleepiness;
    }
    public int getFood(){
        return hungriness;
    }
    public int getLibido(){
        return libido;
    }
    public Calendar getPregnantDate(){
        return pregnantDate;
    }
    public void setAging(int aging){
        this.aging = aging;
    }
    public String toString(){
    
    	if(pregnant)
			return "State :\n"+"Health : "+Integer.toString(health)+"\t Hungryness : " +Integer.toString(hungriness)+
			"\n sleepiness : "+Integer.toString(sleepiness)+"\t libido : "+Integer.toString(libido)+"\n"+ "Pregnant : "+pregnant+"\t"+ " At "+pregnantDate.toString()+
            "\n Aging : "+Integer.toString(aging)+"\n";
			
    	else
			return "State :\n"+"Health : "+Integer.toString(health)+"\t Hungryness : " +Integer.toString(hungriness)+
					"\n sleepiness : "+Integer.toString(sleepiness)+"\t libido : "+Integer.toString(libido)+"\n"+ "Pregnant : "+pregnant+
                    "\n Aging : "+Integer.toString(aging)+"\n";
					
	}
    public void hadSex(boolean sex,Calendar currentTime){
        int nbr = threadLocalRandom.nextInt(PeopleConfig.PEOPLE_FERTILITY);
    	libido = 0;

    	if(sex && !pregnant && nbr == 1){
			pregnantDate = currentTime.copy();
            pregnant = true;
		}
		
	}
	public boolean isPregnant(){
    		return pregnant;
	}
	public boolean giveBirth(Calendar currentTime){
	
    	if(pregnant && pregnantDate !=null && pregnantDate.isDelayElapsed(currentTime,PeopleConfig.PEOPLE_GESTATION_TIME,1)){
    		pregnant = false;
			pregnantDate = null;
			return true;
		}
		return false;
		
	}
	public boolean isDead(){
        return health <= 0;
    }
    public PeopleState copy(){
        return new PeopleState(threadLocalRandom,hungriness,
                sleepiness,libido,health,pregnant,pregnantDate);
    }
    public void resume(PeopleState state){
        this.hungriness = state.getFood();
        this.sleepiness = state.getSleep();
        this.libido = state.getLibido();
        this.health = state.getHealth();
        this.pregnant = state.isPregnant();
        this.pregnantDate = state.getPregnantDate();
    }
}
