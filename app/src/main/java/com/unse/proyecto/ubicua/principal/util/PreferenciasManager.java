package com.unse.proyecto.ubicua.principal.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.util.Set;

public class PreferenciasManager {

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;
    int PRIVATE_MODE = 0;

    public PreferenciasManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(Utils.PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(Utils.IS_FIRST_TIME_LAUNCH, isFirstTime);
        editor.commit();
    }

    public boolean isFirstTimeLaunch() {

        return pref.getBoolean(Utils.IS_FIRST_TIME_LAUNCH, true);
    }

    public boolean userLogged(){
        return pref.getString(Utils.USER_ID, "").isEmpty();
    }

    public void logOut(){
        editor.putString(Utils.USER_ID,"");
        editor.commit();
    }

    public String getValueString(String tag) {
        return pref.getString(tag, "");
    }

    public int getValueInt(String tag) {
        return pref.getInt(tag, 0);
    }

    public void setValue(String tag, String value) {
        editor.putString(tag, value);
        editor.commit();
    }

    public void setValue(String tag, int value) {
        editor.putInt(tag, value);
        editor.commit();
    }

    public boolean getValue(String tag) {
        return pref.getBoolean(tag, true);
    }

    public void setValue(String tag, boolean value) {
        editor.putBoolean(tag, value);
        editor.commit();
    }

    public Set<String> getValueSet(String tag) {
        return pref.getStringSet(tag, null);
    }

    public void setValue(String tag, Set<String> value) {
        editor.putStringSet(tag, value);
        editor.commit();
    }

    public void saveObject(String key, Object object) {
        Gson gson = new Gson();
        String json = gson.toJson(object);
        editor.putString(key, json);
        editor.apply();
    }

    public <T> T getObject(String key, Class<T> clazz) {
        String json = pref.getString(key, null);
        if (json == null) {
            return null;
        }
        Gson gson = new Gson();
        return gson.fromJson(json, clazz);
    }

}