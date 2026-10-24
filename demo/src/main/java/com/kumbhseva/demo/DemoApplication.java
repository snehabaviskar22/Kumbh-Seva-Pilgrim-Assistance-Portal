package com.kumbhseva.demo;

import com.kumbhseva.demo.entity.LostItem;
import com.kumbhseva.demo.entity.MissingPerson;
import com.kumbhseva.demo.entity.User;
import com.kumbhseva.demo.repository.LostItemRepository;
import com.kumbhseva.demo.repository.MissingPersonRepository;
import com.kumbhseva.demo.repository.UserRepository;
import com.kumbhseva.demo.util.PasswordUtils;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Bean
	CommandLineRunner initData(
			UserRepository userRepository,
			LostItemRepository lostItemRepository,
			MissingPersonRepository missingPersonRepository) {
		return args -> {
			// Ensure uploads folder exists
			File uploadDir = new File(Paths.get(System.getProperty("user.dir"), "uploads").toString());
			if (!uploadDir.exists()) {
				uploadDir.mkdirs();
			}

			// Seed Admin User
			User admin = userRepository.findByEmail("admin@kumbhseva.org");
			if (admin == null) {
				admin = new User();
				admin.setName("Administrator");
				admin.setEmail("admin@kumbhseva.org");
				admin.setPassword(PasswordUtils.hashPassword("admin123"));
				admin.setRole("ADMIN");
				admin.setStatus("APPROVED");
				admin.setAssignedZone("Central Command");
				userRepository.save(admin);
				System.out.println("Default Admin account seeded: admin@kumbhseva.org / admin123");
			} else if (!admin.getPassword().matches("^[a-fA-F0-9]{64}$")) {
				admin.setPassword(PasswordUtils.hashPassword("admin123"));
				admin.setStatus("APPROVED");
				userRepository.save(admin);
			}

			// Seed Volunteer User
			User volunteer = userRepository.findByEmail("volunteer@kumbhseva.org");
			if (volunteer == null) {
				volunteer = new User();
				volunteer.setName("Seva Volunteer");
				volunteer.setEmail("volunteer@kumbhseva.org");
				volunteer.setPassword(PasswordUtils.hashPassword("volunteer123"));
				volunteer.setRole("VOLUNTEER");
				volunteer.setStatus("APPROVED");
				volunteer.setAssignedZone("Ram Kund");
				userRepository.save(volunteer);
				System.out.println("Default Volunteer account seeded: volunteer@kumbhseva.org / volunteer123");
			} else if (!volunteer.getPassword().matches("^[a-fA-F0-9]{64}$")) {
				volunteer.setPassword(PasswordUtils.hashPassword("volunteer123"));
				volunteer.setStatus("APPROVED");
				userRepository.save(volunteer);
			}

			// Ensure existing pilgrim has role & status & hashed password
			User existingPilgrim = userRepository.findByEmail("snehabaviskar025@gmail.com");
			if (existingPilgrim != null) {
				if (existingPilgrim.getRole() == null) existingPilgrim.setRole("PILGRIM");
				if (existingPilgrim.getStatus() == null) existingPilgrim.setStatus("APPROVED");
				if (!existingPilgrim.getPassword().matches("^[a-fA-F0-9]{64}$")) {
					existingPilgrim.setPassword(PasswordUtils.hashPassword(existingPilgrim.getPassword()));
				}
				userRepository.save(existingPilgrim);
			}

			// Synchronize any legacy Child / Person reports from lost_items into missing_persons table
			List<LostItem> missingItems = lostItemRepository.findAll();
			for (LostItem li : missingItems) {
				if ("Child / Person".equalsIgnoreCase(li.getCategory())) {
					if (missingPersonRepository.findByLostItemId(li.getId()).isEmpty()) {
						MissingPerson mp = new MissingPerson();
						mp.setLostItemId(li.getId());
						mp.setUserId(li.getUserId());
						mp.setFullName(li.getFullName() != null ? li.getFullName() : li.getItemName());
						mp.setAge(li.getAge());
						mp.setGender(li.getGender());
						mp.setHeight(li.getHeight());
						mp.setPhysicalDescription(li.getPhysicalIdentification() != null ? li.getPhysicalIdentification() : li.getDescription());
						mp.setLastSeenLocation(li.getLocation());
						mp.setLastSeenDatetime(li.getDateTimeLost());
						mp.setContactName(li.getFullName());
						mp.setContactPhone(li.getPhoneNumber());
						mp.setContactEmail(li.getEmail());
						mp.setPhotoUrl(li.getImageUrl());
						mp.setStatus(li.getStatus() != null ? li.getStatus() : "Missing");
						missingPersonRepository.save(mp);
						System.out.println("Synchronized legacy Missing Person record for: " + mp.getFullName());
					}
				}
			}
		};
	}
}
