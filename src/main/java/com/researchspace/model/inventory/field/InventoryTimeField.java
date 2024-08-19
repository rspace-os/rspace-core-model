package com.researchspace.model.inventory.field;

import com.researchspace.model.field.ErrorList;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.hibernate.envers.Audited;

import com.researchspace.model.field.FieldType;

@Entity
@DiscriminatorValue("time")
@Audited
public class InventoryTimeField extends SampleField {

	private static final long serialVersionUID = 8709980361506172103L;

	private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm").withResolverStyle(
			ResolverStyle.STRICT);
	
	public InventoryTimeField() {
		this("");
	}
	
	public InventoryTimeField(String name) {
		super(FieldType.TIME, name);
	}

	@Override
	public boolean isSuggestedFieldForData(String data) {
		return isValidTimeFormat(data);
	}

	@Override
	public ErrorList validate(String fieldData){
		ErrorList errors = super.validate(fieldData);
		if(!(fieldData == null) && !fieldData.isEmpty()){
			if(!isValidTimeFormat(fieldData)) {
				errors.addErrorMsg(String.format("%s is an invalid 24hour time format. Valid format is 00:00.", fieldData));
			}
		}
		return errors;
	}

	private boolean isValidTimeFormat(String time){
		try {
			LocalTime.parse(time, TIME_FORMATTER);
		} catch (DateTimeParseException e) {
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
