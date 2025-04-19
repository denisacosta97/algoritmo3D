package com.unse.proyecto.ubicua.network.model.response;

import java.util.List;

public class FoundedObjectResponse {

    private Integer id;
    private MapObjectResponse object3D;
    private List<String> latLong;
    private List<String> algoritmo;
    private String created;

    public FoundedObjectResponse(Integer id, MapObjectResponse object3D, List<String> latLong, List<String> algoritmo, String created) {
        this.id = id;
        this.object3D = object3D;
        this.latLong = latLong;
        this.algoritmo = algoritmo;
        this.created = created;
    }

    public Integer getId() {
        return id;
    }

    public MapObjectResponse getObject3D() {
        return object3D;
    }

    public List<String> getLatLong() {
        return latLong;
    }

    public List<String> getAlgoritmo() {
        return algoritmo;
    }

    public String getCreated() {
        return created;
    }
}
