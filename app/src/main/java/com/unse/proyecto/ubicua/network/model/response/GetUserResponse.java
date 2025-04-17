package com.unse.proyecto.ubicua.network.model.response;

import com.google.gson.annotations.SerializedName;

public class GetUserResponse {
    @SerializedName("id")
    public int id;

    @SerializedName("name")
    public String name;

    @SerializedName("surname")
    public String surname;

    @SerializedName("career")
    public String career;

    @SerializedName("faculty")
    public String faculty;

    @SerializedName("level")
    public int level;

    @SerializedName("created")
    public String created;

    public GetUserResponse(int id, String name, String surname, String career, String faculty, int level, String created) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.career = career;
        this.faculty = faculty;
        this.level = level;
        this.created = created;
    }
}
