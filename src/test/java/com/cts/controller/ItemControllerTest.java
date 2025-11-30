//package com.cts.controller;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.Mockito.when;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//
//import com.cts.model.ItemDetails;
//import com.cts.service.ItemService;
//
//public class ItemControllerTest {
//
//	@InjectMocks
//	private ItemController itemController;
//
//	@Mock
//	private ItemService itemService;
//
//	@BeforeEach
//	public void setup() {
//		MockitoAnnotations.openMocks(this);
//	}
//
//	@Test
//	public void testLoadPurchaseItems_Success() {
//		// Arrange
//		List<ItemDetails> itemList = Arrays.asList(new ItemDetails(),
//				new ItemDetails());
//		when(itemService.getAllItems()).thenReturn(new ResponseEntity<>(itemList, HttpStatus.OK));
//
//		// Act
//		ResponseEntity<List<ItemDetails>> responseEntity = itemController.loadPurchaseItems();
//
//		// Assert
//		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
//		assertEquals(itemList, responseEntity.getBody());
//	}
//
//	@Test
//	public void testLoadPurchaseItems_Failure() {
//		// Arrange
//		when(itemService.getAllItems()).thenReturn(new ResponseEntity<>(new ArrayList<>(), HttpStatus.BAD_REQUEST));
//
//		// Act
//		ResponseEntity<List<ItemDetails>> responseEntity = itemController.loadPurchaseItems();
//
//		// Assert
//		assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
//		assertEquals(new ArrayList<>(), responseEntity.getBody());
//	}
//}
