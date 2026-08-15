package com.cts.controller;

import com.cts.exception.ApiResponse;
import com.cts.model.UserCartDetails;
import com.cts.service.UserCartService;
import org.springframework.http.ResponseEntity;
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
public class UserCartController {

    private final UserCartService userCartService;

    public UserCartController(UserCartService userCartService) {
        this.userCartService = userCartService;
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<String>> addToCart(
            @RequestParam long userId,
            @RequestParam String itemName,
            @RequestParam int quantity) {

        userCartService.addToCart(userId, itemName, quantity);

        return ResponseEntity.ok(
                ApiResponse.<String>builder()
                        .success(true)
                        .message("Item added to cart")
                        .data(null)
                        .build());
    }

    @GetMapping("/all")
    public ResponseEntity<List<UserCartDetails>> getAllUserCartItems() {
        return userCartService.getAllUserCartItems();
    }

    @GetMapping("/user")
    public ResponseEntity<List<UserCartDetails>> getUserCartItems(@RequestParam long userId) {
        return userCartService.getUserCartItems(userId);
    }

    @PutMapping("/modify")
    public ResponseEntity<String> modifyUserCartItem(@RequestParam long userId,
                                                     @RequestParam int cartItemId,
                                                     @RequestParam int newQuantity) {
        userCartService.modifyItemQuantity(userId, cartItemId, newQuantity);
        return ResponseEntity.ok("Item quantity modified successfully");
    }

    @DeleteMapping("/remove")
    public ResponseEntity<String> removeItem(@RequestParam long userId, @RequestParam int cartItemId) {
        userCartService.removeFromCart(userId, cartItemId);
        return ResponseEntity.ok("Item removed");
    }
}
