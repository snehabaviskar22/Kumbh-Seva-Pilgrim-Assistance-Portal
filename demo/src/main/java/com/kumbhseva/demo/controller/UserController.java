package com.kumbhseva.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.kumbhseva.demo.entity.User;
import com.kumbhseva.demo.repository.UserRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping({"/api/users", "/users"})
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public List<Map<String, Object>> getAllUsers(@RequestParam(required = false) String role) {
        List<User> list;
        if (role != null && !role.trim().isEmpty()) {
            list = userRepository.findByRole(role.trim().toUpperCase());
        } else {
            list = userRepository.findAll();
        }

        return list.stream().map(u -> {
            Map<String, Object> m = new HashMap<>();
            m.put("id", u.getId());
            m.put("name", u.getName());
            m.put("email", u.getEmail());
            m.put("phone", u.getPhone());
            m.put("role", u.getRole() != null ? u.getRole() : "PILGRIM");
            m.put("status", u.getStatus() != null ? u.getStatus() : "APPROVED");
            m.put("assignedZone", u.getAssignedZone() != null ? u.getAssignedZone() : "General");
            m.put("createdAt", u.getCreatedAt());
            return m;
        }).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public Map<String, Object> getUserById(@PathVariable Long id) {
        User u = userRepository.findById(id).orElse(null);
        if (u == null) return null;
        Map<String, Object> m = new HashMap<>();
        m.put("id", u.getId());
        m.put("name", u.getName());
        m.put("email", u.getEmail());
        m.put("phone", u.getPhone());
        m.put("role", u.getRole());
        m.put("status", u.getStatus());
        m.put("assignedZone", u.getAssignedZone());
        m.put("createdAt", u.getCreatedAt());
        return m;
    }

    @PutMapping("/{id}/status")
    public Map<String, Object> updateUserStatus(
            @PathVariable Long id,
            @RequestParam(required = false) String status,
            @RequestBody(required = false) Map<String, String> body) {

        String targetStatus = status;
        if (targetStatus == null && body != null) {
            targetStatus = body.get("status");
        }

        User u = userRepository.findById(id).orElse(null);
        Map<String, Object> res = new HashMap<>();
        if (u != null && targetStatus != null) {
            u.setStatus(targetStatus.toUpperCase());
            userRepository.save(u);
            res.put("status", "success");
            res.put("message", "User status updated to " + targetStatus.toUpperCase());
            res.put("userId", u.getId());
            res.put("newStatus", u.getStatus());
        } else {
            res.put("status", "error");
            res.put("message", "User not found or status missing");
        }
        return res;
    }

    @PutMapping("/{id}/role")
    public Map<String, Object> updateUserRole(
            @PathVariable Long id,
            @RequestParam(required = false) String role,
            @RequestBody(required = false) Map<String, String> body) {

        String targetRole = role;
        if (targetRole == null && body != null) {
            targetRole = body.get("role");
        }

        User u = userRepository.findById(id).orElse(null);
        Map<String, Object> res = new HashMap<>();
        if (u != null && targetRole != null) {
            u.setRole(targetRole.toUpperCase());
            userRepository.save(u);
            res.put("status", "success");
            res.put("message", "User role updated to " + targetRole.toUpperCase());
            res.put("userId", u.getId());
            res.put("newRole", u.getRole());
        } else {
            res.put("status", "error");
            res.put("message", "User not found or role missing");
        }
        return res;
    }

    @DeleteMapping("/{id}")
    public Map<String, Object> deleteUser(@PathVariable Long id) {
        Map<String, Object> res = new HashMap<>();
        User u = userRepository.findById(id).orElse(null);
        if (u == null) {
            res.put("status", "error");
            res.put("message", "User not found");
            return res;
        }
        if ("ADMIN".equalsIgnoreCase(u.getRole()) && "admin@kumbhseva.org".equalsIgnoreCase(u.getEmail())) {
            res.put("status", "error");
            res.put("message", "Primary Administrator cannot be deleted");
            return res;
        }
        userRepository.deleteById(id);
        res.put("status", "success");
        res.put("message", "User deleted successfully");
        return res;
    }
}
