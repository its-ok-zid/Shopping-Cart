package com.cts.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.cts.model.ItemDetails;

public interface ItemService {

	ResponseEntity<List<ItemDetails>> getAllItems();
	
	ItemDetails getItemByName(String name);
}
