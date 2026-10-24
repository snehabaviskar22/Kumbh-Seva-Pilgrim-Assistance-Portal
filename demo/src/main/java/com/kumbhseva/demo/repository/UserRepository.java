package com.kumbhseva.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.kumbhseva.demo.entity.User;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
    long countByRole(String role);
    long countByRoleAndStatus(String role, String status);
    List<User> findByRole(String role);
    List<User> findByStatus(String status);
}