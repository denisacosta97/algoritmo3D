package com.unse.proyecto.ubicua.interfaz.controller;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.unse.proyecto.ubicua.UbicuaApplication;
import com.unse.proyecto.ubicua.network.di.AprendizajeUbicuoService;
import com.unse.proyecto.ubicua.network.model.request.RegisterRequest;
import com.unse.proyecto.ubicua.network.model.response.BaseResponse;
import com.unse.proyecto.ubicua.network.remote.AprendizajeUbicuoApi;
import com.unse.proyecto.ubicua.principal.util.PreferenciasManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterViewModel extends ViewModel {

    private MutableLiveData<Boolean> _registerRes = new MutableLiveData();
    public LiveData<Boolean> registerRes = _registerRes;
    private MutableLiveData<Boolean> _showLoading = new MutableLiveData();
    public LiveData<Boolean> showLoading = _showLoading;
    private AprendizajeUbicuoApi ubicuaApi = AprendizajeUbicuoService.getApi();
    private MutableLiveData<Boolean> _registerError = new MutableLiveData();
    public LiveData<Boolean> registerError = _registerError;
    private PreferenciasManager prefs = new PreferenciasManager(UbicuaApplication.getAppContext());

    public void callRegister(RegisterRequest registerRequest){

        ubicuaApi.registerUser(registerRequest).enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if(response != null){
                    if(response.message() == "El usuario ya existe"){
                        _registerRes.postValue(false);
                    }else{
                        _registerRes.postValue(true);
                    }
                }else{
                    _registerError.postValue(true);
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                _registerError.postValue(true);
            }
        });

    }


}
