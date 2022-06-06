package people;

import java.util.Arrays;

public class PeopleCharacteristics {
    private final int[] weigthList;

    public PeopleCharacteristics(int healthWeight,int sleepWeight,int foodWeight,int libidoWeight,
                                 int incomeWeight,int pregnancyWeight,int possessionWeight,
                                 int greedinessWeight,int stinginess){
        weigthList = new int[9];
        this.weigthList[0] = healthWeight;
        this.weigthList[1] = sleepWeight;
        this.weigthList[2] = foodWeight;
        this.weigthList[3] = libidoWeight;
        this.weigthList[4] = pregnancyWeight;
        this.weigthList[5] = incomeWeight;
        this.weigthList[6] = possessionWeight;
        this.weigthList[7] = greedinessWeight;
        this.weigthList[8] = stinginess;
        normalize();
    }
    public PeopleCharacteristics(int[] weigthList){
        this.weigthList = weigthList;
    }
    /*
        Does : returns the characteristics as a formatted string
     */
    public String toString(){
        return "People characteristics -\n health:"+weigthList[0]+"\t sleep: "+weigthList[1]+"\n"+
                "food:"+weigthList[2]+"\t libido: "+weigthList[3]+"\n"+
                "pregnancy:"+weigthList[4]+"\t income: "+weigthList[5]+"\n"+
                "possession:"+weigthList[6]+"\t greediness: "+weigthList[7]+"\n"
                +"stinginess: "+weigthList[8]+"\n";
    }
    /*
        Does : normalizes the 8 first characteristics of the list.
     */
    public void normalize(){
        int sum = 0;
        for(int i = 0; i < 7 ;i++)
            sum += this.weigthList[i];

        for(int i = 0; i < 7 ;i++){
            this.weigthList[i] = (int) (this.weigthList[i]/(float)sum * 100);
        }
    }
    // Getter & setter methods
    public int[] getWeigthList(){
        return  Arrays.copyOf(weigthList,weigthList.length);
    }
    public void setMale(){
        this.weigthList[4] = 0;
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
    public int getGreediness(){
        return  weigthList[7];
    }
    public int getStinginess(){
        return  weigthList[8];
    }
}
