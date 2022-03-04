package people;

import config.PeopleConfig;
import config.SocietyConfig;
import market.FoodMarket;
import market.Tradable;

import java.util.ArrayList;
import java.util.List;

public class PeoplePossessions{

	private int food;

	//Allows faster access
	private int landSurface;
	private int landPrice;

	private List<Tradable> land;
	private List<Tradable> wood;
	private List<Tradable> houses;

	private float estimatedOwnerShip;

	public PeoplePossessions(){
		food = 0;
		estimatedOwnerShip = 0;
		land = new ArrayList<>();
		wood = new ArrayList<>();
		houses = new ArrayList<>();
	}
	public PeoplePossessions(int food, float estimatedOwnerShip, int landSurface, int landPrice, List<Tradable> wood, List<Tradable>land,List<Tradable>houses){
		this.food = food;
		this.estimatedOwnerShip = estimatedOwnerShip;
		this.landSurface = landSurface;
		this.landPrice = landPrice;
		this.wood = new ArrayList<>(wood);
		this.houses = new ArrayList<>(houses);
		this.land = new ArrayList<>(land);
	}
	public List<Tradable> getWood(){return wood;}
	public List<Tradable> getHouses(){return houses;}
	public int getFood(){
		return food;
	}
	public int consumeFood(int nbr){
		int tmp;
		if(food - nbr >= 0){
		    food -=  nbr;
		    return nbr;
		}
		else{
			tmp = food;
			food = 0;
		    return tmp;
		}
	}
	public int neededFood(){
		return PeopleConfig.PEOPLE_FOOD_ROOF-food;
	}
	public List<Tradable> getLand(){return land;}
	public void addFood(int nbr){  		
    	food += nbr;
	}
	public void deleteWood(List<Tradable> wood){
		for(Tradable wd:wood)
			estimatedOwnerShip -= wd.getVolume()*wd.getAskedPrice();
		this.land.removeAll(wood);
	}
	public void deleteLandSurface(List<Tradable> land){
		for(Tradable ld:land){
			landSurface -= ld.getVolume();
			landPrice -= ld.getAskedPrice();
			estimatedOwnerShip -= ld.getVolume()*ld.getAskedPrice();
		}
		this.land.removeAll(land);
	}
	public void addLandSurface(Tradable land){
		this.land.add(land);
		landSurface += land.getVolume();
		landPrice += land.getAskedPrice();
		estimatedOwnerShip += land.getVolume()*land.getAskedPrice();
	}
	public void addWood(Tradable wood){
		this.wood.add(wood);
		estimatedOwnerShip += wood.getAskedPrice()*wood.getVolume();
	}
	public int getLandSurface(){
		return landSurface;
	}
	public int getLandPrice(){
		return landPrice;
	}
	public void addHouse(Tradable house){
		estimatedOwnerShip +=  house.getAskedPrice();
		landSurface += house.getVolume();
    	this.houses.add(house);
	}
	public Tradable getHouse(){
		if(houses.size()>0){
			Tradable house = houses.remove(0);
			landSurface -= house.getVolume();
			estimatedOwnerShip -= house.getAskedPrice();
			return house;
		}
		else
			return null;
	}
	public int getOwnership(FoodMarket foodSupply){
		int bestPrice = 0;
		Tradable bestOffer = foodSupply.getBestOffer();
		if(bestOffer != null)
			bestPrice = (int) bestOffer.getAskedPrice();
		
		if(landSurface>0)
			return (int)(food*bestPrice+estimatedOwnerShip);
		else
			return (int)(food*bestPrice+estimatedOwnerShip);
	}
	// Copy/paste methods used for the AI
	public PeoplePossessions copy(){
		return new PeoplePossessions(food,estimatedOwnerShip,landSurface,landPrice,wood,land,houses);
	}
	public float getEstimatedOwnership(){return estimatedOwnerShip;}
	public void resume(PeoplePossessions possessions){
		this.food = possessions.getFood();
		this.landSurface = possessions.getLandSurface();
		this.landPrice = possessions.getLandPrice();
		this.land = possessions.getLand();
		this.houses = possessions.getHouses();
		this.wood = possessions.getWood();
		this.estimatedOwnerShip = possessions.getEstimatedOwnership();
	}
	public String toString(){
		return "Possesses : food :"+food+"\t house :"+houses.size()+
		"\t land :"+landSurface+" m²\t wood packages: "+ wood.size() +"\n Ownership : €"+estimatedOwnerShip+" \n";
	}
}