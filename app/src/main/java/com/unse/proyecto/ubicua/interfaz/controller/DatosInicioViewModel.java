package com.unse.proyecto.ubicua.interfaz.controller;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.unse.proyecto.ubicua.UbicuaApplication;
import com.unse.proyecto.ubicua.network.di.AprendizajeUbicuoService;
import com.unse.proyecto.ubicua.network.model.request.LoginRequest;
import com.unse.proyecto.ubicua.network.model.response.BaseResponse;
import com.unse.proyecto.ubicua.network.model.response.LoginResponse;
import com.unse.proyecto.ubicua.network.remote.AprendizajeUbicuoApi;
import com.unse.proyecto.ubicua.principal.util.PreferenciasManager;
import com.unse.proyecto.ubicua.principal.util.Utils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DatosInicioViewModel extends ViewModel {

    private MutableLiveData<LoginResponse> _getUserRes = new MutableLiveData();
    public LiveData<LoginResponse> getUserRes = _getUserRes;

    private MutableLiveData<Boolean> _getUserError = new MutableLiveData();
    public LiveData<Boolean> getUserError = _getUserError;

    private MutableLiveData<Boolean> _showLoading = new MutableLiveData();
    public LiveData<Boolean> showLoading = _showLoading;

    private AprendizajeUbicuoApi ubicuaApi = AprendizajeUbicuoService.getApi();

    private PreferenciasManager prefs = new PreferenciasManager(UbicuaApplication.getAppContext());

    public void getUser(String dni, String psw){

        _showLoading.postValue(true);

        if(ubicuaApi == null){
            ubicuaApi = AprendizajeUbicuoService.getApi();
        }

        LoginRequest request  = new LoginRequest(dni, psw);

        ubicuaApi.login(request).enqueue(new Callback<BaseResponse<LoginResponse>>() {
            @Override
            public void onResponse(Call<BaseResponse<LoginResponse>> call, Response<BaseResponse<LoginResponse>> response) {
                _showLoading.postValue(false);
                if(response.isSuccessful() && response.body() != null){
                    BaseResponse<LoginResponse> base = response.body();
                    if (base != null && base.getData() != null) {
                        LoginResponse res = (LoginResponse) base.getData();
                        prefs.saveObject(Utils.USER_ID, res);
                        _getUserRes.postValue(res);
                    }else{
                        _getUserError.postValue(true);
                    }
                }else{
                    _getUserError.postValue(true);
                }

            }

            @Override
            public void onFailure(Call<BaseResponse<LoginResponse>> call, Throwable t) {
                _showLoading.postValue(false);
                _getUserError.postValue(true);
            }
        });
    }


}
