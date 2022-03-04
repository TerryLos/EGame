package people;

import java.util.concurrent.ThreadLocalRandom;
import config.PeopleConfig;

import calendar.Calendar;

/*
PeopleState encapsulates all the variables that take care of a person state 
at a certain time.

It contains its hungryness,sleepyness,sickness and others elements.

*/
public class PeopleState{
    private int aging;
    private int health;
    private int hungryness;
    private int sleepyness;
    private int libido;
    private ThreadLocalRandom threadLocalRandom;
    //When creating a people it's full

	private boolean pregnant;
	private Calendar pregnantDate;

    public PeopleState(){
    	this.threadLocalRandom = ThreadLocalRandom.current();
    	this.hungryness = PeopleConfig.PEOPLE_FOOD_ROOF;
    	this.sleepyness =  PeopleConfig.PEOPLE_SLEEP_ROOF;
    	this.libido = 0;
    	this.health = PeopleConfig.PEOPLE_HEALTH_ROOF;
    	this.pregnant = false;
    }

    //Copy constructor
    public PeopleState(ThreadLocalRandom threadLocalRandom,int hungryness,int sleepyness,int libido,
        int health,boolean pregnant,Calendar pregnantDate){

        this.threadLocalRandom = threadLocalRandom;
        this.hungryness = hungryness;
        this.sleepyness = sleepyness;
        this.libido = libido;
        this.health = health;
        this.pregnant = pregnant;
        this.pregnantDate = pregnantDate;
    }
    //Updates the state and returns the probability vector of the states
    public void updateState(){

        //The more it's hungry/sleepy the more it takes damages from it
        if(hungryness>= -PeopleConfig.PEOPLE_FOOD_ROOF)
    	   hungryness = hungryness - threadLocalRandom.nextInt(PeopleConfig.PEOPLE_EAT_RATE+1);
        if(sleepyness>= -PeopleConfig.PEOPLE_SLEEP_ROOF)
    	   sleepyness = sleepyness - threadLocalRandom.nextInt(PeopleConfig.PEOPLE_SLEEP_RATE+1);
    	if(libido < PeopleConfig.PEOPLE_LIBIDO_ROOF && pregnant != true)
    		libido = libido + threadLocalRandom.nextInt(PeopleConfig.PEOPLE_LIBIDO_RATE+1);

    	if(sleepyness <=0)
    		health = health - threadLocalRandom.nextInt(((int)-sleepyness/10)*PeopleConfig.PEOPLE_DYING_RATE+1);
        if(hungryness <=0)
            health = health - threadLocalRandom.nextInt(((int)-hungryness/10)*PeopleConfig.PEOPLE_DYING_RATE+1);

        if(aging != 0){
            int tmp = threadLocalRandom.nextInt(aging*(PeopleConfig.PEOPLE_DYING_RATE+1));
            health = health - tmp;
        }


    }
    public int eat(int value){
    	int leftOver = 0;
        
        if(value > PeopleConfig.PEOPLE_FOOD_ROOF+1 - hungryness){
            hungryness = PeopleConfig.PEOPLE_FOOD_ROOF;
            leftOver = value + hungryness - PeopleConfig.PEOPLE_FOOD_ROOF;
        }
        else 
            hungryness = hungryness + value;
        
        if(health != PeopleConfig.PEOPLE_HEALTH_ROOF)
            restoreHealth();

        return leftOver;
    }
    public void sleep(){
        
        if(sleepyness + PeopleConfig.PEOPLE_SLEEP_RECOVERY > PeopleConfig.PEOPLE_SLEEP_ROOF)
            sleepyness = sleepyness + (PeopleConfig.PEOPLE_SLEEP_ROOF - sleepyness);

        else
    	   sleepyness = sleepyness + PeopleConfig.PEOPLE_SLEEP_RECOVERY;
    	
    	if(health != PeopleConfig.PEOPLE_HEALTH_ROOF)
    		restoreHealth();
    		
    }
    private void restoreHealth(){
    	int nbr1 = threadLocalRandom.nextInt(2);
    	int nbr2 = threadLocalRandom.nextInt(2);
    	
    	if(nbr1 == nbr2)
    		health = health + threadLocalRandom.nextInt(PeopleConfig.PEOPLE_HEALTH_ROOF+1-health);
    		
    }
    public int getHealth(){
    	return health;
    }
    public int getSleep(){
        return sleepyness;
    }
    public int getFood(){
        return hungryness;
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
			return "State :\n"+"Health : "+Integer.toString(health)+"\t Hungryness : " +Integer.toString(hungryness)+
			"\n sleepyness : "+Integer.toString(sleepyness)+"\t libido : "+Integer.toString(libido)+"\n"+ "Pregnant : "+pregnant+"\t"+ " At "+pregnantDate.toString()+
            "\n Aging : "+Integer.toString(aging)+"\n";
			
    	else
			return "State :\n"+"Health : "+Integer.toString(health)+"\t Hungryness : " +Integer.toString(hungryness)+
					"\n sleepyness : "+Integer.toString(sleepyness)+"\t libido : "+Integer.toString(libido)+"\n"+ "Pregnant : "+pregnant+
                    "\n Aging : "+Integer.toString(aging)+"\n";
					
	}
    public void hadSex(boolean sex,Calendar currentTime){
        int nbr = threadLocalRandom.nextInt(PeopleConfig.PEOPLE_FERTILITY);
    	libido = 0;

    	if(sex && !pregnant && nbr != 1){
			pregnant = true;
			pregnantDate = currentTime.copy();
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
        return new PeopleState(threadLocalRandom,hungryness,
            sleepyness,libido,health,pregnant,pregnantDate);
    }
    public void resume(PeopleState state){
        this.hungryness = state.getFood();
        this.sleepyness = state.getSleep();
        this.libido = state.getLibido();
        this.health = state.getHealth();
        this.pregnant = state.isPregnant();
        this.pregnantDate = state.getPregnantDate();
    }
}
