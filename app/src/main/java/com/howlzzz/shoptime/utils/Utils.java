package com.howlzzz.shoptime.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.firebase.client.ServerValue;
import com.howlzzz.shoptime.model.ShopTime;

import java.text.SimpleDateFormat;
import java.util.HashMap;

/**
 * Utility class
 */
public class Utils {
    /**
     * Format the date with SimpleDateFormat
     */
    public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private Context mContext = null;


    /**
     * Public constructor that takes mContext for later use
     */
    public Utils(Context con) {
        mContext = con;
    }

    public static boolean checkIfOwner(ShopTime shoppingList, String currentUserEmail) {
        //final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        return (shoppingList.getEmail() != null &&
                shoppingList.getEmail().equals(currentUserEmail));
    }

    public static String encodeEmail(String userEmail) {
        return userEmail.replace(".", ",");
    }



        /**
     +     * Adds values to a pre-existing HashMap for updating a property for all of the ShoppingList copies.
     +     * The HashMap can then be used with {} to update the property
     +     * for all ShoppingList copies.
     +     *
     +     * @param listId           The id of the shopping list.
     +     * @param owner            The owner of the shopping list.
     +     * @param mapToUpdate      The map containing the key, value pairs which will be used
     +     *                         to update the Firebase database. This MUST be a Hashmap of key
     +     *                         value pairs who's urls are absolute (i.e. from the root node)
     +     * @param propertyToUpdate The property to update
     +     * @param valueToUpdate    The value to update
     +     * @return The updated HashMap with the new value inserted in all lists
     +     */
        public static HashMap<String, Object> updateMapForAllWithValue
        (final String listId,
         final String owner, HashMap<String, Object> mapToUpdate,
         String propertyToUpdate, Object valueToUpdate) {
        mapToUpdate.put("/" + Constants.FIREBASE_LOCATION_USER_LISTS + "/" + owner + "/"+listId + "/" + propertyToUpdate, valueToUpdate);
       // TODO the second method is updateMapWithTimestampLastChanged.
       // This method will similarly take in a HashMap of String and Objects. It will then set all copies
       // of the desired shopping list to have their TimestampLastChanged updated with the
       // ServerValue.TIMESTAMP value. As with above, right now this is only one list.
           return mapToUpdate;
       }

       /**
     +     * Adds values to a pre-existing HashMap for updating all Last Changed Timestamps for all of
     +     * the ShoppingList copies. This method uses {@link #updateMapForAllWithValue} to update the
     +     * last changed timestamp for all ShoppingList copies.
     +     *
     +     * @param listId               The id of the shopping list.
     +     * @param owner                The owner of the shopping list.
     +     * @param mapToAddDateToUpdate The map containing the key, value pairs which will be used
     +     *                             to update the Firebase database. This MUST be a Hashmap of key
     +     *                             value pairs who's urls are absolute (i.e. from the root node)
     +     * @return
     +     */
        public static HashMap<String, Object> updateMapWithTimestampLastChanged
        (final String listId,
         final String owner, HashMap<String, Object> mapToAddDateToUpdate) {
            /**
         +         * Set raw version of date to the ServerValue.TIMESTAMP value and save into dateCreatedMap
         +         */
            HashMap<String, Object> timestampNowHash = new HashMap<>();
            timestampNowHash.put(Constants.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);

            updateMapForAllWithValue(listId, owner, mapToAddDateToUpdate,
                    Constants.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);
            return mapToAddDateToUpdate;
        }

}


