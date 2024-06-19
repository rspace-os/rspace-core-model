package com.researchspace.model.inventory;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Transient;
import javax.validation.ConstraintViolationException;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents location inside RSpace Inventory Container.
 * Location can store SubSample, or another Container.
 */
@Entity
@Getter
@Setter
@EqualsAndHashCode(of = {"id", "coordX", "coordY"} )
public class ContainerLocation implements Serializable {

	private static final long serialVersionUID = -8738857988396221527L;

	private Long id;
	
	private Container container;
	
	private Container storedContainer;
	private SubSample storedSubSample;
	
	private int coordX;
	private int coordY;

	public ContainerLocation(Container parentContainer) {
		container = parentContainer;
	}

	/** for hibernate  */
	public ContainerLocation() { }
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getId() {
		return id;
	}

	/**
	 * @return parent container, cannot be null
	 */
	@ManyToOne(optional = false, cascade = CascadeType.MERGE)
	public Container getContainer() {
		return container;
	}
	
	@OneToOne(mappedBy = "parentLocation", cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH })
	public Container getStoredContainer() {
		return storedContainer;
	}
	
	@OneToOne(mappedBy = "parentLocation", cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH })
	public SubSample getStoredSubSample() {
		return storedSubSample;
	}
	
	@Transient
	public InventoryRecord getStoredRecord() {
		if (storedContainer != null) {
			return storedContainer;
		}
		return storedSubSample;
	}

	/**
	 * Shouldn't be called directly, use {@link Container#setRecordInLocation(MovableInventoryRecord, ContainerLocation)}
	 * @param record
	 */
	void addStoredRecord(InventoryRecord record) {
		if (getStoredRecord() != null) {
			throw new IllegalStateException("location already has a record: " + getStoredRecord().getGlobalIdentifier());
		}
		if (record.isContainer()) {
			setStoredContainer((Container) record);
		} else if (record.isSubSample()) {
			setStoredSubSample((SubSample) record);
		} else {
			throw new IllegalArgumentException("can't put record: " + record.getGlobalIdentifier() + " into container location");
		}
	}
	
	/**
	 * Shouldn't be called directly, use {@link Container#removeStoredRecord(ContainerLocation)}
	 */
	void removeStoredRecord() {
		setStoredContainer(null);
		setStoredSubSample(null);
	}

	@PrePersist
	@PreUpdate
	public void validateBeforeSave() {
		if (storedContainer != null && storedSubSample != null) { 
			throw new ConstraintViolationException("Location cannot store both container and subsample", null);
		}
		if (coordY < 1 || coordX < 1) {
			throw new ConstraintViolationException(String.format(
					"Valid location coordinates start at (1,1), cannot be (%d,%d)", coordY, coordX), null);
		}
	}
	
}
