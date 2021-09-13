package statistics;

import logger.Logger;
import logger.LoggerException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

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
        }catch (SQLException | ClassNotFoundException e){
            e.printStackTrace();
            throw new ExceptionInInitializerError();
        }
    }
    public void write(int day,int population,int foodSupply,int landSupply,int houseSupply) throws LoggerException{
        try{
            String query = " INSERT INTO eLifeGameStats (day,population,foodSupply,landSupply,houseSupply)" +
                    "VALUES ("+day+","+population+","+foodSupply+","+landSupply+","+houseSupply+")";

            connectionDatabase.createStatement().executeQuery(query);
        }catch (SQLException s){
            Logger.WARN("Couldn't write into the database at day "+day);
        }

    }
}
