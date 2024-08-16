package com.dmitrijmrsh.jwt.auth.service.controller.payload;

public record UserSignUpPayload(String username, String password, String email) {
}
