package com.example.demo.repositories;

import java.util.Optional;
import com.example.demo.models.Task;
import com.example.demo.models.User;
import org.springframework.data.jpa.repository.JpaRepository;



public interface TaskRepository extends JpaRepository<Task, Long> {
//    Optional<Task> findByNameAndPassword(String name, String password);
}
