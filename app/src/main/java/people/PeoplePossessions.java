package people;

import config.PeopleConfig;
import config.SocietyConfig;
import market.FoodMarket;
import market.Tradable;

import java.util.ArrayList;
import java.util.List;

public class PeoplePossessions{

	private Stored food;
	private Stored land;
	private Stored wood;
	private Stored houses;
	private Stored stone;
	private float lastOwnership;

	public PeoplePossessions(){
		food = new Stored();
		lastOwnership = 0;
		land = new Stored();
		wood = new Stored();
		houses = new Stored();
		stone = new Stored();
	}
	public PeoplePossessions(float lastOwnership, Stored wood, Stored land,Stored houses,Stored food,Stored stone){
		this.food = food.copy();
		this.lastOwnership = lastOwnership;
		this.wood = wood.copy();
		this.houses = houses.copy();
		this.land = land.copy();
		this.stone = stone.copy();
	}

	/*
		The underlying functions are just giving access to the Stored class.
		To read about their working, please read comments in Stored.java
	 */
	public List<Tradable> consumeFood(int nbr){
		return this.food.delete(nbr);
	}
	public void addFood(Tradable food){
		this.food.add(food);
	}

	public void addWood(Tradable wood){
		this.wood.add(wood);
	}
	public List<Tradable> deleteWood(int amount){
		return this.wood.delete(amount);
	}

	public void addLandSurface(Tradable land){
		this.land.add(land);
	}
	public List<Tradable> deleteLandSurface(int surface){
		return this.land.delete(surface);
	}

	public void addStone(Tradable stone){
		this.stone.add(stone);
	}
	public List<Tradable> deleteStone(int amount){
		return this.stone.delete(amount);
	}

	public void addHouse(Tradable house){
    	this.houses.add(house);
	}
	public void resetOwnership(){lastOwnership = getOwnerShip();}
	public int getOwnershipBalance(){
		return (int)(lastOwnership-getOwnerShip());
	}
	// Copy/paste methods used for the AI
	public PeoplePossessions copy(){
		return new PeoplePossessions(lastOwnership,wood,land,houses,food,stone);
	}
	public void resume(PeoplePossessions possessions){
		this.food = possessions.getFood().copy();
		this.land = possessions.getLand().copy();
		this.houses = possessions.getHouses().copy();
		this.wood = possessions.getWood().copy();
		this.lastOwnership = possessions.getOwnerShip();
		this.stone = possessions.getStone().copy();
	}
	/*
		Getter and toString methods.
	 */
	public Stored getLand(){ return land;}
	public int getLandPrice(){ return (int)land.getTotalPrice();}
	public int getLandSurface() { return land.getTotalQuantity();}
	public Stored getHouses(){ return houses;}
	public Stored getWood(){ return wood;}
	public Stored getFood(){ return food;}
	public Stored getStone(){ return stone;}
	public Tradable getHouse(){
		List<Tradable> toRet = houses.delete(1);
		if(toRet.size()>0){
			return toRet.remove(0);
		}
		else
			return null;
	}
	public float getOwnerShip(){
		return land.getTotalPrice()+ houses.getTotalPrice()+food.getTotalPrice()+wood.getTotalPrice()+stone.getTotalPrice();
	}
	public int getFoodQtity(){
		return food.getTotalQuantity();
	}
	public int neededFood(){
		if(food.getTotalQuantity()>PeopleConfig.PEOPLE_FOOD_ROOF)
			return 0;
		return PeopleConfig.PEOPLE_FOOD_ROOF-food.getTotalQuantity();
	}
	public String toString(){
		return "Possesses : food :"+food.getTotalQuantity()+"\t house :"+houses.getTotalQuantity()+
		"\t land :"+land.getTotalQuantity()+" m²\n stone: "+stone.getTotalQuantity()+"\t wood : "+ wood.getTotalQuantity() +"\n Ownership : €"+getOwnerShip()+" \n";
	}
}