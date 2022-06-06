package people.mating;

import people.People;
import people.PeopleCharacteristics;

import java.util.LinkedList;

public class MatingArea {
    LinkedList<People> matingList;

    public MatingArea(){
        matingList = new LinkedList<>();
    }
    synchronized public boolean setInMating(People people){
        if(alreadyIn(people))
            return false;

        matingList.addLast(people);
        return true;
    }
    synchronized public People getFromMating(){
        if(matingList.size()==0)
            return null;

        return matingList.pop();
    }
    private boolean alreadyIn(People people){
        for (People peo : matingList) {
            if (peo == people)
                return true;
        }
        return false;
    }
}
