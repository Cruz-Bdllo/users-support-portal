package com.code.supportportal.listener;

import com.code.supportportal.domain.User;
import com.code.supportportal.service.login.LoginAttemptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationLoginSuccessListener {

    private LoginAttemptService attemptService;

    @Autowired
    public AuthenticationLoginSuccessListener(LoginAttemptService attemptService) {
        this.attemptService = attemptService;
    }

    @EventListener
    public void onLoginSuccess(AuthenticationSuccessEvent event) {
        Object principal = event.getAuthentication().getPrincipal();
        if(principal instanceof User){
            User user = (User) event.getAuthentication().getPrincipal();
            attemptService.deleteUserFromCache(user.getUsername());
        }
    } // end listener

} // end class event listener
