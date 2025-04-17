package com.unse.proyecto.ubicua.network.model.response;

import androidx.annotation.Nullable;

public class LoginResponse {

    @Nullable
    public Integer id;
    @Nullable
    public String name;
    @Nullable
    public String surname;
    @Nullable
    public String career;
    @Nullable
    public String faculty;
    @Nullable
    public Integer level;
    @Nullable
    public String created;

    public LoginResponse(@Nullable Integer id,
                         @Nullable String name,
                         @Nullable String surname,
                         @Nullable String career,
                         @Nullable String faculty,
                         @Nullable Integer level,
                         @Nullable String created) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.career = career;
        this.faculty = faculty;
        this.level = level;
        this.created = created;
    }

}
