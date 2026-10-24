package com.kumbhseva.demo.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.kumbhseva.demo.entity.User;
import com.kumbhseva.demo.repository.UserRepository;
import com.kumbhseva.demo.util.PasswordUtils;

import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin(
        originPatterns = "*",
        allowCredentials = "true"
)
public class AuthController {

    @Autowired
    private UserRepository repo;

    /* ================= REGISTER ================= */
    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody User user, HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        if (user.getEmail() == null || user.getEmail().trim().isEmpty() ||
            user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            response.put("status", "error");
            response.put("message", "Email and password are required");
            return response;
        }

        String email = user.getEmail().trim().toLowerCase();

        if (repo.findByEmail(email) != null) {
            response.put("status", "error");
            response.put("message", "An account already exists with this email address");
            return response;
        }

        String requestedRole = (user.getRole() != null) ? user.getRole().trim().toUpperCase() : "PILGRIM";

        // Security check: ordinary users cannot self-assign ADMIN role
        if ("ADMIN".equals(requestedRole)) {
            Object sessionRole = (session != null) ? session.getAttribute("role") : null;
            if (!"ADMIN".equals(sessionRole)) {
                response.put("status", "error");
                response.put("message", "Administrator privileges cannot be self-assigned. Defaulting to Pilgrim.");
                requestedRole = "PILGRIM";
            }
        }

        user.setEmail(email);
        user.setRole(requestedRole);

        // Volunteer accounts require administrator approval
        if ("VOLUNTEER".equals(requestedRole)) {
            user.setStatus("PENDING");
        } else {
            user.setStatus("APPROVED");
        }

        // Secure password hashing
        user.setPassword(PasswordUtils.hashPassword(user.getPassword().trim()));

        User saved = repo.save(user);

        response.put("status", "success");
        response.put("message", "Registered successfully");
        response.put("userId", saved.getId());
        response.put("email", saved.getEmail());
        response.put("name", saved.getName());
        response.put("role", saved.getRole());
        response.put("accountStatus", saved.getStatus());
        return response;
    }

    /* ================= LOGIN ================= */
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody User user, HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        if (user.getEmail() == null || user.getPassword() == null) {
            response.put("status", "error");
            response.put("message", "Email and password are required");
            return response;
        }

        String email = user.getEmail().trim().toLowerCase();
        User existing = repo.findByEmail(email);

        if (existing != null && PasswordUtils.verifyPassword(user.getPassword(), existing.getPassword())) {
            
            // Check Volunteer Approval Status
            if ("VOLUNTEER".equalsIgnoreCase(existing.getRole()) && "PENDING".equalsIgnoreCase(existing.getStatus())) {
                response.put("status", "pending_approval");
                response.put("message", "Your volunteer registration is pending Administrator approval. Please contact the Mela control room.");
                return response;
            }

            if ("REJECTED".equalsIgnoreCase(existing.getStatus())) {
                response.put("status", "error");
                response.put("message", "This account has been deactivated or rejected by Mela Administration.");
                return response;
            }

            // Upgrade legacy plaintext password to secure hash
            if (existing.getPassword() == null || !existing.getPassword().matches("^[a-fA-F0-9]{64}$")) {
                existing.setPassword(PasswordUtils.hashPassword(user.getPassword().trim()));
                repo.save(existing);
            }

            String role = existing.getRole() != null ? existing.getRole() : "PILGRIM";
            String name = existing.getName() != null ? existing.getName() : "Pilgrim";

            session.setAttribute("userId", existing.getId());
            session.setAttribute("user", existing.getEmail());
            session.setAttribute("role", role);
            session.setAttribute("name", name);

            response.put("status", "success");
            response.put("message", "Login Success");
            response.put("userId", existing.getId());
            response.put("email", existing.getEmail());
            response.put("name", name);
            response.put("role", role);
            response.put("accountStatus", existing.getStatus());
            return response;
        }

        response.put("status", "error");
        response.put("message", "Invalid email or password");
        return response;
    }

    /* ================= CHECK SESSION ================= */
    @GetMapping("/check-session")
    public Map<String, Object> checkSession(HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        Object user = session.getAttribute("user");
        if (user != null) {
            response.put("loggedIn", true);
            response.put("status", "logged-in");
            response.put("userId", session.getAttribute("userId"));
            response.put("email", user.toString());
            response.put("role", session.getAttribute("role") != null ? session.getAttribute("role").toString() : "PILGRIM");
            response.put("name", session.getAttribute("name") != null ? session.getAttribute("name").toString() : "Pilgrim");
            return response;
        }

        response.put("loggedIn", false);
        response.put("status", "not-logged-in");
        return response;
    }

    /* ================= LOGOUT ================= */
    @GetMapping("/logout")
    public Map<String, Object> logout(HttpSession session) {
        session.invalidate();
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Logged out");
        return response;
    }
}