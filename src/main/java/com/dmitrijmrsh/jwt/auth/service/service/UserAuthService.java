package com.dmitrijmrsh.jwt.auth.service.service;

import com.dmitrijmrsh.jwt.auth.service.payload.GetUserInfoPayload;
import com.dmitrijmrsh.jwt.auth.service.payload.UserLogInPayload;
import com.dmitrijmrsh.jwt.auth.service.payload.UserSignUpPayload;

public interface UserAuthService {
    GetUserInfoPayload signup(UserSignUpPayload userSignUpPayload);
    String login(UserLogInPayload userLogInPayload);
}
