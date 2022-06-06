package config;

public final class EnvConfig{
    //Represents the number of iterations per day.
    public static int ACTION_BY_DAY = 3;
    public static int NBR_SIM_DAY = 3*365;
    public static int SOCIETY_NBR = 1;

    public static String LOGPATH = "";
    public static boolean DEBUG = false;
    public static boolean FULL_INFO = true;
    //0 disables printing the state of the society,
    //Otherwise prints each PRINTING_RATE days.
    public static int PRINTING_RATE = 0;

    //Increase it to make the AI look in deeper states.
    //The program may run a LOT longer depending on the increase.
    public static int AI_DEPTH_SEARCH = 2;

}
