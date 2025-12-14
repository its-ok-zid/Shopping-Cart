//package com.cts.dto;
//
//
//
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.junit.jupiter.api.Assertions.assertNull;
//
//import org.junit.jupiter.api.Test;
//
//public class SignUpRequestTest {
//
//    @Test
//    public void testConstructorAndGetters() {
//        // Arrange
//    	int userId=1;
//        String username = "zidan@gmail.com";
//        String password = "zid1234@";
//
//        // Act
//        SignUpRequestDTO signUpRequest = new SignUpRequestDTO(userId,username, password);
//
//        // Assert
//        assertNotNull(signUpRequest);
//        assertEquals(username, signUpRequest.getUsername());
//        assertEquals(password, signUpRequest.getPassword());
//    }
//
//    @Test
//    public void testSetters() {
//        // Arrange
//        String newUsername = "zidan@gmail.com";
//        String newPassword = "zid1234@";
//        SignUpRequestDTO signUpRequest = new SignUpRequestDTO();
//
//        // Act
//        signUpRequest.setUsername(newUsername);
//        signUpRequest.setPassword(newPassword);
//
//        // Assert
//        assertEquals(newUsername, signUpRequest.getUsername());
//        assertEquals(newPassword, signUpRequest.getPassword());
//    }
//
//    @Test
//    public void testEmptyConstructor() {
//        // Arrange & Act
//        SignUpRequestDTO signUpRequest = new SignUpRequestDTO();
//
//        // Assert
//        assertNotNull(signUpRequest);
//        assertNull(signUpRequest.getUsername());
//        assertNull(signUpRequest.getPassword());
//    }
//}
