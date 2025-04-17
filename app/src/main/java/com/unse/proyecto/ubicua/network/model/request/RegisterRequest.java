package com.unse.proyecto.ubicua.network.model.request;

import com.google.gson.annotations.SerializedName;

public class RegisterRequest {
    @SerializedName("dni")
    public String dni;
    @SerializedName("password")
    public String password;
    @SerializedName("name")
    public String name;
    @SerializedName("surname")
    public String surname;

    public RegisterRequest(String dni, String password, String name, String surname) {
        this.dni = dni;
        this.password = password;
        this.name = name;
        this.surname = surname;
    }
}
