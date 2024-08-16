package com.dmitrijmrsh.jwt.auth.service.entity.enums;

public enum UserAuthorityEnum {
    ROLE_ADMIN,
    ROLE_USER;

    public String getAuthorityInString() {
        return name();
    }
}
