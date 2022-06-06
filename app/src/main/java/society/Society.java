package society;

import config.EnvConfig;
import config.PeopleConfig;
import config.SocietyConfig;
import calendar.Calendar;
import market.*;
import people.BankAccount;
import people.PeopleThread;
import people.People;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import statistics.DatabaseWriter;
import work.JobOffers;
import logger.Logger;
import logger.LoggerException;

/*
- Written by Loslever Terry 2021-07-10 -> Ongoing
- Society encapsulates its residents and its resources.
- It also possesses a name and is isolated from other societies.
*/
public class Society{

    private int id;
    private List<Market> markets;
    private BankAccount societyAccount;
    private PeopleThread peopleThread;
    private JobOffers jobOffers;
    private int peopleNbr;
    private Calendar time;
    private Calendar taxTime;
    private Calendar unemployedTime;

    public Society(Calendar time,int id){
        this.id = id;
        this.time = time;
        //Inits the list of jobs
        SocietyConfig.AVAILABLE_JOBS_DIC = new HashMap<>();
        SocietyConfig.AVAILABLE_JOBS_DIC.put("Nowork",-1);
        SocietyConfig.AVAILABLE_JOBS_DIC.put("Unemployed",0);
        SocietyConfig.AVAILABLE_JOBS_DIC.put("Farmer",1);
        SocietyConfig.AVAILABLE_JOBS_DIC.put("Builder",2);
        SocietyConfig.AVAILABLE_JOBS_DIC.put("Woodcutter",3);
        SocietyConfig.AVAILABLE_JOBS_DIC.put("Miner",4);
        //Sets the date to the adult time for the first ppl of the society
        this.societyAccount = new BankAccount(SocietyConfig.INIT_STATE_MONEY,-1);
        this.markets = new ArrayList<Market>();
        this.markets.add(new FoodMarket(societyAccount,SocietyConfig.INIT_FOOD_PRICE,SocietyConfig.INIT_FOOD_SUPPLY));
        this.markets.add(new HouseMarket(societyAccount,SocietyConfig.INIT_HOUSE_PRICE,SocietyConfig.INIT_HOUSE_SUPPLY));
        this.markets.add(new LandMarket(societyAccount,SocietyConfig.INIT_LAND_PRICE,SocietyConfig.INIT_LAND_SURFACE));
        this.markets.add(new WoodMarket(societyAccount,SocietyConfig.INIT_WOOD_PRICE,SocietyConfig.INIT_WOOD_SUPPLY));
        this.markets.add(new StoneMarket(societyAccount,SocietyConfig.INIT_STONE_PRICE,SocietyConfig.INIT_STONE_SUPPLY));

        this.jobOffers = new JobOffers((FoodMarket)markets.get(0),(HouseMarket)markets.get(1),(WoodMarket)markets.get(3),
                (StoneMarket)markets.get(4),time);
        this.peopleThread = new PeopleThread(time,markets,jobOffers,societyAccount);
        this.peopleNbr = SocietyConfig.PEOPLE_NBR;
        this.time.incYears(PeopleConfig.PEOPLE_ADULT_AGE);
        this.taxTime = time.copy();
        this.unemployedTime = time.copy();;
    }
    
    public int runSociety(DatabaseWriter writer,boolean write) throws LoggerException{

        int currentPopulation = peopleThread.getPeopleNbr();
        int postPopulation = 0;
        //Saves the init state of the simulation
        if(time.getDaysSinceStart() == 1)
            writer.write(true,id,0,peopleThread,(FoodMarket) markets.get(0),(LandMarket) markets.get(2),(HouseMarket) markets.get(1),
                    (WoodMarket) markets.get(3),(StoneMarket) markets.get(4),jobOffers,peopleThread,societyAccount.getBankAccount());
        /*
            The Society pays the unemployed agents at the start of each month.
         */
        if(time.getDay()==1 && time.getMonth()!=unemployedTime.getMonth()){
            unemployedTime = time.copy();
            List<People> unemployed = peopleThread.getUnemployed();
            if(SocietyConfig.ALLOW_NEGATIVE_STATE_MONEY)
                for(People u:unemployed){
                    societyAccount.withdrawMoneyDebt(SocietyConfig.BASE_INCOME_UNEMPLOYED);
                    u.getBankAccount().addMoney(SocietyConfig.BASE_INCOME_UNEMPLOYED,"Unemployed");
                }
            else{
                float toPay = societyAccount.getBankAccount()/(float)unemployed.size();
                if(toPay>SocietyConfig.BASE_INCOME_UNEMPLOYED)
                    toPay = SocietyConfig.BASE_INCOME_UNEMPLOYED;
                for(People u:unemployed){
                    societyAccount.withdrawMoney(toPay);
                    u.getBankAccount().addMoney(toPay,"Unemployed");
                }
            }

        }
        //Gathers tax each year
        if(time.getYear()-taxTime.getYear()==1){
            taxTime = time.copy();
            List<People> people = peopleThread.getPeopleList();
            for(People p: people){
                societyAccount.addMoney(SocietyConfig.YEAR_TAX,"Tax");
                p.getBankAccount().withdrawMoneyDebt(SocietyConfig.YEAR_TAX);
            }
        }
        int result = peopleThread.execIteration();

        if(write || result==1 )
            writer.write(result==1,id,time.getDaysSinceStart(),peopleThread,(FoodMarket) markets.get(0),(LandMarket) markets.get(2),(HouseMarket) markets.get(1),
                    (WoodMarket) markets.get(3),(StoneMarket) markets.get(4),jobOffers,peopleThread,societyAccount.getBankAccount());
        if(result == 0){
            postPopulation = peopleThread.getPeopleNbr();
            peopleNbr += (postPopulation-currentPopulation);
            /*
             Prints the society state each 7 days
             */
            if(EnvConfig.PRINTING_RATE !=0 && time.getDay()%EnvConfig.PRINTING_RATE==0)
                Logger.INFO("Date : "+time.getMonth()+","+time.getDay()+"\n"+ toString());

            return 0;
        }

        Logger.WARN("Society "+ id +" has come to an end. No people left in it.");
        peopleNbr = 0;
        return -1;
    }

    public int getPeopleNbr(){
        return peopleNbr;
    }
    public int getId() {
        return id;
    }

    public String toString(){
        return "-----------------------------------------------------------------\n"
                +time.toString()+"\n"+" People Nbr: "+peopleNbr+"\n"+peopleThread.toString();
    }
}
