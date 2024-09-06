package com.dmitrijmrsh.jwt.auth.service.controller;

import com.dmitrijmrsh.jwt.auth.service.payload.GetUserInfoPayload;
import com.dmitrijmrsh.jwt.auth.service.payload.UserLogInPayload;
import com.dmitrijmrsh.jwt.auth.service.payload.UserSignUpPayload;
import com.dmitrijmrsh.jwt.auth.service.repository.UserRepository;
import com.dmitrijmrsh.jwt.auth.service.service.UserAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "user_auth_rest_controller")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class UserAuthRestController {

    private final UserAuthService userAuthService;

    @Operation(
            summary = "Регистрация пользователя",
            description = "Выполняет регистрацию пользователя по имени, фамилии, паролю и email."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Пользователь зарегистрирован"),
            @ApiResponse(responseCode = "400", description = "Некорректный формат данных в теле запроса"),
            @ApiResponse(responseCode = "422", description = "Пользователь с таким email " +
                    "уже существует")
    })
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


    @Operation(
            summary = "Аутентификация пользователя",
            description =
                    "Выполняет аутентификацию по email и паролю. " +
                    "В случае успеха возвращает JWT."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь аутентифицирован"),
            @ApiResponse(responseCode = "400", description = "Некорректный формат данных в теле запроса"),
            @ApiResponse(responseCode = "404", description = "Пользователь с данным email не найден"),
    })
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
