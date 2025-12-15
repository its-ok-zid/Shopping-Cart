//package com.cts.serviceImpl;
//
//import static org.junit.jupiter.api.Assertions.assertAll;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//import static org.mockito.Mockito.times;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//import java.util.List;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//
//import com.cts.model.ItemDetails;
//import com.cts.repository.ItemRepository;
//@ExtendWith(MockitoExtension.class)
//public class ItemServiceImplTest {
//
//    @InjectMocks
//    ItemServiceImpl itemService;
//
//    @Mock
//    ItemRepository itemRepository;
//
//    ItemDetails itemDetails;
//
//    @BeforeEach
//    void setup() {
//        itemDetails = new ItemDetails();
//        itemDetails.setName("SZ1424");
//        itemDetails.setItemDescription("Sample Item");
//        itemDetails.setItemCost(100);
//    }
//
//    @Test
//    void shouldReturnAllItemsWhenRepositoryReturnsData() {
//        List<ItemDetails> items = List.of(itemDetails);
//        when(itemRepository.findAll()).thenReturn(items);
//
//        ResponseEntity<List<ItemDetails>> response = itemService.getAllItems();
//
//        assertAll(
//            () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
//            () -> assertEquals(1, response.getBody().size()),
//            () -> assertEquals("SZ1424", response.getBody().get(0).getName()),
//            () -> assertEquals("Sample Item", response.getBody().get(0).getItemDescription())
//        );
//
//        verify(itemRepository, times(1)).findAll();
//    }
//
//    @Test
//    void shouldReturnBadRequestWhenRepositoryThrowsException() {
//        when(itemRepository.findAll()).thenThrow(new RuntimeException("Database error"));
//
//        ResponseEntity<List<ItemDetails>> response = itemService.getAllItems();
//
//        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
//        assertTrue(response.getBody().isEmpty());
//
//        verify(itemRepository, times(1)).findAll();
//    }
//
//    @Test
//    void shouldReturnItemDetailsForValidSku() {
//        when(itemRepository.findItemByName("SZ1424")).thenReturn(itemDetails);
//
//        ItemDetails result = itemService.getItemByName("SZ1424");
//
//        assertAll(
//            () -> assertEquals("SZ1424", result.getName()),
//            () -> assertEquals("Sample Item", result.getItemDescription()),
//            () -> assertEquals(100, result.getItemCost())
//        );
//
//        verify(itemRepository, times(1)).findItemByName("SZ1424");
//    }
//
//    @Test
//    void shouldReturnNullForInvalidSku() {
//        when(itemRepository.findItemByName("INVALID")).thenReturn(null);
//
//        ItemDetails result = itemService.getItemByName("INVALID");
//
//        assertEquals(null, result);
//        verify(itemRepository, times(1)).findItemByName("INVALID");
//    }
//}
