package com.code.supportportal.service.user;

import com.code.supportportal.domain.User;
import com.code.supportportal.principal.UserDetailsPrincipal;
import com.code.supportportal.repository.UserRepository;
import com.code.supportportal.service.login.LoginAttemptService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
@Qualifier("myUserDetailsService")
public class UserDetailsServicePrincipal implements UserDetailsService {

    private UserRepository userRepository;
    private LoginAttemptService attemptService;
    private Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Autowired
    public UserDetailsServicePrincipal(UserRepository userRepository,
                                       LoginAttemptService attemptService) {
        this.userRepository = userRepository;
        this.attemptService = attemptService;
    } // end constructor

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = Optional.of(userRepository.findUserByUsername(username))
                .orElseThrow(() -> new UsernameNotFoundException("User not found by username: " + username));

        user.ifPresent(u -> {
            validateUserAttempt(u);
            u.setLastLoginDateDisplay(u.getLastLoginDate());
            u.setLastLoginDate(new Date());

            userRepository.save(u);
        });

        LOGGER.info("Returning found user by username");
        return user.map(UserDetailsPrincipal::new).get();
    }

    private void validateUserAttempt(User user) {
        if(user.isNotLocked()){
            if(attemptService.hasExceedMaxAttempts(user.getUsername())){
                user.setNotLocked(false);
            }else {
                user.setNotLocked(true);
            }
        }else{
            attemptService.deleteUserFromCache(user.getUsername());
        }
    } // end method

} // end class
