package com.unse.proyecto.ubicua.interfaz.controller;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.unse.proyecto.ubicua.UbicuaApplication;
import com.unse.proyecto.ubicua.network.di.AprendizajeUbicuoService;
import com.unse.proyecto.ubicua.network.model.response.BaseResponse;
import com.unse.proyecto.ubicua.network.model.response.LoginResponse;
import com.unse.proyecto.ubicua.network.model.response.MapObjectResponse;
import com.unse.proyecto.ubicua.network.remote.AprendizajeUbicuoApi;
import com.unse.proyecto.ubicua.principal.util.PreferenciasManager;
import com.unse.proyecto.ubicua.principal.util.Utils;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivityViewModel extends ViewModel {

    private MutableLiveData<Boolean> _showLoading = new MutableLiveData();
    public LiveData<Boolean> showLoading = _showLoading;
    private AprendizajeUbicuoApi ubicuaApi = AprendizajeUbicuoService.getApi();
    private PreferenciasManager prefs = new PreferenciasManager(UbicuaApplication.getAppContext());

    private MutableLiveData<ArrayList<MapObjectResponse>> _mapObject = new MutableLiveData();
    public LiveData<ArrayList<MapObjectResponse>> mapObject = _mapObject;

    public void getObjectLocations(){
        _showLoading.postValue(true);
        String id = (((LoginResponse)prefs.getObject(Utils.USER_ID, LoginResponse.class)).id).toString();
        ubicuaApi.getMapObjects(id).enqueue(new Callback<BaseResponse<ArrayList<MapObjectResponse>>>() {
            @Override
            public void onResponse(Call<BaseResponse<ArrayList<MapObjectResponse>>> call, Response<BaseResponse<ArrayList<MapObjectResponse>>> response) {
                if(response.isSuccessful()){
                    if(response.body().getData() != null &&
                            !response.body().getData().isEmpty()){
                        _mapObject.postValue(response.body().getData());
                    }else{
                        _mapObject.postValue(new ArrayList());
                    }
                }else{
                    _mapObject.postValue(new ArrayList());
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<ArrayList<MapObjectResponse>>> call, Throwable t) {
                _mapObject.postValue(new ArrayList());
            }
        });



    }





}
