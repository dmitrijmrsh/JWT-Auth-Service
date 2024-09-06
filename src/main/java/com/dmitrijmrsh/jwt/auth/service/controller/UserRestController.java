package com.dmitrijmrsh.jwt.auth.service.controller;

import com.dmitrijmrsh.jwt.auth.service.payload.GetUserInfoPayload;
import com.dmitrijmrsh.jwt.auth.service.payload.UpdateUserPayload;
import com.dmitrijmrsh.jwt.auth.service.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Tag(name = "user_rest_controller")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserRestController {

    private final UserService userService;

    @Operation(
            summary = "Получение данных текущего пользователя",
            description =
                    "В случае успеха - возвращает JSON с данными текущего пользователя. " +
                    "В Authorization хэдере необходим JWT-токен",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Данные текущего пользователя получены"),
            @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован"),
            @ApiResponse(responseCode = "404", description = "Пользователь с данным email не найден"),
            @ApiResponse(responseCode = "500", description = "Недействительный JWT-токен")
    })
    @GetMapping
    public ResponseEntity<?> getCurrentUserInfo(@AuthenticationPrincipal UserDetails userDetails) {
        GetUserInfoPayload userInfoPayload = userService.getCurrentUser(userDetails);
        return ResponseEntity.ok().body(userInfoPayload);
    }

    @Operation(
            summary = "Обновление данных текущего пользователя",
            description =
                    "В теле принимает имя, фамилию, email. " +
                    "В Authorization хэдере необходим JWT-токен",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Данные текущего пользователя обновлены"),
            @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован"),
            @ApiResponse(responseCode = "404", description = "Пользователь с данным email не найден"),
            @ApiResponse(responseCode = "400", description = "Некорректный формат данных в теле запроса"),
            @ApiResponse(responseCode = "422", description = "Пользователь с данным email " +
                    "уже существует"),
            @ApiResponse(responseCode = "500", description = "Недействительный JWT-токен")
    })
    @PatchMapping
    public ResponseEntity<?> updateCurrentUserData(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UpdateUserPayload updateUserPayload,
            BindingResult bindingResult
    ) throws BindException {

        if (bindingResult.hasErrors()) {
            if (bindingResult instanceof BindException exception) {
                throw exception;
            }

            throw new BindException(bindingResult);
        }

        GetUserInfoPayload userInfoPayload = userService.updateCurrentUserData(
                userDetails, updateUserPayload
        );

        return ResponseEntity.ok().body(userInfoPayload);
    }

    @Operation(
            summary = "Удаление учётной записи текущего пользователя",
            description = "В Authorization хэдере необходим JWT-токен.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Учетная запись пользователя удалена"),
            @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован"),
            @ApiResponse(responseCode = "500", description = "Недействительный JWT-токен")
    })
    @DeleteMapping
    public ResponseEntity<?> deleteCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        userService.deleteCurrentUser(userDetails);
        return ResponseEntity.noContent().build();
    }
}
