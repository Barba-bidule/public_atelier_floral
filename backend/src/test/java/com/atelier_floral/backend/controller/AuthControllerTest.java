package com.atelier_floral.backend.controller;

import com.atelier_floral.backend.dto.auth.AuthRequest;
import com.atelier_floral.backend.dto.auth.TokenValidationResponse;
import com.atelier_floral.backend.exception.ConflictException;
import com.atelier_floral.backend.exception.NoContentException;
import com.atelier_floral.backend.model.User;
import com.atelier_floral.backend.service.CustomUserDetailsService;
import com.atelier_floral.backend.service.UserService;
import com.atelier_floral.backend.utils.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @Mock
    private UserService userService;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthController authController;

    @Test
    void loginAuthenticatesUserAndReturnsGeneratedToken() {
        AuthRequest request = AuthRequest.builder()
                .username("florist")
                .password("secret")
                .build();
        User user = User.builder().login("florist").build();
        when(userService.findUserByLogin("florist")).thenReturn(user);
        when(jwtUtil.generateToken(user)).thenReturn("jwt-token");

        ResponseEntity<String> response = authController.login(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("jwt-token", response.getBody());
        verify(authenticationManager).authenticate(
                new UsernamePasswordAuthenticationToken("florist", "secret"));
        verify(userService).findUserByLogin("florist");
        verify(jwtUtil).generateToken(user);
    }

    @Test
    void loginPropagatesAuthenticationFailure() {
        AuthRequest request = AuthRequest.builder()
                .username("florist")
                .password("wrong")
                .build();
        when(authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken("florist", "wrong")))
                .thenThrow(new IllegalArgumentException("Invalid credentials"));

        org.junit.jupiter.api.Assertions.assertThrows(
                IllegalArgumentException.class, () -> authController.login(request));
    }

    @Test
    void registerReturnsGeneratedTokenWhenUserIsCreated() {
        User user = User.builder().login("florist").build();
        when(userService.saveUser("florist", "secret", "Ada", "Lovelace", "Garden Road"))
                .thenReturn(user);
        when(jwtUtil.generateToken(user)).thenReturn("jwt-token");

        ResponseEntity<String> response = authController.save(
                "florist", "secret", "Ada", "Lovelace", "Garden Road");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("jwt-token", response.getBody());
        verify(userService).saveUser("florist", "secret", "Ada", "Lovelace", "Garden Road");
    }

    @Test
    void registerReturnsNoContentWhenRequiredDetailsAreMissing() {
        when(userService.saveUser("florist", "", "Ada", "Lovelace", "Garden Road"))
                .thenThrow(new NoContentException("Please fill all the fields"));

        ResponseEntity<String> response = authController.save(
                "florist", "", "Ada", "Lovelace", "Garden Road");

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void registerReturnsConflictWhenLoginAlreadyExists() {
        when(userService.saveUser("florist", "secret", "Ada", "Lovelace", "Garden Road"))
                .thenThrow(new ConflictException("User with this login already exists"));

        ResponseEntity<String> response = authController.save(
                "florist", "secret", "Ada", "Lovelace", "Garden Road");

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void registerReturnsInternalServerErrorWhenAnUnexpectedErrorOccurs() {
        when(userService.saveUser("florist", "secret", "Ada", "Lovelace", "Garden Road"))
                .thenThrow(new IllegalStateException("Unexpected failure"));

        ResponseEntity<String> response = authController.save(
                "florist", "secret", "Ada", "Lovelace", "Garden Road");

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void validateTokenReturnsUserWhenTokenIsValid() {
        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername("florist")
                .password("hashed-password")
                .authorities("ROLE_USER")
                .build();
        User user = User.builder().login("florist").build();
        when(jwtUtil.extractUsername("jwt-token")).thenReturn("florist");
        when(userDetailsService.loadUserByUsername("florist")).thenReturn(userDetails);
        when(jwtUtil.validateToken("jwt-token", "florist")).thenReturn(true);
        when(userService.findUserByLogin("florist")).thenReturn(user);

        ResponseEntity<TokenValidationResponse> response =
                authController.validateToken("jwt-token");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isValid());
        assertEquals("florist", response.getBody().getUsername());
        assertSame(user, response.getBody().getUser());
    }

    @Test
    void validateTokenReturnsInvalidResponseWhenTokenDoesNotValidate() {
        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername("florist")
                .password("hashed-password")
                .authorities("ROLE_USER")
                .build();
        when(jwtUtil.extractUsername("jwt-token")).thenReturn("florist");
        when(userDetailsService.loadUserByUsername("florist")).thenReturn(userDetails);
        when(jwtUtil.validateToken("jwt-token", "florist")).thenReturn(false);

        ResponseEntity<TokenValidationResponse> response =
                authController.validateToken("jwt-token");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(response.getBody().isValid());
        assertEquals("florist", response.getBody().getUsername());
        assertNull(response.getBody().getUser());
    }

    @Test
    void validateTokenReturnsInvalidResponseWhenTokenCannotBeParsed() {
        when(jwtUtil.extractUsername("malformed-token"))
                .thenThrow(new IllegalArgumentException("Malformed token"));

        ResponseEntity<TokenValidationResponse> response =
                authController.validateToken("malformed-token");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(response.getBody().isValid());
        assertNull(response.getBody().getUsername());
        assertNull(response.getBody().getUser());
    }
}
