package com.cts.repository;

import com.cts.model.User;
import com.cts.model.UserCartDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserCartRepository extends JpaRepository<UserCartDetails, Integer> {

    List<UserCartDetails> findByUserId(long userId);

    Optional<UserCartDetails> findByUserAndName(User user, String name);

    Optional<UserCartDetails> findByCartItemIdAndUserId(int cartItemId, long userId);
}
