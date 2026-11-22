package com.atelier_floral.backend.service;

import com.atelier_floral.backend.exception.ConflictException;
import com.atelier_floral.backend.exception.NoContentException;
import com.atelier_floral.backend.model.User;
import com.atelier_floral.backend.repository.UserRepository;
import com.atelier_floral.backend.enums.RoleEnum;
import jakarta.annotation.Resource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Resource
    private PasswordEncoder passwordEncoder;
    @Resource
    private UserRepository userRepository;

    public User findUserByLogin(String login) {
        return userRepository.findByLogin(login).orElse(null);
    }

    public User saveUser(String login, String password, String firstname, String lastname, String address) {
        if(login == null || password == null || firstname == null || lastname == null || address == null) throw new NoContentException("Please fill all the fields");
        if(login.isEmpty() || password.isEmpty() || firstname.isEmpty() || lastname.isEmpty() || address.isEmpty()) throw new NoContentException("Please fill all the fields");
        if(userRepository.findByLogin(login).isPresent()) throw new ConflictException("User with this login already exists");
        return userRepository.save(
               User.builder()
                       .login(login)
                       .hashedPassword(passwordEncoder.encode(password))
                       .firstname(firstname)
                       .lastname(lastname)
                       .role(RoleEnum.ROLE_USER.name())
                       .address(address)
                       .build()
        );
    }

    public List<User> findAll() {
        List<User> users = userRepository.findAll();
        if(users.isEmpty()) throw new NoContentException("No users found");
        return users;
    }

}
