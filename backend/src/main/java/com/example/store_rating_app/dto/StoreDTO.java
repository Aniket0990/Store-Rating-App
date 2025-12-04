package com.example.store_rating_app.dto;

import java.util.List;

public class StoreDTO {

    private Long id;
    private String name;
    private String email;
    private String address;
    private String description;
    private Double averageRating;

    private List<RatingDTO> ratings; // For owner rating details

    private Long ownerId;
    private String ownerName;

    // Full Constructor (Owner Section)
    public StoreDTO(Long id, String name, String email, String address, String description,
            Double averageRating, List<RatingDTO> ratings, Long ownerId, String ownerName) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.address = address;
        this.description = description;
        this.averageRating = averageRating;
        this.ratings = ratings;
        this.ownerId = ownerId;
        this.ownerName = ownerName;
    }

    // Constructor (Admin store list)
    public StoreDTO(Long id, String name, String email, String address, String description,
            Double averageRating, Long ownerId, String ownerName) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.address = address;
        this.description = description;
        this.averageRating = averageRating;
        this.ownerId = ownerId;
        this.ownerName = ownerName;
    }

    public StoreDTO() {
    }

    // Getters + Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    public List<RatingDTO> getRatings() {
        return ratings;
    }

    public void setRatings(List<RatingDTO> ratings) {
        this.ratings = ratings;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }
}
