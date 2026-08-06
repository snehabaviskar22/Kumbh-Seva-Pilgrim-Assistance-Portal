package com.kumbhseva.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.kumbhseva.demo.entity.Claim;
import com.kumbhseva.demo.repository.ClaimRepository;

@RestController
@RequestMapping("/claims")
@CrossOrigin(origins = "*")
public class ClaimController {

    @Autowired
    private ClaimRepository repo;

    @PostMapping
    public Claim submitClaim(@RequestBody Claim claim) {
        return repo.save(claim);
    }
}