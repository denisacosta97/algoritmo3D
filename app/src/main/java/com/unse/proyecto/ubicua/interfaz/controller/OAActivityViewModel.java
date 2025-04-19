package com.unse.proyecto.ubicua.interfaz.controller;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.unse.proyecto.ubicua.network.di.AprendizajeUbicuoService;
import com.unse.proyecto.ubicua.network.remote.AprendizajeUbicuoApi;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OAActivityViewModel extends ViewModel {

    private MutableLiveData<Boolean> _showLoading = new MutableLiveData();
    public LiveData<Boolean> showLoading = _showLoading;
    private AprendizajeUbicuoApi ubicuaApi = AprendizajeUbicuoService.getApi();

    private MutableLiveData<byte[]> _learningObject = new MutableLiveData();
    public LiveData<byte[]> learningObject = _learningObject;

    public void getObjectFile(String filename){
        _showLoading.postValue(true);
        ubicuaApi.downloadLearningObjects(filename).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ExecutorService executor = Executors.newSingleThreadExecutor();
                    executor.execute(() -> {
                        try {
                            InputStream inputStream = response.body().byteStream();
                            byte[] bytes = readBytesFromInputStream(inputStream);
                            _learningObject.postValue(bytes); // Lo observás en la UI si querés
                        } catch (IOException e) {
                            e.printStackTrace();
                            _learningObject.postValue(null);
                        }
                    });
                } else {
                    _learningObject.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                _learningObject.postValue(null);
            }
        });
    }

    private byte[] readBytesFromInputStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] data = new byte[4096];
        int nRead;
        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        return buffer.toByteArray();
    }

}
