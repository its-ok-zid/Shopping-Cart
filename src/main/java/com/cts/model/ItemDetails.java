package com.cts.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Entity
@NoArgsConstructor
@Data
@Table(name = "item")
public class ItemDetails {

    @Id
    @Column(name = "item_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "item_name")
    private String name;

    @Column(name = "item_description")
    private String itemDescription;

    @Column(name = "item_cost")
    private float itemCost;

    @Column(name = "mfr_number")
    private String mfrNo;

    @Column(name = "item_stock")
    private int stock;

    @Column(name = "thumbnail_id")
    private String thumbnailId;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ItemDetails that = (ItemDetails) o;
        return id == that.id && Float.compare(itemCost, that.itemCost) == 0 && stock == that.stock && Objects.equals(name, that.name) && Objects.equals(itemDescription, that.itemDescription) && Objects.equals(mfrNo, that.mfrNo) && Objects.equals(thumbnailId, that.thumbnailId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, itemDescription, itemCost, mfrNo, stock, thumbnailId);
    }
}
