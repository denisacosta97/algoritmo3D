package com.unse.proyecto.ubicua.interfaz.controller;

import androidx.lifecycle.ViewModel;

import com.unse.proyecto.ubicua.UbicuaApplication;
import com.unse.proyecto.ubicua.network.model.response.LoginResponse;
import com.unse.proyecto.ubicua.principal.util.PreferenciasManager;
import com.unse.proyecto.ubicua.principal.util.Utils;

public class DialogoMenuViewController extends ViewModel {

    private PreferenciasManager prefs = new PreferenciasManager(UbicuaApplication.getAppContext());


    public LoginResponse getSesionInfo(){
        return prefs.getObject(Utils.USER_ID, LoginResponse.class);
    }


    public void logOut() {
        prefs.logOut();
    }
}
