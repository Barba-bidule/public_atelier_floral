package com.atelier_floral.backend.controller;

import com.atelier_floral.backend.exception.NoContentException;
import com.atelier_floral.backend.model.User;
import com.atelier_floral.backend.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Test
    void findByLoginReturnsUserWhenFound() {
        User user = User.builder().login("florist").firstname("Ada").build();
        when(userService.findUserByLogin("florist")).thenReturn(user);

        ResponseEntity<User> response = userController.findByLogin("florist");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(user, response.getBody());
        verify(userService).findUserByLogin("florist");
    }

    @Test
    void findByLoginReturnsNoContentWhenServiceThrowsNoContentException() {
        when(userService.findUserByLogin("missing"))
                .thenThrow(new NoContentException("User not found"));

        ResponseEntity<User> response = userController.findByLogin("missing");

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void findByLoginReturnsInternalServerErrorWhenServiceThrowsUnexpectedException() {
        when(userService.findUserByLogin("florist"))
                .thenThrow(new IllegalStateException("Unexpected failure"));

        ResponseEntity<User> response = userController.findByLogin("florist");

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void findAllReturnsUsersWhenAvailable() {
        List<User> users = List.of(User.builder().login("florist").build());
        when(userService.findAll()).thenReturn(users);

        ResponseEntity<List<User>> response = userController.findAll();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(users, response.getBody());
        verify(userService).findAll();
    }

    @Test
    void findAllReturnsNoContentWhenServiceThrowsNoContentException() {
        when(userService.findAll()).thenThrow(new NoContentException("No users found"));

        ResponseEntity<List<User>> response = userController.findAll();

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void findAllReturnsInternalServerErrorWhenServiceThrowsUnexpectedException() {
        when(userService.findAll()).thenThrow(new IllegalStateException("Unexpected failure"));

        ResponseEntity<List<User>> response = userController.findAll();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
    }
}
