package com.unse.proyecto.ubicua;

import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDexApplication;

public class UbicuaApplication extends MultiDexApplication {

    private static UbicuaApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static Context getAppContext() {
        return instance.getApplicationContext();
    }

}
