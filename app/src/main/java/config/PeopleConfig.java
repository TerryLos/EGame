package config;

public final class PeopleConfig{
    //variables regarding ppl behavior
    public static int PEOPLE_SLEEP_RATE = 5;
    public static int PEOPLE_EAT_RATE = 5;
    public static int PEOPLE_DYING_RATE = 3;
    public static int PEOPLE_LIBIDO_RATE = 5;
    public static int PEOPLE_SLEEP_RECOVERY = 25;
    
    public static int PEOPLE_FOOD_ROOF = 100;
    public static int PEOPLE_SLEEP_ROOF = 100;
    public static int PEOPLE_LIBIDO_ROOF = 100;
	public static int PEOPLE_HEALTH_ROOF = 100;

    //in months
    public static int PEOPLE_GESTATION_TIME = 2;
    //Likelihood not to be pregnant 1/PPL_FERTILITY
    public static int PEOPLE_FERTILITY = 4;
    //in years
    public static int PEOPLE_ADULT_AGE = 2;

    //Genetic
    public static int PEOPLE_MUTATION_RATE = 4;
    public static int PEOPLE_MUTATION_AMP = 10;
    //Changes job based on money left on account or on income
    public static boolean PEOPLE_ONACCOUNT = true;
    //Rates at switch AI is allowed to search for a new job (in days)
    public static int PEOPLE_WORK_SWITCH = 20;

    public static int MAX_EXPERIENCE_COEFF = 2;
}
