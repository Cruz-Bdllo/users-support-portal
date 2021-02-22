package com.code.supportportal.resources;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserResource {

    @GetMapping
    public String showUser() {
        return "Hello everyone";
    } // endpoint

} // end users resource
