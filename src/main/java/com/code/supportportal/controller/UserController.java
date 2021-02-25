package com.code.supportportal.controller;

import com.code.supportportal.constant.SecurityConstant;
import com.code.supportportal.domain.User;
import com.code.supportportal.exception.domain.ExceptionHandling;
import com.code.supportportal.principal.UserDetailsPrincipal;
import com.code.supportportal.service.user.UserService;
import com.code.supportportal.utility.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;

import static com.code.supportportal.constant.SecurityConstant.JWT_TOKEN_HEADER;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/user")
public class UserController extends ExceptionHandling {
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
