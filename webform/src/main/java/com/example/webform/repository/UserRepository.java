package com.example.webform.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.webform.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    
}