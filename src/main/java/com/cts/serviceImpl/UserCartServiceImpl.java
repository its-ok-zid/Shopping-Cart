package com.cts.serviceImpl;

import com.cts.model.ItemDetails;
import com.cts.model.User;
import com.cts.model.UserCartDetails;
import com.cts.repository.ItemRepository;
import com.cts.repository.UserCartRepository;
import com.cts.repository.UserRepository;
import com.cts.service.UserCartService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserCartServiceImpl implements UserCartService {

    private final UserCartRepository userCartRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Autowired
    public UserCartServiceImpl(UserCartRepository userCartRepository,
                               UserRepository userRepository,
                               ItemRepository itemRepository) {
        this.userCartRepository = userCartRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    public void addToCart(long userId, String itemName, int itemQuantity) {
        // find user
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        }
        User user = userOptional.get();

        // find item by name
        ItemDetails itemDetails = itemRepository.findItemByName(itemName);
        if (itemDetails == null) {
            throw new IllegalArgumentException("Item not found with name: " + itemName);
        }

        // check if this item already exists in user's cart (by name)
        Optional<UserCartDetails> optionalUserCartDetails = userCartRepository.findByUserAndName(user, itemName);
        if (optionalUserCartDetails.isPresent()) {
            // update existing quantity
            UserCartDetails existing = optionalUserCartDetails.get();
            int updatedQuantity = existing.getItemQuantity() + itemQuantity;
            existing.setItemQuantity(updatedQuantity);
            userCartRepository.save(existing);
        } else {
            // create new cart row
            UserCartDetails newRow = new UserCartDetails();
            newRow.setUser(user);
            newRow.setName(itemName);
            newRow.setItemQuantity(itemQuantity);
            newRow.setItemDescription(itemDetails.getItemDescription());
            newRow.setItemCost(itemDetails.getItemCost());
            // set status - adjust type if your entity expects Character/String
            newRow.setStatus('A');
            userCartRepository.save(newRow);
        }
    }

    @Override
    public ResponseEntity<List<UserCartDetails>> getAllUserCartItems() {
        try {
            List<UserCartDetails> all = userCartRepository.findAll();
            return new ResponseEntity<>(all, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<List<UserCartDetails>> getUserCartItems(long userId) {
        try {
            // ensure user exists (optional)
            if (!userRepository.existsById(userId)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ArrayList<>());
            }
            List<UserCartDetails> items = userCartRepository.findByUserId(userId);
            return ResponseEntity.ok(items);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ArrayList<>());
        }
    }

    @Override
    public void modifyItemQuantity(long userId, int cartItemId, int newQuantity) {
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("User not found with id: " + userId);
        }

        Optional<UserCartDetails> optional = userCartRepository.findByCartItemIdAndUserId(cartItemId, userId);
        if (optional.isEmpty()) {
            throw new IllegalArgumentException("Cart item not found for this user or does not belong to user");
        }

        UserCartDetails cart = optional.get();
        if (newQuantity <= 0) {
            userCartRepository.delete(cart);
        } else {
            cart.setItemQuantity(newQuantity);
            userCartRepository.save(cart);
        }
    }

    @Override
    public void removeFromCart(long userId, int cartItemId) {
        Optional<UserCartDetails> optional = userCartRepository.findByCartItemIdAndUserId(cartItemId, userId);
        if (optional.isEmpty()) {
            throw new IllegalArgumentException("Cart item not found for this user or does not belong to user");
        }
        userCartRepository.delete(optional.get());
    }


    public long getUserId(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            throw new IllegalStateException("User is not authenticated");
        }

        Object principal = auth.getPrincipal();

        if (principal instanceof User) {
            Long id = ((User) principal).getId();
            if (id != null) {
                return id;
            }
        }

        // Fallback to lookup by username from repository (handles Spring Security's User principal)
        String username = auth.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("User not found: " + username))
                .getId();
    }
}
