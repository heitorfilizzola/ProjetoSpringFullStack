package com.example.demo.repositories;

import java.util.Optional;

import com.example.demo.models.User;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByNameAndPassword(String name, String password);
    Optional<User> findByName(String name);

    String name(@NotNull String name);
}
