package com.kumbhseva.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.kumbhseva.demo.entity.LostItem;
import com.kumbhseva.demo.repository.LostItemRepository;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://127.0.0.1:5500")
public class LostItemController {

    @Autowired
    private LostItemRepository repo;

    
    @PostMapping("/report-lost")
    public String createLostItem(
            @RequestParam String fullName,
            @RequestParam String phoneNumber,
            @RequestParam(required = false) String email,
            @RequestParam String itemName,
            @RequestParam String category,
            @RequestParam String description,
            @RequestParam String location,
            @RequestParam String dateTimeLost,
            @RequestParam(required = false) MultipartFile itemPhoto,

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
        item.setDateTimeLost(LocalDateTime.parse(dateTimeLost + ":00"));

        item.setAge(age);
        item.setHeight(height);
        item.setGender(gender);
        item.setPhysicalIdentification(physicalIdentification);

      
        if (itemPhoto != null && !itemPhoto.isEmpty()) {
            try {
                String uploadDir = System.getProperty("user.dir") + "/uploads/";
                File dir = new File(uploadDir);
                if (!dir.exists()) dir.mkdirs();

                String fileName = System.currentTimeMillis() + "_" + itemPhoto.getOriginalFilename();
                String filePath = uploadDir + fileName;

                itemPhoto.transferTo(new File(filePath));

                item.setImageUrl("/uploads/" + fileName);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        repo.save(item);

        return "Saved successfully";
    }

    
    @GetMapping("/lost-items")
    public List<LostItem> getAll() {
        return repo.findAll();
    }
    @GetMapping("/track/{id}")
public LostItem trackItem(@PathVariable Long id) {

    return repo.findById(id).orElse(null);

}
}