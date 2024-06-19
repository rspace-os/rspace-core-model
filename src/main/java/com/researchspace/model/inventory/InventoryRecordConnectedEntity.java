package com.researchspace.model.inventory;

import javax.persistence.CascadeType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Transient;
import javax.validation.ConstraintViolationException;

import org.hibernate.envers.Audited;

import com.researchspace.model.core.GlobalIdentifier;

import lombok.Getter;
import lombok.Setter;

/**
 * Abstract class inherited by entities connected to Inventory Records
 */
@MappedSuperclass
@Getter
@Setter
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Audited
public abstract class InventoryRecordConnectedEntity {

	/* 
	 * We want a foreign-key link to InventoryRecord, but as IR is abstract and not a db table
	 * we need a field (column) for each concrete subclass i.e. Sample/SubSample/Container. 
	 */
	private Sample sample;
	private SubSample subSample;
	private Container container;

	@ManyToOne(cascade = CascadeType.MERGE)
	private Sample getSample() {
		return sample;
	}

	@ManyToOne(cascade = CascadeType.MERGE)
	private SubSample getSubSample() {
		return subSample;
	}

	@ManyToOne(cascade = CascadeType.MERGE)
	private Container getContainer() {
		return container;
	}

	/**
	 * @return inventory record holding this field.
	 */
	@Transient
	public InventoryRecord getInventoryRecord() {
		if (sample != null) {
			return sample;
		}
		if (subSample != null) {
			return subSample;
		}
		if (container != null) {
			return container;
		}
		return null;
	}
	
	/**
	 * Sets parent Sample/SubSample.
	 */
	public void setInventoryRecord(InventoryRecord invRec) {
		if (invRec instanceof Sample) {
			sample = (Sample) invRec;
		} else if (invRec instanceof SubSample) {
			subSample = (SubSample) invRec;
		} else if (invRec instanceof Container) {
			container = (Container) invRec;
		}
	}

	/**
	 * @return global id of a record to which this entity is connected to (or null if none)
	 */
	@Transient
	public GlobalIdentifier getConnectedRecordOid() {
		InventoryRecord parent = getInventoryRecord();
		return parent != null ? parent.getOid() : null;
	}
	
	@Transient
	public String getConnectedRecordGlobalIdentifier() {
		GlobalIdentifier parentOid = getConnectedRecordOid();
		return parentOid != null ? parentOid.getIdString() : null;
	}	

	@PrePersist
	@PreUpdate
	public void validateBeforeSave() {
		int parentCount = getNonInventoryRecordParentCount();
		parentCount = sample == null ? parentCount : ++parentCount;
		parentCount = subSample == null ? parentCount : ++parentCount;
		parentCount = container == null ? parentCount : ++parentCount;
		if (parentCount != 1) { 
			throw new ConstraintViolationException(this.getClass().getSimpleName()  + " must be connected to exactly one inventory record", null);
		}
	}

	@Transient
	protected int getNonInventoryRecordParentCount() {
		return 0;
	}

}
