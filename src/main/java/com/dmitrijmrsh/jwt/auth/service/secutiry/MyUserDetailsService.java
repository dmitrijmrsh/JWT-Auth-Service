package com.dmitrijmrsh.jwt.auth.service.secutiry;

import com.dmitrijmrsh.jwt.auth.service.entity.User;
import com.dmitrijmrsh.jwt.auth.service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MyUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> mayBeUser = userRepository.findUserByUsername(username);

        if (mayBeUser.isEmpty()) {
            throw new UsernameNotFoundException("There is no user with that name");
        }

        return org.springframework.security.core.userdetails.User
                .withUsername(username)
                .password(mayBeUser.get().getPassword())
                .authorities(mayBeUser.get().getAuthorities())
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }
}
