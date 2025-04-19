package com.unse.proyecto.ubicua.principal.modelo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.unse.proyecto.ubicua.network.model.response.FoundedObjectResponse;

import java.util.List;

public class FoundedObject implements Parcelable {

    private Integer dni;
    private Objeto3D object3D;
    private List<String> latLong;
    private List<String> algoritmo;
    private String created;

    public FoundedObject(Integer dni, Objeto3D object3D, List<String> latLong, List<String> algoritmo, String created) {
        this.dni = dni;
        this.object3D = object3D;
        this.latLong = latLong;
        this.algoritmo = algoritmo;
        this.created = created;
    }

    protected FoundedObject(Parcel in) {
        if (in.readByte() == 0) {
            dni = null;
        } else {
            dni = in.readInt();
        }
        object3D = in.readParcelable(Objeto3D.class.getClassLoader());
        latLong = in.createStringArrayList();
        algoritmo = in.createStringArrayList();
        created = in.readString();
    }

    public static FoundedObject build(FoundedObjectResponse response){
        Objeto3D objeto3D = Objeto3D.build(response.getObject3D());
        return new FoundedObject(response.getId(), objeto3D, response.getLatLong(),
                response.getAlgoritmo(), response.getCreated());
    }

    public static final Creator<FoundedObject> CREATOR = new Creator<FoundedObject>() {
        @Override
        public FoundedObject createFromParcel(Parcel in) {
            return new FoundedObject(in);
        }

        @Override
        public FoundedObject[] newArray(int size) {
            return new FoundedObject[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (dni == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(dni);
        }
        dest.writeParcelable(object3D, flags);
        dest.writeStringList(latLong);
        dest.writeStringList(algoritmo);
        dest.writeString(created);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public Objeto3D getObject3D() {
        return object3D;
    }

    public List<String> getLatLong() {
        return latLong;
    }

    public LatLng getLocation(){
        LatLng latLng = null;
        if (latLong != null && latLong.size() >= 2) {
            String lat = latLong.get(latLong.size() - 2);
            String lng = latLong.get(latLong.size() - 1);
            latLng = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
        }
        return latLng;
    }

    public List<String> getAlgoritmo() {
        return algoritmo;
    }

    public String getCreated() {
        return created;
    }
}
