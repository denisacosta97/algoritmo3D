package com.unse.proyecto.ubicua.network.model.request;

public class LoginRequest {
    String dni;
    String password;

    public LoginRequest(String dni, String password) {
        this.dni = dni;
        this.password = password;
    }


}
