package com.example.store_rating_app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.store_rating_app.entity.Store;
import com.example.store_rating_app.entity.User;

public interface StoreRepository extends JpaRepository<Store, Long> {

    // Find stores for an owner
    List<Store> findByOwner(User owner);

    // Fetch all stores for an owner with ratings + rating.user eagerly loaded
    @Query("SELECT DISTINCT s FROM Store s "
            + "LEFT JOIN FETCH s.ratings r "
            + "LEFT JOIN FETCH r.user "
            + "WHERE s.owner.id = :ownerId")
    List<Store> findStoresWithRatingsByOwnerId(@Param("ownerId") Long ownerId);

    // Fetch store by id + ratings + rating.user (used by store details endpoint)
    @Query("SELECT s FROM Store s "
            + "LEFT JOIN FETCH s.owner o "
            + "LEFT JOIN FETCH s.ratings r "
            + "LEFT JOIN FETCH r.user "
            + "WHERE s.id = :id")
    Optional<Store> findByIdWithRatings(@Param("id") Long id);

}
