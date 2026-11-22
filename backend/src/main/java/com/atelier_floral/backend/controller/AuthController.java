package com.atelier_floral.backend.controller;

import com.atelier_floral.backend.dto.auth.AuthRequest;
import com.atelier_floral.backend.dto.auth.TokenValidationResponse;
import com.atelier_floral.backend.exception.ConflictException;
import com.atelier_floral.backend.exception.NoContentException;
import com.atelier_floral.backend.model.User;
import com.atelier_floral.backend.service.CustomUserDetailsService;
import com.atelier_floral.backend.service.UserService;
import com.atelier_floral.backend.utils.JwtUtil;
import jakarta.annotation.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Resource
    private AuthenticationManager authenticationManager;
    @Resource
    private CustomUserDetailsService userDetailsService;
    @Resource
    private UserService userService;
    @Resource
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        User user = userService.findUserByLogin(request.getUsername());
        return ResponseEntity.ok(jwtUtil.generateToken(user));
    }

    @PostMapping("/register")
    public ResponseEntity<String> save(@RequestParam("login") String login, @RequestParam("password") String password, @RequestParam("firstname") String firstname, @RequestParam("lastname") String lastname, @RequestParam("address") String address) {
        try {
            User userCreated = userService.saveUser(login, password, firstname, lastname, address);
            return ResponseEntity.ok(jwtUtil.generateToken(userCreated));
        }
        catch (NoContentException exception) { return ResponseEntity.noContent().build(); }
        catch (ConflictException exception) { return ResponseEntity.status(HttpStatus.CONFLICT).build(); }
        catch (Exception exception) { return ResponseEntity.internalServerError().build(); }
    }

    @GetMapping("/validate")
    public ResponseEntity<TokenValidationResponse> validateToken(@RequestParam("token") String token) {
        String username = null;
        boolean valid = false;

        try {
            username = jwtUtil.extractUsername(token);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            valid = jwtUtil.validateToken(token, userDetails.getUsername());
        }
        catch (Exception ignored) { }

        return ResponseEntity.ok(
                TokenValidationResponse.builder()
                    .valid(valid)
                    .username(username)
                    .user(!valid ? null : userService.findUserByLogin(username))
                    .build()
        );
    }

}
