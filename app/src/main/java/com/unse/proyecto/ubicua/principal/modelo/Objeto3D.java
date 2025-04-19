package com.unse.proyecto.ubicua.principal.modelo;

import static com.unse.proyecto.ubicua.network.di.AprendizajeUbicuoService.BASE_URL;

import android.os.Parcel;
import android.os.Parcelable;
import android.provider.BaseColumns;

import com.google.android.gms.maps.model.LatLng;
import com.unse.compilador.lenguaje.model.Movement;
import com.unse.proyecto.ubicua.network.model.response.MapObjectResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class Objeto3D implements Parcelable {

    public static Float MAX_DEFAULT = 0.5000f;

    private String nombre;
    private String url;
    private String urlImg;
    private Double lat;
    private Double log;
    private int id;
    private List<Movement> movementList;
    private String blockObject;
    private Float maxSize;
    private ObjetoCaptura mObjetoCaptura;

    public Objeto3D() {
        this.id = 0;
    }

    protected Objeto3D(Parcel in) {
        id = in.readInt();
        url = in.readString();
        nombre = in.readString();
        urlImg = in.readString();
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
        if (in.readByte() == 0) {
            maxSize = null;
        } else {
            maxSize = in.readFloat();
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
        return objeto3D;
    }

    public List<Movement> getMovementList() {
        return movementList;
    }

    public void setMovementList(List<Movement> movementList) {
        this.movementList = movementList;
    }

    public String getBlockObject() {
        return blockObject;
    }

    public void setBlockObject(String blockObject) {
        this.blockObject = blockObject;
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

    public Float getMaxSize() {
        return maxSize;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(url);
        dest.writeString(nombre);
        dest.writeString(urlImg);
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
        if (maxSize == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeFloat(maxSize);
        }
        dest.writeParcelable(mObjetoCaptura, flags);
    }

    public static Objeto3D build(MapObjectResponse objectResponse) {
        Objeto3D objeto3D = new Objeto3D();
        objeto3D.setId(objectResponse.id);
        objeto3D.setNombre(objectResponse.nombre);
        objeto3D.setUrlImg(BASE_URL + "objects/image/" + objectResponse.imagen);
        objeto3D.setUrl(BASE_URL + "objects/model/" + objectResponse.modelo);
        return objeto3D;
    }

    public static Objeto3D build(MapObjectResponse objectResponse, List<Movement> movements, String blockObject) {
        Objeto3D objeto3D = build(objectResponse);
        objeto3D.setBlockObject(blockObject);
        objeto3D.setMovementList(movements);
        return objeto3D;
    }
}
