package com.kumbhseva.demo.controller;

import com.kumbhseva.demo.entity.FoundItem;
import com.kumbhseva.demo.entity.LostItem;
import com.kumbhseva.demo.repository.LostItemRepository;
import com.kumbhseva.demo.repository.FoundItemRepository;
import com.kumbhseva.demo.repository.ClaimRepository;
import com.kumbhseva.demo.repository.InquiryRepository;
import com.kumbhseva.demo.repository.MissingPersonRepository;
import com.kumbhseva.demo.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class DashboardController {

    @Autowired
    private LostItemRepository lostItemRepository;

    @Autowired
    private FoundItemRepository foundItemRepository;

    @Autowired
    private ClaimRepository claimRepository;

    @Autowired
    private InquiryRepository inquiryRepository;

    @Autowired
    private MissingPersonRepository missingPersonRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/api/dashboard")
    public Map<String, Object> getDashboardData() {

        Map<String, Object> data = new HashMap<>();

        long lostCount = lostItemRepository.count();
        long foundCount = foundItemRepository.count();
        long returnedClaims = claimRepository.countByStatus("Returned") + claimRepository.countByStatus("Approved");
        long pendingClaims = claimRepository.countByStatus("Pending");
        long inquiries = inquiryRepository.count();
        long missingPersons = missingPersonRepository.count();

        // User & Volunteer metrics
        long totalUsers = userRepository.count();
        long volunteerCount = userRepository.countByRole("VOLUNTEER");
        long pilgrimCount = userRepository.countByRole("PILGRIM");
        long adminCount = userRepository.countByRole("ADMIN");
        long pendingVolunteers = userRepository.countByRoleAndStatus("VOLUNTEER", "PENDING");

        data.put("lostCount", lostCount);
        data.put("foundCount", foundCount);
        data.put("returnedCount", returnedClaims);
        data.put("pendingCount", pendingClaims);
        data.put("inquiryCount", inquiries);
        data.put("missingPersonsCount", missingPersons);

        data.put("totalUsers", totalUsers);
        data.put("volunteerCount", volunteerCount);
        data.put("pilgrimCount", pilgrimCount);
        data.put("adminCount", adminCount);
        data.put("pendingVolunteers", pendingVolunteers);

        // Fetch recent items for the live feed
        List<Map<String, Object>> recentList = new ArrayList<>();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MMM dd");

        List<LostItem> lostItems = lostItemRepository.findAll();
        for (LostItem item : lostItems) {
            Map<String, Object> m = new HashMap<>();
            m.put("id", item.getId());
            m.put("ref", "#LF" + String.format("%03d", item.getId()));
            m.put("type", "lost");
            m.put("name", item.getItemName());
            m.put("category", item.getCategory());
            m.put("location", item.getLocation());
            m.put("date", item.getDateTimeLost() != null ? item.getDateTimeLost().format(fmt) : "");
            m.put("status", item.getStatus() != null ? item.getStatus() : "Lost");
            m.put("imageUrl", item.getImageUrl());
            recentList.add(m);
        }

        List<FoundItem> foundItems = foundItemRepository.findAll();
        for (FoundItem item : foundItems) {
            Map<String, Object> m = new HashMap<>();
            m.put("id", item.getId());
            m.put("ref", "#FD" + String.format("%03d", item.getId()));
            m.put("type", "found");
            m.put("name", item.getItemName());
            m.put("category", item.getCategory());
            m.put("location", item.getLocationFound());
            m.put("date", item.getDatetimeFound() != null ? item.getDatetimeFound().format(fmt) : "");
            m.put("status", item.getStatus() != null ? item.getStatus() : "Found");
            m.put("imageUrl", item.getImageUrl());
            recentList.add(m);
        }

        // Sort by ID descending (most recent first)
        recentList.sort((a, b) -> Long.compare((Long) b.get("id"), (Long) a.get("id")));

        data.put("recentItems", recentList);

        return data;
    }
}
