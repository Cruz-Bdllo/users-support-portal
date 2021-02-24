package com.code.supportportal.controller;

import com.code.supportportal.exception.domain.ExceptionHandling;
import com.code.supportportal.exception.domain.UserNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController extends ExceptionHandling {

    @GetMapping("/home")
    public String showUser() {
        throw new UserNotFoundException("The user was not found");
    } // endpoint

} // end users resource
