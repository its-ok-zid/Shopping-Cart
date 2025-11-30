//package com.cts.model;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.Mockito.mock;
//
//import org.junit.jupiter.api.Test;
//
//public class UserCartDetailsTest {
//
////    @Test
////    public void testConstructorAndGetters() {
////        // Arrange
////        UserDetails userDetails = new UserDetails();
////        userDetails.setUsername("testUser");
////        userDetails.setPassword("password");
////
////        // Act
////        UserCartDetails userCartDetails = new UserCartDetails();
////
////        // Assert
////        assertEquals(0, userCartDetails.getCartItemId());
////        assertEquals(userDetails, userCartDetails.getUserDetails());
////        assertEquals(2, userCartDetails.getItemQuantity());
////        assertEquals("Item description", userCartDetails.getItemDescription());
////        assertEquals(100.0f, userCartDetails.getItemCost());
////        assertEquals('A', userCartDetails.getStatus());
////    }
//
//    @Test
//    public void testSetterMethods() {
//        // Arrange
//        UserCartDetails userCartDetails = new UserCartDetails();
//
//        // Act
//        userCartDetails.setCartItemId(1);
//        userCartDetails.setUserDetails(new UserDetails());
//        userCartDetails.setName("SKU123");
//        userCartDetails.setItemQuantity(2);
//        userCartDetails.setItemDescription("Item description");
//        userCartDetails.setItemCost(100.0f);
//        userCartDetails.setStatus('A');
//
//        // Assert
//        assertEquals(1, userCartDetails.getCartItemId());
//        assertEquals("SKU123", userCartDetails.getName());
//        assertEquals(2, userCartDetails.getItemQuantity());
//        assertEquals("Item description", userCartDetails.getItemDescription());
//        assertEquals(100.0f, userCartDetails.getItemCost());
//        assertEquals('A', userCartDetails.getStatus());
//    }
//
//
//    @Test
//    public void testGetUserDetails() {
//        // Arrange
//        UserDetails userDetails = mock(UserDetails.class);
//        UserCartDetails userCartDetails = new UserCartDetails();
//        userCartDetails.setUserDetails(userDetails);
//
//        // Act
//        UserDetails retrievedUserDetails = userCartDetails.getUserDetails();
//
//        // Assert
//        assertEquals(userDetails, retrievedUserDetails);
//    }
//
//    // Additional test cases can be added here to cover any custom business logic or utility methods
//}
