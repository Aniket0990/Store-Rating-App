package com.example.store_rating_app.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.store_rating_app.entity.Rating;
import com.example.store_rating_app.entity.Store;
import com.example.store_rating_app.entity.User;
import com.example.store_rating_app.repository.RatingRepository;
import com.example.store_rating_app.repository.StoreRepository;
import com.example.store_rating_app.service.UserService;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private StoreRepository storeRepo;

    @Autowired
    private RatingRepository ratingRepo;

    @Autowired
    private UserService userService;

    // Get all stores (name, address, rating)
    @GetMapping("/stores")
    public ResponseEntity<?> getAllStores() {
        List<Store> stores = storeRepo.findAll();

        List<Map<String, Object>> response = stores.stream().map(s -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", s.getId());
            map.put("name", s.getName());
            map.put("email", s.getEmail());
            map.put("address", s.getAddress());
            map.put("description", s.getDescription());
            map.put("avgRating", s.getRatings() == null ? 0.0
                    : s.getRatings().stream()
                            .mapToInt(Rating::getScore)
                            .average()
                            .orElse(0.0)
            );
            return map;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    //  Search by name or address (contains)
    @GetMapping("/stores/search")
    public ResponseEntity<?> searchStores(@RequestParam String keyword) {
        String lower = keyword.toLowerCase();

        List<Store> stores = storeRepo.findAll().stream()
                .filter(s
                        -> s.getName().toLowerCase().contains(lower)
                || s.getAddress().toLowerCase().contains(lower)
                )
                .collect(Collectors.toList());

        return ResponseEntity.ok(stores);
    }

    // Update password
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(
            @RequestBody Map<String, String> body,
            Authentication auth) {

        User user = (User) auth.getPrincipal();

        String oldPass = body.get("oldPassword");
        String newPass = body.get("newPassword");

        String result = userService.updatePassword(user.getId(), oldPass, newPass);

        return ResponseEntity.ok(Map.of("message", result));
    }

    @GetMapping("/dashboard-stats")
    public ResponseEntity<?> getUserDashboardStats(Authentication auth) {

        User user = (User) auth.getPrincipal();

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalStores", storeRepo.count());
        long ratedStoresCount = ratingRepo.countByUserId(user.getId());
        stats.put("ratedStores", ratedStoresCount);

        return ResponseEntity.ok(stats);
    }

    @GetMapping("/store/{id}")
    public ResponseEntity<?> getStoreDetails(@PathVariable Long id, Authentication auth) {

        Optional<Store> s = storeRepo.findById(id);
        if (s.isEmpty()) {
            return ResponseEntity.badRequest().body("Store not found");
        }

        Store store = s.get();

        double avgRating = store.getRatings().stream()
                .mapToInt(Rating::getScore)
                .average()
                .orElse(0.0);

        // Convert ratings properly to JSON-friendly DTO
        List<Map<String, Object>> ratingList = store.getRatings().stream()
                .map(r -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", r.getId());
                    map.put("score", r.getScore());
                    map.put("userName", r.getUser() != null ? r.getUser().getName() : "Unknown");
                    return map;
                })
                .collect(Collectors.toList());

        Map<String, Object> res = new HashMap<>();
        res.put("id", store.getId());
        res.put("name", store.getName());
        res.put("email", store.getEmail());
        res.put("address", store.getAddress());
        res.put("description", store.getDescription());
        res.put("avgRating", avgRating);
        res.put("ratings", ratingList);

        // Add logged-in user's own rating
        User user = (User) auth.getPrincipal();
        Rating myRating = store.getRatings().stream()
                .filter(r -> r.getUser() != null && r.getUser().getId().equals(user.getId()))
                .findFirst()
                .orElse(null);

        res.put("myRating", myRating != null ? myRating.getScore() : null);

        return ResponseEntity.ok(res);
    }

}
