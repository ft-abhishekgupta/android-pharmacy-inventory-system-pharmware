package com.android.pharmware;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Abhishek on 10-07-2017.
 */

public class PharmWare extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
