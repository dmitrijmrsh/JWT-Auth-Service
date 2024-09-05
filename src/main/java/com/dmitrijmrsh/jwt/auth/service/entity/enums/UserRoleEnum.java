package com.dmitrijmrsh.jwt.auth.service.entity.enums;

public enum UserRoleEnum {
    ROLE_ADMIN,
    ROLE_MANAGER,
    ROLE_USER;

    public String getRoleInString() {
        return name();
    }

}
