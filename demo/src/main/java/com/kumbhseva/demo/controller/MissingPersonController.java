package com.kumbhseva.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.kumbhseva.demo.entity.MissingPerson;
import com.kumbhseva.demo.repository.MissingPersonRepository;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping({"/missing-persons", "/api/missing-persons"})
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class MissingPersonController {

    @Autowired
    private MissingPersonRepository repo;

    private String getUploadDir() {
        String dir = Paths.get(System.getProperty("user.dir"), "uploads").toString() + File.separator;
        File f = new File(dir);
        if (!f.exists()) {
            f.mkdirs();
        }
        return dir;
    }

    @GetMapping
    public List<MissingPerson> getAllMissingPersons(@RequestParam(required = false) String status) {
        if (status != null && !status.trim().isEmpty()) {
            return repo.findByStatus(status.trim());
        }
        return repo.findAll();
    }

    @GetMapping("/{id}")
    public MissingPerson getMissingPersonById(@PathVariable Long id) {
        return repo.findById(id).orElse(null);
    }

    @PostMapping
    public MissingPerson reportMissingPerson(
            @RequestParam String fullName,
            @RequestParam(required = false) Integer age,
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) String height,
            @RequestParam(required = false) String physicalDescription,
            @RequestParam String lastSeenLocation,
            @RequestParam(required = false) String lastSeenDatetime,
            @RequestParam String contactName,
            @RequestParam String contactPhone,
            @RequestParam(required = false) String contactEmail,
            @RequestParam(required = false) Long userId,
            @RequestParam(value = "photo", required = false) MultipartFile photo
    ) {
        MissingPerson mp = new MissingPerson();
        mp.setFullName(fullName);
        mp.setAge(age);
        mp.setGender(gender);
        mp.setHeight(height);
        mp.setPhysicalDescription(physicalDescription);
        mp.setLastSeenLocation(lastSeenLocation);

        LocalDateTime dt;
        try {
            if (lastSeenDatetime != null && lastSeenDatetime.length() == 16) {
                dt = LocalDateTime.parse(lastSeenDatetime + ":00");
            } else if (lastSeenDatetime != null) {
                dt = LocalDateTime.parse(lastSeenDatetime);
            } else {
                dt = LocalDateTime.now();
            }
        } catch (Exception e) {
            dt = LocalDateTime.now();
        }
        mp.setLastSeenDatetime(dt);

        mp.setContactName(contactName);
        mp.setContactPhone(contactPhone);
        mp.setContactEmail(contactEmail);
        mp.setUserId(userId);
        mp.setStatus("Missing");

        if (photo != null && !photo.isEmpty()) {
            try {
                String uploadDir = getUploadDir();
                String original = photo.getOriginalFilename();
                String cleanName = (original != null) ? original.replaceAll("[^a-zA-Z0-9.-]", "_") : "photo.jpg";
                String fileName = System.currentTimeMillis() + "_" + cleanName;
                File targetFile = new File(uploadDir + fileName);
                photo.transferTo(targetFile);
                mp.setPhotoUrl("/uploads/" + fileName);

                // Mirror to parent uploads directory safely
                try {
                    File parentDir = new File(new File(System.getProperty("user.dir")).getParentFile(), "uploads");
                    if (parentDir.exists()) {
                        Files.copy(targetFile.toPath(), new File(parentDir, fileName).toPath(), StandardCopyOption.REPLACE_EXISTING);
                    }
                } catch (Exception ignored) {}
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return repo.save(mp);
    }

    @PutMapping("/{id}/status")
    public MissingPerson updateStatus(
            @PathVariable Long id,
            @RequestParam(required = false) String status,
            @RequestBody(required = false) Map<String, String> body) {
        MissingPerson mp = repo.findById(id).orElse(null);
        if (mp != null) {
            String newStatus = status;
            if (newStatus == null && body != null) {
                newStatus = body.get("status");
            }
            if (newStatus != null) {
                mp.setStatus(newStatus);
                return repo.save(mp);
            }
        }
        return mp;
    }

    @DeleteMapping("/{id}")
    public Map<String, Object> deleteMissingPerson(@PathVariable Long id) {
        Map<String, Object> res = new HashMap<>();
        if (repo.existsById(id)) {
            repo.deleteById(id);
            res.put("status", "success");
            res.put("message", "Missing person record deleted successfully");
        } else {
            res.put("status", "error");
            res.put("message", "Record not found");
        }
        return res;
    }
}
