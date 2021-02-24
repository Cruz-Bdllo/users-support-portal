package com.code.supportportal.service.user;

import com.code.supportportal.repository.UserRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserServiceImpTest {

    @Mock
    private UserRepository repo;

    @MockBean
    private PasswordEncoder passwordEncoder;

    private UserServiceImp userService = new UserServiceImp(repo, passwordEncoder);

    @Test
    @Disabled("Change private to public for test")
    void testGenerateUserId() {
        /*String userId = userService.generateUserId();
        System.out.println(userId);
        assertNotNull(userId);
        assertEquals(10, userId.length());*/
    }

    @Test
    @Disabled("Change private to public for test")
    void testGenerateRandomPassword() {
        /*String passwordRandom = userService.generatePassword();
        System.out.println(passwordRandom);
        assertNotNull(passwordRandom);
        assertEquals(10, passwordRandom.length());*/
    }

    @Test
    @Disabled("Change private to public for test")
    void testPasswordEncode() {
        /*String passwordRandom = userService.generatePassword();
        String encoded = userService.encodedPassword(passwordRandom);
        System.out.println(encoded.length());
        assertNotNull(encoded);*/
    }

}