package society;

import config.SocietyConfig;
import calendar.Calendar;
import market.FoodMarket;
import market.HouseMarket;
import market.LandMarket;
import market.Market;
import people.BankAccount;
import people.PeopleThread;
import people.People;

import java.sql.SQLException;
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

    private String name;
    private List<Market> markets;
    private BankAccount societyAccount;
    private PeopleThread peopleThread;
    private JobOffers jobOffers;
    private int peopleNbr;
    private Calendar time;

    public Society(Calendar time,String name){
        this.name = name;
        this.societyAccount = new BankAccount(0,-1);
        this.markets = new ArrayList<Market>();
        this.markets.add(new FoodMarket(SocietyConfig.INIT_FOOD_PRICE,SocietyConfig.INIT_FOOD_SUPPLY,societyAccount));
        this.markets.add(new HouseMarket(SocietyConfig.INIT_HOUSE_SUPPLY,SocietyConfig.INIT_HOUSE_PRICE,societyAccount));
        this.markets.add(new LandMarket(SocietyConfig.INIT_LAND_PRICE,SocietyConfig.INIT_LAND_SURFACE,societyAccount));
        this.jobOffers = new JobOffers((FoodMarket)markets.get(0),(HouseMarket)markets.get(1));
        this.peopleThread = new PeopleThread(time,markets,jobOffers,societyAccount);
        this.peopleNbr = SocietyConfig.PEOPLE_NBR;
        this.time = time;
    }
    
    public int runSociety() throws LoggerException{
        DatabaseWriter writer;
        int currentPopulation = peopleThread.getPeopleNbr();
        int postPopulation = 0;
        try {
            writer = new DatabaseWriter();
        }catch (ExceptionInInitializerError e){
            Logger.ERROR("Can't connect to the database.");
            return -1;
        }

        /*
            The Society pays the unemployed agent at the start of each months.
         */
        if(time.getDay()==1){
            List<People> unemployed = peopleThread.getUnemployed();
            for(People u:unemployed){
                u.getBankAccount().addMoney(SocietyConfig.BASE_INCOME_UNEMPLOYED);

                if(societyAccount.getBankAccount()<SocietyConfig.BASE_INCOME_UNEMPLOYED)
                    societyAccount.addMoney(SocietyConfig.BASE_INCOME_UNEMPLOYED);
                societyAccount.withdrawMoney(SocietyConfig.BASE_INCOME_UNEMPLOYED);
                
            }
        }
        int result = peopleThread.execIteration();

        writer.write(time.getDaysSinceStart(),peopleThread.getPeopleNbr(),markets.get(0).getSupply(),markets.get(1).getSupply(),markets.get(2).getSupply());

        if(result == 1){
            Logger.WARN("Society "+name+" has come to an end. No people left in it.");
        }

        else if(result == 0){
            postPopulation = peopleThread.getPeopleNbr();
            peopleNbr += (postPopulation-currentPopulation);

            if(postPopulation - currentPopulation > 0)
                Logger.INFO(" "+ time.toString() +" "+Integer.toString(postPopulation-currentPopulation)+" baby/ies are born !");
                    
            else if(postPopulation - currentPopulation < 0)
                Logger.INFO(" "+ time.toString() +" "+Integer.toString(currentPopulation-postPopulation)+" people have died !");

            /*
             Prints the society state each 7 days
             */
            if(time.getDay()%7==0)
                Logger.INFO("\t"+ toString());

            return 0;
        }

        peopleNbr = 0;
        return -1;
    }

    public int getPeopleNbr(){
        return peopleNbr;
    }
    public String getName() {
        return name;
    }

    public String toString(){
        return "-----------------------------------------------------------------\n"+time.toString()+"\n"+markets.get(0).toString()+"\n"+
        markets.get(2).toString()+societyAccount.toString()+"\t"+" People Nbr: "+Integer.toString(peopleNbr)+"\n"+peopleThread.toString();
    }
}
