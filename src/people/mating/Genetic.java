package people.mating;

import config.PeopleConfig;
import people.People;
import people.PeopleCharacteristics;

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
                System.out.println("Genetic modification appeared !" + baseGenCode[i]);
                baseGenCode[i] += (int)(10 * ThreadLocalRandom.current().nextGaussian());
                if(baseGenCode[i] < 0)
                    baseGenCode[i] = 0;
                System.out.println(baseGenCode[i]);
            }
        }
        return baseGenCode;
    }

}