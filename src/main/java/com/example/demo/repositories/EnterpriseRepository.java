package com.example.demo.repositories;

import com.example.demo.models.Enterprise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
@Repository
public interface EnterpriseRepository extends JpaRepository<Enterprise, String> {
    Optional<Enterprise> findByName(String name);
}