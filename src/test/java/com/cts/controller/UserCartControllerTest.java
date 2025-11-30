package com.cts.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.cts.model.UserCartDetails;
import com.cts.service.ItemService;
import com.cts.service.UserCartService;

@ExtendWith(MockitoExtension.class)
class UserCartControllerTest {
//
//    @InjectMocks
//    private UserCartController userCartController;
//
//    @Mock
//    private UserCartService userCartService;
//
//    @Mock
//    private ItemService itemService;
//
//    private UserCartDetails userCartDetails;
//
//    private int userId;
//
//    @BeforeEach
//    void setUp() {
//        userCartDetails = new UserCartDetails();
//        userCartDetails.setName("ITEM123");
//        userCartDetails.setItemQuantity(2);
//        userCartDetails.setItemDescription("Sample Item");
//        userCartDetails.setItemCost(100);
//    }
//
//    // Test for addToCart method
//
//    @Test
//    void testAddToCart_Success() {
//        doNothing().when(userCartService).addToCart(1, "ITEM123", 2);
//
//        ResponseEntity<String> response = userCartController.addToCart(1, "ITEM123", 2);
//
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals("Item added to cart successfully", response.getBody());
//    }
//
//    @Test
//    void testAddToCart_Error() {
//        doThrow(new RuntimeException("Error while processing request!"))
//                .when(userCartService).addToCart(1, "ITEM123", 2);
//
//        ResponseEntity<String> response = userCartController.addToCart(1, "ITEM123", 2);
//
//        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
//        assertEquals("Error while processing request!", response.getBody());
//    }
//
//    // Test for getAllUserCartItems method (retrieving all cart items)
//
//    @Test
//    void testGetAllUserCartItems_Success() {
//        List<UserCartDetails> cartItems = List.of(userCartDetails);
//        when(userCartService.getAllUserCartItems()).thenReturn(new ResponseEntity<>(cartItems, HttpStatus.OK));
//
//        ResponseEntity<List<UserCartDetails>> response = userCartController.getUserCartItems();
//
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals(1, response.getBody().size());
//        assertEquals("ITEM123", response.getBody().get(0).getName());
//    }
//
//    @Test
//    void testGetAllUserCartItems_Error() {
//        when(userCartService.getAllUserCartItems()).thenReturn(new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR));
//
//        ResponseEntity<List<UserCartDetails>> response = userCartController.getUserCartItems();
//
//        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
//        assertNull(response.getBody());
//    }
//
//    // Test for modifyUserCartItem method
//
//    @Test
//    void testModifyUserCartItem_Success() {
//        doNothing().when(userCartService).modifyItemQuantity(1, "ITEM123", userCartDetails);
//
//        ResponseEntity<String> response = userCartController.modifyUserCartItem(1, "ITEM123", userCartDetails);
//
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals("Item quantity modified successfully", response.getBody());
//    }
//
//    @Test
//    void testModifyUserCartItem_Error() {
//        // Arrange: Simulate a RuntimeException thrown by userCartService.modifyItemQuantity
//        doThrow(new RuntimeException("Error modifying cart item"))
//                .when(userCartService).modifyItemQuantity(1, "ITEM123", userCartDetails);
//
//        // Act: Call the controller method
//        ResponseEntity<String> response = userCartController.modifyUserCartItem(1, "ITEM123", userCartDetails);
//
//        // Assert: Verify the response status and body
//        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
//        assertEquals("Error modifying cart item", response.getBody());
//
//        // Verify: Ensure modifyItemQuantity was called once with expected arguments
//        verify(userCartService, times(1)).modifyItemQuantity(1, "ITEM123", userCartDetails);
//    }
//
//    // Test for getAllUserCartItems (with userId) method
//
//    @Test
//    void testGetUserCartItems_Success() {
//        List<UserCartDetails> cartItems = List.of(userCartDetails);
//        when(userCartService.getUserCartItems(1)).thenReturn(new ResponseEntity<>(cartItems, HttpStatus.OK));
//
//        ResponseEntity<List<UserCartDetails>> response = userCartController.getAllUserCartItems(1);
//
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals(1, response.getBody().size());
//        assertEquals("ITEM123", response.getBody().get(0).getName());
//    }
//
//    @Test
//    void testGetAllUserCartItems_ExceptionHandling() {
//        // Arrange: Simulate a RuntimeException thrown by userCartService
//        doThrow(new RuntimeException("Database error"))
//                .when(userCartService).getUserCartItems(userId);
//
//        // Act: Call the controller method
//        ResponseEntity<List<UserCartDetails>> response = userCartController.getAllUserCartItems(userId);
//
//        // Assert: Verify the response status and body
//        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
//        assertNull(response.getBody());
//
//        // Verify: Ensure the service method was called once
//        verify(userCartService, times(1)).getUserCartItems(userId);
//    }
//
}
