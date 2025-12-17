package com.cts.repository;

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class UserRepositoryTest {
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Test
//    void testFindByUsername_UserExists() {
//        // Arrange
//        UserDetails userDetails = new UserDetails();
//        userDetails.setUsername("zidan@gmail.com");
//        userDetails.setPassword("Zid@1234");
//
//        // Save the user details to the database
//        userRepository.save(userDetails);
//
//        // Act
//        Optional<UserDetails> foundUser = userRepository.findByUsername("testUser");
//
//        // Assert
//        assertTrue(foundUser.isPresent());
//        assertEquals("testUser", foundUser.get().getUsername());
//    }
//
//
//
//    @Test
//    void testFindByUsername_UserDoesNotExist() {
//        // Act
//        Optional<UserDetails> foundUser = userRepository.findByUsername("nonExistentUser");
//
//        // Assert
//        assertFalse(foundUser.isPresent());
//    }
}
