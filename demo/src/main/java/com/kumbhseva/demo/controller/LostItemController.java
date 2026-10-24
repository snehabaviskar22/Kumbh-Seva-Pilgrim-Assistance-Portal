package com.kumbhseva.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.kumbhseva.demo.entity.Claim;
import com.kumbhseva.demo.entity.FoundItem;
import com.kumbhseva.demo.entity.LostItem;
import com.kumbhseva.demo.entity.MissingPerson;
import com.kumbhseva.demo.entity.User;
import com.kumbhseva.demo.repository.ClaimRepository;
import com.kumbhseva.demo.repository.FoundItemRepository;
import com.kumbhseva.demo.repository.LostItemRepository;
import com.kumbhseva.demo.repository.MissingPersonRepository;
import com.kumbhseva.demo.repository.UserRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class LostItemController {

    @Autowired
    private LostItemRepository repo;

    @Autowired
    private FoundItemRepository foundItemRepo;

    @Autowired
    private ClaimRepository claimRepo;

    @Autowired
    private MissingPersonRepository missingPersonRepo;

    @Autowired
    private UserRepository userRepo;

    private String getUploadDir() {
        String dir = Paths.get(System.getProperty("user.dir"), "uploads").toString() + File.separator;
        File f = new File(dir);
        if (!f.exists()) {
            f.mkdirs();
        }
        return dir;
    }
    
    @PostMapping("/report-lost")
    public LostItem createLostItem(
            @RequestParam String fullName,
            @RequestParam String phoneNumber,
            @RequestParam(required = false) String email,
            @RequestParam String itemName,
            @RequestParam String category,
            @RequestParam String description,
            @RequestParam String location,
            @RequestParam String dateTimeLost,
            @RequestParam(value = "userId", required = false) Long userId,
            @RequestParam(value = "itemPhoto", required = false) MultipartFile itemPhoto,
            @RequestParam(value = "personPhoto", required = false) MultipartFile personPhoto,

            @RequestParam(required = false) Integer age,
            @RequestParam(required = false) String height,
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) String physicalIdentification
    ) {

        LostItem item = new LostItem();

        item.setFullName(fullName);
        item.setPhoneNumber(phoneNumber);
        item.setEmail(email);
        item.setItemName(itemName);
        item.setCategory(category);
        item.setDescription(description);
        item.setLocation(location);

        // Auto-associate user if not passed explicitly
        if (userId != null) {
            item.setUserId(userId);
        } else if (email != null && !email.trim().isEmpty()) {
            User existingUser = userRepo.findByEmail(email.trim().toLowerCase());
            if (existingUser != null) {
                item.setUserId(existingUser.getId());
            }
        }

        LocalDateTime dt;
        try {
            if (dateTimeLost != null && dateTimeLost.length() == 16) {
                dt = LocalDateTime.parse(dateTimeLost + ":00");
            } else if (dateTimeLost != null) {
                dt = LocalDateTime.parse(dateTimeLost);
            } else {
                dt = LocalDateTime.now();
            }
        } catch (Exception e) {
            dt = LocalDateTime.now();
        }
        item.setDateTimeLost(dt);

        item.setAge(age);
        item.setHeight(height);
        item.setGender(gender);
        item.setPhysicalIdentification(physicalIdentification);
        item.setStatus("Lost");

        // Handle file upload
        MultipartFile uploadFile = (itemPhoto != null && !itemPhoto.isEmpty()) ? itemPhoto : personPhoto;

        if (uploadFile != null && !uploadFile.isEmpty()) {
            try {
                String uploadDir = getUploadDir();
                String original = uploadFile.getOriginalFilename();
                String cleanName = (original != null) ? original.replaceAll("[^a-zA-Z0-9.-]", "_") : "photo.jpg";
                String fileName = System.currentTimeMillis() + "_" + cleanName;
                File targetFile = new File(uploadDir + fileName);

                uploadFile.transferTo(targetFile);
                item.setImageUrl("/uploads/" + fileName);

                // Mirror to parent uploads folder safely
                try {
                    File parentDir = new File(new File(System.getProperty("user.dir")).getParentFile(), "uploads");
                    if (parentDir.exists()) {
                        Files.copy(targetFile.toPath(), new File(parentDir, fileName).toPath(), StandardCopyOption.REPLACE_EXISTING);
                    }
                } catch (Exception ignored) {}

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        LostItem saved = repo.save(item);

        // If category is Child / Person, automatically synchronize to missing_persons table
        if ("Child / Person".equalsIgnoreCase(category)) {
            MissingPerson mp = new MissingPerson();
            mp.setLostItemId(saved.getId());
            mp.setUserId(saved.getUserId());
            mp.setFullName(fullName != null ? fullName : itemName);
            mp.setAge(age);
            mp.setGender(gender);
            mp.setHeight(height);
            mp.setPhysicalDescription(physicalIdentification != null ? physicalIdentification : description);
            mp.setLastSeenLocation(location);
            mp.setLastSeenDatetime(saved.getDateTimeLost());
            mp.setContactName(fullName);
            mp.setContactPhone(phoneNumber);
            mp.setContactEmail(email);
            mp.setPhotoUrl(saved.getImageUrl());
            mp.setStatus("Missing");
            missingPersonRepo.save(mp);
        }

        return saved;
    }

    @GetMapping("/lost-items")
    public List<LostItem> getAllLostItems() {
        return repo.findAll();
    }

    @GetMapping("/lost-items/{id}")
    public LostItem getLostItemById(@PathVariable Long id) {
        return repo.findById(id).orElse(null);
    }

    @PutMapping("/lost-items/{id}/status")
    public LostItem updateStatus(
            @PathVariable Long id, 
            @RequestParam(required = false) String status,
            @RequestBody(required = false) Map<String, String> body) {
        LostItem item = repo.findById(id).orElse(null);
        if (item != null) {
            String newStatus = status;
            if (newStatus == null && body != null) {
                newStatus = body.get("status");
            }
            if (newStatus != null) {
                item.setStatus(newStatus);
                repo.save(item);

                final String finalStatus = newStatus;
                // Also update missing_persons if linked
                missingPersonRepo.findByLostItemId(id).ifPresent(mp -> {
                    mp.setStatus(finalStatus);
                    missingPersonRepo.save(mp);
                });
            }
        }
        return item;
    }

    @DeleteMapping("/lost-items/{id}")
    public Map<String, Object> deleteLostItem(@PathVariable Long id) {
        Map<String, Object> res = new HashMap<>();
        if (repo.existsById(id)) {
            // Delete corresponding missing person entry if present
            missingPersonRepo.findByLostItemId(id).ifPresent(mp -> missingPersonRepo.delete(mp));
            repo.deleteById(id);
            res.put("status", "success");
            res.put("message", "Item deleted successfully");
        } else {
            res.put("status", "error");
            res.put("message", "Item not found");
        }
        return res;
    }

    @GetMapping("/track/{id}")
    public Map<String, Object> trackReport(@PathVariable Long id) {
        Map<String, Object> res = new HashMap<>();

        // Check Lost Items
        LostItem lost = repo.findById(id).orElse(null);
        if (lost != null) {
            res.put("found", true);
            res.put("type", "Lost Item Report");
            res.put("id", lost.getId());
            res.put("ref", "#LF" + String.format("%03d", lost.getId()));
            res.put("name", lost.getItemName());
            res.put("category", lost.getCategory());
            res.put("location", lost.getLocation());
            res.put("status", lost.getStatus());
            res.put("date", lost.getDateTimeLost());
            res.put("reporter", lost.getFullName());
            res.put("imageUrl", lost.getImageUrl());
            res.put("stage", computeStage(lost.getStatus()));
            return res;
        }

        // Check Found Items
        FoundItem found = foundItemRepo.findById(id).orElse(null);
        if (found != null) {
            res.put("found", true);
            res.put("type", "Found Item Intake");
            res.put("id", found.getId());
            res.put("ref", "#FD" + String.format("%03d", found.getId()));
            res.put("name", found.getItemName());
            res.put("category", found.getCategory());
            res.put("location", found.getLocationFound());
            res.put("status", found.getStatus());
            res.put("date", found.getDatetimeFound());
            res.put("reporter", found.getFullName());
            res.put("imageUrl", found.getImageUrl());
            res.put("stage", computeStage(found.getStatus()));
            return res;
        }

        // Check Claims
        Claim claim = claimRepo.findById(id).orElse(null);
        if (claim != null) {
            res.put("found", true);
            res.put("type", "Claim Verification");
            res.put("id", claim.getId());
            res.put("ref", "#C" + String.format("%03d", claim.getId()));
            res.put("name", "Claim on Item #" + claim.getItemId());
            res.put("category", "Claim");
            res.put("status", claim.getStatus());
            res.put("date", claim.getCreatedAt());
            res.put("reporter", claim.getFullName());
            res.put("stage", computeClaimStage(claim.getStatus()));
            return res;
        }

        res.put("found", false);
        res.put("message", "No record found matching reference #" + id);
        return res;
    }

    private int computeStage(String status) {
        if (status == null) return 1;
        String s = status.toLowerCase();
        if (s.contains("return") || s.contains("closed") || s.contains("reunited")) return 4;
        if (s.contains("verif") || s.contains("match") || s.contains("found") || s.contains("claim")) return 3;
        if (s.contains("review") || s.contains("progress") || s.contains("tracing")) return 2;
        return 1;
    }

    private int computeClaimStage(String status) {
        if (status == null) return 2;
        String s = status.toLowerCase();
        if (s.contains("approv") || s.contains("return")) return 4;
        if (s.contains("verif")) return 3;
        if (s.contains("reject")) return 1;
        return 2;
    }
}