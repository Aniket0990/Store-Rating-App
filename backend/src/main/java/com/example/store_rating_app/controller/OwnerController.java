package com.example.store_rating_app.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.store_rating_app.entity.Rating;
import com.example.store_rating_app.entity.Store;
import com.example.store_rating_app.entity.User;
import com.example.store_rating_app.repository.StoreRepository;
import com.example.store_rating_app.service.OwnerService;

@RestController
@RequestMapping("/api/owner")
@CrossOrigin(origins = "*")
public class OwnerController {

    @Autowired
    private OwnerService ownerService;

    @Autowired
    private StoreRepository storeRepo;

    // Get all stores assigned to this owner (used in StoreList.jsx)
    @GetMapping("/stores")
    public ResponseEntity<?> getStoresForOwner(Authentication auth) {

        User owner = (User) auth.getPrincipal();

        if (!"OWNER".equals(owner.getRole())) {
            return ResponseEntity.status(403).body("Access denied");
        }

        List<Store> stores = storeRepo.findByOwner(owner);

        List<Map<String, Object>> result = stores.stream().map(s -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", s.getId());
            map.put("name", s.getName());
            map.put("email", s.getEmail());
            map.put("address", s.getAddress());
            map.put("description", s.getDescription());
            map.put("avgRating", s.getRatings().stream()
                    .mapToInt(Rating::getScore)
                    .average()
                    .orElse(0.0));
            return map;
        }).toList();

        return ResponseEntity.ok(result);
    }

    // Password update
    @PutMapping("/update-password")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, String> body, Authentication auth) {
        User owner = (User) auth.getPrincipal();

        String oldPass = body.get("oldPassword");
        String newPass = body.get("newPassword");

        try {
            String result = ownerService.updatePassword(owner.getId(), oldPass, newPass);
            return ResponseEntity.ok(Map.of("message", result));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/average-rating")
    public ResponseEntity<Double> getAverageRating(Authentication auth) {
        User owner = (User) auth.getPrincipal();
        double avg = ownerService.getAverageRating(owner.getId());
        return ResponseEntity.ok(avg);
    }

    @GetMapping("/ratings")
    public ResponseEntity<List<Map<String, Object>>> getStoreRatings(Authentication auth) {
        User owner = (User) auth.getPrincipal();
        List<Rating> ratings = ownerService.getStoreRatings(owner.getId());

        List<Map<String, Object>> list = ratings.stream().map(r -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", r.getId());
            map.put("userName", r.getUser() != null ? r.getUser().getName() : "Unknown");
            map.put("storeName",
                    r.getStore() != null && r.getStore().getName() != null
                    ? r.getStore().getName()
                    : ""
            );
            map.put("score", r.getScore());
            return map;
        }).toList();

        return ResponseEntity.ok(list);
    }

    @GetMapping("/users-who-rated")
    public ResponseEntity<List<User>> getUsersWhoRated(Authentication auth) {
        User owner = (User) auth.getPrincipal();
        return ResponseEntity.ok(ownerService.getUsersWhoRated(owner.getId()));
    }
}
