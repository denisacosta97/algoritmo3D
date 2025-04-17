package com.unse.proyecto.ubicua.principal.modelo;

import android.os.Parcel;
import android.os.Parcelable;
import android.provider.BaseColumns;

public class ObjetoCaptura implements Parcelable {

    public static String TABLA = "objeto_captura";
    public static String KEY_ID = BaseColumns._ID;
    public static String KEY_FECHA = "fecha";
    public static String KEY_CATEGORIA = "cat";

    private int id, cat;
    private String fecha;

    public ObjetoCaptura() {
    }

    public ObjetoCaptura(int id, int categoria, String fecha) {
        this.id = id;
        this.cat = categoria;
        this.fecha = fecha;
    }

    protected ObjetoCaptura(Parcel in) {
        id = in.readInt();
        cat = in.readInt();
        fecha = in.readString();
    }

    public static final Creator<ObjetoCaptura> CREATOR = new Creator<ObjetoCaptura>() {
        @Override
        public ObjetoCaptura createFromParcel(Parcel in) {
            return new ObjetoCaptura(in);
        }

        @Override
        public ObjetoCaptura[] newArray(int size) {
            return new ObjetoCaptura[size];
        }
    };

    public int getCat() {
        return cat;
    }

    public void setCat(int cat) {
        this.cat = cat;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(cat);
        dest.writeString(fecha);
    }
}
