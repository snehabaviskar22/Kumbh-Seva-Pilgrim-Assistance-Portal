package com.kumbhseva.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.kumbhseva.demo.entity.Claim;
import com.kumbhseva.demo.entity.FoundItem;
import com.kumbhseva.demo.entity.LostItem;
import com.kumbhseva.demo.repository.ClaimRepository;
import com.kumbhseva.demo.repository.FoundItemRepository;
import com.kumbhseva.demo.repository.LostItemRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping({"/claims", "/api/claims"})
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class ClaimController {

    @Autowired
    private ClaimRepository repo;

    @Autowired
    private FoundItemRepository foundItemRepo;

    @Autowired
    private LostItemRepository lostItemRepo;

    @PostMapping
    public Claim submitClaim(@RequestBody Claim claim) {
        if (claim.getStatus() == null || claim.getStatus().trim().isEmpty()) {
            claim.setStatus("Pending");
        }
        Claim saved = repo.save(claim);

        // Update corresponding found item to "Claimed" if currently "Found"
        if (claim.getItemId() != null) {
            foundItemRepo.findById(claim.getItemId()).ifPresent(foundItem -> {
                if ("Found".equalsIgnoreCase(foundItem.getStatus())) {
                    foundItem.setStatus("Claimed");
                    foundItemRepo.save(foundItem);
                }
            });
        }
        return saved;
    }

    @GetMapping
    public List<Claim> getAllClaims() {
        return repo.findAll();
    }

    @GetMapping("/{id}")
    public Claim getClaimById(@PathVariable Long id) {
        return repo.findById(id).orElse(null);
    }

    @PutMapping("/{id}/status")
    public Claim updateClaimStatus(
            @PathVariable Long id,
            @RequestParam(required = false) String status,
            @RequestBody(required = false) Map<String, String> body) {

        Claim claim = repo.findById(id).orElse(null);
        if (claim == null) {
            return null;
        }

        String targetStatus = status;
        if (targetStatus == null && body != null) {
            targetStatus = body.get("status");
        }
        if (targetStatus != null) {
            claim.setStatus(targetStatus);
            repo.save(claim);

            // If claim is approved or returned, mark the item as returned
            if ("Approved".equalsIgnoreCase(targetStatus) || "Returned".equalsIgnoreCase(targetStatus)) {
                if (claim.getItemId() != null) {
                    foundItemRepo.findById(claim.getItemId()).ifPresent(item -> {
                        item.setStatus("Returned");
                        foundItemRepo.save(item);
                    });
                    lostItemRepo.findById(claim.getItemId()).ifPresent(item -> {
                        item.setStatus("Returned");
                        lostItemRepo.save(item);
                    });
                }
            } else if ("Rejected".equalsIgnoreCase(targetStatus)) {
                // If claim is rejected, release the item back to Found
                if (claim.getItemId() != null) {
                    foundItemRepo.findById(claim.getItemId()).ifPresent(item -> {
                        if ("Claimed".equalsIgnoreCase(item.getStatus())) {
                            item.setStatus("Found");
                            foundItemRepo.save(item);
                        }
                    });
                }
            }
        }
        return claim;
    }

    @DeleteMapping("/{id}")
    public Map<String, String> deleteClaim(@PathVariable Long id) {
        Map<String, String> res = new HashMap<>();
        if (repo.existsById(id)) {
            repo.deleteById(id);
            res.put("status", "success");
            res.put("message", "Claim deleted successfully");
        } else {
            res.put("status", "error");
            res.put("message", "Claim not found");
        }
        return res;
    }
}