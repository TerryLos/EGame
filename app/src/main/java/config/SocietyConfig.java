package config;

import java.util.HashMap;

public final class SocietyConfig{

    //Reprensents the number of people at start
    public static int PEOPLE_NBR = 15;
    public static int CHILD_ROOF = 4;
    public static int INIT_PEOPLE_MONEY = 3500;
    public static boolean ALLOW_NEGATIVE_STATE_MONEY = false;
    public static int INIT_STATE_MONEY = 100000;

    public static int INIT_FOOD_SUPPLY = 5000;
    public static float INIT_FOOD_PRICE = 4;

    public static int INIT_HOUSE_SUPPLY = 0;
    public static float INIT_HOUSE_PRICE = 2000;

    public static int INIT_LAND_SURFACE = 4000;
    public static int LAND_PARCEL = 100;
    public static float INIT_LAND_PRICE = 1.2F;

    public static int INIT_WOOD_SUPPLY = 0;
    public static float INIT_WOOD_PRICE = 4;

    public static int INIT_STONE_SUPPLY = 0;
    public static float INIT_STONE_PRICE = 4;

    //Software elements
    //Nbr of threads allowed by the user on the machine, by default 5 ppl on a thread.
    public static int PEOPLE_PER_THREAD = 2;

    //Work parameters
    public static HashMap<String,Integer> AVAILABLE_JOBS_DIC;

    public static int MAX_FARMER_NBR = 100;
    public static int FARMER_WORK_GATHERING = 20;
    public static float FARMER_YIELD = 2.5F;

    public static int MAX_WOODCUTTER_NBR = 20;
    public static int MAX_TREE_ON_LAND = 40;
    public static float WOODCUTTER_YIELD = 2F;
    public static int WOODCUTTER_WORK_GATHERING = 30;
    public static int WOOD_GROWTH_RATE = 80;

    public static int MAX_BUILDER_NBR = 20;
    public static int BUILDER_WORK_GATHERING = 60;
    public static int BUILDER_WOOD_QTITY = 20;
    public static int BUILDER_LAND_QTITY = 100;
    public static int BUILDER_STONE_QTITY = 20;

    public static int MAX_MINER_NBR = 20;
    public static int MINER_WORK_GATHERING = 5;
    public static float MINER_YIELD = .5F;
    public static int MINER_DIFFICULTY = 2;

    public static int BASE_INCOME_UNEMPLOYED = 300;
    public static boolean AVRG_INCOME = false;

    //between 0% and 100%
    public static float VAT = 10;
    public static float YEAR_TAX = 200;


}
