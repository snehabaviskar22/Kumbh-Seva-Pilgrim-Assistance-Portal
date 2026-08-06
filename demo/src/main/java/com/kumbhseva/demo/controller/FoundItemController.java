package com.kumbhseva.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;

import com.kumbhseva.demo.entity.FoundItem;
import com.kumbhseva.demo.repository.FoundItemRepository;

@RestController
@RequestMapping("/found-items")
@CrossOrigin(origins = "http://127.0.0.1:5500")
public class FoundItemController {

    @Autowired
    private FoundItemRepository repo;

    private final String uploadDir = System.getProperty("user.dir") + "/uploads/";
    
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
            @RequestParam("image") MultipartFile image
    ) throws Exception {

       
        File dir = new File(uploadDir);
        if (!dir.exists()) dir.mkdirs();

        String fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();
        File file = new File(uploadDir + fileName);
        image.transferTo(file);

        
        FoundItem item = new FoundItem();
        item.setFullName(fullName);
        item.setPhone(phone);
        item.setEmail(email);
        item.setItemName(itemName);
        item.setCategory(category);
        item.setDescription(description);
        item.setLocationFound(locationFound);
        item.setDatetimeFound(LocalDateTime.parse(datetimeFound));
        item.setDepositedAt(depositedAt);
        item.setImageUrl("/uploads/" + fileName);

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

    @DeleteMapping("/{id}")
    public String deleteItem(@PathVariable Long id) {
        repo.deleteById(id);
        return "Item deleted successfully";
    }
}