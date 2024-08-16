package com.dmitrijmrsh.jwt.auth.service.service;

import com.dmitrijmrsh.jwt.auth.service.entity.User;

import java.util.AbstractMap;

public interface UserAuthService {
    String login(String username, String password);
    AbstractMap.SimpleEntry<User, String> signup(String username, String password, String email);
}
