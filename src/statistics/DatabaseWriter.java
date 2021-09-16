package statistics;

import config.EnvConfig;
import logger.Logger;
import logger.LoggerException;
import market.FoodMarket;
import market.HouseMarket;
import market.LandMarket;
import people.PeopleThread;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;

public class DatabaseWriter {
    Connection connectionDatabase;

    public DatabaseWriter() throws ExceptionInInitializerError,LoggerException {
            Logger.INFO("Init the database writer.");
            /*
               Accessing drivers.
             */
        try{
            Class.forName("com.mysql.jdbc.Driver");
            connectionDatabase = DriverManager.getConnection("jdbc:mysql://localhost:3306/elifeDB",
                    "elifegame", "elifegame");
            /*
                Refresh the database from possible previous runs
             */
            connectionDatabase.createStatement().executeUpdate("DELETE FROM eLifeGameStats;");
            connectionDatabase.createStatement().executeUpdate("DELETE FROM eLifeGameFoodStats;");
            connectionDatabase.createStatement().executeUpdate("DELETE FROM eLifeGameLandStats;");
            connectionDatabase.createStatement().executeUpdate("DELETE FROM eLifeGameHouseStats;");

        }catch (SQLException | ClassNotFoundException e){
            if(EnvConfig.DEBUG)
                Logger.DEBUG(Arrays.toString(e.getStackTrace()));
            throw new ExceptionInInitializerError();
        }
    }
    public boolean isConnected() throws LoggerException{
        try{
            return  connectionDatabase == null || !connectionDatabase.isClosed();
        }catch (SQLException s){
            Logger.ERROR("Couldn't close the mysql connection.");
        }
       return false;
    }
    public void write(int day, PeopleThread population, FoodMarket foodSupply, LandMarket landSupply, HouseMarket houseSupply) throws LoggerException{

        try{
            connectionDatabase.createStatement().executeUpdate(" INSERT INTO eLifeGameStats VALUES ("+day+","+population.getPeopleNbr()+","+population.getYouthNbr()+");");

            connectionDatabase.createStatement().executeUpdate(" INSERT INTO eLifeGameFoodStats VALUES ("+day+","+foodSupply.getTotalSupply()+","+foodSupply.meanItemPrice()+");");

            connectionDatabase.createStatement().executeUpdate(" INSERT INTO eLifeGameLandStats VALUES ("+day+","+landSupply.getTotalSupply()+","+landSupply.meanItemPrice()+");");

            connectionDatabase.createStatement().executeUpdate( " INSERT INTO eLifeGameHouseStats VALUES ("+day+","+houseSupply.getTotalSupply()+","+houseSupply.meanItemPrice()+");");
        }catch (SQLException s){
            if(EnvConfig.DEBUG)
                Logger.DEBUG(Arrays.toString(s.getStackTrace()));
            Logger.WARN("Couldn't write into the database at day "+day + " , "+s.getMessage());
        }

    }
    public void close() throws LoggerException{
        try{
            connectionDatabase.close();
        }catch (SQLException s){
            Logger.ERROR("Couldn't close the mysql connection.");
        }

    }
}
