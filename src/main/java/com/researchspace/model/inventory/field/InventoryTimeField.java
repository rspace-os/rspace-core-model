package com.researchspace.model.inventory.field;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.hibernate.envers.Audited;

import com.researchspace.model.field.FieldType;

@Entity
@DiscriminatorValue("time")
@Audited
public class InventoryTimeField extends SampleField {

	private static final long serialVersionUID = 8709980361506172103L;	

	/**
	 * Time field doesn't seem to have a required format internally 
	 * (i.e. InventoryTimeField/TimeFieldForm seems to be able to store 
	 * any string as data), but for any checks/type guesses this can be used.
	 */
	private static DateFormat SUGGESTED_TIME_FORMAT = new SimpleDateFormat("HH:mm");
	static {
		SUGGESTED_TIME_FORMAT.setLenient(false);
	}
	
	public InventoryTimeField() {
		this("");
	}
	
	public InventoryTimeField(String name) {
		super(FieldType.TIME, name);
	}

	@Override
	public boolean isSuggestedFieldForData(String data) {
		try {
			SUGGESTED_TIME_FORMAT.parse(data);
		} catch (ParseException e) {
			return false;
		}
		return true;
	}
	
	@Override
	public InventoryTimeField shallowCopy() {
		InventoryTimeField timeField = new InventoryTimeField();
		copyFields(timeField);
		return timeField;
	}

}
