package com.cts.serviceImpl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.cts.model.ItemDetails;
import com.cts.repository.ItemRepository;
import com.cts.service.ItemService;

@Service
public class ItemServiceImpl implements ItemService {

	@Autowired
	ItemRepository itemRepository;

	@Override
	public ResponseEntity<List<ItemDetails>> getAllItems() {

		try {
			return new ResponseEntity<>(itemRepository.findAll(), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ResponseEntity<>(new ArrayList<>(), HttpStatus.BAD_REQUEST);

	}

	@Override
	public ItemDetails getItemByName(String name) {
		return itemRepository.findItemByName(name);
	}
	
	

}
