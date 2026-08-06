package com.kumbhseva.demo.controller;

import com.kumbhseva.demo.repository.LostItemRepository;
import com.kumbhseva.demo.repository.FoundItemRepository;
import com.kumbhseva.demo.repository.ClaimRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://127.0.0.1:5500")
public class DashboardController {

    @Autowired
    private LostItemRepository lostItemRepository;

    @Autowired
    private FoundItemRepository foundItemRepository;

    @Autowired
    private ClaimRepository claimRepository;

    @GetMapping("/api/dashboard")
    public Map<String, Object> getDashboardData() {

        Map<String, Object> data = new HashMap<>();

        data.put("lostCount", lostItemRepository.count());

        data.put("foundCount", foundItemRepository.count());

        data.put("returnedCount",
                claimRepository.countByStatus("Returned"));

        data.put("pendingCount",
                claimRepository.countByStatus("Pending"));

        return data;
    }
}
