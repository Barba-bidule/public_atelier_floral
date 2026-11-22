package com.atelier_floral.backend.configuration;

import com.atelier_floral.backend.enums.RoleEnum;
import com.atelier_floral.backend.model.User;
import com.atelier_floral.backend.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer {

    private static final String DEFAULT_ADMIN_EMAIL = "admin.dev@gmail.com";
    private static final String DEFAULT_ADMIN_ADDRESS = "3 Pl. de l'Abreuvoir, 59131 Rousies";

    @Resource
    private UserRepository userRepository;
    @Resource
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    public void createDefaultAdmin() {
        if (userRepository.findByLogin(DEFAULT_ADMIN_EMAIL).isEmpty()) {
            userRepository.save(
                    User.builder()
                        .login(DEFAULT_ADMIN_EMAIL)
                        .hashedPassword(passwordEncoder.encode("dev_env_pw"))
                        .firstname("admin")
                        .lastname("admin")
                        .role(RoleEnum.ROLE_ADMIN.name())
                        .address("3 Pl. de l'Abreuvoir, 59131 Rousies")
                        .build()
            );

        }
    }

}
