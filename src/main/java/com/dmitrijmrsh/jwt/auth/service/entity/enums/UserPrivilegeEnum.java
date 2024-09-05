package com.dmitrijmrsh.jwt.auth.service.entity.enums;

public enum UserPrivilegeEnum {
    PRIVILEGE_STANDARD,
    PRIVILEGE_HIGH,
    PRIVILEGE_VIP;

    public String getPrivilegeInString() {
        return name();
    }
}
