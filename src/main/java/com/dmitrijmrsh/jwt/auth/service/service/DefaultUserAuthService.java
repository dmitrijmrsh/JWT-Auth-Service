package com.dmitrijmrsh.jwt.auth.service.service;

import com.dmitrijmrsh.jwt.auth.service.entity.Authority;
import com.dmitrijmrsh.jwt.auth.service.entity.User;
import com.dmitrijmrsh.jwt.auth.service.exception.CustomException;
import com.dmitrijmrsh.jwt.auth.service.repository.UserRepository;
import com.dmitrijmrsh.jwt.auth.service.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DefaultUserAuthService implements UserAuthService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Override
    public String login(String username, String password) {

        try {
            log.info("Entered try/catch block in login method");
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
            return jwtTokenProvider.createToken(username, userRepository.findUserByUsername(username)
                    .map(User::getAuthorities).orElseThrow(
                            () -> new CustomException("User with that username is not found", HttpStatus.NOT_FOUND)
                    ));
        } catch (AuthenticationException ex) {
            throw new CustomException("Invalid username/password", HttpStatus.UNPROCESSABLE_ENTITY);
        }

    }

    @Override
    public AbstractMap.SimpleEntry<User, String> signup(String username, String password, String email) {

        if (!userRepository.existsUserByUsername(username) && !userRepository.existsUserByEmail(email)) {
            String passwordEncoded = passwordEncoder.encode(password);
            List<Authority> authorities = new ArrayList<>();
            User user = userRepository.save(new User(-1, username, passwordEncoded, email, authorities));
            String jwtToken = jwtTokenProvider.createToken(username, authorities);
            return new AbstractMap.SimpleEntry<>(user, jwtToken);
        } else {
            throw new CustomException("User with this username/password already exists", HttpStatus.UNPROCESSABLE_ENTITY);
        }

    }

//    @Override
//    @Transactional
//     public void updateUserData(HashMap<UserUpdateEnum, String> userData, List<Authority> newAuthorities) {
//
//        if (!userData.get(OLD_USERNAME).equals(userData.get(NEW_USERNAME)) &&
//                userRepository.existsUserByUsername(userData.get(NEW_USERNAME))
//        ) {
//            throw new CustomException("User with this username already exists", HttpStatus.UNPROCESSABLE_ENTITY);
//        }
//
//        if (!userData.get(OLD_EMAIL).equals(userData.get(NEW_EMAIL)) &&
//                userRepository.existsUserByEmail(userData.get(NEW_EMAIL))
//        ) {
//            throw new CustomException("User with this email already exists", HttpStatus.UNPROCESSABLE_ENTITY);
//        }
//
//        userRepository.findUserByUsername(userData.get(OLD_USERNAME))
//                .map(user -> {
//                    user.setUsername(userData.get(NEW_USERNAME));
//                    user.setEmail(userData.get(NEW_EMAIL));
//                    user.setPassword(userData.get(NEW_PASSWORD));
//                    user.setAuthorities(newAuthorities);
//                    return user;
//                });
//
//    }
//
//    @Override
//    @Transactional
//    public void deleteUser(String username) {
//
//        userRepository.delete(
//                userRepository.findUserByUsername(username)
//                        .orElseThrow(() -> new CustomException("User with that username is not found",
//                                HttpStatus.NOT_FOUND))
//        );
//
//    }
}
