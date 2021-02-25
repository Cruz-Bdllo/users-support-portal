package com.code.supportportal.controller;

import com.code.supportportal.constant.FileConstant;
import com.code.supportportal.constant.SecurityConstant;
import com.code.supportportal.domain.User;
import com.code.supportportal.exception.domain.ExceptionHandling;
import com.code.supportportal.principal.UserDetailsPrincipal;
import com.code.supportportal.service.user.UserService;
import com.code.supportportal.utility.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static com.code.supportportal.constant.FileConstant.*;
import static com.code.supportportal.constant.SecurityConstant.JWT_TOKEN_HEADER;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;

@RestController
@RequestMapping("/user")
public class UserController extends ExceptionHandling {
    private static final String EMAIL_SENT = "An email was send to: ";
    // pass: zxOydHDsKZ
    private UserService userService;
    private AuthenticationManager manager;
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    public UserController(UserService userService, AuthenticationManager manager,
                          JwtTokenProvider jwtTokenProvider) {
        this.userService = userService;
        this.manager = manager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    /* ~ ENDPOINTS
    ---------------------------------------------------- */
    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user) throws MessagingException {
        User newUser = userService
                .register(user.getFirstName(), user.getLastName(), user.getUsername(), user.getEmail());
        return new ResponseEntity<>(newUser, CREATED);
    } // endpoint

    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestBody User user){
        authenticate(user.getUsername(), user.getPassword());

        User loginUser = userService.findUserByUsername(user.getUsername());
        UserDetailsPrincipal principal = new UserDetailsPrincipal(loginUser);

        HttpHeaders jwtToken = getHeader(principal);
        return new ResponseEntity<>(loginUser, jwtToken, OK);
    }

    @PostMapping("/add")
    public ResponseEntity<User> addUser(@RequestParam String firstName, @RequestParam String lastName,
                                        @RequestParam String username, @RequestParam String email,
                                        @RequestParam String role, @RequestParam String isNonLocked,
                                        @RequestParam String isActive,
                                        @RequestParam(required = false) MultipartFile profileImage) throws IOException {
        User newUser = userService.addNewUser(firstName, lastName, username, email, role,
                Boolean.parseBoolean(isNonLocked), Boolean.parseBoolean(isActive), profileImage);
        return new ResponseEntity<>(newUser, CREATED);
    } // end method


    @PostMapping("/update")
    public ResponseEntity<User> update(@RequestParam String currentUser, @RequestParam String firstName,
                                       @RequestParam String lastName, @RequestParam String username,
                                       @RequestParam String email, @RequestParam String role,
                                       @RequestParam String isNonLocked, @RequestParam String isActive,
                                       @RequestParam(required = false) MultipartFile profileImage) throws IOException {
        User updatedUser = userService.updateUser(currentUser, firstName, lastName, username, email, role,
                Boolean.parseBoolean(isNonLocked), Boolean.parseBoolean(isActive), profileImage);
        return new ResponseEntity<>(updatedUser, OK);
    } // end method

    @GetMapping("/find/{username}")
    public ResponseEntity<User> findUsername(@PathVariable String username) {
        User user = userService.findUserByUsername(username);
        return new ResponseEntity<>(user, OK);
    }

    @GetMapping("/list")
    public ResponseEntity<List<User>> getUsers() {
        List<User> users = userService.getUsers();
        return new ResponseEntity<>(users, OK);
    }

    @GetMapping("/reset-password/{email}")
    public ResponseEntity<String> resetPassword(@PathVariable String email){
        userService.resetPassword(email);
        return new ResponseEntity<>(EMAIL_SENT + email, OK);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyAuthority('user:delete')")
    public ResponseEntity<String> delete(@PathVariable long id) {
        userService.deleteUser(id);
        return new ResponseEntity<>("The user was deleted", NO_CONTENT);
    }

    @PostMapping("/update-profile-image")
    public ResponseEntity<User> updateProfileImage(@RequestParam String username,
                                                   @RequestParam MultipartFile profileImage) throws IOException {
        User userUpdated = userService.updateProfileImage(username, profileImage);
        return new ResponseEntity<>(userUpdated, OK);
    }

    @GetMapping(path = "/image/{username}/{fileName}", produces = IMAGE_JPEG_VALUE)
    public byte[] getProfileImage(@PathVariable String username, @PathVariable String fileName) throws IOException {
        return Files.readAllBytes(Paths.get(USER_FOLDER + username + FORWARD_SLASH + fileName));
    }

    @GetMapping(path = "/image/profile/{username}", produces = IMAGE_JPEG_VALUE)
    public byte[] getProfileImage(@PathVariable String username) throws IOException {
        URL urlRoboBash = new URL(TEMP_PROFILE_IMAGE_BASE_URL + username);
        ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
        try(InputStream inputStream= urlRoboBash.openStream()){
            int bytesRead;
            byte[] chunck = new byte[1024];
            while((bytesRead = inputStream.read(chunck)) > 0){
                arrayOutputStream.write(chunck, 0, bytesRead);
            }
        }
        return arrayOutputStream.toByteArray();
    }


    /* ~ OTHER METHODS
    ---------------------------------------------------- */
    private HttpHeaders getHeader(UserDetailsPrincipal principal) {
        String token = jwtTokenProvider.createToken(principal);
        HttpHeaders tokenHeader = new HttpHeaders();
        tokenHeader.add(JWT_TOKEN_HEADER, token);
        return tokenHeader;
    }

    private void authenticate(String username, String password) {
        manager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    }

} // end users resource
