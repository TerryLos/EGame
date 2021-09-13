package config;

import java.util.HashMap;

public final class SocietyConfig{

    //Reprensents the number of people at start
    public static int PEOPLE_NBR = 50;
    public static int INIT_PEOPLE_MONEY = 2000;

    public static int INIT_FOOD_SUPPLY = 2000;
    public static int INIT_FOOD_PRICE = 5;

    public static int INIT_HOUSE_SUPPLY = 5;
    public static int INIT_HOUSE_PRICE = 6000;

    public static int INIT_LAND_SURFACE = 2000;
    public static int LAND_PARCEL = 20;
    public static int INIT_LAND_PRICE = 1;

    //Software elements
    //Nbr of threads allowed by the user on the machine, by default 5 ppl on a thread.
    public static int PEOPLE_PER_THREAD = 2;


    
    
    //Changes in the future since we want to implement free markets.
    public static int HOUSE_PRICE = 4000;

    //Work parameters
    public static HashMap<String,Integer> AVAILABLE_JOBS_DIC;

    public static int MAX_FARMER_NBR = 20;
    public static int BASE_INCOME_FARMER = 100;
    public static int FARMER_WORK_GATHERING = 40;
    public static int FARMER_YIELD = 10;

    public static int MAX_BUILDER_NBR = 10;
    public static int BASE_INCOME_BUILDER = 0;
    public static int BUILDER_WORK_GATHERING = 270;

    public static int BASE_INCOME_UNEMPLOYED = 900;



}
