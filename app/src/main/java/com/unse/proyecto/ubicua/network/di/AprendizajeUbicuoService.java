package com.unse.proyecto.ubicua.network.di;

import com.unse.proyecto.ubicua.network.remote.AprendizajeUbicuoApi;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AprendizajeUbicuoService {

    public static final String BASE_URL = "http://192.168.1.79:8080/";
    private static AprendizajeUbicuoApi instance = null;

    public static AprendizajeUbicuoApi getApi() {
        if (instance == null) {
            instance = createApi();
        }
        return instance;
    }

    private static AprendizajeUbicuoApi createApi() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(AprendizajeUbicuoApi.class);
    }
}