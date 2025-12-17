package com.cts.repository;

import com.cts.model.ItemDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends JpaRepository<ItemDetails, Long> {

    ItemDetails findItemByName(String name);
}
