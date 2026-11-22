package com.atelier_floral.backend.controller;

import com.atelier_floral.backend.exception.NoContentException;
import com.atelier_floral.backend.model.User;
import com.atelier_floral.backend.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/service/users")
public class UserController {

    @Resource
    private UserService userService;


    @GetMapping("/one")
    public ResponseEntity<User> findByLogin(@RequestParam("login") String login) {
        try { return ResponseEntity.ok(userService.findUserByLogin(login)); }
        catch (NoContentException exception) { return ResponseEntity.noContent().build(); }
        catch (Exception exception) { return ResponseEntity.internalServerError().build(); }
    }

}
