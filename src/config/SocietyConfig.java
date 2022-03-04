package config;

import java.util.HashMap;

public final class SocietyConfig{

    //Reprensents the number of people at start
    public static int PEOPLE_NBR = 20;
    public static int CHILD_ROOF = 4;
    public static int INIT_PEOPLE_MONEY = 3000;

    public static int INIT_FOOD_SUPPLY = 5000;
    public static int INIT_FOOD_PRICE = 9;

    public static int INIT_HOUSE_SUPPLY = 5;
    public static int INIT_HOUSE_PRICE = 2000;

    public static int INIT_LAND_SURFACE = 20000;
    public static int LAND_PARCEL = 100;
    public static int INIT_LAND_PRICE = 1;

    //Software elements
    //Nbr of threads allowed by the user on the machine, by default 5 ppl on a thread.
    public static int PEOPLE_PER_THREAD = 2;

    //Work parameters
    public static HashMap<String,Integer> AVAILABLE_JOBS_DIC;

    public static int MAX_FARMER_NBR = 100;
    public static int FARMER_WORK_GATHERING = 30;
    public static int FARMER_YIELD = 2;

    public static int MAX_WOODCUTTER_NBR = 100;
    public static int MAX_TREE_ON_LAND = 10;
    public static int WOODCUTTER_YIELD = 2;
    public static int WOODCUTTER_WORK_GATHERING = 30;
    public static int WOOD_GROWTH_RATE=80;

    public static int MAX_BUILDER_NBR = 100;
    public static int BUILDER_WORK_GATHERING = 30;
    public static int BUILDER_WOOD_QTITY = 1000;
    public static int BUILDER_LAND_QTITY = 100;

    public static int BASE_INCOME_UNEMPLOYED = 600;
    public static boolean AVRG_INCOME = false;

    //between 0% and 100%
    public static float VAT = 5;
    public static float YEAR_TAX = 200;



}
