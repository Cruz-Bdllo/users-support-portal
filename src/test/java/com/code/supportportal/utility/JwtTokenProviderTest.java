package com.code.supportportal.utility;

import com.code.supportportal.domain.User;
import com.code.supportportal.principal.UserDetailsPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenProviderTest {

    private User user;
    @Autowired
    private JwtTokenProvider providerBean;
    private String[] authoritiesTest = {"read", "delete"};


    @BeforeEach
    void setUp() {
        user = new User();
        user.setAuthorities(this.authoritiesTest);
        user.setUsername("codexy");
        user.setPassword("1234");
        user.setActive(true);
        user.setNotLocked(true);
    }

    @Test
    void testGetAuthoritiesFromUser() {
        JwtTokenProvider provider = new JwtTokenProvider();
        UserDetailsPrincipal principal = new UserDetailsPrincipal(user);
        String[] actual = provider.getClaimsForUser(principal);
        assertEquals(2, actual.length);
        assertEquals("codexy", principal.getUsername());
    }


} // end class test