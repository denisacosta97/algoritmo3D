package com.unse.proyecto.ubicua.interfaz.controller;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.model.LatLng;
import com.unse.compilador.lenguaje.model.Movement;
import com.unse.proyecto.ubicua.UbicuaApplication;
import com.unse.proyecto.ubicua.compilador.CompilerModule;
import com.unse.proyecto.ubicua.network.di.AprendizajeUbicuoService;
import com.unse.proyecto.ubicua.network.model.request.ObjectRequest;
import com.unse.proyecto.ubicua.network.model.response.BaseResponse;
import com.unse.proyecto.ubicua.network.model.response.LearningObjectResponse;
import com.unse.proyecto.ubicua.network.model.response.LoginResponse;
import com.unse.proyecto.ubicua.network.model.response.MapObjectResponse;
import com.unse.proyecto.ubicua.network.model.response.UserResponse;
import com.unse.proyecto.ubicua.network.remote.AprendizajeUbicuoApi;
import com.unse.proyecto.ubicua.principal.modelo.Objeto3D;
import com.unse.proyecto.ubicua.principal.util.PreferenciasManager;
import com.unse.proyecto.ubicua.principal.util.Utils;

import java.util.ArrayList;
import java.util.List;

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

    private MutableLiveData<LearningObjectResponse> _mapLearningObject = new MutableLiveData();
    public LiveData<LearningObjectResponse> mapLearningObject = _mapLearningObject;

    private MutableLiveData<Objeto3D> _addObject = new MutableLiveData();
    public LiveData<Objeto3D> addObject = _addObject;

    public void getUser() {
        String id = (prefs.getObject(Utils.USER_ID, LoginResponse.class).id).toString();
        ubicuaApi.getUser(id).enqueue(new Callback<BaseResponse<UserResponse>>() {
            @Override
            public void onResponse(Call<BaseResponse<UserResponse>> call, Response<BaseResponse<UserResponse>> response) {
                if (response.isSuccessful() && response.body().getData() != null) {
                    UserResponse userResponse = (UserResponse) response.body().getData();
                    prefs.saveObject(Utils.USER_DATA, userResponse);
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<UserResponse>> call, Throwable t) {
            }
        });
    }

    public void addObject(List<Movement> movements, List<LatLng> latLng, Objeto3D objeto3D) {
        String id = (prefs.getObject(Utils.USER_ID, LoginResponse.class).id).toString();
        List<String> latLongs = new ArrayList<>();
        for (LatLng coord : latLng) {
            latLongs.add(String.valueOf(coord.latitude));
            latLongs.add(String.valueOf(coord.longitude));
        }
        List<String> moves = CompilerModule.movementsToString(movements);
        ObjectRequest objectRequest = new ObjectRequest(Integer.parseInt(id), objeto3D.getId(),
                latLongs, moves);
        ubicuaApi.addDiscoverObject(id, objectRequest).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    _addObject.postValue(objeto3D);
                } else {
                    _addObject.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                _addObject.postValue(null);
            }
        });
    }


    public void getObjectLocations() {
        _showLoading.postValue(true);
        String id = (prefs.getObject(Utils.USER_ID, LoginResponse.class).id).toString();
        ubicuaApi.getMapObjects(id).enqueue(new Callback<BaseResponse<ArrayList<MapObjectResponse>>>() {
            @Override
            public void onResponse(Call<BaseResponse<ArrayList<MapObjectResponse>>> call, Response<BaseResponse<ArrayList<MapObjectResponse>>> response) {
                if (response.isSuccessful() && response.body().getData() != null &&
                        !response.body().getData().isEmpty()) {
                    _mapObject.postValue(response.body().getData());
                } else {
                    _mapObject.postValue(new ArrayList());
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<ArrayList<MapObjectResponse>>> call, Throwable t) {
                _mapObject.postValue(new ArrayList());
            }
        });
    }

    public void getLearningObject() {
        String id = (prefs.getObject(Utils.USER_ID, LoginResponse.class).id).toString();
        ubicuaApi.getLearningObjects(id).enqueue(new Callback<BaseResponse<LearningObjectResponse>>() {
            @Override
            public void onResponse(Call<BaseResponse<LearningObjectResponse>> call, Response<BaseResponse<LearningObjectResponse>> response) {
                if (response.isSuccessful() && response.body().getData() != null) {
                    _mapLearningObject.postValue(response.body().getData());
                } else {
                    _mapLearningObject.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<LearningObjectResponse>> call, Throwable t) {
                _mapLearningObject.postValue(null);
            }
        });
    }


}
