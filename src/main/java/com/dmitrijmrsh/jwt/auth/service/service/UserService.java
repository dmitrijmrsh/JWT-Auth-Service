package com.dmitrijmrsh.jwt.auth.service.service;

import com.dmitrijmrsh.jwt.auth.service.payload.GetUserInfoPayload;
import com.dmitrijmrsh.jwt.auth.service.payload.UpdateUserPayload;
import org.springframework.security.core.userdetails.UserDetails;

public interface UserService {
    GetUserInfoPayload getCurrentUser(UserDetails userDetails);
    GetUserInfoPayload updateCurrentUserData(UserDetails userDetails, UpdateUserPayload updateUserPayload);
    void deleteCurrentUser(UserDetails userDetails);
}
