package com.unse.proyecto.ubicua.network.model.response;

public class ObjectRulesResponse {
    private Integer id;
    private Integer idObjeto;
    private String code;
    private String empty;
    private String success;
    private String error;

    public ObjectRulesResponse(Integer id, Integer idObjeto, String code, String empty, String success, String error) {
        this.id = id;
        this.idObjeto = idObjeto;
        this.code = code;
        this.empty = empty;
        this.success = success;
        this.error = error;
    }

    public String getCode() {
        return code;
    }

    public String getEmpty() {
        return empty;
    }

    public String getSuccess() {
        return success;
    }

    public String getError() {
        return error;
    }
}
