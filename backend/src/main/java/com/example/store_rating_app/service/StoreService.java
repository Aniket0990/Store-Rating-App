package com.example.store_rating_app.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.store_rating_app.dto.StoreDTO;
import com.example.store_rating_app.entity.Rating;
import com.example.store_rating_app.entity.Store;
import com.example.store_rating_app.entity.User;
import com.example.store_rating_app.repository.StoreRepository;
import com.example.store_rating_app.repository.UserRepository;

@Service
public class StoreService {

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Get all stores (DTO response)
    public List<StoreDTO> getAllStores() {
        return storeRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public Store saveStore(Store store) {
        return storeRepository.save(store);
    }

    // Create store with owner 
    public Store createStore(Store store, Long ownerId) {
        if (ownerId != null) {
            User owner = userRepository.findById(ownerId)
                    .orElseThrow(() -> new RuntimeException("Owner not found"));
            if (!"OWNER".equals(owner.getRole())) {
                throw new RuntimeException("Selected user is not an OWNER");
            }
            store.setOwner(owner);
        }
        return storeRepository.save(store);
    }

    // ✔ Update store 
    public Store updateStore(Long storeId, Store updatedStore) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new RuntimeException("Store not found"));

        store.setName(updatedStore.getName());
        store.setAddress(updatedStore.getAddress());
        store.setDescription(updatedStore.getDescription());
        store.setEmail(updatedStore.getEmail());

        return storeRepository.save(store);
    }

    //  Delete store
    public void deleteStore(Long storeId) {
        storeRepository.deleteById(storeId);
    }

    // Convert Store → StoreDTO
    private StoreDTO toDTO(Store store) {

        double avg = 0.0;

        if (store.getRatings() != null && !store.getRatings().isEmpty()) {
            avg = store.getRatings()
                    .stream()
                    .mapToDouble(Rating::getScore)
                    .average()
                    .orElse(0.0);
        }

        Long ownerId = store.getOwner() != null ? store.getOwner().getId() : null;
        String ownerName = store.getOwner() != null ? store.getOwner().getName() : null;

        return new StoreDTO(
                store.getId(),
                store.getName(),
                store.getEmail(),
                store.getAddress(),
                store.getDescription(),
                avg,
                ownerId,
                ownerName
        );
    }

    // Password update
    public String updatePassword(Long userId, String oldPassword, String newPassword) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check old password
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            return "Old password is incorrect!";
        }

        // Encode the new password
        user.setPassword(passwordEncoder.encode(newPassword));

        userRepository.save(user);

        return "Password updated successfully!";
    }

    // Return list of users who rated a store (used in owner store-specific ratings) with basic info
    public List<Map<String, Object>> getUsersWhoRatedStore(Long storeId) {

        Store store = storeRepository.findByIdWithRatings(storeId)
                .orElseThrow(() -> new RuntimeException("Store not found"));

        return store.getRatings().stream().map(r -> {
            Map<String, Object> map = new HashMap<>();
            map.put("ratingId", r.getId());
            map.put("score", r.getScore());
            map.put("userId", r.getUser().getId());
            map.put("userName", r.getUser().getName());
            map.put("userEmail", r.getUser().getEmail());
            return map;
        }).collect(Collectors.toList());
    }
}
