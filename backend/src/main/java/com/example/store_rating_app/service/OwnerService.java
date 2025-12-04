package com.example.store_rating_app.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.store_rating_app.entity.Rating;
import com.example.store_rating_app.entity.Store;
import com.example.store_rating_app.entity.User;
import com.example.store_rating_app.repository.StoreRepository;
import com.example.store_rating_app.repository.UserRepository;

@Service
public class OwnerService {

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Return average across all ratings on owner's stores
    public double getAverageRating(Long ownerId) {
        List<Store> stores = storeRepository.findStoresWithRatingsByOwnerId(ownerId);
        return stores.stream()
                .flatMap(s -> s.getRatings() == null ? List.<Rating>of().stream() : s.getRatings().stream())
                .mapToDouble(Rating::getScore)
                .average()
                .orElse(0.0);
    }

    // Return all ratings across owner's stores (flat list)
    public List<Rating> getStoreRatings(Long ownerId) {
        List<Store> stores = storeRepository.findStoresWithRatingsByOwnerId(ownerId);
        return stores.stream()
                .flatMap(s -> s.getRatings() == null ? List.<Rating>of().stream() : s.getRatings().stream())
                .collect(Collectors.toList());
    }

    // Unique users who rated any of this owner's stores
    public List<User> getUsersWhoRated(Long ownerId) {
        List<Store> stores = storeRepository.findStoresWithRatingsByOwnerId(ownerId);
        return stores.stream()
                .flatMap(s -> s.getRatings() == null ? List.<Rating>of().stream() : s.getRatings().stream())
                .map(Rating::getUser)
                .distinct()
                .collect(Collectors.toList());
    }

    // Update owner password
    public String updatePassword(Long ownerId, String oldPassword, String newPassword) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new RuntimeException("Owner not found"));

        // Check old password
        if (!passwordEncoder.matches(oldPassword, owner.getPassword())) {
            throw new RuntimeException("Old password is incorrect");
        }

        // Update new password
        owner.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(owner);

        return "Password updated successfully";
    }
}
