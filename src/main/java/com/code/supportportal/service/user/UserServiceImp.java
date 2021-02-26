package com.code.supportportal.service.user;

import com.code.supportportal.domain.User;
import com.code.supportportal.enums.Role;
import com.code.supportportal.exception.domain.EmailExistException;
import com.code.supportportal.exception.domain.EmailNotFoundException;
import com.code.supportportal.exception.domain.UserNotFoundException;
import com.code.supportportal.exception.domain.UsernameExistException;
import com.code.supportportal.repository.UserRepository;
import com.code.supportportal.service.email.EmailService;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.mail.MessagingException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;

import static com.code.supportportal.constant.FileConstant.*;
import static com.code.supportportal.constant.UserServiceImpConstant.*;
import static com.code.supportportal.enums.Role.ROLE_USER;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.apache.commons.lang3.StringUtils.EMPTY;

@Service
public class UserServiceImp implements UserService{


    private UserRepository userRepo;
    private BCryptPasswordEncoder passwordEncoder;
    private EmailService emailService;
    private final Logger LOGGER = LoggerFactory.getLogger(UserServiceImp.class);

    @Autowired
    public UserServiceImp(UserRepository userRepo,
                          BCryptPasswordEncoder passwordEncoder,
                          EmailService emailService) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    @Override
    public User register(String firstName, String lastName, String username, String email) throws MessagingException {
        validateUsernameAndEmail(EMPTY, username, email);

        User newUser = new User();
        String password = generatePassword();

        newUser.setUserId(generateUserId());
        newUser.setFirstName(firstName);
        newUser.setLastName(lastName);
        newUser.setUsername(username);
        newUser.setEmail(email);
        newUser.setPassword(encodedPassword(password));
        newUser.setJoinDate(new Date());
        newUser.setActive(true);
        newUser.setNotLocked(true);
        newUser.setRole(ROLE_USER.name());
        newUser.setAuthorities(ROLE_USER.getAuthorities());
        newUser.setProfileImageUrl(getTemporaryProfileImageUrl(username));
        LOGGER.info("Password: " + password);

        userRepo.save(newUser);
        // change for a better service
        // emailService.sendEmailWithNewPassword(newUser.getFirstName(), password, newUser.getEmail());
        return newUser;
    }

    public String encodedPassword(String password) {
        return passwordEncoder.encode(password);
    }

    private String getTemporaryProfileImageUrl(String username) {
        // Get current domain context (ie: https://domain or http://localhost:8081)
        return ServletUriComponentsBuilder.fromCurrentContextPath().path(DEFAULT_USER_IMAGE_PATH+username).toUriString();
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

    @Override
    public User addNewUser(String firstName, String lastName, String username, String email,
                           String role, boolean isNonLocked, boolean isActive, MultipartFile profileImage) throws IOException {
        validateUsernameAndEmail(EMPTY, username, email);
        User user = new User();
        String password = generatePassword();
        String userId = generateUserId();
        user.setUserId(userId);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(encodedPassword(password));
        user.setActive(isActive);
        user.setNotLocked(isNonLocked);
        user.setJoinDate(new Date());
        user.setRole(getRoleEnumName(role).name());
        user.setAuthorities(getRoleEnumName(role).getAuthorities());
        user.setProfileImageUrl(getTemporaryProfileImageUrl(username));
        userRepo.save(user);

        saveProfileImageInDirectory(user, profileImage);
        return user;
    }

    private void saveProfileImageInDirectory(User user, MultipartFile profileImage) throws IOException {
        if(profileImage != null) {
            // Get current folder of user (user.home/supportportal/user/{myUsername})
            Path userFolder = Paths.get(USER_FOLDER + user.getUsername()).toAbsolutePath().normalize();

            // Validate if exists
            if(!Files.exists(userFolder)){
                // if not exists then we created
                Files.createDirectories(userFolder);
                LOGGER.info(DIRECTORY_CREATED);
            }

            // Delete any image into directory (i.e. user.home/supportportal/user/{myUsername}/myusername.jpg)
            Files.deleteIfExists(Paths.get(userFolder + user.getUsername() + DOT + JPG_EXTENSION));

            // Storage the image in the path given
            Files.copy(profileImage.getInputStream(),
                    userFolder.resolve(user.getUsername() + DOT + JPG_EXTENSION),
                    REPLACE_EXISTING);
            user.setProfileImageUrl(setProfileImageUrl(user.getUsername()));
            userRepo.save(user);
            LOGGER.info(FILE_SAVED_IN_FILE_SYSTEM + profileImage.getOriginalFilename());

        } // end validation
    } // end save in directory

    private String setProfileImageUrl(String username) {
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(USER_IMAGE_PATH+ username+FORWARD_SLASH + username + DOT+JPG_EXTENSION).toUriString();
    }

    private Role getRoleEnumName(String role) {
        return Role.valueOf(role.toUpperCase());
    }

    @Override
    public User updateUser(String currentUsername, String newFirstName, String newLastName, String newUsername,
                           String newEmail, String role, boolean isNonLocked, boolean isActive, MultipartFile profileImage) throws IOException {
        User userCurrent = validateUsernameAndEmail(currentUsername, newUsername, newEmail);
        userCurrent.setFirstName(newFirstName);
        userCurrent.setLastName(newLastName);
        userCurrent.setUsername(newUsername);
        userCurrent.setEmail(newEmail);
        userCurrent.setRole(getRoleEnumName(role).name());
        userCurrent.setAuthorities(getRoleEnumName(role).getAuthorities());
        userCurrent.setNotLocked(isNonLocked);
        userCurrent.setActive(isActive);
        userRepo.save(userCurrent);
        saveProfileImageInDirectory(userCurrent, profileImage);

        return userCurrent;
    }

    @Override
    public void deleteUser(Long id) {
        userRepo.deleteById(id);
    }

    @Override
    public void resetPassword(String email) {
        User user = userRepo.findUserByEmail(email);
        if(user == null){
            throw new EmailNotFoundException(String.format(EMAIL_NOT_FOUND, email));
        }
        // Generate new password
        String password = generatePassword();
        LOGGER.info("The new password for email " + email + " is: " + password);
        // Save new Password
        user.setPassword(encodedPassword(password));
        userRepo.save(user);
        // send by email
        // emailService.sendEmailWithNewPassword(user.getFirstName(), password, user.getEmail());
    }

    @Override
    public User updateProfileImage(String username, MultipartFile profileImage) throws IOException {
        User user = validateUsernameAndEmail(username, null, null);
        saveProfileImageInDirectory(user, profileImage);
        return user;
    }
}
