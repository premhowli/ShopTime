package com.howlzzz.shoptime.model;

/**
 * Created by PHowli on 10/24/2016.
 */
public class ShoppingListItem {
    private String itemName;
    private String owner;

    public ShoppingListItem() {}
    public ShoppingListItem(String itemName){
        this.itemName=itemName;
        this.owner="Anonymous Owner";
    }

public String getItemName(){
    return itemName;
}
    public String getOwner(){
        return owner;
    }


}
