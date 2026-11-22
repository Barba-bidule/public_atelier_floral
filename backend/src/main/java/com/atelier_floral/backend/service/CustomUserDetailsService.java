package com.atelier_floral.backend.service;
import com.atelier_floral.backend.model.User;
import com.atelier_floral.backend.repository.UserRepository;
import jakarta.annotation.Resource;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Resource
    private UserRepository userRepository;


    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        User user = userRepository.findByLogin(login).orElse(null);
        if (user == null) throw new UsernameNotFoundException("User not found");

        return new org.springframework.security.core.userdetails.User(
                user.getLogin(),
                user.getHashedPassword(),
                Collections.singletonList(user::getRole)
        );
    }
}
