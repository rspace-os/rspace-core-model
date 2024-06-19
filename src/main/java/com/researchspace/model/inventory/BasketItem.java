package com.researchspace.model.inventory;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents an item stored stored in inventory basket.
 */
@Entity
@Setter
@EqualsAndHashCode(of={"id"}, callSuper = false)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BasketItem extends InventoryRecordConnectedEntity implements Serializable {

	private Long id;
	
	private Basket basket;
	
	public BasketItem(Basket parentBasket, InventoryRecord invRec) {
		setBasket(parentBasket);
		setInventoryRecord(invRec);
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	public Long getId() {
		return id;
	}

	@ManyToOne(cascade = CascadeType.MERGE)
	@JoinColumn(nullable = false)
	private Basket getBasket() {
		return basket;
	}
	
}
