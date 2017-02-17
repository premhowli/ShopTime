package com.howlzzz.shoptime;

import android.app.Application;

import com.firebase.client.Firebase;
import com.firebase.client.Logger;

public class ShopTime extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
        /* Enable disk persistence  */
        Firebase.getDefaultConfig().setPersistenceEnabled(true);
        Firebase.getDefaultConfig().setLogLevel(Logger.Level.DEBUG);

    }
}
