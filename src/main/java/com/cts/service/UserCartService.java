package com.cts.service;

import com.cts.model.UserCartDetails;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface UserCartService {

    /**
     * Add an item (by item name) to the given user's cart.
     * If item already exists in cart, increases the quantity by the provided itemQuantity.
     */
    void addToCart(long userId, String itemName, int itemQuantity);

    /**
     * Get all cart rows in the system.
     */
    ResponseEntity<List<UserCartDetails>> getAllUserCartItems();

    /**
     * Get cart items for a specific user.
     */
    ResponseEntity<List<UserCartDetails>> getUserCartItems(long userId);

    /**
     * Modify the quantity of a cart entry (cartItemId) for the given user.
     */
    void modifyItemQuantity(long userId, int cartItemId, int newQuantity);

    /**
     * Remove a cart entry for a user.
     */
    void removeFromCart(long userId, int cartItemId);

}
