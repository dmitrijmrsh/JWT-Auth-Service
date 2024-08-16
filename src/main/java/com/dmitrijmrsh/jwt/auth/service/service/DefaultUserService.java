package com.dmitrijmrsh.jwt.auth.service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class DefaultUserService implements UserService {

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
