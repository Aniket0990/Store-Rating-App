package com.example.store_rating_app.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.store_rating_app.entity.Rating;
import com.example.store_rating_app.entity.User;
import com.example.store_rating_app.service.RatingService;

@RestController
@RequestMapping("/api/ratings")
@CrossOrigin(origins = "*")
public class RatingController {

    @Autowired
    private RatingService ratingService;

    // Submit or update rating
    @PostMapping("/{storeId}")
    public ResponseEntity<?> rateStore(
            @PathVariable Long storeId,
            @RequestBody Map<String, Integer> body,
            Authentication auth) {

        int score = body.get("score");
        User user = (User) auth.getPrincipal();

        if (score < 1 || score > 5) {
            return ResponseEntity.badRequest().body("Rating must be between 1 to 5");
        }

        Rating saved = ratingService.submitOrUpdateRating(storeId, user, score);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/user")
    public ResponseEntity<?> getMyRatings(Authentication auth) {
        User user = (User) auth.getPrincipal();
        // Fetch ratings by logged-in user
        return ResponseEntity.ok(ratingService.getAllRatingsByUser(user));
    }

    //  User’s own rating for store
    @GetMapping("/{storeId}/my-rating")
    public ResponseEntity<?> getMyRating(@PathVariable Long storeId, Authentication auth) {
        User user = (User) auth.getPrincipal();
        return ResponseEntity.ok(ratingService.getUserRating(storeId, user));
    }
}
