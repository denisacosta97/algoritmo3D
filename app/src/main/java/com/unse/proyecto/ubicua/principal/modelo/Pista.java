package com.unse.proyecto.ubicua.principal.modelo;

import android.os.Parcel;
import android.os.Parcelable;
import android.provider.BaseColumns;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

public class Pista implements Parcelable {

    public static String TABLA = "pista";
    public static String KEY_ID = BaseColumns._ID;
    public static String KEY_NOMBRE = "descripcion";
    public static String KEY_LAT = "lat";
    public static String KEY_LOG = "log";

    private int id;
    private Double lat, log;
    private String descripcion;

    public Pista() {
        this.id = 0;
    }

    public Pista(int id, Double lat, Double log, String descripcion) {
        this.id = id;
        this.lat = lat;
        this.log = log;
        this.descripcion = descripcion;
    }

    protected Pista(Parcel in) {
        id = in.readInt();
        if (in.readByte() == 0) {
            lat = null;
        } else {
            lat = in.readDouble();
        }
        if (in.readByte() == 0) {
            log = null;
        } else {
            log = in.readDouble();
        }
        descripcion = in.readString();
    }

    public static final Creator<Pista> CREATOR = new Creator<Pista>() {
        @Override
        public Pista createFromParcel(Parcel in) {
            return new Pista(in);
        }

        @Override
        public Pista[] newArray(int size) {
            return new Pista[size];
        }
    };

    public static Pista mapper(JSONObject o) {
        Pista pista = null;
        try {
            int id = o.getInt("id");
            String url = o.getString("desc");
            Double lat = o.getJSONArray("ubi").getDouble(1);
            Double log = o.getJSONArray("ubi").getDouble(0);
            pista = new Pista(id, lat, log, url);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return pista;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLog() {
        return log;
    }

    public void setLog(Double log) {
        this.log = log;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        if (lat == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(lat);
        }
        if (log == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(log);
        }
        dest.writeString(descripcion);
    }

    public LatLng getLatLng() {
        return new LatLng(lat, log);
    }
}
