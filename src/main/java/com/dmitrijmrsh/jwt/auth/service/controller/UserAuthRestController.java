package com.dmitrijmrsh.jwt.auth.service.controller;

import com.dmitrijmrsh.jwt.auth.service.payload.GetUserInfoPayload;
import com.dmitrijmrsh.jwt.auth.service.payload.UserLogInPayload;
import com.dmitrijmrsh.jwt.auth.service.payload.UserSignUpPayload;
import com.dmitrijmrsh.jwt.auth.service.repository.UserRepository;
import com.dmitrijmrsh.jwt.auth.service.service.UserAuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class UserAuthRestController {

    private final UserAuthService userAuthService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(
            @Valid @RequestBody UserSignUpPayload userSignUpPayload,
            BindingResult bindingResult
    ) throws BindException {

        if (bindingResult.hasErrors()) {
            if (bindingResult instanceof BindException exception) {
                throw exception;
            }

            throw new BindException(bindingResult);
        }

        GetUserInfoPayload userInfoPayload = userAuthService.signup(userSignUpPayload);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userInfoPayload);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Valid @RequestBody UserLogInPayload userLogInPayload,
            BindingResult bindingResult
    ) throws BindException {

        if (bindingResult.hasErrors()) {
            if (bindingResult instanceof BindException exception) {
                throw exception;
            }

            throw new BindException(bindingResult);
        }

        String token = userAuthService.login(userLogInPayload);

        return ResponseEntity.ok()
                .body(Map.of(
                        "token", token
                ));
    }
}
