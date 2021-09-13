package people;

import config.EnvConfig;
import market.FoodMarket;
import market.Market;
import market.Tradable;
import calendar.Calendar;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadLocalRandom;
import config.PeopleConfig;
import config.SocietyConfig;
import people.mating.Genetic;
import people.mating.MatingArea;
import work.JobOffers;
import work.Unemployed;
import work.Work;
import logger.LoggerException;
/*
- Written by Loslever Terry 2021-07-08 -> Ongoing
- Key class of the project. It the future versions it should implement
- a AI engine which will take rationnal decisions instead of random ones.
*/

public class People implements Callable<People>{
    private int id;
    private Calendar birthDate;
    private Calendar currentTime;

    private boolean sex;   
    private MatingArea sexAvailable;
    private People partner;

    private JobOffers jobOffers;
    private BankAccount account;
    private Work work;
    /*
    Oder of the markets in the list:
      - 0 food
      - 1 house
      - 2 land
     */
    private List<Market> markets;

    private PeopleState currentState;
    private PeoplePossessions possession;
    private PeopleCharacteristics peopleCharacteristics;

    public People(int id,Calendar currentTime,List<Market> markets,MatingArea sexAvailable,JobOffers jobOffers,PeopleCharacteristics peopleCharacteristics){
    	this.id = id;
        this.sexAvailable = sexAvailable;
    	this.sex = ThreadLocalRandom.current().nextBoolean();
        if(id<SocietyConfig.PEOPLE_NBR)
            this.account = new BankAccount(SocietyConfig.INIT_PEOPLE_MONEY,id);
        else
            this.account = new BankAccount(0,id);
		this.currentTime = currentTime;
        this.markets=markets;
    	this.birthDate = currentTime.copy();
        this.jobOffers = jobOffers;
    	this.currentState = new PeopleState();
        this.possession = new PeoplePossessions();
        this.work = new Work();
        this.peopleCharacteristics = peopleCharacteristics;

    }
    public People(int id){
        this.id = id;
    }
    /*
        Sets the society account into the agent account.
     */
    public void setStateAccount(BankAccount account){
        this.account.setStateAccount(account);
    }
    /*
        Returns the society account
     */
    public BankAccount getStateAccount(){
        return this.account.getStateAccount();
    }
    /*
        Clears everything before deleting the agent.
        It transfers all bought land to the state and delete the existing offers.
        It also gives back the current job of the agent.
     */
    public void suicide() throws LoggerException{
        int land = possession.getLandSurface();
        BankAccount societyAccount = account.getStateAccount();
        //Deletes its offer
        work.deleteOffer();
        //Gives back the land bought.
        while(land > 0){
            markets.get(2).addSupply(new Tradable(SocietyConfig.INIT_LAND_PRICE,SocietyConfig.LAND_PARCEL,societyAccount));
            land -= SocietyConfig.LAND_PARCEL;
        }
        jobOffers.takeOffer("Unemployed",work,account);
    }
    @Override
    public String toString(){

        String charact = "";
        if(EnvConfig.FULL_INFO)
            charact = peopleCharacteristics.toString();

    	if(sex)
    		return currentState.toString()+charact+"sexe : F\t"+account.toString()+work.toString()+possession.toString();

    	else
    		return currentState.toString()+charact+"sexe : M\t"+account.toString()+work.toString()+possession.toString();
    }
    /*
        Function called by the thread manager.
        Main function of the agent which decides what action it should take according
        to multiple elements.
     */
    @Override
    public People call() throws Exception{
        int maxIncome = 0;
        int tmpIncome = 0;
        String job="Unemployed";

        if(currentTime.getYear() - birthDate.getYear() >= PeopleConfig.PEOPLE_ADULT_AGE){
            //currentState.setAging((currentTime.getYear() - birthDate.getYear())/PeopleConfig.PEOPLE_ADULT_AGE);
        	currentState.updateState();
            //TODO ADD AGING WHICH IS ALREADY READY IN PEOPLE STATE
    	    //Looks for new jobs 4 times a year
            if(currentTime.getDay() == 1 && (currentTime.getMonth()%3)==1){
                //Updates its revenues.
                for(String jobs : SocietyConfig.AVAILABLE_JOBS_DIC.keySet()){
                    tmpIncome = jobOffers.getIncome(jobs);
                    if(tmpIncome > maxIncome && tmpIncome >work.getIncome()){
                        job = jobs;
                        maxIncome = tmpIncome;
                    }
                }

                if(maxIncome >= work.getIncome()){
                    work = jobOffers.takeOffer(job,work,account);
                    work.setPossession(possession);
                }
            }
            if(currentTime.getDay() == 1){
                work.setIncome(account.getAverageIncome());
                account.resetMoneyHistoric();
            }
    	//The element is dead
        	if(currentState.isDead())
        		return new People(-1);

        	else if (getNextAction()==1){
                People tmp = new People(0,currentTime,markets,sexAvailable,jobOffers,Genetic.geneticModification(this,partner));
                partner = null;
                return tmp;
            }
    	
        }

        return null;
    }
    public int getId(){
        return id;
    }
    public Work getWork(){
        return work;
    }
    public BankAccount getBankAccount(){
        return account;
    }
    public Calendar getTime(){
    	return currentTime;
    }
    public void setId(int id){
        this.id = id;
    }
    public PeopleCharacteristics getPeopleCharacteristics(){
        return peopleCharacteristics;
    }

