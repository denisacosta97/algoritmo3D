package com.unse.proyecto.ubicua.network.model.response;

public class MapObjectResponse {

    public int id;
    public String nombre;
    public int xp;
    public int nivel;
    public String modelo;
    public String imagen;
    public double lat;
    public double lon;

    public MapObjectResponse(int id, String nombre, int xp, int nivel, String modelo, String imagen, double lat, double lon) {
        this.id = id;
        this.nombre = nombre;
        this.xp = xp;
        this.nivel = nivel;
        this.modelo = modelo;
        this.imagen = imagen;
        this.lat = lat;
        this.lon = lon;
    }

}
