package com.unse.proyecto.ubicua.network.remote;

import com.unse.proyecto.ubicua.network.model.request.LoginRequest;
import com.unse.proyecto.ubicua.network.model.request.RegisterRequest;
import com.unse.proyecto.ubicua.network.model.response.BaseResponse;
import com.unse.proyecto.ubicua.network.model.response.LoginResponse;
import com.unse.proyecto.ubicua.network.model.response.MapObjectResponse;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface AprendizajeUbicuoApi {

    @POST("auth/login")
    Call<BaseResponse<LoginResponse>> login(@Body LoginRequest body);

    @GET("auth/user/{id}")
    Call<BaseResponse<LoginResponse>> getUser(@Path("id") String dni);

    @POST("auth/register")
    Call<BaseResponse> registerUser(@Body RegisterRequest body);

    @GET("objects")
    Call<BaseResponse<ArrayList<MapObjectResponse>>> getMapObjects(@Query("dni") String dni);


}