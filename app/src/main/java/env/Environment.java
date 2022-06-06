package env;

import calendar.Calendar;
import config.EnvConfig;
import config.SocietyConfig;
import java.util.ArrayList;
import java.util.List;
import society.Society;
import logger.Logger;
import logger.LoggerException;
import statistics.DatabaseWriter;

/*
- Written by Loslever Terry on going
- Environment in which societies are played.
- Handles the simulation and the constants values.
*/
public class Environment {
    private static DatabaseWriter dbWriter;
    public static void main(String[] args){
        String endState;
        try{
            dbWriter = new DatabaseWriter();
            List<Calendar> calendarList = new ArrayList<>();
            List<Society> societyList = new ArrayList<>();
            for(int i = 0; i < EnvConfig.SOCIETY_NBR; i++){
                Calendar calendar = new Calendar();
                calendarList.add(calendar);
                societyList.add(new Society(calendar,i));
            }


            for(int i = 0;i < societyList.size(); i++){
                /*
                Only writes the final state if everything went ok.
                */
                if(!startSimulation(calendarList.get(i),societyList.get(i))){
                    endState = "Final state :\n"+societyList.get(i).toString();
                    System.out.println(endState);
                    Logger.INFO(endState);

                }
            }
            Logger.stop();
            if(dbWriter.isConnected())
                dbWriter.close();
        }catch(LoggerException le){
            le.printStackTrace();
        }
    }
    /*
        Input : calendar (Calendar), society (Society)
        Does : starts teh simulation for the society's instance.
        Returns true if the society stayed alive until the end or false if all the agents died
     */
    private static boolean startSimulation(Calendar calendar,Society society) throws LoggerException{
        int i =0;
        boolean writeRight = true;

        if(envVariableCheckUp())
            System.exit(-1);
        System.out.println("---------------------Starting Simulation------------------");
        Logger.WARN("---------------------Starting Simulation------------------");

        if(EnvConfig.DEBUG)
            Logger.INFO("\t"+society.toString());

        //Iteration counter before switching to the next day
        while(i<EnvConfig.ACTION_BY_DAY*EnvConfig.NBR_SIM_DAY){
            //Error or death of the society
            if(society.runSociety(dbWriter,writeRight) == -1){
                return false;
            }
            writeRight = false;
            if(i % EnvConfig.ACTION_BY_DAY == 0 ){
                calendar.incDays(1);
                writeRight = true;
            }

            i = (i + 1);
        }

        System.out.println("---------------------End of Simulation------------------");
        System.out.println("Press Ctrl+C to close the engine.\n");
        Logger.WARN("---------------------End of Simulation------------------");
        return true;
    }
    /*
        Does : checks the simulation's parameters. Returns true if any variable
        is not correctly set, false otherwise.
     */
    private static boolean envVariableCheckUp() throws LoggerException{

        if(EnvConfig.ACTION_BY_DAY <= 0){
            Logger.ERROR("{START UP} ACTION_BY_DAY constant should be > 0.");
            return true;
        }

        if(SocietyConfig.PEOPLE_PER_THREAD == 0 || SocietyConfig.PEOPLE_NBR == 0){
            Logger.ERROR("{START UP} PEOPLE_PER_THREAD or PEOPLE_NBR constants shouldn't be empty.");
            return true;
        }

        if((SocietyConfig.PEOPLE_PER_THREAD % SocietyConfig.PEOPLE_NBR) != 0){
            Logger.WARN("{START UP} The number of thread should ideally be accepted by a integer division.");
        }

        if(EnvConfig.AI_DEPTH_SEARCH < 0){
            Logger.ERROR("{START UP} AI_DEPTH_SEARCH can't be negative.");
            return true;
        }

        return false;
    }
}

