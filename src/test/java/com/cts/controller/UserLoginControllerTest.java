//package com.cts.controller;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.Mockito.when;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//
//import com.cts.dto.SignUpRequest;
//import com.cts.exception.LoginException;
//
//public class UserLoginControllerTest {
//
//    @Mock
//    private UserLoginService loginService;
//
//    @InjectMocks
//    private UserLoginController loginController;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    void testLogin_Success() throws LoginException {
//        // Arrange
//        String username = "testUser";
//        String password = "password";
//        SignUpRequest signUpRequest = new SignUpRequest(1,username, password);
//        UserDetails userDetails = new UserDetails();
//        userDetails.setUsername(username);
//        userDetails.setPassword(password);
//        when(loginService.authenticateUser(username, password)).thenReturn(userDetails);
//
//        // Act
//        ResponseEntity<UserDetails> response = loginController.login(signUpRequest);
//
//        // Assert
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals(userDetails, response.getBody());
//    }
//
//    @Test
//    void testLogin_InvalidCredentials() {
//        // Arrange
//        SignUpRequest signUpRequest = new SignUpRequest(0,null, null);
//
//        // Act and Assert
//        LoginException exception = org.junit.jupiter.api.Assertions.assertThrows(LoginException.class,
//                () -> loginController.login(signUpRequest));
//        assertEquals("Invalid username or Password", exception.getMessage());
//    }
//
//    @Test
//    void testGetUserDetails_Success() {
//        // Arrange
//        int userId = 1;
//        UserDetails userDetails = new UserDetails();
//        userDetails.setUserId(userId);
//        when(loginService.getuserDetails(userId)).thenReturn(userDetails);
//
//        // Act
//        ResponseEntity<UserDetails> responseEntity = loginController.getUserDetails(userId);
//
//        // Assert
//        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
//        assertEquals(userDetails, responseEntity.getBody());
//    }
//
//
//
//
//
//}
