package com.yazilimxyz.enterprise_ticket_system.config;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.yazilimxyz.enterprise_ticket_system.entities.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}