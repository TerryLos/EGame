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
    private int id;
    int actions;
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
    public boolean isAdult(){
        return birthDate.isDelayElapsed(currentTime,PeopleConfig.PEOPLE_ADULT_AGE,2);
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
        int landAvgPrice = 0;
        int money = account.getBankAccount();
        Tradable house;
        BankAccount societyAccount = account.getStateAccount();
        int childNbr = children.size();
        if(land > 0)
            landAvgPrice = possession.getLandPrice()/land;
        //Deletes its offer
        work.deleteOffer();
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
                child.getPeoplePossessions().addLandSurface(new Tradable(land / (float)childNbr, (land / childNbr)
                        * landAvgPrice,child.getBankAccount()));
            }
        }
        jobOffers.takeOffer("Nowork",work,null,null);
    }
    @Override
    public String toString(){

        String charact = "";
        if(EnvConfig.FULL_INFO){
            charact = peopleCharacteristics.toString();
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
            currentState.setAging((currentTime.getYear() - birthDate.getYear())/PeopleConfig.PEOPLE_ADULT_AGE);
        	currentState.updateState();
    	    //Computes the income each month
            work.setIncome(account.getReceivedMoney(work.jobString()));
            if(currentTime.getDay() == 1 && actions == 0){
                jobOffers.setIncome(getBankAccount(),currentTime);
                account.resetMoneyHistoric();
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
    public synchronized void addChild(People child){ children.add(child);}
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
        this.account.setOwner(id);
        this.id = id;
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

        PeopleState[] startState = new PeopleState[3];
        BankAccount[] startAccount = new BankAccount[3];
        PeoplePossessions[] startPossession = new PeoplePossessions[3];

        startState[0] = currentState.copy();
        startAccount[0] = account.copy();
        startPossession[0] = possession.copy();

        for(int i=0;i<9;i++){
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

            for(int j=0;j<9;j++){
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
            jobIncome = (int)(work.yieldEstimate()*work.priceEstimate(peopleCharacteristics.getGreedyness())*work.getWorkStatus());

        if(currentState.isPregnant() && currentTime.timeElapsedMonth(currentState.getPregnantDate()) > PeopleConfig.PEOPLE_GESTATION_TIME){
            return currentState.getHealth()*peopleCharacteristics.getHealth()+currentState.getSleep()*peopleCharacteristics.getSleep()+
                    currentState.getFood()*peopleCharacteristics.getFood()-currentState.getLibido()*peopleCharacteristics.getLibido() -
                    currentTime.timeElapsedMonth(currentState.getPregnantDate()) * peopleCharacteristics.getPregnancy() +
                    jobIncome*peopleCharacteristics.getIncome()
                    +(possession.getOwnership((FoodMarket) markets.get(0))/100*peopleCharacteristics.getPossession());
        }
        else
            return currentState.getHealth()*peopleCharacteristics.getHealth()+currentState.getSleep()*peopleCharacteristics.getSleep()+
                    currentState.getFood()*peopleCharacteristics.getFood()-currentState.getLibido()*peopleCharacteristics.getLibido() +
                    jobIncome*peopleCharacteristics.getIncome()
                    +(possession.getOwnership((FoodMarket) markets.get(0))/100*peopleCharacteristics.getPossession());
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
            amountToPay = (int) t.getAskedPrice();
            if(t.getVolume() != 0){
                possession.addFood(t.getVolume());
                amountToPay = (int) (t.getAskedPrice()*t.getVolume());
            }

            if(onSupply && t.getReceiverAccount() != null)
                t.getReceiverAccount().addMoney(amountToPay,"Farmer");
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
            amountToPay = (int) t.getAskedPrice();
            if(t.getVolume() != 0){
                possession.addLandSurface(t);
                amountToPay = (int) (t.getAskedPrice()*t.getVolume());
            }

            if(onSupply && t.getReceiverAccount() != null)
                t.getReceiverAccount().addMoney(amountToPay,"Land");
        }

        //Avoids locking and unlocking uselessly
        if(amountToPay>0)
            account.withdrawMoney(amountToPay);
    }
    private void buyWood(boolean onSupply){
        List<Tradable> offers;
        int amountToPay = 0;

        if(onSupply)
            offers = markets.get(3).consumeSupply(work.getNeededWood(),account.getBankAccount());
        else
            offers = markets.get(3).peekSupply(work.getNeededWood(),account.getBankAccount());

        for(Tradable t:offers){
            amountToPay = (int) t.getAskedPrice();
            if(t.getVolume() != 0){
                possession.addWood(t);
                amountToPay = (int) (t.getAskedPrice()*t.getVolume());
            }

            if(onSupply && t.getReceiverAccount() != null)
                t.getReceiverAccount().addMoney(amountToPay,"Woodcutter");
        }

        //Avoids locking and unlocking uselessly
        if(amountToPay>0)
            account.withdrawMoney(amountToPay);
    }
    private void buyHouse(boolean onSupply){
        Tradable offer = markets.get(1).getBestOffer();
        if(offer.getReceiverAccount() != null && account.withdrawMoney(offer.getAskedPrice())){
            if(onSupply){
                //The house has been bought in between the peek and the poll
                if(!markets.get(1).takeBestOffer(offer))
                    return;
                offer.getReceiverAccount().addMoney(offer.getAskedPrice(),"Builder");
                possession.addHouse(offer);
            }
        }
    }
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
                    work.work(peopleCharacteristics.getGreedyness());
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
            case 8:
                buyWood(takeAction);
                break;


            default:
                return -1;
        }

        return 0;

    }
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
        //Can't swich too othen
        //Or looks for job when he gets broke or doesn't receive enough income
        //Lets the first month pass in order to have the income computed
        else if(((PeopleConfig.PEOPLE_ONACCOUNT && account.getBankAccount() <= peopleCharacteristics.getJobChangeRate())
            || (!PeopleConfig.PEOPLE_ONACCOUNT && lastSalary <= peopleCharacteristics.getJobChangeRate()))
            && work.getLastWorkSwitch().isDelayElapsed(currentTime,PeopleConfig.PEOPLE_WORK_SWITCH,0) &&
            currentTime.getDaysSinceStart()>31){
            for (String jobs : SocietyConfig.AVAILABLE_JOBS_DIC.keySet()) {
                //No need to check the current job
                if(jobs.equals("Nowork") || (work.jobString().equals(jobs)))
                    continue;

                workIncome = jobOffers.getIncome(jobs);
                marketIncome = jobOffers.getIncomeByMarket(jobs,possession);
                if( workIncome > marketIncome)
                    jobRank.add(new Job(workIncome,jobs));
                else
                    jobRank.add(new Job(marketIncome,jobs));
            }
            lookup = jobRank.poll();
            while(jobRank.size()!= 0 && lookup.getSalary() > lastSalary){
                if(jobOffers.isAvailable(lookup.getName())){
                    work = jobOffers.takeOffer(lookup.getName(),work,account,possession);
                    work.setLastWorkSwitch(currentTime);
                    if(!work.jobString().equals("Nowork"))
                        break;
                }
                lookup = jobRank.poll();
            }
        }
    }

}
