package com.example.store_rating_app.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.store_rating_app.dto.RatingDTO;
import com.example.store_rating_app.dto.StoreDTO;
import com.example.store_rating_app.entity.Rating;
import com.example.store_rating_app.entity.Store;
import com.example.store_rating_app.repository.RatingRepository;
import com.example.store_rating_app.repository.StoreRepository;

@RestController
@RequestMapping("/api/stores")
@CrossOrigin(origins = "*")
public class StoreController {

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private RatingRepository ratingRepository;

    @GetMapping
    public List<StoreDTO> getAllStores() {
        return storeRepository.findAll().stream().map(store -> {

            double avgRating = ratingRepository.findByStoreId(store.getId())
                    .stream()
                    .mapToInt(Rating::getScore)
                    .average()
                    .orElse(0.0);

            Long ownerId = store.getOwner() != null ? store.getOwner().getId() : null;
            String ownerName = store.getOwner() != null ? store.getOwner().getName() : null;

            return new StoreDTO(
                    store.getId(),
                    store.getName(),
                    store.getEmail(),
                    store.getAddress(),
                    store.getDescription(),
                    avgRating,
                    ownerId,
                    ownerName
            );
        }).collect(Collectors.toList());
    }

    @GetMapping("/{storeId}")
    public ResponseEntity<?> getStore(@PathVariable Long storeId) {

        Optional<Store> storeOpt = storeRepository.findByIdWithRatings(storeId);

        if (storeOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Store not found");
        }

        Store store = storeOpt.get();

        List<RatingDTO> ratingDTOs = ratingRepository.findByStoreId(store.getId())
                .stream()
                .map(r -> new RatingDTO(
                r.getId(),
                r.getScore(),
                r.getUser() != null ? r.getUser().getName() : null
        ))
                .collect(Collectors.toList());

        double avgRating = ratingDTOs.stream()
                .mapToInt(RatingDTO::getScore)
                .average()
                .orElse(0.0);
        StoreDTO dto = new StoreDTO(
                store.getId(),
                store.getName(),
                store.getEmail(),
                store.getAddress(),
                store.getDescription(),
                avgRating,
                ratingDTOs,
                store.getOwner() != null ? store.getOwner().getId() : null,
                store.getOwner() != null ? store.getOwner().getName() : null
        );

        return ResponseEntity.ok(dto);
    }

}
