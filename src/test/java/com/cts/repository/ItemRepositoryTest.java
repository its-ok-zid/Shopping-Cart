package com.cts.repository;

import com.cts.model.ItemDetails;
import com.cts.serviceImpl.ItemServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemRepositoryTest {

    @Mock
    private ItemRepository itemRepositoryMock;

    @InjectMocks
    private ItemServiceImpl itemService;

    @Test
    public void testfindItemByName_ItemExists() {

        // Arrange
        String sku = "testSKU";
        ItemDetails item = new ItemDetails();
        item.setName(sku);
        when(itemRepositoryMock.findItemByName(sku)).thenReturn(item);

        // Act
        ItemDetails result = itemService.getItemByName(sku);

        // Assert
        assertEquals(sku, result.getName());
    }

    @Test
    public void testfindItemByName_ItemDoesNotExist() {
        // Arrange
        String sku = "nonExistingSKU";
        when(itemRepositoryMock.findItemByName(sku)).thenReturn(null);

        // Act
        ItemDetails result = itemService.getItemByName(sku);

        // Assert
        assertEquals(null, result);
    }
}