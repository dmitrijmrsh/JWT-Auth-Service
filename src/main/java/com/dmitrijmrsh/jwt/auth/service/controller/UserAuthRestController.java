package com.dmitrijmrsh.jwt.auth.service.controller;

import com.dmitrijmrsh.jwt.auth.service.controller.payload.UserLogInPayload;
import com.dmitrijmrsh.jwt.auth.service.controller.payload.UserSignUpPayload;
import com.dmitrijmrsh.jwt.auth.service.entity.User;
import com.dmitrijmrsh.jwt.auth.service.service.UserAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.AbstractMap;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class UserAuthRestController {

    private final UserAuthService userAuthService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLogInPayload userLogInPayload) {

        log.info("Entered /login auth-controller method");

        String jwtToken = userAuthService.login(userLogInPayload.username(), userLogInPayload.password());
        return ResponseEntity.ok().body(Map.of("token", jwtToken));

    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody UserSignUpPayload userSignUpPayload) {

        log.info("Entered /signup auth-controller method");

        AbstractMap.SimpleEntry<User, String> userToTokenMapping = userAuthService.signup(
                userSignUpPayload.username(),
                userSignUpPayload.password(),
                userSignUpPayload.email()
        );

        User user = userToTokenMapping.getKey();
        String jwtToken = userToTokenMapping.getValue();

        return ResponseEntity.ok().body(Map.of(
                "username", user.getUsername(),
                "password", user.getPassword(),
                "email", user.getEmail(),
                "token", jwtToken
        ));

    }
}
