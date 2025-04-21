package com.unse.proyecto.ubicua.principal.modelo;

import android.os.Parcel;
import android.os.Parcelable;

import com.unse.proyecto.ubicua.network.model.response.LearningObjectResponse;
import com.unse.proyecto.ubicua.network.model.response.ObjectRulesResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LearningObject implements Parcelable {

    private Integer id;
    private String nombre;
    private String archivo;
    private Integer level;
    private Boolean active;
    private String created;
    private List<ObjectRules> rules;

    public LearningObject(Integer id, String nombre, String archivo, Integer level, Boolean active, String created, List<ObjectRules> rules) {
        this.id = id;
        this.nombre = nombre;
        this.archivo = archivo;
        this.level = level;
        this.active = active;
        this.created = created;
        this.rules = rules;
    }

    protected LearningObject(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        nombre = in.readString();
        archivo = in.readString();
        if (in.readByte() == 0) {
            level = null;
        } else {
            level = in.readInt();
        }
        byte tmpActive = in.readByte();
        active = tmpActive == 0 ? null : tmpActive == 1;
        created = in.readString();
        rules = in.createTypedArrayList(ObjectRules.CREATOR);
    }

    public static final Creator<LearningObject> CREATOR = new Creator<LearningObject>() {
        @Override
        public LearningObject createFromParcel(Parcel in) {
            return new LearningObject(in);
        }

        @Override
        public LearningObject[] newArray(int size) {
            return new LearningObject[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(id);
        }
        dest.writeString(nombre);
        dest.writeString(archivo);
        if (level == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(level);
        }
        if (active == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) (active ? 1 : 2));
        }
        dest.writeString(created);
        dest.writeTypedList(rules);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static LearningObject build(LearningObjectResponse response) {
        List<ObjectRules> rules = new ArrayList<>();
        for (ObjectRulesResponse rulesResponse : response.getRules()) {
            rules.add(new ObjectRules(rulesResponse.getCode(), rulesResponse.getEmpty(),
                    rulesResponse.getSuccess(), rulesResponse.getError()));
        }
        return new LearningObject(response.getId(), response.getNombre(),
                response.getArchivo(), response.getLevel(), response.getActive(), response.getCreated(), rules);
    }

    // Getters (opcional, si los necesitás)
    public Integer getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getArchivo() {
        return archivo;
    }

    public Integer getLevel() {
        return level;
    }

    public Boolean getActive() {
        return active;
    }

    public String getCreated() {
        return created;
    }

    public List<ObjectRules> getRules() {
        return rules;
    }
}

