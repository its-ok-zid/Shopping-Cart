package com.cts.repository;

import java.util.List;
import java.util.Optional;

import com.cts.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cts.model.UserCartDetails;

@Repository
public interface UserCartRepository extends JpaRepository<UserCartDetails, Integer> {

    List<UserCartDetails> findByUserId(long userId);

    Optional<UserCartDetails> findByUserAndName(User user, String name);

    Optional<UserCartDetails> findByCartItemIdAndUserId(int cartItemId, long userId);
}
