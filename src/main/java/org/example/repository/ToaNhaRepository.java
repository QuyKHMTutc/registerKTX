package org.example.repository;

import org.example.model.ToaNha;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ToaNhaRepository extends JpaRepository<ToaNha, Integer> {
    // Repository cho ToaNha
}