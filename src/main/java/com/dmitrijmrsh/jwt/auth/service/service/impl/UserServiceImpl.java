package com.dmitrijmrsh.jwt.auth.service.service.impl;

import com.dmitrijmrsh.jwt.auth.service.entity.User;
import com.dmitrijmrsh.jwt.auth.service.entity.enums.UserPrivilegeEnum;
import com.dmitrijmrsh.jwt.auth.service.exception.CustomException;
import com.dmitrijmrsh.jwt.auth.service.payload.GetUserInfoPayload;
import com.dmitrijmrsh.jwt.auth.service.payload.UpdateUserPayload;
import com.dmitrijmrsh.jwt.auth.service.repository.UserRepository;
import com.dmitrijmrsh.jwt.auth.service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final MessageSource messageSource;

    @Override
    public GetUserInfoPayload getCurrentUser(UserDetails userDetails) {
        String email = userDetails.getUsername();

        User user = userRepository.findUserByEmail(email)
                .orElseThrow(() -> new CustomException(this.messageSource.getMessage(
                        "user.auth.errors.user.not.found.by.email", null, Locale.getDefault()
                ), HttpStatus.NOT_FOUND));

        return new GetUserInfoPayload(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPrivilege().getPrivilegeInString()
        );
    }

    @Override
    @Transactional
    public GetUserInfoPayload updateCurrentUserData(UserDetails userDetails, UpdateUserPayload updateUserPayload) {
        String email = userDetails.getUsername();
        final Integer[] userId = new Integer[1];
        final UserPrivilegeEnum[] userPrivilege = new UserPrivilegeEnum[1];

        userRepository.findUserByEmail(email).ifPresentOrElse(
                user -> {
                    userId[0] = user.getId();
                    userPrivilege[0] = user.getPrivilege();
                    user.setFirstName(updateUserPayload.firstName());
                    user.setLastName(updateUserPayload.lastName());
                    user.setEmail(updateUserPayload.email());
                    userRepository.save(user);
                },
                () -> {
                    throw new CustomException(this.messageSource.getMessage(
                            "user.auth.errors.user.not.found.by.email", null, Locale.getDefault()
                    ), HttpStatus.NOT_FOUND);
                }
        );

        return new GetUserInfoPayload(
                userId[0],
                updateUserPayload.firstName(),
                updateUserPayload.lastName(),
                updateUserPayload.email(),
                userPrivilege[0].getPrivilegeInString()
        );
    }

    @Override
    @Transactional
    public void deleteCurrentUser(UserDetails userDetails) {
        userRepository.deleteUserByEmail(userDetails.getUsername());
    }
}
