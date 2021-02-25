package com.code.supportportal.service.user;

import com.code.supportportal.domain.User;
import com.code.supportportal.exception.domain.EmailExistException;
import com.code.supportportal.exception.domain.UsernameExistException;
import com.code.supportportal.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.mail.MessagingException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static com.code.supportportal.constant.UserServiceImpConstant.USER_DEFAULT_IMAGE_PATH;
import static com.code.supportportal.enums.Role.ROLE_USER;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
class UserServiceImpTest {
    @Mock
    private UserRepository repo;
    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImp serviceImp;


    private final String username = "codexy";
    private final String email = "cubj25@gmail.com";
    private final User userExpected = new User(1L, "0123456789", "juan", "badillo", "codexy", "123jb123bj12b312",
            "cubj25@gmail.com", null, null, new Date(), USER_DEFAULT_IMAGE_PATH, ROLE_USER.name(),
            ROLE_USER.getAuthorities(), true, true);
    private final List<User> usersExpected = Arrays.asList(
            userExpected,
            new User(2L, "0123456781", "jua", "adillo", "jcode", "123jb123bj12b312",
                    "cubj25@gmail.cm", null, null, new Date(), USER_DEFAULT_IMAGE_PATH, ROLE_USER.name(),
                    ROLE_USER.getAuthorities(), true, true),
            new User(3L, "0123456782", "jun", "bdillo", "codi", "123jb123bj12b312",
                    "cubj25@gmail.co", null, null, new Date(), USER_DEFAULT_IMAGE_PATH, ROLE_USER.name(),
                    ROLE_USER.getAuthorities(), true, true)
    );

    @Test
    void testRegister() throws MessagingException {
        User userRequest = new User();
        userRequest.setFirstName(userExpected.getFirstName());
        userRequest.setLastName(userExpected.getLastName());
        userRequest.setUsername(userExpected.getUsername());
        userRequest.setEmail(userExpected.getEmail());

        User userActual = serviceImp.register(userRequest.getFirstName(), userRequest.getLastName(),
                userRequest.getUsername(), userRequest.getEmail());


        assertNotNull(userActual);
        assertNotNull(userActual.getUserId());
        assertTrue(userActual.isActive());
        assertTrue(userActual.isNotLocked());

        assertEquals(userRequest.getFirstName(), userActual.getFirstName());
        assertEquals(userRequest.getLastName(), userActual.getLastName());

    }

    @Test
    void testRegisterWithIfUsernameAlreadyExists() {

        User userRequest = new User();
        userRequest.setFirstName(userExpected.getFirstName());
        userRequest.setLastName(userExpected.getLastName());
        userRequest.setUsername(userExpected.getUsername());
        userRequest.setEmail(userExpected.getEmail());

        when(repo.findUserByUsername(username)).thenReturn(Optional.of(userExpected));
        assertThrows(UsernameExistException.class, () -> serviceImp.register(userRequest.getFirstName(), userRequest.getLastName(),
                userRequest.getUsername(), userRequest.getEmail()));

    }

    @Test
    void testRegisterWithIfEmailAlreadyExists() {

        User userRequest = new User();
        userRequest.setFirstName(userExpected.getFirstName());
        userRequest.setLastName(userExpected.getLastName());
        userRequest.setUsername(userExpected.getUsername());
        userRequest.setEmail(userExpected.getEmail());

        when(repo.findUserByEmail(email)).thenReturn(userExpected);
        assertThrows(EmailExistException.class, () -> serviceImp.register(userRequest.getFirstName(), userRequest.getLastName(),
                userRequest.getUsername(), userRequest.getEmail()));

    }

    @Test
    void testGetUsers() {
        when(repo.findAll()).thenReturn(usersExpected);
        List<User> usersActual = serviceImp.getUsers();
        assertNotNull(usersActual);
        assertEquals(usersExpected.size(), usersActual.size());
    }

    @Test
    void testFindUserByUsername() {
        when(repo.findUserByUsername(username)).thenReturn(Optional.of(userExpected));
        User userActual = serviceImp.findUserByUsername(username);
        System.out.println(userActual.getUsername());
        assertNotNull(userActual);
        assertEquals(username, userActual.getUsername());
    }

    @Test
    void testFindUserByEmail() {
        when(repo.findUserByEmail(email)).thenReturn(userExpected);
        User userActual = serviceImp.findUserByEmail(email);
        assertNotNull(userActual);
        assertEquals(email, userActual.getEmail());
    }
}