package com.unse.proyecto.ubicua.network.remote;

import com.unse.proyecto.ubicua.network.model.request.LoginRequest;
import com.unse.proyecto.ubicua.network.model.request.ObjectRequest;
import com.unse.proyecto.ubicua.network.model.request.RegisterRequest;
import com.unse.proyecto.ubicua.network.model.response.BaseResponse;
import com.unse.proyecto.ubicua.network.model.response.FoundedObjectResponse;
import com.unse.proyecto.ubicua.network.model.response.LearningObjectResponse;
import com.unse.proyecto.ubicua.network.model.response.LoginResponse;
import com.unse.proyecto.ubicua.network.model.response.MapObjectResponse;
import com.unse.proyecto.ubicua.network.model.response.UserResponse;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Streaming;

public interface AprendizajeUbicuoApi {

    @POST("auth/login")
    Call<BaseResponse<LoginResponse>> login(@Body LoginRequest body);

    @GET("users/{id}")
    Call<BaseResponse<UserResponse>> getUser(@Path("id") String dni);

    @POST("auth/register")
    Call<BaseResponse> registerUser(@Body RegisterRequest body);

    @GET("objects")
    Call<BaseResponse<ArrayList<MapObjectResponse>>> getMapObjects(@Query("dni") String dni);

    @GET("objects/user/{dni}")
    Call<BaseResponse<List<FoundedObjectResponse>>> getFoundedObjects(@Path("dni") String dni);

    @POST("objects/user/{dni}")
    Call<Void> addDiscoverObject(@Path("dni") String dni, @Body ObjectRequest request);

    @GET("learning-objects/next")
    Call<BaseResponse<LearningObjectResponse>> getLearningObjects(@Query("dni") String dni);

    @Streaming
    @GET("learning-objects/zip/{name}")
    Call<ResponseBody> downloadLearningObjects(@Path("name") String filename);


}