package statistics;

import config.EnvConfig;
import logger.Logger;
import logger.LoggerException;
import market.*;
import people.PeopleThread;
import work.JobOffers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;

public class DatabaseWriter {
    private final Connection connectionDatabase;
    private int cachedNbr;
    private Statement currentStatement;
    public DatabaseWriter() throws ExceptionInInitializerError,LoggerException {
            Logger.INFO("Init the database writer.");
            Statement stat;
            /*
               Accessing drivers.
             */
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            cachedNbr = 0;
            connectionDatabase = DriverManager.getConnection("jdbc:mysql://localhost/elifeDB","elifegame","elifegame");
            stat = connectionDatabase.createStatement();
            /*
                Refresh the database from possible previous runs
             */
            connectionDatabase.setAutoCommit(false);
            stat.addBatch("DELETE FROM eLifeGameStats;");
            stat.addBatch("DELETE FROM eLifeGameFoodStats;");
            stat.addBatch("DELETE FROM eLifeGameLandStats;");
            stat.addBatch("DELETE FROM eLifeGameWoodStats;");
            stat.addBatch("DELETE FROM eLifeGameStoneStats;");
            stat.addBatch("DELETE FROM eLifeGameStateStats;");
            stat.addBatch("DELETE FROM eLifeGameHouseStats;");
            stat.addBatch("DELETE FROM eLifeGameJobStats;");
            stat.executeBatch();
            connectionDatabase.commit();
        }catch (SQLException | ClassNotFoundException e){
            if(EnvConfig.DEBUG)
                Logger.DEBUG(Arrays.toString(e.getStackTrace()));
            throw new ExceptionInInitializerError();
        }
    }
    /*
        Does : returns true if this instance is connected to the MySQL database
     */
    public boolean isConnected() throws LoggerException{
        try{
            return  connectionDatabase == null || !connectionDatabase.isClosed();
        }catch (SQLException s){
            Logger.ERROR("Couldn't close the mysql connection.");
        }
       return false;
    }
    /*
        Does : writes the arguments in their corresponding database's tables.
        The data is cached 10 times before being committed in the database, but writing can be forced
        with the 'force' parameter.
     */
    public void write(boolean force, int id, int day, PeopleThread population, FoodMarket foodSupply, LandMarket landSupply, HouseMarket houseSupply,
                      WoodMarket woodSupply, StoneMarket stoneSupply, JobOffers jobs, PeopleThread peopleThread, int stateMoney) throws LoggerException{
        List<Integer> landByWork = peopleThread.getLandByWork();
        int remaining = landSupply.getSocietySurface();
        try{
            if(cachedNbr == 0)
                currentStatement = connectionDatabase.createStatement();

            currentStatement.addBatch("INSERT INTO eLifeGameHouseStats VALUES ("+id+","+day+","+houseSupply.getTotalSupply()+","+houseSupply.meanItemPrice()+","+houseSupply.getBestOffer().getAskedPrice()
                    +","+houseSupply.offerNbr()+","+houseSupply.getDemand()+");");
            currentStatement.addBatch("INSERT INTO eLifeGameLandStats VALUES ("+id+","+day+","+landSupply.getTotalSupply()+","+landSupply.meanItemPrice()+","+landSupply.getBestOffer().getAskedPrice()
                    +","+landSupply.offerNbr()+","+landSupply.getDemand()+","+remaining+","+houseSupply.getHouseLand()+","+landByWork.get(0)+","+landByWork.get(1)+","+landByWork.get(2)+","+landByWork.get(3)+","+landByWork.get(4)+");");
            currentStatement.addBatch("INSERT INTO eLifeGameStateStats VALUES ("+id+","+day+","+stateMoney+");");
            currentStatement.addBatch("INSERT INTO eLifeGameWoodStats VALUES ("+id+","+day+","+woodSupply.getTotalSupply()+","+woodSupply.meanItemPrice()+","+woodSupply.getBestOffer().getAskedPrice()
                    +","+woodSupply.offerNbr()+","+woodSupply.getDemand()+");");
            currentStatement.addBatch("INSERT INTO eLifeGameStoneStats VALUES ("+id+","+day+","+stoneSupply.getTotalSupply()+","+stoneSupply.meanItemPrice()+","+stoneSupply.getBestOffer().getAskedPrice()
                    +","+stoneSupply.offerNbr()+","+stoneSupply.getDemand()+");");
            currentStatement.addBatch("INSERT INTO eLifeGameFoodStats VALUES ("+id+","+day+","+foodSupply.getTotalSupply()+","+foodSupply.meanItemPrice()+","+foodSupply.getBestOffer().getAskedPrice()
                    +","+foodSupply.offerNbr()+","+foodSupply.getDemand()+");");
            currentStatement.addBatch("INSERT INTO eLifeGameJobStats VALUES ("+id+","+day+","+jobs.getFarmerNbr()+","+jobs.getIncome("Farmer")+","+jobs.getBuilderNbr()+","+jobs.getIncome("Builder")+","+
                    jobs.getWoodcutterNbr()+","+jobs.getIncome("Woodcutter")+","+jobs.getMinerNbr()+","+jobs.getIncome("Miner")+","+jobs.getUnemployedNbr()+");");
            currentStatement.addBatch("INSERT INTO eLifeGameStats VALUES ("+id+","+day+","+population.getPeopleNbr()+","+population.getYouthNbr()+");");

            if(cachedNbr == 10 || force){
                currentStatement.executeBatch();
                connectionDatabase.commit();
                cachedNbr = 0;
            }
            else
                cachedNbr++;
        }catch (SQLException s){
            if(EnvConfig.DEBUG)
                Logger.DEBUG(Arrays.toString(s.getStackTrace()));
            Logger.WARN("Couldn't write into the database at day "+day+" in "+id+" , "+s.getMessage());
        }

    }
    /*
        Does : closes the connection to the database
     */
    public void close() throws LoggerException{
        try{
            if(cachedNbr != 0){
                currentStatement.executeBatch();
                connectionDatabase.commit();
                cachedNbr = 0;
            }
            connectionDatabase.close();
        }catch (SQLException s){
            Logger.ERROR("Couldn't close the mysql connection.");
        }

    }
}
