package com.unse.proyecto.ubicua.network.model.response;

import androidx.annotation.Nullable;

public class UserResponse {

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
    public Integer xp;
    @Nullable
    public Integer xpTotal;
    @Nullable
    public String distance;
    @Nullable
    public Integer count;
    @Nullable
    public Integer objects;
    @Nullable
    public String created;

    public UserResponse(@Nullable Integer id, @Nullable String name, @Nullable String surname, @Nullable String career, @Nullable String faculty, @Nullable Integer level, @Nullable Integer xp, @Nullable Integer xpTotal, @Nullable String distance, @Nullable Integer count, @Nullable Integer objects, @Nullable String created) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.career = career;
        this.faculty = faculty;
        this.level = level;
        this.xp = xp;
        this.xpTotal = xpTotal;
        this.distance = distance;
        this.count = count;
        this.objects = objects;
        this.created = created;
    }

    @Nullable
    public Integer getLevel() {
        return level == null ? 0
                : level == 0 ? 1
                : level;
    }
}
