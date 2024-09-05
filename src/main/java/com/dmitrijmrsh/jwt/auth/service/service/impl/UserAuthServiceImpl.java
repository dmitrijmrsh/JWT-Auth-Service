package com.dmitrijmrsh.jwt.auth.service.service.impl;

import com.dmitrijmrsh.jwt.auth.service.entity.User;
import com.dmitrijmrsh.jwt.auth.service.entity.enums.UserPrivilegeEnum;
import com.dmitrijmrsh.jwt.auth.service.entity.enums.UserRoleEnum;
import com.dmitrijmrsh.jwt.auth.service.exception.CustomException;
import com.dmitrijmrsh.jwt.auth.service.payload.GetUserInfoPayload;
import com.dmitrijmrsh.jwt.auth.service.payload.UserLogInPayload;
import com.dmitrijmrsh.jwt.auth.service.payload.UserSignUpPayload;
import com.dmitrijmrsh.jwt.auth.service.repository.UserRepository;
import com.dmitrijmrsh.jwt.auth.service.security.JwtTokenProvider;
import com.dmitrijmrsh.jwt.auth.service.service.UserAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class UserAuthServiceImpl implements UserAuthService {

    @Value("${security.admin.email}")
    private String adminEmail;

    @Value("${security.admin.password}")
    private String adminPassword;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final MessageSource messageSource;

    @Override
    public GetUserInfoPayload signup(UserSignUpPayload userSignUpPayload) {

        String firstName = userSignUpPayload.firstName();
        String lastName = userSignUpPayload.lastName();
        String email = userSignUpPayload.email();
        String password = userSignUpPayload.password();

        UserRoleEnum role = UserRoleEnum.ROLE_USER;
        UserPrivilegeEnum privilege = UserPrivilegeEnum.PRIVILEGE_STANDARD;

        if (userRepository.existsUserByEmail(email)) {
            throw new CustomException(this.messageSource.getMessage(
                    "user.auth.errors.email.already.exists", null, Locale.getDefault()
            ), HttpStatus.UNPROCESSABLE_ENTITY);
        }

        if (email.equals(adminEmail) && password.equals(adminPassword)) {
            role = UserRoleEnum.ROLE_ADMIN;
            privilege = UserPrivilegeEnum.PRIVILEGE_VIP;
        }

        String passwordEncoded = passwordEncoder.encode(password);

        User user = userRepository.save(new User(
                -1,
                firstName,
                lastName,
                email,
                passwordEncoded,
                role,
                privilege
        ));

        return new GetUserInfoPayload(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPrivilege().getPrivilegeInString()
        );
    }

    @Override
    public String login(UserLogInPayload userLogInPayload) {

        String email = userLogInPayload.email();
        String password = userLogInPayload.password();

        try {

            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    email, password
            ));

            return jwtTokenProvider.createToken(email);

        } catch (CustomException ex) {
            throw new CustomException(ex.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }
}
