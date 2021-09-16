package people;

import config.SocietyConfig;
import market.Market;
import java.util.concurrent.*;
import java.util.ArrayList;
import java.util.List;
import logger.Logger;
import logger.LoggerException;
import calendar.Calendar;
import config.PeopleConfig;
import people.mating.MatingArea;
import work.JobOffers;
import work.Unemployed;

/*
- Written by Loslever Terry 2021-07-08 -> Ongoing
- Handles the multi threading on the people objects.
*/

public class PeopleThread{
	private int lastId;
	private ExecutorService executorService;
	private ArrayList<People> peopleList;
	final private MatingArea sexPartnerWaiting = new MatingArea();
	private List<Market> markets;
	private JobOffers jobOffers;

	public PeopleThread(Calendar calendar,List<Market> markets,JobOffers jobOffers,BankAccount stateAccount){
		int tmp = (int)(SocietyConfig.PEOPLE_NBR / SocietyConfig.PEOPLE_PER_THREAD);
		
		this.executorService = Executors.newFixedThreadPool(tmp);
		this.peopleList = new ArrayList();
		this.markets = markets;
		this.jobOffers = jobOffers;
		this.lastId = 0;
		//Creates the list of ppl

		for(int i =0;i<SocietyConfig.PEOPLE_NBR;i++){
			peopleList.add(new People(i,calendar,markets,sexPartnerWaiting,jobOffers,new PeopleCharacteristics(ThreadLocalRandom.current().nextInt(101),
					ThreadLocalRandom.current().nextInt(101),ThreadLocalRandom.current().nextInt(101),ThreadLocalRandom.current().nextInt(101),
					ThreadLocalRandom.current().nextInt(101),ThreadLocalRandom.current().nextInt(101),ThreadLocalRandom.current().nextInt(101))));
			peopleList.get(i).setStateAccount(stateAccount);
			lastId = i;
		}
		//Sets the date to the adult time for the first ppl of the society
		calendar.incYears(PeopleConfig.PEOPLE_ADULT_AGE);

	}

	public int execIteration() throws LoggerException{
		List<Future<People>> result;
		Calendar time = peopleList.get(0).getTime();
		People returnedPeople = null;
		int boundary = 0;

		try{

			result = executorService.invokeAll(peopleList);
			boundary = result.size();
			for(int tmp=0;tmp<boundary;tmp++){

				returnedPeople = result.get(tmp).get();

				if(returnedPeople == null){
					continue;
				}
				else if(returnedPeople.getId()==-1){
					People toDel = peopleList.get(tmp);
					toDel.suicide();
					peopleList.remove(tmp);
					boundary--;
					if(peopleList.size()==0){
						return 1;
					}
				}
				else{
					lastId++;
					returnedPeople.setId(lastId);
					returnedPeople.setStateAccount((peopleList.get(0).getStateAccount()));
					peopleList.add(returnedPeople);
				}

			}

		}catch(ExecutionException e){
			Logger.ERROR(" ExecutionException in the threadpool "+e.getMessage());
			e.printStackTrace();
			executorService.shutdown();
			return -1;
		}

		catch(InterruptedException  e){
			Logger.ERROR(" InterruptedException in the threadpool "+e.getMessage());
			e.printStackTrace();
			executorService.shutdown();
			return -1;
		}

		return 0;
	}
	public List<People> getUnemployed(){
		List<People> tmp = new ArrayList<>();
		for(People p:peopleList){
			if(p.getWork() instanceof Unemployed)
				tmp.add(p);
		}
		return tmp;
	}
	public int getYouthNbr(){
		int nbr = 0;

		for(People people : peopleList){
			if(!people.isAdult())
				nbr++;
		}
		return nbr;
	}
	public String toString(){
		People tmp;
		String result = "";

		for (People people : peopleList) {
			tmp = people;
			result = result + Logger.ANSI_GREEN + "\n People : " + Integer.toString(tmp.getId()) + Logger.ANSI_RESET + " " + tmp.toString();
		}

		return result;
	}
	public int getPeopleNbr(){
		return peopleList.size();
	}
}

