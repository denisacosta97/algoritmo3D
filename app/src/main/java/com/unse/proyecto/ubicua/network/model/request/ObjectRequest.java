package com.unse.proyecto.ubicua.network.model.request;

import java.util.List;

public class ObjectRequest {
    private int dni;
    private int object;
    private List<String> latLong;
    private List<String> algorithm;

    public ObjectRequest(int dni, int object, List<String> latLong, List<String> algorithm) {
        this.dni = dni;
        this.object = object;
        this.latLong = latLong;
        this.algorithm = algorithm;
    }
}

