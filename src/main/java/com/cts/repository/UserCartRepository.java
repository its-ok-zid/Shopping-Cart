package com.cts.repository;

import java.util.List;
import java.util.Optional;

import com.cts.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cts.model.UserCartDetails;

@Repository
public interface UserCartRepository extends JpaRepository<UserCartDetails, Integer> {

    /**
     * Find all cart rows that belong to the given user id (user.id).
     * Derives to: where user.id = :userId
     */
    List<UserCartDetails> findByUserId(long userId);

    /**
     * Find a cart row by user and item name.
     * Derives to: where user = :user and name = :name
     */
    Optional<UserCartDetails> findByUserAndName(User user, String name);

    /**
     * Find a cart row by its cartItemId and the owning user's id.
     * Derives to: where cartItemId = :cartItemId and user.id = :userId
     */
    Optional<UserCartDetails> findByCartItemIdAndUserId(int cartItemId, long userId);
}
