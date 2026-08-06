package com.kumbhseva.demo.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.kumbhseva.demo.entity.User;
import com.kumbhseva.demo.repository.UserRepository;

@RestController
@CrossOrigin(
        origins = {"http://127.0.0.1:5500", "http://localhost:5500"},
        allowCredentials = "true"
)
public class AuthController {

    @Autowired
    private UserRepository repo;

    /* ================= REGISTER ================= */
    @PostMapping("/register")
    public String register(@RequestBody User user) {

        if(user.getEmail() == null || user.getPassword() == null){
            return "Invalid data";
        }

        if(repo.findByEmail(user.getEmail()) != null){
            return "User already exists";
        }

        repo.save(user);
        return "Registered successfully";
    }

    /* ================= LOGIN ================= */
    @PostMapping("/login")
    public String login(@RequestBody User user, HttpSession session) {

        User existing = repo.findByEmail(user.getEmail());

        if(existing != null && existing.getPassword().equals(user.getPassword())){

            session.setAttribute("user", existing.getEmail()); // store only email
            return "Login Success";  // ✅ match frontend
        }

        return "Invalid Credentials";
    }

    /* ================= CHECK SESSION ================= */
    @GetMapping("/check-session")
    public String checkSession(HttpSession session) {

        if(session.getAttribute("user") != null){
            return "logged-in";   // ✅ match frontend
        }

        return "not-logged-in";
    }

    /* ================= LOGOUT ================= */
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "Logged out";
    }
}