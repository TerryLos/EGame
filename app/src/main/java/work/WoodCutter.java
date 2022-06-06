package work;

import calendar.Calendar;
import config.SocietyConfig;
import market.Tradable;
import market.WoodMarket;
import people.BankAccount;
import people.PeoplePossessions;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class WoodCutter extends Work{
    WoodMarket market;
    int totalCut;
    List<Wood> cutTrees;
    float yieldPrice;
    public WoodCutter(WoodMarket woodMarket, BankAccount account, PeoplePossessions possessions){
        super();
        //So it allows to produce food since the start.
        this.market = woodMarket;
        this.account = account;
        this.possession = possessions;
        this.totalCut = 0;
        this.cutTrees = new ArrayList<>();
    }
    @Override
    public void work(int greediness){
        int yield = 0;
        List<Wood> todel = new ArrayList<>();

        //Can only work if he has lands
        if(possession.getLandSurface()>0 && (yield = yieldEstimate()) != 0){
            if(workedTime>=SocietyConfig.WOODCUTTER_WORK_GATHERING){
                cutTrees.add(new Wood(yield,0));
                market.addSupply(new Tradable((float)priceEstimate(greediness),yield,account));
                totalCut += yield;
                this.yieldPrice=0;
                gainExperience();
                workedTime = 0;
            }
            else
                workedTime++;
            for(Wood el:cutTrees){
                if(el.updateTimer()){
                    totalCut -= el.getQuantity();
                    todel.add(el);
                }
            }
            cutTrees.removeAll(todel);
        }
    }

    @Override
    public String toString(){
        return "job : Woodcutter\t Income : "+Integer.toString(this.income)+"\t Experience : "+getExperience()+"\n"
                +"Production at :"+Integer.toString(workedTime)+"\t on :"+Integer.toString(SocietyConfig.WOODCUTTER_WORK_GATHERING)+"\n";
    }
    @Override
    public String jobString(){
        return "Woodcutter";
    }
    @Override
    public int yieldEstimate(){
        int possibleCutTree = (int)(possession.getLandSurface()*SocietyConfig.WOODCUTTER_YIELD*getExperience());
        int max = possession.getLandSurface()*SocietyConfig.MAX_TREE_ON_LAND;
        if(possibleCutTree + totalCut > max)
            possibleCutTree = max-totalCut;
        return possibleCutTree;
    }
    public float scarcityOnLand(){
        int maxTree = possession.getLandSurface()*SocietyConfig.MAX_TREE_ON_LAND;
        float scarce = (float)(maxTree-totalCut-yieldEstimate());
        if(maxTree/scarce> 1000 || scarce == 0)
            return 1000;
        return maxTree/scarce;
    }
    @Override
    public float priceEstimate(int greediness){
        Tradable bestOffer = market.getBestOffer();
        float avgLandPrice = possession.getLandPrice()/(float)possession.getLandSurface();
        int commodity = account.getSpentMoney();
        if(possession.getLandSurface() == 0)
            return 0;
        if (this.yieldPrice != 0)
            return this.yieldPrice;
        //Avoids spike at startup because buying a land is an investment and shouldn't
        //Be paid back directly
        if(account.getSpentMoney()>= possession.getLandPrice())
            commodity = account.getSpentMoney() - possession.getLandPrice();

        float basePrice = (float) (greedinessCoeff(greediness,market.offerNbr())*Math.abs(1+ThreadLocalRandom.current().nextGaussian() * scarcityOnLand()
                * ((avgLandPrice + commodity)/ yieldEstimate())));

        float lowestMarketPrice = bestOffer.getAskedPrice();
        //Wants to at least cover its expenses.
        if(basePrice>= lowestMarketPrice)
            this.yieldPrice = basePrice;
            //Wants to maximise its revenues therefore place himself as first on the market
        else
            this.yieldPrice = lowestMarketPrice - (float) ThreadLocalRandom.current().nextDouble(lowestMarketPrice-basePrice);

        return this.yieldPrice;
    }
    @Override
    public float getWorkStatus(){
        if(possession.getLandSurface()>0 && yieldEstimate() != 0)
            return workedTime/(float)SocietyConfig.WOODCUTTER_WORK_GATHERING;
        return 0;
    }
}
class Wood{
    int quantity;
    int iteration;
    public Wood(int quantity,int iteration){
        this.quantity = quantity;
        this.iteration = iteration;
    }
    public boolean updateTimer(){
        iteration++;
        return iteration >= SocietyConfig.WOOD_GROWTH_RATE;
    }
    public int getQuantity(){
        return quantity;
    }
}
