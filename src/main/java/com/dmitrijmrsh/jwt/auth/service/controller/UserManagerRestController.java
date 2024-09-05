package com.dmitrijmrsh.jwt.auth.service.controller;

import com.dmitrijmrsh.jwt.auth.service.entity.User;
import com.dmitrijmrsh.jwt.auth.service.payload.UpdateUserPrivilegePayload;
import com.dmitrijmrsh.jwt.auth.service.payload.UpdateUserRolePayload;
import com.dmitrijmrsh.jwt.auth.service.service.UserManagerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("api/v1/manager")
@RequiredArgsConstructor
public class UserManagerRestController {

    private final UserManagerService userManagerService;

    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @GetMapping("/get/{id:\\d+}")
    public User getUserById(@PathVariable("id") Integer id) {
        return userManagerService.getUserById(id);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @GetMapping("/get/all")
    public List<User> getAllUsers(@AuthenticationPrincipal UserDetails userDetails) {
        return userManagerService.getAllUsers(userDetails);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/update/role/{id:\\d+}")
    public ResponseEntity<?> updateUserRole(
            @PathVariable("id") Integer id,
            @Valid @RequestBody UpdateUserRolePayload updateUserRolePayload,
            BindingResult bindingResult
    ) throws BindException {

        if (bindingResult.hasErrors()) {
            if (bindingResult instanceof BindException exception) {
                throw exception;
            }

            throw new BindException(bindingResult);
        }

        userManagerService.setRoleToUserById(id, updateUserRolePayload);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @PatchMapping("/update/privilege/{id:\\d+}")
    public ResponseEntity<?> updateUserPrivilege(
            @PathVariable("id") Integer id,
            @Valid @RequestBody UpdateUserPrivilegePayload updateUserPrivilegePayload,
            @AuthenticationPrincipal UserDetails userDetails,
            BindingResult bindingResult
    ) throws BindException {

        if (bindingResult.hasErrors()) {
            if (bindingResult instanceof BindException exception) {
                throw exception;
            }

            throw new BindException(bindingResult);
        }

        userManagerService.setPrivilegeToUserById(id, updateUserPrivilegePayload, userDetails);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("delete/{id:\\d+}")
    public ResponseEntity<?> deleteUserById(
            @PathVariable("id") Integer id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        userManagerService.deleteUserById(id, userDetails);
        return ResponseEntity.noContent().build();
    }

}
