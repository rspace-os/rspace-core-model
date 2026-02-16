package com.researchspace.model.inventory.field;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import org.hibernate.envers.Audited;

import com.researchspace.model.field.FieldType;

@Entity
@DiscriminatorValue("text")
@Audited
public class InventoryTextField extends SampleField {

	private static final long serialVersionUID = -1757280226055607179L;

	public InventoryTextField() {
		this("");
	}
	
	public InventoryTextField(String name) {
		super(FieldType.TEXT, name);
	}

	@Override
	public InventoryTextField shallowCopy() {
		InventoryTextField copy = new InventoryTextField();
		copyFields(copy);
		return copy;
	}

}
