package com.dmitrijmrsh.jwt.auth.service.service;

import com.dmitrijmrsh.jwt.auth.service.entity.User;
import com.dmitrijmrsh.jwt.auth.service.payload.UpdateUserPrivilegePayload;
import com.dmitrijmrsh.jwt.auth.service.payload.UpdateUserRolePayload;
import com.dmitrijmrsh.jwt.auth.service.payload.UserIdPayload;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public interface UserManagerService {
    void setRoleToUserById(Integer id, UpdateUserRolePayload updateUserRolePayload);
    void setPrivilegeToUserById(Integer id, UpdateUserPrivilegePayload updateUserPrivilegePayload, UserDetails userDetails);
    void deleteUserById(Integer id, UserDetails userDetails);
    User getUserById(Integer id);
    List<User> getAllUsers(UserDetails userDetails);
}
