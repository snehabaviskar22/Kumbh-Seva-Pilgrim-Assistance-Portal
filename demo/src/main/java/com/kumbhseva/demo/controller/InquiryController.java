package com.kumbhseva.demo.controller;

import com.kumbhseva.demo.entity.Inquiry;
import com.kumbhseva.demo.repository.InquiryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
public class InquiryController {

    @Autowired
    private InquiryRepository repo;

    @PostMapping({"/api/inquiries", "/contact"})
    public Map<String, Object> submitInquiry(@RequestBody Inquiry inquiry) {
        if (inquiry.getCreatedAt() == null) {
            inquiry.setCreatedAt(LocalDateTime.now());
        }
        if (inquiry.getStatus() == null) {
            inquiry.setStatus("New");
        }
        Inquiry saved = repo.save(inquiry);
        return Map.of(
            "status", "success",
            "message", "Inquiry received successfully. Our team will contact you shortly.",
            "id", saved.getId()
        );
    }

    @GetMapping("/api/inquiries")
    public List<Inquiry> getAllInquiries() {
        return repo.findAllByOrderByCreatedAtDesc();
    }

    @PutMapping("/api/inquiries/{id}/status")
    public Inquiry updateInquiryStatus(@PathVariable Long id, @RequestParam(required = false) String status, @RequestBody(required = false) Map<String, String> body) {
        Inquiry inquiry = repo.findById(id).orElse(null);
        if (inquiry != null) {
            String newStatus = status;
            if (newStatus == null && body != null) {
                newStatus = body.get("status");
            }
            if (newStatus != null) {
                inquiry.setStatus(newStatus);
            }
            return repo.save(inquiry);
        }
        return null;
    }
}
