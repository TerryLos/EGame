package env;

import calendar.Calendar;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.*;
import config.EnvConfig;
import config.SocietyConfig;
import java.util.ArrayList;
import society.Society;
import calendar.Calendar;
import logger.Logger;
import logger.LoggerException;
import statistics.DatabaseWriter;

/*
- Written by Loslever Terry 2021-07-08 -> On going
- Environnment in which societies are played.
- Handles the simulation and the constants values.
*/
public class Environnment {

    public static void main(String[] args){
        String endState;
        try{
            Calendar calendar = new Calendar();
            Society society = new Society(calendar,"Simulation");

            /*
                Only writes the final state if everything went ok.
             */
            if(!(new Environnment().startSimulation(calendar,society))){
                endState = "Final state :\n"+society.toString();
                System.out.println(endState);
                Logger.INFO(endState);
                Logger.WARN("---------------------End  Of  Simulation------------------");
            }
            Logger.stop();

        }catch(LoggerException le){
            le.printStackTrace();
        }
    }
    
    private boolean startSimulation(Calendar calendar,Society society) throws LoggerException{
        int i =0;
        boolean writeRight = true;
        DatabaseWriter dbWriter = new DatabaseWriter();
        if(envVariableCheckUp())
            System.exit(-1);

        Logger.WARN("---------------------Starting Simulation------------------");
        Logger.INFO("\t"+society.toString());

        //Iteration counter before switching to the next day
        while(i<EnvConfig.ACTION_BY_DAY*EnvConfig.NBR_SIM_DAY){
            //Error or death of the society
            if(society.runSociety(dbWriter,writeRight) == -1)
                return false;

            writeRight = false;

            if(i % EnvConfig.ACTION_BY_DAY == 0 ){
                calendar.incDays(1);
                writeRight = true;
            }

            i = (i + 1);
        }
        if(dbWriter.isConnected())
            dbWriter.close();
        Logger.WARN("---------------------End of Simulation------------------");
        return true;
    }

    private boolean envVariableCheckUp() throws LoggerException{

        if(EnvConfig.ACTION_BY_DAY <= 0){
            Logger.ERROR("{START UP} ACTION_BY_DAY constant should be > 0.");
            return true;
        }

        if(SocietyConfig.PEOPLE_PER_THREAD == 0 || SocietyConfig.PEOPLE_NBR == 0){
            Logger.ERROR("{START UP} PEOPLE_PER_THREAD or PEOPLE_NBR constants shouldn't be empty.");
            return true;
        }

        if((SocietyConfig.PEOPLE_PER_THREAD % SocietyConfig.PEOPLE_NBR) != 0){
            Logger.WARN("{START UP} The number of thread should idealy be accepted by a integer division.");
        }

        return false;
    }
}

