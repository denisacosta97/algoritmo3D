package com.unse.proyecto.ubicua;

import android.app.Application;
import android.content.Context;

public class UbicuaApplication extends Application {

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
