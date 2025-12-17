package com.cts.serviceImpl;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UserCartServiceImplTest {
//
//	@InjectMocks
//	UserCartServiceImpl userCartService;
//
//	@Mock
//	UserCartRepository userCartRepository;
//
//	@Mock
//	UserRepository userRepository;
//
//	@Mock
//	ItemRepository itemRepository;
//
//	private UserDetails userDetails;
//	private UserCartDetails userCartDetails;
//	private ItemDetails itemDetails;
//
//	@BeforeEach
//	void setUp() {
//
//		userDetails = new UserDetails();
//		userDetails.setUserId(1);
//		userDetails.setUsername("zidan");
//		userDetails.setPassword("Shalinium");
//
//		userCartDetails = new UserCartDetails();
//		userCartDetails.setName("SZ1424");
//		userCartDetails.setItemQuantity(10);
//		userCartDetails.setUserDetails(userDetails);
//
//		itemDetails = new ItemDetails();
//		itemDetails.setName("SZ1424");
//		itemDetails.setItemDescription("Smartphone");
//		itemDetails.setItemCost(100);
//
//	}
//
//	@Test
//	void testAddToCart_UserExist_ItemAlreadyInCart() {
//		when(userRepository.findById(1)).thenReturn(Optional.of(userDetails));
//		when(itemRepository.findItemByName("SZ1424")).thenReturn(itemDetails);
//		when(userCartRepository.findByUserDetailsAndSku(userDetails, "SZ1424"))
//				.thenReturn(Optional.of(userCartDetails));
//
//		userCartService.addToCart(1, "SZ1424", 11);
//		verify(userCartRepository).save(userCartDetails);
//		assertEquals(11, userCartDetails.getItemQuantity());
//	}
//
//	@Test
//	void testAddToCart_UserExist_ItemNotInCart() {
//		when(userRepository.findById(1)).thenReturn(Optional.of(userDetails));
//		when(itemRepository.findItemByName("SZ1424")).thenReturn(itemDetails);
//		when(userCartRepository.findByUserDetailsAndSku(userDetails, "SZ1424")).thenReturn(Optional.empty());
//
//		userCartService.addToCart(1, "SZ1424", 7);
//
//		verify(userCartRepository).save(any(UserCartDetails.class));
//	}
//
//	@Test
//	void testAddToCart_UserNotFound() {
//		when(userRepository.findById(1)).thenReturn(Optional.empty());
//
//		Exception exception = assertThrows(IllegalArgumentException.class,
//				() -> userCartService.addToCart(1, "SZ1424", 3));
//
//		assertEquals("User not found", exception.getMessage());
//	}
//
//	@Test
//	void testAddToCart_ItemNotFound() {
//
//		when(userRepository.findById(1)).thenReturn(Optional.of(userDetails));
//		when(itemRepository.findItemByName("SZ1424")).thenReturn(null);
//
//		Exception exception = assertThrows(IllegalArgumentException.class,
//				() -> userCartService.addToCart(1, "SZ1424", 3));
//
//		assertEquals("Item not found!!", exception.getMessage());
//	}
//
//	@Test
//	void testGetAllUserCartItems_Success() {
//
//		List<UserCartDetails> cartItems = List.of(userCartDetails);
//
//		when(userCartRepository.findAll()).thenReturn(cartItems);
//
//		ResponseEntity<List<UserCartDetails>> response = userCartService.getAllUserCartItems();
//
//		assertEquals(HttpStatus.OK, response.getStatusCode());
//		assertEquals(1, response.getBody().size());
//	}
//
//	@Test
//	void testGetAllUserCartItems_Exception() {
//		when(userCartRepository.findAll()).thenThrow(new RuntimeException("Database error"));
//		ResponseEntity<List<UserCartDetails>> response = userCartService.getAllUserCartItems();
//		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
//		assertTrue(response.getBody().isEmpty());
//	}
//
//	@Test
//	void testModifyItemQuantity_Success() {
//		when(userRepository.findById(1)).thenReturn(Optional.of(userDetails));
//		when(userCartRepository.findByUserDetailsAndSku(userDetails, "SZ1424"))
//				.thenReturn(Optional.of(userCartDetails));
//
//		UserCartDetails updatedUserCartDetails = new UserCartDetails();
//		updatedUserCartDetails.setItemQuantity(7);
//
//		userCartService.modifyItemQuantity(1, "SZ1424", updatedUserCartDetails);
//
//		verify(userCartRepository).save(userCartDetails);
//		assertEquals(7, userCartDetails.getItemQuantity());
//	}
//
//	@Test
//	void testModifyItemQuantity_UserNotFound() {
//		when(userRepository.findById(1)).thenReturn(Optional.empty());
//
//		Exception exception = assertThrows(IllegalArgumentException.class,
//				() -> userCartService.modifyItemQuantity(1, "ITEM123", userCartDetails));
//
//		assertEquals("User not found", exception.getMessage());
//	}
//
//
//	@Test
//	void testModifyItemQuantity_ItemNotInCart() {
//		when(userRepository.findById(1)).thenReturn(Optional.of(userDetails));
//		when(userCartRepository.findByUserDetailsAndSku(userDetails, "ITEM123")).thenReturn(Optional.empty());
//
//		Exception exception = assertThrows(IllegalArgumentException.class,
//				() -> userCartService.modifyItemQuantity(1, "ITEM123", userCartDetails));
//
//		assertEquals("Item  not found in user's cart", exception.getMessage());
//	}
//
//	@Test
//	void testGetUserCartItems_Success() {
//		List<UserCartDetails> cartItems = List.of(userCartDetails);
//		when(userCartRepository.findByUserDetailsUserId(1)).thenReturn(cartItems);
//
//		ResponseEntity<List<UserCartDetails>> response = userCartService.getUserCartItems(1);
//
//		assertEquals(HttpStatus.OK, response.getStatusCode());
//		assertEquals(1, response.getBody().size());
//
//	}
//
//	@Test
//	void testUserCartItems_Exception() {
//		when(userCartRepository.findByUserDetailsUserId(1)).thenThrow(new RuntimeException("Database error"));
//
//		ResponseEntity<List<UserCartDetails>> response = userCartService.getUserCartItems(1);
//
//		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
//		assertTrue(response.getBody().isEmpty());
//	}

}
