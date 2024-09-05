package com.dmitrijmrsh.jwt.auth.service.security;

import com.dmitrijmrsh.jwt.auth.service.entity.User;
import com.dmitrijmrsh.jwt.auth.service.exception.CustomException;
import com.dmitrijmrsh.jwt.auth.service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class MyUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final MessageSource messageSource;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Optional<User> mayBeUser = userRepository.findUserByEmail(email);

        if (!userRepository.existsUserByEmail(email)) {
            throw new CustomException(this.messageSource.getMessage(
                    "user.auth.errors.user.not.found.by.email", null, Locale.getDefault()
            ), HttpStatus.NOT_FOUND);
        }

        return org.springframework.security.core.userdetails.User
                .withUsername(email)
                .password(mayBeUser.get().getPassword())
                .authorities(mayBeUser.get().getRole().getRoleInString())
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }
}
