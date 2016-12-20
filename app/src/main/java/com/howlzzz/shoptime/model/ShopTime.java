package com.howlzzz.shoptime.model;

import com.firebase.client.ServerValue;
import com.shaded.fasterxml.jackson.annotation.JsonIgnore;
import com.shaded.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;

/**
 * Created by PHowli on 8/22/2016.
 */
public class ShopTime {
    String listName;
    String owner;
    String email;
    @JsonProperty
    private Object created;
    /*private HashMap<String, Object> dateCreated;
    private HashMap<String, Object> dateLastChanged;*/

    public ShopTime() {
    }

    public ShopTime(String listName, String email,String owner) {
        this.listName = listName;
        this.email=email;
        this.owner = owner;

        //this.dateCreated = dateCreated;
        this.created = ServerValue.TIMESTAMP;

        //Date last changed will always be set to ServerValue.TIMESTAMP
        /*HashMap<String, Object> dateLastChangedObj = new HashMap<String, Object>();
        dateLastChangedObj.put("date", ServerValue.TIMESTAMP);
        this.dateLastChanged = dateLastChangedObj;*/
    }

    @JsonIgnore
    public Long getCreatedTimestamp() {
        if (created instanceof Long) {
            return (Long) created;
        } else {
            return null;
        }
    }
    @Override
    public String toString() {
        return listName + " by " + owner;
    }

    public Object getCreated() {
        return created;
    }

    public String getOwner() {
        return owner;
    }

    public String getListName() {
        return listName;
    }
    public String getEmail(){
        return email; }
}
