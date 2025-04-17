package com.unse.proyecto.ubicua.principal.modelo;

import android.os.Parcel;
import android.os.Parcelable;
import android.provider.BaseColumns;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

public class Objeto3D implements Parcelable {

    public static String TABLA = "objeto";
    public static String KEY_ID = BaseColumns._ID;
    public static String KEY_NOMBRE = "nombre";
    public static String KEY_URL = "url";
    public static String KEY_URL_IMG = "urlimg";
    public static String KEY_CATEGORIA = "cat";
    public static String KEY_XP = "xp";
    public static String KEY_PROB = "prob";
    public static String KEY_LAT = "lat";
    public static String KEY_LOG = "log";

    private int id, categoria, xp;
    private String url, nombre, urlImg;
    private Double probabilidad, lat, log;
    private ObjetoCaptura mObjetoCaptura;

    public Objeto3D() {
        this.id = 0;
    }

    public Objeto3D(int id, int categoria, int xp, String url, String nombre, String urlImg,
                    Double probabilidad, Double lat, Double log) {
        this.id = id;
        this.categoria = categoria;
        this.xp = xp;
        this.url = url;
        this.nombre = nombre;
        this.urlImg = urlImg;
        this.probabilidad = probabilidad;
        this.lat = lat;
        this.log = log;
    }

    protected Objeto3D(Parcel in) {
        id = in.readInt();
        categoria = in.readInt();
        xp = in.readInt();
        url = in.readString();
        nombre = in.readString();
        urlImg = in.readString();
        if (in.readByte() == 0) {
            probabilidad = null;
        } else {
            probabilidad = in.readDouble();
        }
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
        mObjetoCaptura = in.readParcelable(ObjetoCaptura.class.getClassLoader());
    }

    public ObjetoCaptura getObjetoCaptura() {
        return mObjetoCaptura;
    }

    public void setObjetoCaptura(ObjetoCaptura objetoCaptura) {
        mObjetoCaptura = objetoCaptura;
    }

    public static final Creator<Objeto3D> CREATOR = new Creator<Objeto3D>() {
        @Override
        public Objeto3D createFromParcel(Parcel in) {
            return new Objeto3D(in);
        }

        @Override
        public Objeto3D[] newArray(int size) {
            return new Objeto3D[size];
        }
    };

    public static Objeto3D mapper(JSONObject o) {
        Objeto3D objeto3D = null;
        try {
            int id = o.getInt("id");
            int categoria = o.getInt("c");
            int xp = o.getInt("xp");
            String url = o.getString("url");
            String urlImg = o.getString("img");
            String nombre = o.getString("n");
            Double probabilidad = o.getDouble("p");
            Double lat = o.getJSONArray("ubi").getDouble(1);
            Double log = o.getJSONArray("ubi").getDouble(0);
            objeto3D = new Objeto3D(id, categoria, xp, url, nombre, urlImg, probabilidad, lat, log);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return objeto3D;
    }

    public String getUrlImg() {
        return urlImg;
    }

    public void setUrlImg(String urlImg) {
        this.urlImg = urlImg;
    }

    public LatLng getLatLng() {
        return new LatLng(lat, log);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCategoria() {
        return categoria;
    }

    public void setCategoria(int categoria) {
        this.categoria = categoria;
    }

    public int getXp() {
        return xp;
    }

    public void setXp(int xp) {
        this.xp = xp;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Double getProbabilidad() {
        return probabilidad;
    }

    public void setProbabilidad(Double probabilidad) {
        this.probabilidad = probabilidad;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(categoria);
        dest.writeInt(xp);
        dest.writeString(url);
        dest.writeString(nombre);
        dest.writeString(urlImg);
        if (probabilidad == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(probabilidad);
        }
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
        dest.writeParcelable(mObjetoCaptura, flags);
    }
}
