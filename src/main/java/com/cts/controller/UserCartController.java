package com.cts.controller;

import com.cts.model.User;
import com.cts.model.UserCartDetails;
import com.cts.repository.UserRepository;
import com.cts.service.UserCartService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usercart")
public class UserCartController {

    private final UserCartService userCartService;
    private final UserRepository userRepository;

    public UserCartController(UserCartService userCartService, UserRepository userRepository) {
        this.userCartService = userCartService;
        this.userRepository = userRepository;
    }

    // Authenticated users add to own cart
    @PostMapping("/add")
    public ResponseEntity<String> addToCart(Authentication auth,
                                            @RequestParam String itemName,
                                            @RequestParam int itemQuantity) {
        String username = auth.getName();
        User user = userRepository.findByUsername(username).orElseThrow();
        userCartService.addToCart(user.getId(), itemName, itemQuantity);
        return ResponseEntity.ok("Item added to cart successfully");
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
