package com.howlzzz.shoptime.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.howlzzz.shoptime.model.ShopTime;

import java.text.SimpleDateFormat;

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


}