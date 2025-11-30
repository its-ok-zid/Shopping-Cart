//package com.cts.repository;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertFalse;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//import static org.mockito.Mockito.when;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import com.cts.model.UserCartDetails;
//
//@ExtendWith(MockitoExtension.class)
//public class UserCartRepositoryTest {
//
//    @Mock
//    private UserCartRepository userCartRepository;
//
//    @Test
//    void testFindByUserDetailsUserId_UserExists() {
//        // Arrange
//        UserDetails userDetails = new UserDetails();
//        userDetails.setUserId(1); // Set user ID
//        UserCartDetails userCartDetails = new UserCartDetails();
//        userCartDetails.setUserDetails(userDetails);
//        userCartDetails.setName("SKU123");
//        List<UserCartDetails> cartList = new ArrayList<>();
//        cartList.add(userCartDetails);
//        when(userCartRepository.findByUserDetailsUserId(1)).thenReturn(cartList);
//
//        // Act
//        List<UserCartDetails> foundCartItems = userCartRepository.findByUserDetailsUserId(1);
//
//        // Assert
//        assertFalse(foundCartItems.isEmpty());
//        assertEquals(1, foundCartItems.size());
//        assertEquals("SKU123", foundCartItems.get(0).getName());
//    }
//
//    @Test
//    void testFindByUserDetailsUserId_UserDoesNotExist() {
//        // Arrange
//        when(userCartRepository.findByUserDetailsUserId(1)).thenReturn(new ArrayList<>());
//
//        // Act
//        List<UserCartDetails> foundCartItems = userCartRepository.findByUserDetailsUserId(1);
//
//        // Assert
//        assertTrue(foundCartItems.isEmpty());
//    }
//
//    @Test
//    void testFindByUserDetailsAndSku_UserCartDetailsFound() {
//        // Arrange
//        UserDetails userDetails = new UserDetails();
//        userDetails.setUserId(1); // Set user ID
//        UserCartDetails userCartDetails = new UserCartDetails();
//        userCartDetails.setUserDetails(userDetails);
//        userCartDetails.setName("SKU123");
//        when(userCartRepository.findByUserDetailsAndSku(userDetails, "SKU123")).thenReturn(Optional.of(userCartDetails));
//
//        // Act
//        Optional<UserCartDetails> foundUserCartDetails = userCartRepository.findByUserDetailsAndSku(userDetails, "SKU123");
//
//        // Assert
//        assertTrue(foundUserCartDetails.isPresent());
//        assertEquals("SKU123", foundUserCartDetails.get().getName());
//    }
//
//    @Test
//    void testFindByUserDetailsAndSku_UserCartDetailsNotFound() {
//        // Arrange
//        UserDetails userDetails = new UserDetails();
//        userDetails.setUserId(1); // Set user ID
//        when(userCartRepository.findByUserDetailsAndSku(userDetails, "SKU123")).thenReturn(Optional.empty());
//
//        // Act
//        Optional<UserCartDetails> foundUserCartDetails = userCartRepository.findByUserDetailsAndSku(userDetails, "SKU123");
//
//        // Assert
//        assertFalse(foundUserCartDetails.isPresent());
//    }
//}
