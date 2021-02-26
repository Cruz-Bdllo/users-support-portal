package com.code.supportportal.listener;

import com.code.supportportal.service.login.LoginAttemptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;


@Component
public class AuthenticationLoginFailureListener {

    private LoginAttemptService attemptService;

    @Autowired
    public AuthenticationLoginFailureListener(LoginAttemptService attemptService) {
        this.attemptService = attemptService;
    }

    @EventListener
    public void onFailureLogin(AuthenticationFailureBadCredentialsEvent event) {
        Object failLogin = event.getAuthentication().getPrincipal();
        if(failLogin instanceof String) {
            String username = (String) event.getAuthentication().getPrincipal();
            attemptService.addUserToCache(username);
        } // end verify
    } // end listener

} // end class event listener