    /*--------------------------------------------------------AI part--------------------------------------------------*/
    /*
    Each action has a number associated to it.
    0 - eat
    1 - sleep
    2 - work
    3 - sex
    4 - give birth
    5 - buy food
    */
    /*checks what should be the next action
	For now ,a worker as a 8h shift per day. Since we allow 3 actions on a day.
	Therefore he should have selected to work at least one time per day, unless he is unemployed.
	*/
    private int getNextAction(){
        int highestActionUtility=0;
        int highestActionIndex=0;
        int utility = 0;


        if(work.getLastWorkedTime()== null || (!currentTime.equals(work.getLastWorkedTime()) && !(work instanceof Unemployed)))
            return action(2,true);

        PeopleState startState[] = new PeopleState[3];
        BankAccount startAccount[] = new BankAccount[3];
        PeoplePossessions startPossession[] = new PeoplePossessions[3];

        startState[0] = currentState.copy();
        startAccount[0] = account.copy();
        startPossession[0] = possession.copy();

        for(int i=0;i<8;i++){
            //Checks its utility
            action(i,false);
            utility = getBestUtility(2,startState,startAccount,startPossession);

            if(EnvConfig.DEBUG)
                System.out.println(Integer.toString(utility) + " "+Integer.toString(i));

            if(utility > highestActionUtility){
                highestActionUtility = utility;
                highestActionIndex = i;
            }
            //Reset the state before the next prediction round.
            currentState.resume(startState[0]);
            account.resume(startAccount[0] );
            possession.resume(startPossession[0]);
        }
        return action(highestActionIndex,true);

    }
    private int getBestUtility(int searchDepth,PeopleState []startState,BankAccount []startAccount,PeoplePossessions []startPossession){
        int bestUtility = 0;
        int tmp = 0;
        if(searchDepth == 0){
            return utility();
        }
        else{
            startState[searchDepth] = currentState.copy();
            startAccount[searchDepth] = account.copy();
            startPossession[searchDepth] = possession.copy();

            for(int j=0;j<8;j++){
                action(j,false);
                tmp = getBestUtility(searchDepth-1,startState,startAccount,startPossession);

                if(EnvConfig.DEBUG){
                    for(int k =0 ; k < 4-searchDepth;k++)
                        System.out.print("	");
                    System.out.println(Integer.toString(tmp) + " "+Integer.toString(j));
                }

                if(tmp>bestUtility){
                    bestUtility = tmp;
                }
                currentState.resume(startState[searchDepth]);
                account.resume(startAccount[searchDepth]);
                possession.resume(startPossession[searchDepth]);
            }
            return bestUtility;
        }
    }
    /*
        Takes as argument the current job.
        Returns the value given to current state of the people.
     */
    public int utility(){
        int jobIncome = 0;
        if(work.yieldEstimate()>0)
            jobIncome = (int)work.yieldEstimate()*work.priceEstimate();

        if(currentState.isPregnant() && currentTime.timeElapsedMonth(currentState.getPregnantDate()) > PeopleConfig.PEOPLE_GESTATION_TIME){
            return currentState.getHealth()*peopleCharacteristics.getHealth()+currentState.getSleep()*peopleCharacteristics.getSleep()+
                    currentState.getFood()*peopleCharacteristics.getFood()-currentState.getLibido()*peopleCharacteristics.getLibido() -
                    currentTime.timeElapsedMonth(currentState.getPregnantDate()) * peopleCharacteristics.getPregnancy() +
                    jobIncome*peopleCharacteristics.getIncome()+
                    ((int)account.getBankAccount()/100+(int)(possession.getOwnership((FoodMarket) markets.get(0))/100))*peopleCharacteristics.getPossession();
        }
        else
            return currentState.getHealth()*peopleCharacteristics.getHealth()+currentState.getSleep()*peopleCharacteristics.getSleep()+
                    currentState.getFood()*peopleCharacteristics.getFood()-currentState.getLibido()*peopleCharacteristics.getLibido() +
                    jobIncome*peopleCharacteristics.getIncome()+
                    ((int)account.getBankAccount()/100+(int)(possession.getOwnership((FoodMarket) markets.get(0))/100))*peopleCharacteristics.getPossession();
    }

