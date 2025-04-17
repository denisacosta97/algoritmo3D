package com.unse.proyecto.ubicua.compilador;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class CompilerModule {

    ArrayList<LatLng> puntos;
    int nivel;
    String output;

    public CompilerModule(ArrayList<LatLng> puntos, int nivel) {
        this.puntos = puntos;
        this.nivel = nivel;
    }

    public void build(){

    }
}
