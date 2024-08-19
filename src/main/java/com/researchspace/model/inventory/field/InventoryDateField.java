package com.researchspace.model.inventory.field;

import com.researchspace.model.field.ErrorList;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;

import java.time.format.ResolverStyle;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.hibernate.envers.Audited;

import com.researchspace.model.field.FieldType;

@Entity()
@DiscriminatorValue("date")
@Audited
public class InventoryDateField extends SampleField {

	private static final long serialVersionUID = 2350588560175243555L;

	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	public InventoryDateField() {
		this("");
	}
	
	public InventoryDateField(String name) {
		super(FieldType.DATE, name);
	}

	@Override
	public ErrorList validate(String fieldData){
		ErrorList errors = super.validate(fieldData);
		if(!isValidDateFormat(fieldData)) {
			errors.addErrorMsg(String.format("%s is an invalid date format. Valid format is yyyy-MM-dd.", fieldData));
		}
		return errors;
	}
	
	@Override
	public boolean isSuggestedFieldForData(String dateString) {
		return isValidDateFormat(dateString);
	}

	private boolean isValidDateFormat(String date){
		try {
			LocalDate.parse(date, DATE_FORMATTER);
		} catch (DateTimeParseException e) {
			return false;
		}
		return true;
	}
	
	@Override
	public InventoryDateField shallowCopy() {
		InventoryDateField dateField = new InventoryDateField();
		copyFields(dateField);
		return dateField;
	}

}
