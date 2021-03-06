package people.mating;

import config.PeopleConfig;
import people.People;
import people.PeopleCharacteristics;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

public class Genetic {

    static public PeopleCharacteristics geneticModification(People one, People two){
        int[] baseGenCode;
        if(one.utility()> two.utility())
            baseGenCode = one.getPeopleCharacteristics().getWeigthList();
        else
            baseGenCode = two.getPeopleCharacteristics().getWeigthList();
        return new PeopleCharacteristics(mutation(baseGenCode));
    }
    private static int[] mutation(int[] baseGenCode){

        for(int i=0;i<baseGenCode.length;i++){
            if(ThreadLocalRandom.current().nextInt(PeopleConfig.PEOPLE_MUTATION_RATE) == 1){
                baseGenCode[i] += (int)(PeopleConfig.PEOPLE_MUTATION_AMP * ThreadLocalRandom.current().nextGaussian());
                //Greediness is between 0 and 9 (for math puproses)
                if(baseGenCode[i] > 9 && i == 7)
                    baseGenCode[i] = 9;
                if(baseGenCode[i] < 0)
                    baseGenCode[i] = 0;
            }
        }
        return baseGenCode;
    }

}
