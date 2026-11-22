package com.atelier_floral.backend.repository;

import com.atelier_floral.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByLogin(String login);

}
