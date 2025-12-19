
package com.researchspace.model.inventory;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.hibernate.search.annotations.Field;

/**
 * Basic model used to represent all barcodes added to inventory items
 * 
 */
@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(callSuper = false, of = {"id", "barcodeData", "format", "creationDate"})
@Audited
public class Barcode extends InventoryRecordConnectedEntity implements Serializable {

	private static final long serialVersionUID = 1015505407767174713L;

	private Long id;
	
	// indexing barcode content separately 
	@Field(name = "barcodeData")
	private String barcodeData;

	private String format;

	// indexing barcode description together with field data
	@Field(name = "fieldData") 
	private String description;

	private Date creationDate;
	private String createdBy;
	private boolean deleted;
	
	public Barcode(String barcodeData, String createdBy) {
		setBarcodeData(barcodeData);
		setCreatedBy(createdBy);
		setCreationDate(new Date());
	}

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	public Long getId() {
		return id;
	}
	
	/**
	 * Date of entity creation, i.e. date of uploading inventory file to RSpace.
	 * Returns a copy of the stored date object for better encapsulation
	 */
	@Column(nullable = false, updatable = false)
	@Temporal(TemporalType.TIMESTAMP)
	public Date getCreationDate() {
		return creationDate == null ? null : new Date(creationDate.getTime());
	}

	/**
	 * Performs shallow copy of the attachment with copied reference to FileProperty. 
	 * Does not set InventoryRecord relation.
	 */
	public Barcode shallowCopy() {
		Barcode copy = new Barcode(getBarcodeData(), getCreatedBy());
		copy.setDescription(getDescription());
		copy.setDeleted(isDeleted());
		return copy;
	}
	
}
