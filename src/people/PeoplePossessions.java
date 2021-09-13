package people;

import config.PeopleConfig;
import config.SocietyConfig;
import market.FoodMarket;
import market.Tradable;

public class PeoplePossessions{

	private int food;
	private int housePrice;
	private int landSurface;
	private int landPrice;

	public PeoplePossessions(){
		food = 0;
		housePrice = 0;
	}
	public PeoplePossessions(int food,int housePrice,int landSurface,int landPrice){
		this.food = food;
		this.housePrice = housePrice;
		this.landSurface = landSurface;
		this.landPrice = landPrice;
	}
	public int getFood(){
		return food;
	}
	public int getHousePrice(){
		return housePrice;
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
	public void addFood(int nbr){  		
    	food += nbr;
	}
	public void addLandSurface(int nbr,int price){
		landSurface += nbr;
		landPrice += price;
	}
	public int getLandSurface(){
		return landSurface;
	}
	public int getLandPrice(){
		return landPrice;
	}
	public void addHouse(int nbr){  		
    	housePrice += nbr*SocietyConfig.HOUSE_PRICE;
	}
	public int getOwnership(FoodMarket foodSupply){
		int bestPrice = 0;
		Tradable bestOffer = foodSupply.getBestOffer();
		if(bestOffer != null)
			bestPrice = bestOffer.getAskedPrice();
		
		if(landSurface>0)
			return food*bestPrice+housePrice+(int)landPrice/landSurface;
		else
			return food*bestPrice+housePrice;
	}
	// Copy/paste methods used for the AI
	public PeoplePossessions copy(){
		return new PeoplePossessions(food,housePrice,landSurface,landPrice);
	}
	public void resume(PeoplePossessions possessions){
		this.food = possessions.getFood();
		this.housePrice = possessions.getHousePrice();
		this.landSurface = possessions.getLandSurface();
		this.landPrice = possessions.getLandPrice();
	}
	public String toString(){
		return "Possesses : food :"+Integer.toString(food)+"\t house :"+Integer.toString((int)housePrice/SocietyConfig.HOUSE_PRICE)+
		"\t land :"+Integer.toString(landSurface)+" m²\n";
	}
}