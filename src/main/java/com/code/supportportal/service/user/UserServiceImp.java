package com.code.supportportal.service.user;

import com.code.supportportal.domain.User;
import com.code.supportportal.exception.domain.EmailExistException;
import com.code.supportportal.exception.domain.UserNotFoundException;
import com.code.supportportal.exception.domain.UsernameExistException;
import com.code.supportportal.repository.UserRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Date;
import java.util.List;

import static com.code.supportportal.constant.UserServiceImpConstant.*;
import static com.code.supportportal.enums.Role.ROLE_USER;

@Service
public class UserServiceImp implements UserService{


    private UserRepository userRepo;
    private BCryptPasswordEncoder passwordEncoder;
    private final Logger LOGGER = LoggerFactory.getLogger(UserServiceImp.class);

    @Autowired
    public UserServiceImp(UserRepository userRepo,
                          BCryptPasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User register(String firstName, String lastName, String username, String email) {
        validateUsernameAndEmail(StringUtils.EMPTY, username, email);
        User newUser = new User();

        String password = generatePassword();
        String passwordEncoded = encodedPassword(password);

        newUser.setUserId(generateUserId());
        newUser.setFirstName(firstName);
        newUser.setLastName(lastName);
        newUser.setUsername(username);
        newUser.setEmail(email);
        newUser.setPassword(passwordEncoded);
        newUser.setJoinDate(new Date());
        newUser.setActive(true);
        newUser.setNotLocked(true);
        newUser.setRole(ROLE_USER.name());
        newUser.setAuthorities(ROLE_USER.getAuthorities());
        newUser.setProfileImageUrl(getTemporaryProfileImageUrl());
        LOGGER.info("Password: " + password);

        userRepo.save(newUser);
        return newUser;
    }

    public String encodedPassword(String password) {
        return passwordEncoder.encode(password);
    }

    private String getTemporaryProfileImageUrl() {
        // Get current domain context (ie: https://domain or http://localhost:8081)
        return ServletUriComponentsBuilder.fromCurrentContextPath().path(USER_DEFAULT_IMAGE_PATH).toUriString();
    }

    private String generatePassword() {
        return RandomStringUtils.randomAlphanumeric(10);
    }

    private String generateUserId() {
        //return UUID.randomUUID().toString(); // Probar
        return RandomStringUtils.randomNumeric(10); // generate string of length 10
    }

    private User validateUsernameAndEmail(String currentUsername, String newUsername, String newEmail){
        User findNewUser = findUserByUsername(newUsername);
        User emailUser = findUserByEmail(newEmail);
        if(StringUtils.isNotBlank(currentUsername)){ // If update user
            User currentUser = findUserByUsername(currentUsername);
            if(currentUser == null){
                throw new UserNotFoundException(USERNAME_NOT_FOUND + currentUsername);
            }

            if(findNewUser != null && !currentUser.getId().equals(findNewUser.getId())) {
                throw new UsernameExistException(USERNAME_ALREADY_EXISTS);
            }

            if(emailUser != null && !currentUser.getId().equals(emailUser.getId())) {
                throw new EmailExistException(EMAIL_ALREADY_EXIST);
            }
            return currentUser;
        }else{ // If new user
            if(findNewUser != null){
                throw new UsernameExistException(USERNAME_ALREADY_EXISTS);
            }
            if(emailUser != null){
                throw new EmailExistException(EMAIL_ALREADY_EXIST);
            }
            return null;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getUsers() {
        return userRepo.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public User findUserByUsername(String username) {
        /*return userRepo.findUserByUsername(username)*/
        /*        .orElseThrow(() -> new UserNotFoundException(USERNAME_NOT_FOUND));*/
        return userRepo.findUserByUsername(username).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public User findUserByEmail(String email) {
        return userRepo.findUserByEmail(email);
    }


}
