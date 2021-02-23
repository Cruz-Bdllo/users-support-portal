package com.code.supportportal.utility;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.code.supportportal.domain.User;
import com.code.supportportal.principal.UserDetailsPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;


import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@ActiveProfiles("test")
class JwtTokenProviderTest {

    @Autowired
    private JwtTokenProvider providerBean;
    private User user;
    private final String[] authoritiesTest = {"read", "delete"};
    private String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJhdWQiOiJVc2VyIG1hbmFnZW1lbnQgcG9ydGFsIiwic3ViIjoiY29kZXh5IiwiaXNzIjoiQXNkZSBEZWxlZ2FjaW9uYWwiLCJleHAiOjE2MTQ0MDU2MDAsImlhdCI6MTYxNDA0NTMxNSwiYXV0aG9yaXRpZXMiOlsicmVhZCIsImRlbGV0ZSJdfQ.p2uFvB3-QZEiJXXX6gM8lkqPKGW6VwIeqSNq4ItfcOPB-uRdFRR22ej0eWQoXYJS3ggg_nRZkxacy3YacUuxhA";


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
    void testCreateToken() {
         UserDetailsPrincipal principal = new UserDetailsPrincipal(user);

        String tokenCreated = providerBean.createToken(principal);
        System.out.println(tokenCreated);
        assertNotNull(tokenCreated);
    }

    @Test
    void testGetAuthoritiesFromUser() {
        JwtTokenProvider provider = new JwtTokenProvider();
        UserDetailsPrincipal principal = new UserDetailsPrincipal(user);
        String[] actual = provider.getClaimsForUser(principal);
        assertEquals(2, actual.length);
        assertEquals("codexy", principal.getUsername());
    }

    @Test
    void testGetAuthorities() {
        Set<GrantedAuthority> actual = providerBean.getAuthorities(token);
        System.out.println(actual);
        assertNotNull(actual);
        assertEquals(2, actual.size());
    }

    @Test
    void testGetSubject() {
        String expected = "codexy";
        String actual = providerBean.getSubject(token);
        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    void testIsValidToken() {
        String username = "codexy";
        boolean isValid = providerBean.isTokenValid(username, token);
        assertTrue(isValid);
    }

    @Test
    void testIsNotValidTokenThrowException() {
        String username = "codexy";
        String tokenInvalid = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJhdWQiOiJVc2VyIG1hbmFnZW1lbnQgcG9ydGFsIiwic3ViIjoiY29kZXh5IiwiaXNzIjoiQXNkZSBEZWxlZ2FjaW9uYWwiLCJleHAiOjE2MTQ0MDU2MDAsImlhdCI6MTYxNDA0NTMxNSwiXV0aG9yaXRpZXMiOlsicmVhZCIsImRlbGV0ZSJdfQ.p2uFvB3-QZEiJXXX6gM8lkqPKGW6VwIeqSNq4ItfcOPB-uRdFRR22ej0eWQoXYJS3ggg_nRZkxacy3YacUuxhA";
        assertThrows(JWTVerificationException.class, () ->
                providerBean.isTokenValid(username, tokenInvalid));
    }

    @Test
    void testGetClaimsForToken() {
        String[] expectedClaims = {"read", "delete"};
        String[] actualClaims = providerBean.getClaimsForToken(token);
        // System.out.println(actualClaims.length);
        assertNotNull(actualClaims[0]);
        assertEquals(expectedClaims.length, actualClaims.length);
        assertEquals(expectedClaims[0], actualClaims[0]);
    }

} // end class test