    /*
    Use onSupply to avoid setting again the supply state. Since it's synchronized, with more
    people we could face a complexity problem.
    */
    private void buyFood(boolean onSupply){
        List<Tradable> offers;
        int amountToPay=0;
        int additionalFood=0;

        if(currentState.getFood()<0)
            additionalFood = -currentState.getFood();
        if(onSupply)
            offers = markets.get(0).consumeSupply(possession.neededFood()+additionalFood,account.getBankAccount());
        else
            offers = markets.get(0).peekSupply(possession.neededFood()+additionalFood,account.getBankAccount());

        for(Tradable t:offers){
            possession.addFood(t.getVolume());
            amountToPay += t.getAskedPrice()*t.getVolume();
            if(onSupply && t.getReceiverAccount() != null)
                t.getReceiverAccount().addMoney(t.getAskedPrice()*t.getVolume());
        }

        //Avoids locking and unlocking uselessly
        if(amountToPay>0)
            account.withdrawMoney(amountToPay);
    }
    //Buys the land parcel by parcel
    private void buyLand(boolean onSupply){
        List<Tradable> offers;
        int amountToPay = 0;

        if(onSupply)
            offers = markets.get(2).consumeSupply(SocietyConfig.LAND_PARCEL,account.getBankAccount());
        else
            offers = markets.get(2).peekSupply(SocietyConfig.LAND_PARCEL,account.getBankAccount());

        for(Tradable t:offers){
            possession.addLandSurface(t.getVolume(),t.getAskedPrice());
            amountToPay += t.getAskedPrice()*t.getVolume();
            if(onSupply && t.getReceiverAccount() != null)
                t.getReceiverAccount().addMoney(t.getAskedPrice());
        }

        //Avoids locking and unlocking uselessly
        if(amountToPay>0)
            account.withdrawMoney(amountToPay);
    }
    private void buyHouse(boolean onSupply){
        if(account.withdrawMoney(SocietyConfig.HOUSE_PRICE) && markets.get(1).getSupply()>0){
            if(onSupply)
                markets.get(1).consumeSupply(1,account.getBankAccount());
            possession.addHouse(1);
        }
    }
    private void sex(){
        People partner;
        if(sex){
            if((partner = sexAvailable.getFromMating())!=null){
                this.partner = partner;
                currentState.hadSex(sex,currentTime);
            }
        }
        else{
            if(!sexAvailable.setInMating(this))
                currentState.hadSex(sex,currentTime);
        }

    }
    private void eat(){
        int remaining = PeopleConfig.PEOPLE_FOOD_ROOF - currentState.getFood();

        //Since eat returns the leftovers, add give them back to the possession
        possession.addFood(currentState.eat(possession.consumeFood(remaining)));

    }
    /*
		Modifies the people state according to the given action
	*/
    private int action(int actionIndex,boolean takeAction){

        switch(actionIndex){

            case 0:
                eat();
                break;

            case 1:
                currentState.sleep();
                break;

            case 2:

                if(takeAction){
                    work.setWorkingTime(currentTime);
                    work.work();
                }
                break;

            case 3:
                sex();
                break;

            case 4:
                if(currentState.giveBirth(currentTime))
                    return 1;

            case 5:
                buyFood(takeAction);
                break;
            case 6:
                buyHouse(takeAction);
                break;
            case 7:
                buyLand(takeAction);
                break;


            default:
                return -1;
        }

        return 0;

    }

}
