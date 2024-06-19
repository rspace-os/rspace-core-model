package com.researchspace.model.inventory.field;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.hibernate.envers.Audited;

import com.researchspace.model.field.FieldType;

@Entity()
@DiscriminatorValue("date")
@Audited
public class InventoryDateField extends SampleField {

	private static final long serialVersionUID = 2350588560175243555L;

	private static DateFormat SUGGESTED_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	static {
		SUGGESTED_DATE_FORMAT.setLenient(false);
	}

	public InventoryDateField() {
		this("");
	}
	
	public InventoryDateField(String name) {
		super(FieldType.DATE, name);
	}

	/**
	 * Date field doesn't seem to have a required format internally 
	 * (i.e. accepts any string as data), but for stricter checks 
	 * or type guess this can be used.
	 */
	private static final DateTimeFormatter LENIENT_ISO_DATE_FORMATTER = 
		new DateTimeFormatterBuilder()
			.appendOptional(DateTimeFormatter.ISO_DATE_TIME)
			.appendOptional(DateTimeFormatter.ISO_DATE)
			.appendOptional(DateTimeFormatter.BASIC_ISO_DATE)
			.toFormatter()
			.withZone(ZoneId.systemDefault());
	
	@Override
	public boolean isSuggestedFieldForData(String dateString) {
		try {
			LocalDate.parse(dateString, LENIENT_ISO_DATE_FORMATTER);
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
