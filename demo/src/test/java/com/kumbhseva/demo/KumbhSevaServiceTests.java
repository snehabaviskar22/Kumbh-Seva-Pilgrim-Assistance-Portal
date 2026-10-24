package com.kumbhseva.demo;

import com.kumbhseva.demo.controller.*;
import com.kumbhseva.demo.entity.*;
import com.kumbhseva.demo.repository.*;
import com.kumbhseva.demo.util.PasswordUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class KumbhSevaServiceTests {

    @Autowired
    private AuthController authController;

    @Autowired
    private UserController userController;

    @Autowired
    private ClaimController claimController;

    @Autowired
    private LostItemController lostItemController;

    @Autowired
    private FoundItemController foundItemController;

    @Autowired
    private MissingPersonController missingPersonController;

    @Autowired
    private DashboardController dashboardController;

    @Autowired
    private InquiryController inquiryController;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ClaimRepository claimRepository;

    @Test
    void testAuthWorkflowAndRoleSecurity() {
        MockHttpSession session = new MockHttpSession();

        // 1. Test login with seeded admin
        User adminLogin = new User();
        adminLogin.setEmail("admin@kumbhseva.org");
        adminLogin.setPassword("admin123");

        Map<String, Object> loginResp = authController.login(adminLogin, session);
        assertEquals("success", loginResp.get("status"));
        assertEquals("ADMIN", loginResp.get("role"));
        assertNotNull(loginResp.get("userId"));

        // 2. Test check-session
        Map<String, Object> sessionResp = authController.checkSession(session);
        assertTrue((Boolean) sessionResp.get("loggedIn"));
        assertEquals("admin@kumbhseva.org", sessionResp.get("email"));

        // 3. Test Role Escalation Prevention: Ordinary user cannot register as ADMIN
        User rogueUser = new User();
        rogueUser.setName("Rogue User");
        rogueUser.setEmail("rogue_" + System.currentTimeMillis() + "@test.com");
        rogueUser.setPassword("password123");
        rogueUser.setRole("ADMIN");

        MockHttpSession unauthSession = new MockHttpSession(); // No admin in session
        Map<String, Object> regResp = authController.register(rogueUser, unauthSession);
        // Role escalation is blocked and defaulted to PILGRIM or rejected
        assertNotEquals("ADMIN", regResp.get("role"));

        // 4. Test Volunteer registration sets status to PENDING
        User volUser = new User();
        volUser.setName("Field Volunteer");
        volUser.setEmail("fieldvol_" + System.currentTimeMillis() + "@test.com");
        volUser.setPassword("volpass123");
        volUser.setRole("VOLUNTEER");

        Map<String, Object> volRegResp = authController.register(volUser, unauthSession);
        assertEquals("success", volRegResp.get("status"));
        assertEquals("PENDING", volRegResp.get("accountStatus"));

        // Verify volunteer cannot login while PENDING
        User volLogin = new User();
        volLogin.setEmail(volUser.getEmail());
        volLogin.setPassword("volpass123");
        Map<String, Object> volLoginResp = authController.login(volLogin, new MockHttpSession());
        assertEquals("pending_approval", volLoginResp.get("status"));

        // Admin approves volunteer
        Long volId = (Long) volRegResp.get("userId");
        Map<String, Object> appResp = userController.updateUserStatus(volId, "APPROVED", null);
        assertEquals("success", appResp.get("status"));

        // Now volunteer logs in successfully
        Map<String, Object> volApprovedLogin = authController.login(volLogin, new MockHttpSession());
        assertEquals("success", volApprovedLogin.get("status"));

        // Test logout
        Map<String, Object> logoutResp = authController.logout(session);
        assertEquals("success", logoutResp.get("status"));
    }

    @Test
    void testDashboardMetrics() {
        Map<String, Object> data = dashboardController.getDashboardData();
        assertNotNull(data);
        assertTrue(data.containsKey("lostCount"));
        assertTrue(data.containsKey("foundCount"));
        assertTrue(data.containsKey("recentItems"));
        assertTrue(data.containsKey("totalUsers"));
        assertTrue(data.containsKey("volunteerCount"));
        assertTrue(data.containsKey("missingPersonsCount"));
    }

    @Test
    void testClaimAndTrackingWorkflow() {
        // Create a claim
        Claim claim = new Claim();
        claim.setFullName("Test Claimant");
        claim.setPhone("9998887776");
        claim.setItemId(1L);
        claim.setProof("Contains silver coin and library card");
        claim.setIdType("Aadhaar");
        claim.setIdNumber("1234-5678-9012");
        claim.setStatus("Pending");

        Claim savedClaim = claimController.submitClaim(claim);
        assertNotNull(savedClaim.getId());
        assertEquals("Pending", savedClaim.getStatus());

        // Update claim status to Approved
        Claim updatedClaim = claimController.updateClaimStatus(savedClaim.getId(), "Approved", null);
        assertNotNull(updatedClaim);
        assertEquals("Approved", updatedClaim.getStatus());

        // Track by ID
        Map<String, Object> trackResult = lostItemController.trackReport(savedClaim.getId());
        assertNotNull(trackResult);
        assertTrue((Boolean) trackResult.get("found"));
    }

    @Test
    void testMissingPersonWorkflow() {
        MissingPerson mp = new MissingPerson();
        mp.setFullName("Test Missing Pilgrim");
        mp.setAge(65);
        mp.setGender("Male");
        mp.setHeight("5ft 7in");
        mp.setPhysicalDescription("Wearing white dhoti and saffron scarf");
        mp.setLastSeenLocation("Tapovan Ghat");
        mp.setContactName("Ramesh Kumar");
        mp.setContactPhone("9822012345");
        mp.setContactEmail("ramesh@example.com");

        MissingPerson saved = missingPersonController.reportMissingPerson(
                mp.getFullName(), mp.getAge(), mp.getGender(), mp.getHeight(),
                mp.getPhysicalDescription(), mp.getLastSeenLocation(), null,
                mp.getContactName(), mp.getContactPhone(), mp.getContactEmail(),
                null, null
        );

        assertNotNull(saved.getId());
        assertEquals("Missing", saved.getStatus());

        // Update status to Located
        MissingPerson updated = missingPersonController.updateStatus(saved.getId(), "Located", null);
        assertEquals("Located", updated.getStatus());

        // Retrieve all
        List<MissingPerson> list = missingPersonController.getAllMissingPersons(null);
        assertTrue(list.size() > 0);
    }

    @Test
    void testInquirySubmission() {
        Inquiry inq = new Inquiry();
        inq.setName("Test Pilgrim");
        inq.setEmail("testpilgrim@example.com");
        inq.setPhone("9876543210");
        inq.setQueryType("General Information");
        inq.setMessage("What are the timings for Ram Kund Shahi Snan?");

        Map<String, Object> resp = inquiryController.submitInquiry(inq);
        assertEquals("success", resp.get("status"));
        assertNotNull(resp.get("id"));
    }
}
