package com.unse.proyecto.ubicua.principal.modelo;

import android.os.Parcel;
import android.os.Parcelable;

public class ObjectRules implements Parcelable {
    private String code;
    private String empty;
    private String success;
    private String error;

    public ObjectRules(String code, String empty, String success, String error) {
        this.code = code;
        this.empty = empty;
        this.success = success;
        this.error = error;
    }

    protected ObjectRules(Parcel in) {
        code = in.readString();
        empty = in.readString();
        success = in.readString();
        error = in.readString();
    }

    public static final Creator<ObjectRules> CREATOR = new Creator<ObjectRules>() {
        @Override
        public ObjectRules createFromParcel(Parcel in) {
            return new ObjectRules(in);
        }

        @Override
        public ObjectRules[] newArray(int size) {
            return new ObjectRules[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(code);
        dest.writeString(empty);
        dest.writeString(success);
        dest.writeString(error);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    // Getters
    public String getCode() {
        return code;
    }

    public String getEmpty() {
        return empty;
    }

    public String getSuccess() {
        return success;
    }

    public String getError() {
        return error;
    }
}

