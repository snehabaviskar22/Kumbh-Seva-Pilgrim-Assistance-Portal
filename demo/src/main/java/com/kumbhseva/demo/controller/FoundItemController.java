package com.kumbhseva.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.kumbhseva.demo.entity.FoundItem;
import com.kumbhseva.demo.entity.User;
import com.kumbhseva.demo.repository.FoundItemRepository;
import com.kumbhseva.demo.repository.UserRepository;

@RestController
@RequestMapping("/found-items")
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class FoundItemController {

    @Autowired
    private FoundItemRepository repo;

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
    
    @PostMapping
    public FoundItem createItem(
            @RequestParam String fullName,
            @RequestParam String phone,
            @RequestParam(required = false) String email,
            @RequestParam String itemName,
            @RequestParam String category,
            @RequestParam String description,
            @RequestParam String locationFound,
            @RequestParam String datetimeFound,
            @RequestParam String depositedAt,
            @RequestParam(value = "userId", required = false) Long userId,
            @RequestParam(value = "image", required = false) MultipartFile image
    ) throws Exception {

        String uploadDir = getUploadDir();
        String imageUrl = null;

        if (image != null && !image.isEmpty()) {
            String original = image.getOriginalFilename();
            String cleanName = (original != null) ? original.replaceAll("[^a-zA-Z0-9.-]", "_") : "photo.jpg";
            String fileName = System.currentTimeMillis() + "_" + cleanName;
            File targetFile = new File(uploadDir + fileName);
            image.transferTo(targetFile);
            imageUrl = "/uploads/" + fileName;

            // Also mirror to parent uploads folder safely
            try {
                File parentDir = new File(new File(System.getProperty("user.dir")).getParentFile(), "uploads");
                if (parentDir.exists()) {
                    Files.copy(targetFile.toPath(), new File(parentDir, fileName).toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
            } catch (Exception ignored) {}
        }
        
        FoundItem item = new FoundItem();
        item.setFullName(fullName);
        item.setPhone(phone);
        item.setEmail(email);
        item.setItemName(itemName);
        item.setCategory(category);
        item.setDescription(description);
        item.setLocationFound(locationFound);

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
            if (datetimeFound != null && datetimeFound.length() == 16) {
                dt = LocalDateTime.parse(datetimeFound + ":00");
            } else if (datetimeFound != null) {
                dt = LocalDateTime.parse(datetimeFound);
            } else {
                dt = LocalDateTime.now();
            }
        } catch (Exception e) {
            dt = LocalDateTime.now();
        }
        item.setDatetimeFound(dt);
        item.setDepositedAt(depositedAt);
        item.setImageUrl(imageUrl);
        item.setStatus("Found");

        return repo.save(item);
    }

    @GetMapping
    public List<FoundItem> getAllItems() {
        return repo.findAll();
    }

    @GetMapping("/{id}")
    public FoundItem getItemById(@PathVariable Long id) {
        return repo.findById(id).orElse(null);
    }

    @PutMapping("/{id}/status")
    public FoundItem updateItemStatus(
            @PathVariable Long id,
            @RequestParam(required = false) String status,
            @RequestBody(required = false) Map<String, String> body) {

        FoundItem item = repo.findById(id).orElse(null);
        if (item != null) {
            String newStatus = status;
            if (newStatus == null && body != null) {
                newStatus = body.get("status");
            }
            if (newStatus != null) {
                item.setStatus(newStatus);
                return repo.save(item);
            }
        }
        return item;
    }

    @DeleteMapping("/{id}")
    public Map<String, Object> deleteItem(@PathVariable Long id) {
        Map<String, Object> res = new HashMap<>();
        if (repo.existsById(id)) {
            repo.deleteById(id);
            res.put("status", "success");
            res.put("message", "Found item deleted successfully");
        } else {
            res.put("status", "error");
            res.put("message", "Item not found");
        }
        return res;
    }
}