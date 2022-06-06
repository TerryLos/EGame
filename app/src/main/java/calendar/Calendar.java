package calendar;

import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.HashMap;

public class Calendar {

    private HashMap<Integer,Integer> monthLength;
    private ReentrantReadWriteLock lock;
    private int year;
    private int month;
    private int days;

    private int daysSinceStart;
    //Starts the 1 january 0 by default
    public Calendar(){
        lock = new ReentrantReadWriteLock();
        constructMonthTable();
        year = 0;
        month = 1;
        days = 1;
        daysSinceStart = 1;
    }
    public Calendar(int year,int month,int days){
        lock = new ReentrantReadWriteLock();
        constructMonthTable();
        incYears(year);
        incMonth(month);
        incDays(days);
    }
    //Object 0 is 31 days/month,1 is 30 days/month,2 is either 28 or 29
    private void constructMonthTable(){
        monthLength = new HashMap();
        for(int i = 1;i<=12;i++){
            if(i == 2)
                monthLength.put(i,2);
            else if(i == 1 || i == 3 || i == 5 || i == 7 || i == 8 || i == 10 || i == 12)
                monthLength.put(i,0);
            else
                monthLength.put(i,1);
        }
    }
    //Gets back to the 1-1-0 date
    public void reset(){
        lock.writeLock().lock();
        year = 0;
        month = 1;
        days = 1;
        lock.writeLock().unlock();
    }
    /*
        Input : days (int)
        Does : increments to current date by the amount of days given as argument
        Note : The function is thread-safe
     */
    public void incDays(int days){
        lock.writeLock().lock();
        int remainingDays = 0;
        int leftDays = days;
        while(leftDays > 0){
            try{
                switch(monthLength.get(month)){
                    case 0:
                        remainingDays = 31-this.days;
                        break;
                    case 1:
                        remainingDays = 30-this.days;
                        break;
                    case 2:
                        if(year % 4 != 0)
                            remainingDays = 28-this.days;
                        else
                            remainingDays = 29-this.days;
                        break;
                    default:
                        System.out.println("Shouldn't happen");
                        break;
                }
            }
            catch(NullPointerException exp){
                exp.printStackTrace();
                return;
            }
            if(remainingDays >= leftDays){
                this.days = this.days + leftDays;
                leftDays = 0;
            }
            else
            {
                this.days = 1;
                incMonth(1);
                leftDays = leftDays - (remainingDays + 1);
            }
        }
        daysSinceStart++;
        lock.writeLock().unlock();
    }
    /*
       Input : month (int)
       Does : increments to current date by the amount of months given as argument
       Note : The function is thread-safe
    */
    public void incMonth(int month){
        lock.writeLock().lock();
        int tmp = month+this.month;
        while(tmp > 12){
            incYears(1);
            tmp = tmp - 12;
        }
        this.month = tmp;
        lock.writeLock().unlock();
    }
    /*
       Input : year (int)
       Does : increments to current date by the amount of months given as argument
       Note : The function is thread-safe
    */
    public void incYears(int year){
        lock.writeLock().lock();
        this.year = this.year + year;
        lock.writeLock().unlock();
    }
    /*
       Does : returns the current day of the date
       Note : The function is thread-safe
    */
    public int getDay(){
        lock.readLock().lock();
        int tmp = days;
        lock.readLock().unlock();
        return tmp;
    }
    /*
       Does : returns the current month of the date
       Note : The function is thread-safe
    */
    public int getMonth(){
        lock.readLock().lock();
        int tmp = month;
        lock.readLock().unlock();
        return tmp;
    }
    /*
       Does : returns the current year of the date
       Note : The function is thread-safe
    */
    public int getYear(){
        lock.readLock().lock();
        int tmp = year;
        lock.readLock().unlock();
        return tmp;
    }
    /*
       Does : returns the date in a string format
       Note : The function is thread-safe
    */
    public String toString(){
        lock.readLock().lock();
        String tmp = "Date : "+Integer.toString(year)+"-"+Integer.toString(month)+"-"+Integer.toString(days);
        lock.readLock().unlock();
        return tmp;
    }
    /*
       Input : toCmp (Calendar)
       Does : returns true if the date in this object is the same as the date contained
       by toCmp. Returns false if not.
    */
    public boolean equals(Calendar toCmp){
        return (year == toCmp.getYear() && month == toCmp.getMonth() && days == toCmp.getDay());
    }
    /*
       Does : returns a new instance of Calendar with the same date as this object
    */
    public Calendar copy(){
        return new Calendar(getYear(),getMonth(),getDay());
    }
    /*
       Input : toCmp (Calendar), delay (int), type (int)
       Does : returns true if the delay is elapsed between the date contained
       in this object and the date in toCompare.
       if type == 0 : compares the number of days elapsed
       if type == 1 : compares the number of months elapsed
       if type == 2 : compares the number of years elapsed
    */
    public boolean isDelayElapsed(Calendar toCompare,int delay,int type){
        Calendar tmp = (Calendar)this.copy();
        switch(type){
            case 0:
                tmp.incDays(delay);
                break;
            case 1:
                tmp.incMonth(delay);
                break;
            case 2:
                tmp.incYears(delay);
                break;
        }
        return toCompare.getYear() > tmp.getYear() || (toCompare.getYear() == tmp.getYear() && toCompare.getMonth() > tmp.getMonth()) ||
                (toCompare.getYear() == tmp.getYear() && toCompare.getMonth() == tmp.getMonth() && toCompare.getDay() >= tmp.getDay());
    }
    public int timeElapsedMonth(Calendar toCompare){
        int result = (year-toCompare.getYear())*12+(month-toCompare.getMonth());

        return Math.max(result, 0);
    }
    /*
        Does : returns the current date in days.
     */
    public int getDaysSinceStart(){
        return daysSinceStart;
    }
}
