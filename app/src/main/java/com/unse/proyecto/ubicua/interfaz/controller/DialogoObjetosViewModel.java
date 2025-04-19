package com.unse.proyecto.ubicua.interfaz.controller;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.unse.proyecto.ubicua.UbicuaApplication;
import com.unse.proyecto.ubicua.network.di.AprendizajeUbicuoService;
import com.unse.proyecto.ubicua.network.model.response.BaseResponse;
import com.unse.proyecto.ubicua.network.model.response.FoundedObjectResponse;
import com.unse.proyecto.ubicua.network.model.response.LoginResponse;
import com.unse.proyecto.ubicua.network.remote.AprendizajeUbicuoApi;
import com.unse.proyecto.ubicua.principal.util.PreferenciasManager;
import com.unse.proyecto.ubicua.principal.util.Utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DialogoObjetosViewModel extends ViewModel {

    private MutableLiveData<Boolean> _showLoading = new MutableLiveData();
    public LiveData<Boolean> showLoading = _showLoading;
    private AprendizajeUbicuoApi ubicuaApi = AprendizajeUbicuoService.getApi();
    private PreferenciasManager prefs = new PreferenciasManager(UbicuaApplication.getAppContext());

    private MutableLiveData<List<FoundedObjectResponse>> _object = new MutableLiveData();
    public LiveData<List<FoundedObjectResponse>> object = _object;

    public void getObjects(){
        String id = (prefs.getObject(Utils.USER_ID, LoginResponse.class).id).toString();
        _showLoading.postValue(true);
        ubicuaApi.getFoundedObjects(id).enqueue(new Callback<BaseResponse<List<FoundedObjectResponse>>>() {
            @Override
            public void onResponse(Call<BaseResponse<List<FoundedObjectResponse>>> call, Response<BaseResponse<List<FoundedObjectResponse>>> response) {
                if (response.isSuccessful() && response.body().getData() != null){
                    _object.postValue(response.body().getData());
                }else{
                    _object.postValue(new ArrayList<>());
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<List<FoundedObjectResponse>>> call, Throwable t) {
                _object.postValue(new ArrayList<>());
            }
        });
    }
}
