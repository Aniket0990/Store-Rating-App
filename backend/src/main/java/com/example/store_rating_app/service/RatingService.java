package com.example.store_rating_app.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.store_rating_app.entity.Rating;
import com.example.store_rating_app.entity.Store;
import com.example.store_rating_app.entity.User;
import com.example.store_rating_app.repository.RatingRepository;
import com.example.store_rating_app.repository.StoreRepository;

@Service
public class RatingService {

    @Autowired
    private RatingRepository ratingRepo;

    @Autowired
    private StoreRepository storeRepo;

    // Get or update rating
    public Rating submitOrUpdateRating(Long storeId, User user, int score) {
        Store store = storeRepo.findById(storeId)
                .orElseThrow(() -> new RuntimeException("Store not found"));

        Optional<Rating> existing = ratingRepo.findByUserIdAndStoreId(user.getId(), storeId);

        Rating rating;
        if (existing.isPresent()) {
            rating = existing.get();
            rating.setScore(score);
        } else {
            rating = new Rating();
            rating.setStore(store);
            rating.setUser(user);
            rating.setScore(score);
        }

        return ratingRepo.save(rating);
    }

    public Optional<Rating> getUserRating(Long storeId, User user) {
        return ratingRepo.findByUserIdAndStoreId(user.getId(), storeId);
    }

    public List<Map<String, Object>> getAllRatingsByUser(User user) {
        List<Rating> ratings = ratingRepo.findByUser(user);

        return ratings.stream().map(r -> {
            Map<String, Object> map = new HashMap<>();

            map.put("id", r.getId());
            map.put("userName", user.getName());

            map.put("storeName",
                    r.getStore() != null && r.getStore().getName() != null
                    ? r.getStore().getName()
                    : ""
            );
            map.put("storeEmail",
                    r.getStore() != null ? r.getStore().getEmail() : ""
            );

            map.put("storeAddress",
                    r.getStore() != null ? r.getStore().getAddress() : ""
            );

            map.put("score", r.getScore());

            return map;
        }).collect(Collectors.toList());
    }

}
