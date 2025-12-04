package com.example.store_rating_app.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.store_rating_app.dto.UserDTO;
import com.example.store_rating_app.entity.Rating;
import com.example.store_rating_app.entity.Store;
import com.example.store_rating_app.entity.User;
import com.example.store_rating_app.repository.RatingRepository;
import com.example.store_rating_app.repository.StoreRepository;
import com.example.store_rating_app.repository.UserRepository;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // NEW: Admin stats endpoint
    @GetMapping("/stats")
    public ResponseEntity<?> getAdminStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", userRepository.count());
        stats.put("totalStores", storeRepository.count());
        stats.put("totalRatings", ratingRepository.count());
        return ResponseEntity.ok(stats);
    }

    //  NEW: Return all stores with avgRating 
    @GetMapping("/stores")
    public ResponseEntity<?> getAllStores() {
        List<Store> stores = storeRepository.findAll();

        List<Map<String, Object>> response = stores.stream().map(s -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", s.getId());
            map.put("name", s.getName());
            map.put("email", s.getEmail());
            map.put("address", s.getAddress());
            map.put("avgRating", s.getRatings() == null ? 0.0
                    : s.getRatings().stream()
                            .mapToInt(r -> r.getScore())
                            .average()
                            .orElse(0.0)
            );
            return map;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    // NEW: Return all users with avgRating for OWNER 
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        List<User> users = userRepository.findAll();

        List<Map<String, Object>> response = users.stream().map(u -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", u.getId());
            map.put("name", u.getName());
            map.put("email", u.getEmail());
            map.put("address", u.getAddress());
            map.put("role", u.getRole());

            if ("OWNER".equals(u.getRole())) {
                // calculate avgRating for OWNER's stores
                List<Store> ownerStores = storeRepository.findByOwner(u);
                double avgRating = ownerStores.stream()
                        .flatMap(s -> s.getRatings().stream())
                        .mapToInt(r -> r.getScore())
                        .average()
                        .orElse(0.0);
                map.put("avgRating", avgRating);
            } else {
                map.put("avgRating", null);
            }

            return map;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    // Return only users with role OWNER (admin UI will call this to populate dropdown)
    @GetMapping("/owners")
    public ResponseEntity<List<UserDTO>> getOwners() {
        List<User> owners = userRepository.findByRole("OWNER");
        List<UserDTO> dto = owners.stream().map(u -> new UserDTO(
                u.getId(),
                u.getName(),
                u.getEmail(),
                u.getAddress(),
                u.getRole(),
                null,
                null
        )).collect(Collectors.toList());
        return ResponseEntity.ok(dto);
    }

    // Add store with optional ownerId
    public static class AddStoreRequest {

        public String name;
        public String email;
        public String address;
        public String description;
        public Long ownerId; // optional
    }

    @PostMapping("/add-store")
    public ResponseEntity<?> addStore(@RequestBody AddStoreRequest req) {
        try {
            Store store = new Store();
            store.setName(req.name);
            store.setEmail(req.email);
            store.setAddress(req.address);
            store.setDescription(req.description);

            if (req.ownerId != null) {
                Optional<User> ownerOpt = userRepository.findById(req.ownerId);
                if (ownerOpt.isEmpty()) {
                    return ResponseEntity.badRequest().body("Owner with id " + req.ownerId + " not found");
                }
                User owner = ownerOpt.get();
                if (!"OWNER".equals(owner.getRole())) {
                    return ResponseEntity.badRequest().body("Selected user is not an OWNER");
                }
                store.setOwner(owner);
            }

            Store saved = storeRepository.save(store);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error adding store: " + e.getMessage());
        }
    }

    // Assign or update owner for an existing store
    public static class AssignOwnerRequest {

        public Long ownerId; // null to unassign
    }

    @PutMapping("/stores/{storeId}/assign-owner")
    public ResponseEntity<?> assignOwner(@PathVariable Long storeId, @RequestBody AssignOwnerRequest req) {
        try {
            Optional<Store> storeOpt = storeRepository.findById(storeId);
            if (storeOpt.isEmpty()) {
                return ResponseEntity.status(404).body("Store not found");
            }
            Store store = storeOpt.get();

            if (req.ownerId == null) {
                store.setOwner(null);
            } else {
                Optional<User> ownerOpt = userRepository.findById(req.ownerId);
                if (ownerOpt.isEmpty()) {
                    return ResponseEntity.status(404).body("Owner not found");
                }
                User owner = ownerOpt.get();
                if (!"OWNER".equals(owner.getRole())) {
                    return ResponseEntity.badRequest().body("Selected user is not an OWNER");
                }
                // Option 1: allow overwriting existing owner (your chosen option)
                store.setOwner(owner);
            }

            storeRepository.save(store);
            return ResponseEntity.ok(store);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error assigning owner: " + e.getMessage());
        }
    }

    @PutMapping("/stores/{storeId}/owner/{ownerId}")
    public ResponseEntity<?> updateStoreOwner(@PathVariable Long storeId, @PathVariable Long ownerId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new RuntimeException("Store not found"));

        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new RuntimeException("Owner not found"));

        if (!owner.getRole().equals("OWNER")) {
            throw new RuntimeException("Selected user is not an owner");
        }

        store.setOwner(owner);
        storeRepository.save(store);

        return ResponseEntity.ok("Owner updated successfully");
    }

    // Delete store
    @DeleteMapping("/stores/{storeId}")
    public ResponseEntity<?> deleteStore(@PathVariable Long storeId) {
        if (!storeRepository.existsById(storeId)) {
            return ResponseEntity.status(404).body("Store not found");
        }
        storeRepository.deleteById(storeId);
        return ResponseEntity.ok("Store deleted");
    }

    //  Delete a user by ID 
    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(404).body("User not found");
        }

        User user = userOpt.get();

        // Optional: prevent admin from deleting themselves
        if ("ADMIN".equals(user.getRole())) {
            return ResponseEntity.badRequest().body("Cannot delete an ADMIN user");
        }

        userRepository.delete(user);
        return ResponseEntity.ok("User deleted successfully");
    }

    // Get single user details by ID 
    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(404).body("User not found");
        }

        User user = userOpt.get();
        Map<String, Object> map = new HashMap<>();
        map.put("id", user.getId());
        map.put("name", user.getName());
        map.put("email", user.getEmail());
        map.put("address", user.getAddress());
        map.put("role", user.getRole());

        if ("OWNER".equals(user.getRole())) {
            List<Store> ownerStores = storeRepository.findByOwner(user);
            double avgRating = ownerStores.stream()
                    .flatMap(s -> s.getRatings().stream())
                    .mapToInt(r -> r.getScore())
                    .average()
                    .orElse(0.0);
            map.put("avgRating", avgRating);
        } else {
            map.put("avgRating", null);
        }

        return ResponseEntity.ok(map);
    }

