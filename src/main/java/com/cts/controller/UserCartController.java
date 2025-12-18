package com.cts.controller;

import com.cts.exception.ApiResponse;
import com.cts.model.User;
import com.cts.model.UserCartDetails;
import com.cts.repository.UserRepository;
import com.cts.service.UserCartService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/usercart")
@PreAuthorize("hasRole('USER')")
public class UserCartController {

    private final UserCartService userCartService;
    private final UserRepository userRepository;

    public UserCartController(UserCartService userCartService, UserRepository userRepository) {
        this.userCartService = userCartService;
        this.userRepository = userRepository;
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<String>> addToCart(
            Authentication auth,
            @RequestParam String itemName,
            @RequestParam int quantity) {

        userCartService.addToCart(userCartService.getUserId(auth), itemName, quantity);

        return ResponseEntity.ok(
                ApiResponse.<String>builder()
                        .success(true)
                        .message("Item added to cart")
                        .data(null)
                        .build());
    }


    // Admin: get all carts (protected by SecurityConfig)
    @GetMapping("/all")
    public ResponseEntity<List<UserCartDetails>> getAllUserCartItems() {
        return userCartService.getAllUserCartItems();
    }

    // User: get their own cart
    @GetMapping("/me")
    public ResponseEntity<List<UserCartDetails>> getMyCart(Authentication auth) {
        String username = auth.getName();
        User user = userRepository.findByUsername(username).orElseThrow();
        return userCartService.getUserCartItems(user.getId());
    }

    // Modify quantity of a cart item (owner only)
    @PutMapping("/modify")
    public ResponseEntity<String> modifyUserCartItem(Authentication auth,
                                                     @RequestParam int cartItemId,
                                                     @RequestParam int newQuantity) {
        String username = auth.getName();
        User user = userRepository.findByUsername(username).orElseThrow();
        userCartService.modifyItemQuantity(user.getId(), cartItemId, newQuantity);
        return ResponseEntity.ok("Item quantity modified successfully");
    }

    // Remove item
    @DeleteMapping("/remove")
    public ResponseEntity<String> removeItem(Authentication auth, @RequestParam int cartItemId) {
        String username = auth.getName();
        User user = userRepository.findByUsername(username).orElseThrow();
        userCartService.removeFromCart(user.getId(), cartItemId);
        return ResponseEntity.ok("Item removed");
    }
}
