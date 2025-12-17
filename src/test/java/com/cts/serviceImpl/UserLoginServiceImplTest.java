package com.cts.serviceImpl;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UserLoginServiceImplTest {
//
//    @InjectMocks
//    UserLoginServiceImpl userLoginService;
//
//    @Mock
//    UserRepository userRepository;
//
//    UserDetails userDetails;
//
//    @BeforeEach
//    void setup() {
//        // Initialize a UserDetails object for valid test cases
//        userDetails = new UserDetails();
//        userDetails.setUserId(1);
//        userDetails.setUsername("testUser");
//        userDetails.setPassword("testPassword");
//    }
//
//    // Test case for valid user authentication
//    @Test
//    void testAuthenticateUser_Success() {
//        // Mock the repository to return a user when a valid username is provided
//        when(userRepository.findByUsername("testUser")).thenReturn(java.util.Optional.of(userDetails));
//
//        UserDetails result = userLoginService.authenticateUser("testUser", "testPassword");
//
//        // Verify that the returned user is the one we mocked
//        assertEquals("testUser", result.getUsername());
//    }
//
//    // Test case when the user is not found by username
//    @Test
//    void testAuthenticateUser_UserNotFound() {
//        // Mock the repository to return an empty Optional when username doesn't exist
//        when(userRepository.findByUsername("invalidUser")).thenReturn(java.util.Optional.empty());
//
//        // Verify that an exception is thrown for invalid username
//        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
//            userLoginService.authenticateUser("invalidUser", "testPassword");
//        });
//
//        // Verify the exception message
//        assertEquals("Username not found!!", exception.getMessage());
//    }
//
//    // Test case when password doesn't match for the user
//    @Test
//    void testAuthenticateUser_InvalidPassword() {
//        // Mock the repository to return a user when username is valid
//        when(userRepository.findByUsername("testUser")).thenReturn(java.util.Optional.of(userDetails));
//
//        // Verify that an exception is thrown for invalid password
//        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
//            userLoginService.authenticateUser("testUser", "wrongPassword");
//        });
//
//        // Verify the exception message
//        assertEquals("Invalid username or password", exception.getMessage());
//    }
//
//    // Test case for getting user details by userId when user exists
//    @Test
//    void testGetUserDetails_Success() {
//        // Mock the repository to return a user for the given userId
//        when(userRepository.findById(1)).thenReturn(java.util.Optional.of(userDetails));
//
//        UserDetails result = userLoginService.getuserDetails(1);
//
//        // Verify that the returned user is the one we mocked
//        assertEquals(1, result.getUserId());
//        assertEquals("testUser", result.getUsername());
//    }
//
//    // Test case when user is not found by userId
//    @Test
//    void testGetUserDetails_UserNotFound() {
//        // Mock the repository to return an empty Optional when userId doesn't exist
//        when(userRepository.findById(1)).thenReturn(java.util.Optional.empty());
//
//        // Call the method and verify exception for user not found
//        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
//            userLoginService.getuserDetails(1);
//        });
//
//        // Verify the exception message
//        assertEquals("User not found with ID: 1", exception.getMessage());
//
//        // Verify that the repository method was called once
//        verify(userRepository, times(1)).findById(1);
//    }
}
