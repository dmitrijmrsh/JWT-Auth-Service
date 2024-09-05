package com.dmitrijmrsh.jwt.auth.service.service.impl;

import com.dmitrijmrsh.jwt.auth.service.entity.User;
import com.dmitrijmrsh.jwt.auth.service.entity.enums.UserPrivilegeEnum;
import com.dmitrijmrsh.jwt.auth.service.entity.enums.UserRoleEnum;
import com.dmitrijmrsh.jwt.auth.service.exception.CustomException;
import com.dmitrijmrsh.jwt.auth.service.payload.UpdateUserPrivilegePayload;
import com.dmitrijmrsh.jwt.auth.service.payload.UpdateUserRolePayload;
import com.dmitrijmrsh.jwt.auth.service.repository.UserRepository;
import com.dmitrijmrsh.jwt.auth.service.service.UserManagerService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class UserManagerServiceImpl implements UserManagerService {

    @Value("${security.admin.email}")
    private String adminEmail;

    private final UserRepository userRepository;
    private final MessageSource messageSource;

    @Override
    public User getUserById(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new CustomException(this.messageSource.getMessage(
                        "user.auth.errors.user.not.found.by.id", null, Locale.getDefault()
                ), HttpStatus.NOT_FOUND));
    }

    @Override
    @Transactional
    public List<User> getAllUsers(UserDetails userDetails) {
        HashSet<UserRoleEnum> availableRoles = new HashSet<>();

        User userWhoMakesRequest = userRepository.findUserByEmail(userDetails.getUsername())
                .orElseThrow(() -> new CustomException(this.messageSource.getMessage(
                        "user.auth.errors.user.not.found.by.email", null, Locale.getDefault()
                ), HttpStatus.NOT_FOUND));

        availableRoles.add(UserRoleEnum.ROLE_USER);
        if (userWhoMakesRequest.getRole().equals(UserRoleEnum.ROLE_ADMIN)) {
            availableRoles.add(UserRoleEnum.ROLE_MANAGER);
        }

        List<User> users = userRepository.findAll();

        users.removeIf(user ->
                !availableRoles.contains(user.getRole()) || user.getEmail().equals(userDetails.getUsername())
        );

        return users;
    }

    @Override
    @Transactional
    public void setRoleToUserById(Integer id, UpdateUserRolePayload updateUserRolePayload) {
        UserRoleEnum role = getUserRoleEnumFromString(updateUserRolePayload.role());

        User user = getUserById(id);

        user.setRole(role);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void setPrivilegeToUserById(Integer id, UpdateUserPrivilegePayload updateUserPrivilegePayload, UserDetails userDetails) {
        UserPrivilegeEnum privilege = getUserPrivilegeEnumFromString(updateUserPrivilegePayload.privilege());

        User user = getUserById(id);

        if (user.getEmail().equals(adminEmail) && !userDetails.getUsername().equals(adminEmail)) {
            throw new CustomException(this.messageSource.getMessage(
                    "user.manager.errors.set.privilege.to.admin", null, Locale.getDefault()
            ), HttpStatus.UNPROCESSABLE_ENTITY);
        }

        user.setPrivilege(privilege);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void deleteUserById(Integer id, UserDetails userDetails) {
        User userBeingDeleted = getUserById(id);

        User userWhoDeletes = userRepository.findUserByEmail(userDetails.getUsername())
                        .orElseThrow(() -> new CustomException(this.messageSource.getMessage(
                "user.auth.errors.user.not.found.by.email", null, Locale.getDefault()
        ), HttpStatus.NOT_FOUND));

        if (
                userWhoDeletes.getRole().equals(UserRoleEnum.ROLE_MANAGER) &&
                !userBeingDeleted.getRole().equals(UserRoleEnum.ROLE_USER)
        ) {
            throw new CustomException(this.messageSource.getMessage(
                    "user.manager.errors.deleting.manager", null, Locale.getDefault()
            ), HttpStatus.UNPROCESSABLE_ENTITY);
        }

        userRepository.deleteById(id);
    }

    private UserRoleEnum getUserRoleEnumFromString(String role) {
        return switch (role) {
            case "ROLE_MANAGER" -> UserRoleEnum.ROLE_MANAGER;
            case "ROLE_USER" -> UserRoleEnum.ROLE_USER;
            default -> throw new CustomException(this.messageSource.getMessage(
                    "user.data.validation.errors.role.does.not.exist", null, Locale.getDefault()
            ), HttpStatus.UNPROCESSABLE_ENTITY);
        };
    }

    private UserPrivilegeEnum getUserPrivilegeEnumFromString(String privilege) {
        return switch (privilege) {
            case "PRIVILEGE_STANDARD" -> UserPrivilegeEnum.PRIVILEGE_STANDARD;
            case "PRIVILEGE_HIGH" -> UserPrivilegeEnum.PRIVILEGE_HIGH;
            case "PRIVILEGE_VIP" -> UserPrivilegeEnum.PRIVILEGE_VIP;
            default -> throw new CustomException(this.messageSource.getMessage(
                    "user.data.validation.errors.privilege.does.not.exist", null, Locale.getDefault()
            ), HttpStatus.UNPROCESSABLE_ENTITY);
        };
    }
}
