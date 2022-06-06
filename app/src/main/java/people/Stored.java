package people;

import config.SocietyConfig;
import market.Tradable;

import java.util.ArrayList;
import java.util.List;

public class Stored {
    private List<Tradable> items;
    private float totalPrice;
    private int totalQuantity;

    public Stored(){
        this.items = new ArrayList<>();
        this.totalPrice = 0F;
        this.totalQuantity = 0;
    }
    public Stored(List<Tradable> items,float totalPrice,int totalQuantity){
        this.items = new ArrayList<>();
        for(Tradable it:items)
            this.items.add(it.clone());
        this.totalPrice = totalPrice;
        this.totalQuantity = totalQuantity;
    }
    /*
        Adds an item to the list and updates the buffered values
    */
    public void add(Tradable item){
        this.items.add(item);
        this.totalPrice += item.getAskedPrice()*item.getVolume();
        this.totalQuantity += item.getVolume();
    }
    /*
        Tries to delete the desired quantity "amount" and updates
        the buffered values. If the list contains less than "amount" then it returns
        the complete list.
        Returns what was deleted.
    */
    public List<Tradable> delete(int amount){
        int remainingQtity = amount;
        Tradable current;
        List<Tradable> toRet = new ArrayList<>();

        while(remainingQtity > 0){
            if(items.size()>0){
                current = items.get(0);
                if(current.getVolume() <= remainingQtity){
                    this.totalPrice -= current.getVolume()*current.getAskedPrice();
                    this.totalQuantity -= current.getVolume();
                    remainingQtity -= current.getVolume();
                    toRet.add(items.remove(0));
                }
                else{
                    this.totalPrice -= remainingQtity*current.getAskedPrice();
                    this.totalQuantity -= remainingQtity;
                    current.setVolume(current.getVolume()-remainingQtity);
                    toRet.add(new Tradable(current.getAskedPrice(),remainingQtity,current.getReceiverAccount()));
                    remainingQtity = 0;
              }
            }else
                break;
        }
        return toRet;
    }
    /*
        Getter and copy functions.
    */
    public int getLength(){return items.size();}
    public int getTotalQuantity(){return this.totalQuantity;}
    public float getTotalPrice(){return this.totalPrice;}
    public Stored copy(){
        return new Stored(items,totalPrice,totalQuantity);
    }
    public List<Tradable> getList(){return items;}
    public String toString(){return totalQuantity+" "+totalPrice+" "+items.toString();}
}
