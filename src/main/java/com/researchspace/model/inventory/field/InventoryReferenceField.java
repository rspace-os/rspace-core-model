package com.researchspace.model.inventory.field;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

import org.hibernate.envers.Audited;

import com.researchspace.model.field.FieldType;

/**
 * Stores internal  links as a list of RSpace GlobalIds
 */
@Entity
@Audited
@DiscriminatorValue("reference")
public class InventoryReferenceField extends SampleField {

	private static final long serialVersionUID = -9034869690666898322L;

	public InventoryReferenceField() {
		this("");
	}
	
	public InventoryReferenceField(String name) {
		super(FieldType.REFERENCE, name);
	}

	@Transient
	public String getDefaultStringValue() {
		 return "";
	}

	@Override
	public InventoryReferenceField shallowCopy() {
		InventoryReferenceField sourceField = new InventoryReferenceField();
		copyFields(sourceField);
		return sourceField;
	}

}
