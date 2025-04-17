package com.unse.proyecto.ubicua.network.model.response;

import androidx.annotation.Nullable;

public class BaseResponse<T> {

    String success;
    String message;
    @Nullable
    T data;

    public BaseResponse(String success, String message, @Nullable T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }
    public BaseResponse(String success, String message) {
        this.success = success;
        this.message = message;
    }

    @Nullable
    public T getData() {
        return data;
    }
}
