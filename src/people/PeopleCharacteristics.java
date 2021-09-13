package people;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PeopleCharacteristics {
    private int[] weigthList;

    public PeopleCharacteristics(int healthWeight,int sleepWeight,int foodWeight,int libidoWeight,
                                 int incomeWeight,int pregnancyWeight,int possessionWeight){
        weigthList = new int[7];
        this.weigthList[0] = healthWeight;
        this.weigthList[1] = sleepWeight;
        this.weigthList[2] = foodWeight;
        this.weigthList[3] = libidoWeight;
        this.weigthList[4] = pregnancyWeight;
        this.weigthList[5] = incomeWeight;
        this.weigthList[6] = possessionWeight;
    }
    public PeopleCharacteristics(int[] weigthList){
        this.weigthList = weigthList;
    }

    public int getHealth(){
        return weigthList[0];
    }
    public int getSleep(){
        return weigthList[1];
    }
    public int getFood(){
        return weigthList[2];
    }
    public int getLibido(){
        return weigthList[3];
    }
    public int getPregnancy(){
        return weigthList[4];
    }
    public int getIncome(){
        return weigthList[5];
    }
    public int getPossession(){
        return  weigthList[6];
    }
    public int[] getWeigthList(){
        return  Arrays.copyOf(weigthList,weigthList.length);
    }
    public String toString(){
        return "People characteristics -\n health:"+weigthList[0]+"\t sleep: "+weigthList[1]+"\n"+
                "food:"+weigthList[2]+"\t libido: "+weigthList[3]+"\n"+
                "pregnancy:"+weigthList[4]+"\t income: "+weigthList[5]+"\n"+
                "possession:"+weigthList[6]+"\n";
    }
}