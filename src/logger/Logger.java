package logger;

import config.EnvConfig;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.Date;
import java.text.SimpleDateFormat;

/*
-   Written by Loslever Terry 2021-07-13
*/
public final class Logger{
    //Colors
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    private static FileWriter fw;
    private static BufferedWriter bw;
    private static PrintWriter out;
    private static final SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyy HH:mm");

    private static void init() throws LoggerException{
        if( fw == null || bw == null|| out == null ){

            try{
                //Clears the file at each startup
                PrintWriter tmp = new PrintWriter((EnvConfig.LOGPATH+"lifeEGame-Log.txt"));
                tmp.print("");
                tmp.flush();

                //TODO not set the autoflush,because kills the buffer principle.
                fw = new FileWriter((EnvConfig.LOGPATH+"lifeEGame-Log.txt"),true);
                bw = new BufferedWriter(fw);
                out =  new PrintWriter(bw);
                

            }catch(IOException io){
                throw new LoggerException(ANSI_RED+"[Error] - logger couldn't be instantiated"+ANSI_RESET);
            }
            
        }
        
    }
    public static void stop() throws LoggerException{
        if( fw == null || bw == null|| out == null ){
            throw new LoggerException(ANSI_RED+"[ERROR] - Can't close a non-opened Logger."+ANSI_RESET);
        }

        out.flush();
        out.close();
    }
    public synchronized static void WARN(String message) throws LoggerException{
        init();
        out.println(ANSI_YELLOW+"[WARN] -"+sdf.format(new Date(System.currentTimeMillis()))+"- "+message+ANSI_RESET);
    }
    public synchronized static void INFO(String message) throws LoggerException{
        init();
        out.println("[INFO] -"+sdf.format(new Date(System.currentTimeMillis()))+"- "+message);
    }
    public synchronized static void DEBUG(String message) throws LoggerException{
        init();

        if(EnvConfig.DEBUG){
            out.println(ANSI_CYAN+"[DEBUG] -"+sdf.format(new Date(System.currentTimeMillis()))+"- "+message+ANSI_RESET);
        }
    }
    public synchronized static void ERROR(String message) throws LoggerException{
        init();
        out.println(ANSI_RED+"[ERROR] -"+sdf.format(new Date(System.currentTimeMillis()))+"- "+message+ANSI_RESET);
    }

}
