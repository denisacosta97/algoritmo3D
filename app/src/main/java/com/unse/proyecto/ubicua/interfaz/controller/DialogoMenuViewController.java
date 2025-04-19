package com.unse.proyecto.ubicua.interfaz.controller;

import androidx.lifecycle.ViewModel;

import com.unse.proyecto.ubicua.UbicuaApplication;
import com.unse.proyecto.ubicua.network.model.response.UserResponse;
import com.unse.proyecto.ubicua.principal.util.PreferenciasManager;
import com.unse.proyecto.ubicua.principal.util.Utils;

public class DialogoMenuViewController extends ViewModel {

    private PreferenciasManager prefs = new PreferenciasManager(UbicuaApplication.getAppContext());


    public UserResponse getSesionInfo(){
        return prefs.getObject(Utils.USER_DATA, UserResponse.class);
    }


    public void logOut() {
        prefs.logOut();
    }
}
