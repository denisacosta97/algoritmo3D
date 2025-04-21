package com.unse.proyecto.ubicua.network.model.response;

import java.util.List;

public class LearningObjectResponse {

    private Integer id;
    private String nombre;
    private String archivo;
    private Integer level;
    private Boolean active;
    private String created;
    private List<ObjectRulesResponse> rules;

    public LearningObjectResponse(Integer id, String nombre, String archivo, Integer level, Boolean active, String created, List<ObjectRulesResponse> rules) {
        this.id = id;
        this.nombre = nombre;
        this.archivo = archivo;
        this.level = level;
        this.active = active;
        this.created = created;
        this.rules = rules;
    }

    public Integer getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getArchivo() {
        return archivo;
    }

    public Integer getLevel() {
        return level;
    }

    public Boolean getActive() {
        return active;
    }

    public String getCreated() {
        return created;
    }

    public List<ObjectRulesResponse> getRules() {
        return rules;
    }
}