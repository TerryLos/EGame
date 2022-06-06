package people;

import config.EnvConfig;
import market.FoodMarket;
import market.LandMarket;
import market.Market;
import market.Tradable;
import calendar.Calendar;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadLocalRandom;
import config.PeopleConfig;
import config.SocietyConfig;
import people.mating.Genetic;
import people.mating.MatingArea;
import work.Job;
import work.JobOffers;
import work.Unemployed;
import work.Work;
import logger.LoggerException;
/*
- Written by Loslever Terry 2021-07-08 -> Ongoing
- Key class of the project. In the future versions it should implement
- an AI engine which will take rational decisions instead of random ones.
*/

public class People implements Callable<People>{
    int actions;
    private int id;
    private Calendar birthDate;
    private Calendar currentTime;

    private boolean sex;   
    private MatingArea sexAvailable;
    private People partner;
    private List<People> children;

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
        this.children = new LinkedList<People>();
        this.peopleCharacteristics = peopleCharacteristics;
        if(!sex)
            this.peopleCharacteristics.setMale();

    }
    public People(int id){
        this.id = id;
    }

    /*
        Does : returns true if People has lived more than PEOPLE_ADULT_AGE
     */
    public boolean isAdult(){
        return birthDate.isDelayElapsed(currentTime,PeopleConfig.PEOPLE_ADULT_AGE,2);
    }

    /*
        Does : returns the society account
     */
    public BankAccount getStateAccount(){
        return this.account.getStateAccount();
    }

    /*
        Does : sets the society account into the agent account.
     */
    public void setStateAccount(BankAccount account){
        this.account.setStateAccount(account);
    }

    /*
        Does : clears everything before deleting the agent.
            It transfers all bought land to the state and delete the existing offers.
            It also gives back the current job of the agent.
     */
    public void suicide() throws LoggerException{
        int land = possession.getLandSurface();
        int landAvgPrice = 0;
        int money = account.getBankAccount();
        Tradable house;
        BankAccount societyAccount = account.getStateAccount();
        int childNbr = children.size();
        if(land > 0)
            landAvgPrice = possession.getLandPrice()/land;
        //Deletes its offer
        jobOffers.deleteOffer(account);
        //Gives back the land bought if no children.
        if(childNbr == 0){
            while((house = possession.getHouse()) != null){
                house.setReceiverAccount(societyAccount);
                markets.get(1).addSupply(house);
            }
            while(land > 0){
                markets.get(2).addSupply(new Tradable( ((LandMarket)markets.get(2)).surfacePricing(SocietyConfig.INIT_LAND_PRICE, possession.getLandSurface(), land),SocietyConfig.LAND_PARCEL,societyAccount));
                land -= SocietyConfig.LAND_PARCEL;
            }
        }
        else{
            //Get rid of the houses before so that we have the true bought land
            for (People child : children){
                if((house = possession.getHouse()) != null){
                    house.setReceiverAccount(child.getBankAccount());
                    child.getPeoplePossessions().addHouse(house);
                }
            }
            for (People child : children){
                child.getBankAccount().addMoney(money/(float)childNbr,"Death");
                child.getPeoplePossessions().addLandSurface(new Tradable((land / (float)childNbr)
                        * landAvgPrice,land / childNbr,child.getBankAccount()));
            }
        }
        jobOffers.takeOffer("Nowork",work,null,null);
    }

    /*
        Does : returns the People in string format
     */
    @Override
    public String toString(){

        String charact = "";
        if(EnvConfig.FULL_INFO){
            charact = "ID : "+id+"\t"+peopleCharacteristics.toString();
            charact += "Children nbr : "+children.size()+" ";
        }
    	if(sex)
    		return currentState.toString()+charact+"sex : F\t"+account.toString()+work.toString()+possession.toString();

    	else
    		return currentState.toString()+charact+"sex : M\t"+account.toString()+work.toString()+possession.toString();
    }

    /*
        Function called by the thread manager.
        Main function of the agent which decides what action it should take according
        to multiple elements.
     */
    @Override
    public People call() throws Exception{
        actions += 1;
        actions %= EnvConfig.ACTION_BY_DAY;

        if(isAdult()){
            currentState.setAging(1+(currentTime.getYear() - birthDate.getYear())/PeopleConfig.PEOPLE_ADULT_AGE);
        	currentState.updateState();
    	    //Computes the income each month
            work.setIncome(account.getReceivedMoney(work.jobString()));
            if(currentTime.getDay() == 1 && actions == 0){
                jobOffers.setIncome(getBankAccount(),currentTime);
                account.resetMoneyHistoric();
                possession.resetOwnership();
            }
            jobSeeking();
        	if (getNextAction()==1){
                People tmp = new People(0,currentTime,markets,sexAvailable,jobOffers,Genetic.geneticModification(this,partner));
                this.addChild(tmp);
                partner.addChild(tmp);
                partner = null;
                return tmp;
            }
            //The element is dead
            else if(currentState.isDead())
                return new People(-1);
        }
        return null;
    }

    /*
        Input : child (People)
        Does : addes the 'child' in the child list
     */
    public synchronized void addChild(People child){ children.add(child);}

    //Getter & setter methods
    public int getId(){
        return id;
    }

    public void setId(int id){
        this.account.setOwner(id);
        this.id = id;
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

    public PeopleCharacteristics getPeopleCharacteristics(){
        return peopleCharacteristics;
    }

    public PeoplePossessions getPeoplePossessions(){
        return possession;
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
    6 - buy House
    7 - buy Land
    8 - buy Wood
    9 - buy stone

        Does: returns the index of the next action the agent should do
	*/
    public int getNextAction(){
        int highestActionUtility=0;
        int highestActionIndex=0;
        int utility = 0;

        if(work.getLastWorkedTime()== null || (!currentTime.equals(work.getLastWorkedTime()) && !(work instanceof Unemployed)))
            return action(2,true);

        PeopleState startState = currentState.copy();
        BankAccount startAccount = account.copy();
        PeoplePossessions startPossession = possession.copy();

        for(int i=0;i<10;i++){

            //Males can't give birth
            if( i == 4 && !sex)
                continue;
            //Checks its utility
            action(i,false);
            utility = getBestUtility(EnvConfig.AI_DEPTH_SEARCH);

            if(EnvConfig.DEBUG)
                System.out.println(Integer.toString(utility) + " "+Integer.toString(i));

            if(utility > highestActionUtility){
                highestActionUtility = utility;
                highestActionIndex = i;
            }
            //Reset the state before the next prediction round.
            currentState.resume(startState);
            account.resume(startAccount);
            possession.resume(startPossession);
        }
        return action(highestActionIndex,true);
    }
    /*
        Input : searchDepth (int)
        Does : returns the highest utility value found at the state "searchDepth"
     */
    private int getBestUtility(int searchDepth){
        int bestUtility = 0;
        int tmp = 0;
        PeopleState startState = currentState.copy();
        BankAccount startAccount = account.copy();
        PeoplePossessions startPossession = possession.copy();
        if(searchDepth == 0){
            return utility();
        }
        else{

            for(int j=0;j<10;j++){
                //Males can't give birth
                if( j == 4 && !sex)
                    continue;
                currentState.updateState();
                action(j,false);
                tmp = getBestUtility(searchDepth-1);

                if(EnvConfig.DEBUG){
                    for(int k =0 ; k < 4-searchDepth;k++)
                        System.out.print("	");
                    System.out.println(Integer.toString(tmp) + " "+Integer.toString(j));
                }
                if(tmp>bestUtility){
                    bestUtility = tmp;
                }
                currentState.resume(startState);
                account.resume(startAccount);
                possession.resume(startPossession);
            }
            return bestUtility;
        }
    }
    /*
        Does : returns the value given to current state of the agent
     */
    public int utility(){
        int jobIncome = 0;
        int computedUtility = 0;

        //Life dependant
        computedUtility = currentState.getHealth()*peopleCharacteristics.getHealth()+currentState.getSleep()*peopleCharacteristics.getSleep()+
                currentState.getFood()*peopleCharacteristics.getFood()-currentState.getLibido()*peopleCharacteristics.getLibido();

        if(work.yieldEstimate()>0)
            jobIncome = (int)(work.yieldEstimate()*work.priceEstimate(peopleCharacteristics.getGreediness())*work.getWorkStatus());


        if(currentState.isPregnant() && currentTime.timeElapsedMonth(currentState.getPregnantDate()) > PeopleConfig.PEOPLE_GESTATION_TIME)
              computedUtility -= currentTime.timeElapsedMonth(currentState.getPregnantDate()) * peopleCharacteristics.getPregnancy();

        if(account.getBankAccount() > peopleCharacteristics.getStinginess())
            computedUtility += (jobIncome*peopleCharacteristics.getIncome()/100+(possession.getOwnershipBalance()/100)*peopleCharacteristics.getPossession());

        return computedUtility;
    }
    /*
        Input : onSupply (boolean)
        Does : buys food from the FoodMarket and sets the amount bought in PeoplePossession.
        If onSupply == true, then it consumes from the market, otherwise it peeks.
    */
    private void buyFood(boolean onSupply){
        List<Tradable> offers;
        float amountToPay=0;
        float val = 0;
        int additionalFood=0;

        if(currentState.getFood()<0)
            additionalFood = -currentState.getFood();
        if(onSupply)
            offers = markets.get(0).consumeSupply(possession.neededFood() + additionalFood, account.getBankAccount());
        else
            offers = markets.get(0).peekSupply(possession.neededFood()+additionalFood,account.getBankAccount());

        for(Tradable t:offers){
            //products
            if(t.getVolume() != 0){
                possession.addFood(t);
                val = (t.getAskedPrice()*t.getVolume());
            }
            //VAT
            else
                val = t.getAskedPrice();

            if(onSupply && t.getReceiverAccount() != null){
                amountToPay += val;
                t.getReceiverAccount().addMoney(val,"Farmer");
            }
        }
        //Avoids locking and unlocking uselessly
        if(amountToPay>0)
            account.withdrawMoney(amountToPay);

    }
    /*
        Input : onSupply (boolean)
        Does : buys land from the landMarket and sets the amount bought in PeoplePossession.
        If onSupply == true, then it consumes from the market, otherwise it peeks.
    */
    private void buyLand(boolean onSupply){
        List<Tradable> offers;
        float amountToPay = 0;
        float value = 0;

        if(onSupply)
            offers = markets.get(2).consumeSupply(work.getNeededLand(),account.getBankAccount());
        else
            offers = markets.get(2).peekSupply(work.getNeededLand(),account.getBankAccount());

        for(Tradable t:offers){
            if(t.getVolume() != 0){
                possession.addLandSurface(t);
                amountToPay += (int) (t.getAskedPrice()*t.getVolume());
            }
            //VAT
            else
                amountToPay += t.getAskedPrice();

            if(onSupply && t.getReceiverAccount() != null){
                amountToPay += value;
                t.getReceiverAccount().addMoney(value,"Land");
            }
        }

        //Avoids locking and unlocking uselessly
        if(amountToPay>0)
            account.withdrawMoney(amountToPay);
    }
    /*
        Input : onSupply (boolean)
        Does : buys stone from the StoneMarket and sets the amount bought in PeoplePossession.
        If onSupply == true, then it consumes from the market, otherwise it peeks.
    */
    private void buyStone(boolean onSupply){
        List<Tradable> offers;
        float amountToPay = 0;
        float value = 0;

        if(onSupply)
            offers = markets.get(4).consumeSupply(work.getNeededStone(), account.getBankAccount());
        else
            offers = markets.get(4).peekSupply(work.getNeededStone(),account.getBankAccount());

        for(Tradable t:offers){
            if(t.getVolume() != 0){
                possession.addStone(t);
                value = (int) (t.getAskedPrice()*t.getVolume());
            }
            //VAT
            else
                value = t.getAskedPrice();

            if(onSupply && t.getReceiverAccount() != null){
                amountToPay += value;
                t.getReceiverAccount().addMoney(amountToPay,"Miner");
            }
        }

        //Avoids locking and unlocking uselessly
        if(amountToPay>0)
            account.withdrawMoney(amountToPay);
    }
    /*
        Input : onSupply (boolean)
        Does : buys wood from the WoodMarket and sets the amount bought in PeoplePossession.
        If onSupply == true, then it consumes from the market, otherwise it peeks.
    */
    private void buyWood(boolean onSupply){
        List<Tradable> offers;
        float amountToPay = 0;
        float value = 0;

        if(onSupply)
            offers = markets.get(3).consumeSupply(work.getNeededWood(), account.getBankAccount());
        else
            offers = markets.get(3).peekSupply(work.getNeededWood(),account.getBankAccount());

        for(Tradable t:offers){
            if(t.getVolume() != 0){
                possession.addWood(t);
                value = (int) (t.getAskedPrice()*t.getVolume());
            }
            //VAT
            else
                value = t.getAskedPrice();

            if(onSupply && t.getReceiverAccount() != null){
                amountToPay += value;
                t.getReceiverAccount().addMoney(amountToPay,"Woodcutter");
            }
        }

        //Avoids locking and unlocking uselessly
        if(amountToPay>0)
            account.withdrawMoney(amountToPay);
    }
    /*
        Input : onSupply (boolean)
        Does : buys house from the HouseMarket and sets the amount bought in PeoplePossession.
        If onSupply == true, then it consumes from the market, otherwise it peeks.
    */
    private void buyHouse(boolean onSupply){
        Tradable offer = markets.get(1).getBestOffer();
        float VAT = (offer.getAskedPrice()*SocietyConfig.VAT)/100;
        if(offer.getReceiverAccount() != null && account.withdrawMoney(offer.getAskedPrice()+VAT)){
            if(onSupply){
                //The house has been bought in between the peek and the poll
                if(!markets.get(1).takeBestOffer(offer))
                    return;
                offer.getReceiverAccount().addMoney(offer.getAskedPrice(),"Builder");
                account.getStateAccount().addMoney(VAT,"Builder");
                possession.addHouse(offer);
            }
        }
        else if(offer.getReceiverAccount() != null && account.withdrawMoney(offer.getAskedPrice()+VAT))
            possession.addHouse(offer);
    }
    /*
        Does : sets himself on the mating list if male,
        gets a make out of the mating list if female.
    */
    private void sex(){
        People partner;
        if(sex){
            if((partner = sexAvailable.getFromMating())!=null){
                this.partner = partner;
                currentState.hadSex(sex && children.size()<SocietyConfig.CHILD_ROOF,currentTime);
            }
        }
        else{
            if(!sexAvailable.setInMating(this))
                currentState.hadSex(sex,currentTime);
        }

    }
    /*
        Does : consumes food from PeoplePossession and increases the hungriness from PeopleState
    */
    private void eat(){
        int remaining = PeopleConfig.PEOPLE_FOOD_ROOF - currentState.getFood();
        int totalFood = 0;
        List<Tradable> toEat = possession.consumeFood(remaining);
        //Since eat returns the leftovers, add give them back to the possession
        for(Tradable el:toEat)
            totalFood += el.getVolume();
        currentState.eat(totalFood);

    }
    /*
       Input : actionIndex (int), takeAction (boolean)
       Does : returns 0 if the action related to "actionIndex" has been taken,
       -1 otherwise.
       "TakeAction" indicates if the items should be removed or peeked from
       the markets.
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
                    work.work(peopleCharacteristics.getGreediness());
                }
                break;

            case 3:
                sex();
                break;
            case 4:
                if(currentState.giveBirth(currentTime))
                    return 1;
                break;
            case 5:
                buyFood(takeAction);
                break;
            case 6:
                buyHouse(takeAction);
                break;
            case 7:
                buyLand(takeAction);
                break;
            case 8:
                buyWood(takeAction);
                break;
            case 9:
                buyStone(takeAction);
                break;

            default:
                return -1;
        }

        return 0;

    }
    /*
       Does : Looks for a new job if the conditions are met
   */
    public void jobSeeking() throws LoggerException{
        int workIncome;
        int marketIncome;
        int lastSalary = work.getIncome();
        PriorityQueue<Job> jobRank = new PriorityQueue <>();
        Job lookup;
        //Gives randomly jobs at startup
        if(currentTime.getDaysSinceStart() == 1 || work.getLastWorkSwitch() == null){
            int dictSize = SocietyConfig.AVAILABLE_JOBS_DIC.size();
            int limit = 0;
            List<String> names = new ArrayList<>(SocietyConfig.AVAILABLE_JOBS_DIC.keySet());
            String jobs;
            jobs = names.get(ThreadLocalRandom.current().nextInt(0,dictSize-1));
            while((work = jobOffers.takeOffer(jobs, work, account, possession)).jobString().equals("Unemployed") && limit <10){
                jobs = names.get(ThreadLocalRandom.current().nextInt(0,dictSize-1));
                limit += 1;
            }
            work.setLastWorkSwitch(currentTime);
        }
        //Can't switch too often
        //Or looks for job when he gets broke or doesn't receive enough income
        else if(workCondition(lastSalary)){

            //If they were in a job and not profitable => Unemployed to get back money
            if(!work.jobString().equals("Unemployed")){
                work = jobOffers.takeOffer("Unemployed",work,account,possession);
                work.setLastWorkSwitch(currentTime);
            }
            else{
                for (String jobs : SocietyConfig.AVAILABLE_JOBS_DIC.keySet()) {
                    //No need to check the current job
                    if(jobs.equals("Nowork") || (work.jobString().equals(jobs)))
                        continue;

                    workIncome = jobOffers.getIncome(jobs);
                    marketIncome = jobOffers.getIncomeByMarket(jobs,account,possession,peopleCharacteristics.getGreediness());
                    if( workIncome > marketIncome)
                        jobRank.add(new Job(workIncome,jobs));
                    else
                        jobRank.add(new Job(marketIncome,jobs));
                }
                lookup = jobRank.poll();
                while(jobRank.size()!= 0 && lookup.getSalary() > lastSalary){
                    work = jobOffers.takeOffer(lookup.getName(),work,account,possession);
                    if(!work.jobString().equals("Unemployed"))
                        break;

                    lookup = jobRank.poll();
                }
            }
        }
    }
    /*
       Input : lastSalary (int)
       Does : returns true if the conditions are met to allow an agent to change job, false otherwise
   */
    private boolean workCondition(int lastSalary){
        boolean onAccount = (PeopleConfig.PEOPLE_ONACCOUNT && account.getBankAccount() <= account.getSpentMoney());
        boolean onSalary = (!PeopleConfig.PEOPLE_ONACCOUNT && lastSalary <= account.getSpentMoney());
        boolean isUnemployed = ((work instanceof Unemployed) && work.getLastWorkSwitch().isDelayElapsed(currentTime,jobOffers.getWorkTime(work.jobString()),0));
        boolean brokeWorker = work.getLastWorkSwitch().isDelayElapsed(currentTime,jobOffers.getWorkTime(work.jobString())+PeopleConfig.PEOPLE_WORK_SWITCH,0);

        return ((onAccount || onSalary) && (isUnemployed || brokeWorker));
    }
    public boolean getSex(){ return sex;}
}