//Get single store details by ID with ratings 
    @GetMapping("/stores/{id}")
    public ResponseEntity<?> getStoreById(@PathVariable Long id) {
        Optional<Store> storeOpt = storeRepository.findByIdWithRatings(id);
        if (storeOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Store not found");
        }

        Store s = storeOpt.get();
        Map<String, Object> map = new HashMap<>();
        map.put("id", s.getId());
        map.put("name", s.getName());
        map.put("email", s.getEmail());
        map.put("address", s.getAddress());
        map.put("description", s.getDescription());
        map.put("ownerName", s.getOwner() != null ? s.getOwner().getName() : null);
        map.put("ownerId", s.getOwner() != null ? s.getOwner().getId() : null);
        map.put("ratings", s.getRatings().stream().map(r -> Map.of(
                "id", r.getId(),
                "score", r.getScore(),
                "userId", r.getUser().getId(),
                "userName", r.getUser().getName()
        )).toList());
        map.put("avgRating", s.getRatings().stream().mapToInt(Rating::getScore).average().orElse(0.0));

        return ResponseEntity.ok(map);
    }

    @PostMapping("/add-user")
    public ResponseEntity<?> createUser(@RequestBody User userRequest) {

        // Check if email already exists
        if (userRepository.findByEmail(userRequest.getEmail()).isPresent()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Email already exists");
        }

        // Encode password
        userRequest.setPassword(passwordEncoder.encode(userRequest.getPassword()));

        // Default role USER if not provided
        if (userRequest.getRole() == null) {
            userRequest.setRole("ROLE_USER");
        }

        User saved = userRepository.save(userRequest);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/owners/{ownerId}/stores")
    public ResponseEntity<?> getStoresByOwner(@PathVariable Long ownerId) {
        Optional<User> ownerOpt = userRepository.findById(ownerId);

        if (ownerOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Owner not found");
        }

        List<Store> stores = storeRepository.findByOwner(ownerOpt.get());

        List<Map<String, Object>> result = stores.stream().map(s -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", s.getId());
            map.put("name", s.getName());
            map.put("avgRating",
                    s.getRatings().stream()
                            .mapToInt(Rating::getScore)
                            .average().orElse(0.0)
            );
            return map;
        }).toList();

        return ResponseEntity.ok(result);
    }

    @GetMapping("/users/{userId}/stores")
    public ResponseEntity<?> getStoresByUser(@PathVariable Long userId) {

        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(404).body("User not found");
        }

        User user = userOpt.get();

        // Only owners should have stores
        if (!"OWNER".equals(user.getRole())) {
            return ResponseEntity.ok(List.of()); // return empty list
        }

        List<Store> stores = storeRepository.findByOwner(user);

        List<Map<String, Object>> result = stores.stream().map(s -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", s.getId());
            map.put("name", s.getName());
            map.put("avgRating", s.getRatings().stream()
                    .mapToInt(Rating::getScore)
                    .average().orElse(0.0));
            return map;
        }).toList();

        return ResponseEntity.ok(result);
    }

}
