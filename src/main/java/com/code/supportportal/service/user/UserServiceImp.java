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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Date;
import java.util.List;

import static com.code.supportportal.enums.Role.ROLE_USER;

@Service
public class UserServiceImp implements UserService{

    private UserRepository userRepo;
    private PasswordEncoder passwordEncoder;
    private final Logger LOGGER = LoggerFactory.getLogger(UserServiceImp.class);

    @Autowired
    public UserServiceImp(UserRepository userRepo,
                          PasswordEncoder passwordEncoder) {
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
        return ServletUriComponentsBuilder.fromCurrentContextPath().path("/user/image/profile/temp").toUriString();
    }

    private String generatePassword() {
        return RandomStringUtils.randomAlphanumeric(10);
    }

    private String generateUserId() {
        //return UUID.randomUUID().toString(); // Probar
        return RandomStringUtils.randomNumeric(10); // generate string of length 10
    }

    private User validateUsernameAndEmail(String currentUsername, String newUsername, String newEmail){
        if(StringUtils.isNotBlank(currentUsername)){ // If update user
            User currentUser = findUserByUsername(currentUsername);
            if(currentUser == null){
                throw new UserNotFoundException("No user found with username: " + currentUsername);
            }

            User findNewUser = findUserByUsername(newUsername);
            if(findNewUser != null && !currentUser.getId().equals(findNewUser.getId())) {
                throw new UsernameExistException("The username already exists");
            }

            User emailUser = findUserByEmail(newEmail);
            if(emailUser != null && !currentUser.getId().equals(emailUser.getId())) {
                throw new EmailExistException("The emails already exists");
            }
            return currentUser;
        }else{ // If new user
            User userFind = findUserByUsername(newUsername);
            if(userFind != null){
                throw new UsernameExistException("The username already exist");
            }
            User emailFind = findUserByEmail(newEmail);
            if(emailFind != null){
                throw new EmailExistException("The email already exist");
            }
            return null;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getUsers() {
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public User findUserByUsername(String username) {
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public User findUserByEmail(String email) {
        return null;
    }


}
