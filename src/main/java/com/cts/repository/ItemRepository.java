package com.cts.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cts.model.ItemDetails;

@Repository
public interface ItemRepository extends JpaRepository<ItemDetails, Long> {

	ItemDetails findItemByName(String name);
}
