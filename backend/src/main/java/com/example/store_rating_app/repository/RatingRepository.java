package com.example.store_rating_app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.store_rating_app.entity.Rating;
import com.example.store_rating_app.entity.Store;
import com.example.store_rating_app.entity.User;

public interface RatingRepository extends JpaRepository<Rating, Long> {

    List<Rating> findByStore(Store store);

    Optional<Rating> findByStoreAndUser(Store store, User user);

    Optional<Rating> findByUserIdAndStoreId(Long userId, Long storeId);

    @Query("SELECT r FROM Rating r WHERE r.store.id = :storeId")
    List<Rating> findByStoreId(@Param("storeId") Long storeId);

    List<Rating> findByUser(User user);

    long countByUserId(Long userId);

}
