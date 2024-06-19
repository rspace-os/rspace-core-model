package com.researchspace.model.elninventory;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;

import com.researchspace.model.inventory.InventoryRecord;
import com.researchspace.model.inventory.InventoryRecordConnectedEntity;
import com.researchspace.model.units.QuantityInfo;

import lombok.EqualsAndHashCode;
import lombok.Setter;

/**
 * Represents usage of a particular inventory material within ELN experiment.
 */
@Entity
@Audited
@Setter
@EqualsAndHashCode(of={"id"}, callSuper = false)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MaterialUsage extends InventoryRecordConnectedEntity implements Serializable {

	private static final long serialVersionUID = 4330059378109785385L;

	private Long id;
	
	private QuantityInfo usedQuantity;
	
	private ListOfMaterials parentLom;
	
	public MaterialUsage(ListOfMaterials parentLom, InventoryRecord invRec, QuantityInfo usedQuantity) {
		setParentLom(parentLom);
		setInventoryRecord(invRec);
		setUsedQuantity(usedQuantity);
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	public Long getId() {
		return id;
	}

	@Embedded
	@AttributeOverrides({
		@AttributeOverride(name = "unitId", column = @Column(name = "quantityUnitId")),
		@AttributeOverride(name = "numericValue", column = @Column(name = "quantityNumericValue", precision = 19, scale = 3))
	})
	public QuantityInfo getUsedQuantity() {
		return usedQuantity;
	}

	public void setUsedQuantity(QuantityInfo newQuantity) {
		if (newQuantity != null && newQuantity.getUnitId() != null) {
			if (BigDecimal.ZERO.compareTo(newQuantity.getNumericValue()) > 0) {
				throw new IllegalArgumentException("Trying to set negative record quantity: " + newQuantity.getNumericValue());
			}
		}
		this.usedQuantity = newQuantity;
	}
	
	@ManyToOne(cascade = CascadeType.MERGE)
	@JoinColumn(nullable = false)
	private ListOfMaterials getParentLom() {
		return parentLom;
	}
	
	@Transient
	public String getUsedQuantityPlainString() {
		return getUsedQuantity() == null ? "" : getUsedQuantity().toPlainString();
	}

}
