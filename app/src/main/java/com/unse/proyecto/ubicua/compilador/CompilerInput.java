package com.unse.proyecto.ubicua.compilador;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class CompilerInput {

    private List<LatLng> puntos;
    private int nivel;

    public CompilerInput(List<LatLng> puntos, int nivel) {
        this.puntos = puntos;
        this.nivel = nivel;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{ [").append(nivel).append("]\n[");

        for (int i = 0; i < puntos.size(); i++) {
            LatLng punto = puntos.get(i);
            sb.append("(")
                    .append(punto.latitude)
                    .append(",")
                    .append(punto.longitude)
                    .append(")");

            if (i != puntos.size() - 1) {
                sb.append(",\n\t");
            }
        }

        sb.append("]\n}");
        return sb.toString();
    }
}
