package com.dmitrijmrsh.jwt.auth.service.controller;

import com.dmitrijmrsh.jwt.auth.service.payload.GetUserInfoPayload;
import com.dmitrijmrsh.jwt.auth.service.payload.UpdateUserPayload;
import com.dmitrijmrsh.jwt.auth.service.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserRestController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<?> getCurrentUserInfo(@AuthenticationPrincipal UserDetails userDetails) {
        GetUserInfoPayload userInfoPayload = userService.getCurrentUser(userDetails);
        return ResponseEntity.ok().body(userInfoPayload);
    }

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

    @DeleteMapping
    public ResponseEntity<?> deleteCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        userService.deleteCurrentUser(userDetails);
        return ResponseEntity.noContent().build();
    }
}
