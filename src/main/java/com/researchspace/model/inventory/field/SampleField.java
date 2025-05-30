package com.researchspace.model.inventory.field;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.hibernate.envers.Audited;
import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Analyzer;
import org.hibernate.search.annotations.Store;

import com.researchspace.model.core.GlobalIdPrefix;
import com.researchspace.model.core.GlobalIdentifier;
import com.researchspace.model.field.ErrorList;
import com.researchspace.model.field.FieldType;
import com.researchspace.model.field.ValidatingField;
import com.researchspace.model.inventory.InventoryFile;
import com.researchspace.model.inventory.Sample;

import lombok.Getter;
import lombok.Setter;

/**
 * Sample field is used to hold field data for Samples.
 */
@Entity
@Setter
@Getter
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Audited
public abstract class SampleField implements Serializable, ValidatingField, Comparable<SampleField> {

	private static final long serialVersionUID = 6951489011943609794L;

	private Long id;

	private String name;
	private FieldType type;
	private Long modificationDate = new Date().getTime();
	private String data;
	private Integer columnIndex;
	private boolean deleted;
	private boolean deleteOnSampleUpdate;
	private boolean mandatory;

	private Sample sample;
	private SampleField templateField;

	SampleField(FieldType type, String fieldName) {
		this.type = type;
		this.name = fieldName;
	}

	public static SampleField fromFieldTypeString(String fieldTypeString) {
		Validate.notNull(fieldTypeString, "cannot create SampleField for null field type");

		String fieldType = StringUtils.capitalize(fieldTypeString);
		switch (fieldType) {
		case FieldType.STRING_TYPE:
			return new InventoryStringField();
		case FieldType.TEXT_TYPE:
			return new InventoryTextField();
		case FieldType.DATE_TYPE:
			return new InventoryDateField();
		case FieldType.NUMBER_TYPE:
			return new InventoryNumberField();
		case FieldType.TIME_TYPE:
			return new InventoryTimeField();
		case FieldType.ATTACHMENT_TYPE:
			return new InventoryAttachmentField();
		case FieldType.REFERENCE_TYPE:
			return new InventoryReferenceField();
		case FieldType.CHOICE_TYPE:
			return new InventoryChoiceField();
		case FieldType.RADIO_TYPE:
			return new InventoryRadioField();
		case FieldType.URI_TYPE:
			return new InventoryUriField();
    case FieldType.IDENTIFIER_TYPE:
			return new InventoryIdentifierField();
		default:
			throw new IllegalArgumentException(String.format("Unsupported field type %s", fieldType));
		}
	}

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	public Long getId() {
		return id;
	}

	@Column(nullable = false, length = 50)
	public String getName() {
		return name;
	}

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	public FieldType getType() {
		return type;
	}

	@Transient
	public Date getModificationDateAsDate() {
		return modificationDate != null? new Date(modificationDate):null;
	}

	@Column(name = "data", length = 1000)
	public String getData() {
		return data;
	}

	@Transient
	@org.hibernate.search.annotations.Field(analyzer = @Analyzer(definition = "structureAnalyzer"),
			analyze = Analyze.YES, name = "fieldData", store = Store.NO)
	public String getFieldData() {
		return getData();
	}

	/**
	 * Validate and saves incoming string as field data.
	 *
	 * @throws IllegalArgumentException if is invalid data
	 * @param fieldData
	 */
	public void setFieldData(String fieldData) {
		assertFieldDataValid(fieldData);
		setData(fieldData);
	}

	/**
	 * Checks if incoming string is a valid data for the field
	 *
	 * @throws IllegalArgumentException if is invalid data
	 * @param fieldData
	 */
	public void assertFieldDataValid(String fieldData) {
		ErrorList el = validate(fieldData);
		if (el.hasErrorMessages()) {
			throw new IllegalArgumentException(String.format("[%s] is invalid for field type %s: %s",
					fieldData, getType().getType(), el.getAllErrorMessagesAsStringsSeparatedBy(",")));
		}
	}

	/**
	 * Boolean flag to check if field supports storing data as list of options,
	 * e.g. as radio or choice fields does.
	 */
	@Transient
	public boolean isOptionsStoringField() {
		return false;
	}

	/**
	 * Convenient method for retrieving all possible options of radio/choice field.
	 * (only implemented by these classes)
	 */
	@Transient
	public List<String> getAllOptions() {
		throw new UnsupportedOperationException(getType() + " field doesn't support selected options");
	}

	/**
	 * Convenient method for retrieving selected options of choice/radio field.
	 * (only implemented by these classes)
	 */
	@Transient
	public List<String> getSelectedOptions() {
		throw new UnsupportedOperationException(getType() + " field doesn't support selected options");
	}

	/**
	 * Convenient method for setting selected options of choice/radio field.
	 * (only implemented by these classes)
	 */
	@Transient
	public void setSelectedOptions(List<String> selectedOptions) {
		throw new UnsupportedOperationException(getType() + " field doesn't support selected options");
	}

	/**
	 * Convenient method for retrieving field attachment.
	 * (only implemented by InventoryAttachmentField class)
	 *
	 * @return attached inventory file, or null.
	 */
	@Transient
	public InventoryFile getAttachedFile() {
		return null;
	}

	/**
	 * Convenient method for setting field attachment
	 * (only implemented by InventoryAttachmentField class)
	 */
	@Transient
	public void setAttachedFile(InventoryFile file) {
		throw new UnsupportedOperationException(getType() + " field doesn't support file attachments");
	}


	@ManyToOne
	@JoinColumn(nullable = false)
	public Sample getSample() {
		return sample;
	}

	/**
	 * The template field that was used for creating this sample field.
	 *
	 * The template can be modified independently of samples created from it,
	 * so unless the sample points to the latest template definition its field
	 * properties may differ from template field properties.
	 *
	 */
	@ManyToOne
	public SampleField getTemplateField() {
		return templateField;
	}

	/**
	 * Subclasses can override with validation. this implementation only checks if field
	 * is not mandatory and empty. This method can be invoked by client code and is also invoked via
	 * {@code setFieldData(data)}
	 *
	 * @param fieldData
	 */
	public ErrorList validate(String fieldData) {
		ErrorList result = new ErrorList();
		boolean isTemplateField = getSample() != null && getSample().isTemplate();
		if (isMandatory() && !isTemplateField && !isValidValueForMandatoryField(fieldData)) {
			result.addErrorMsg("Field [" + getName() + "] is mandatory, but no content is provided");
		}
		return result;
	}

	@Transient
	public boolean isValidValueForMandatoryField(String fieldData) {
		return !StringUtils.isBlank(fieldData);
	}

	public abstract SampleField shallowCopy();

	@Transient
	public GlobalIdentifier getOid() {
		return new GlobalIdentifier(GlobalIdPrefix.SF, getId());
	}

	/**
	 * Should this sample field be suggested as a best match for incoming data?
	 * To be used for guessing field type out of incoming string.
	 *
	 * @param data
	 * @return
	 */
	public boolean isSuggestedFieldForData(String data) {
		return false;
	}

	@Override
	public int compareTo(SampleField other) {
		return columnIndex.compareTo(other.columnIndex);
	}

	/**
	 * Template method for copying data of a Field. Does not copy relationships
	 * or ids.
	 */
	protected void copyFields(SampleField copy) {
		copy.setName(getName());
		copy.setType(getType());
		copy.setModificationDate(new Date().getTime());
		copy.setFieldData(getFieldData());
		copy.setColumnIndex(getColumnIndex());
		copy.setDeleted(isDeleted());
		copy.setMandatory(isMandatory());
	}

	/**
	 * Note: don't call this method directly, but rather call {@link #{Sample.updateToLatestTemplateDefinition()}
	 * to update whole sample.
	 *
	 * This method applies changes from latest template field:
	 *  - updates name of the field (if changed)
	 *  - updates columnIndex of the field (if changed)
	 *  - applies 'deleted' flag to the field, but only if field value is empty. If field value
	 *    is not empty, an exception is thrown.
	 *
	 * @return true if method resulted in any change (modification) to the field
	 * @throws IllegalStateException if the field or stored data is not compatible with latest template definition,
	 *     e.g. doesn't have connected template field, or doesn't have data and latest field is marked as mandatory.
	 */
	public boolean updateToLatestTemplateDefinition() {
		if (templateField == null) {
			throw new IllegalStateException("Field [" + getName() + "] has no connected template field");
		}

		boolean fieldUpdated = false;

		// apply change to field name
		if (!getName().equals(templateField.getName())) {
			setName(templateField.getName());
			fieldUpdated = true;
		}
		// apply change to columnIndex
		if (getColumnIndex() != null && !getColumnIndex().equals(templateField.getColumnIndex())) {
			setColumnIndex(templateField.getColumnIndex());
			fieldUpdated = true;
		}
		// apply field deletion
		if (!isDeleted() && templateField.isDeleted() && templateField.isDeleteOnSampleUpdate()) {
			setDeleted(true);
			fieldUpdated = true;
		}
		if (isMandatory() != templateField.isMandatory()) {
			setMandatory(templateField.isMandatory());
			if (!isValidValueForMandatoryField(getFieldData())) {
				throw new IllegalStateException("Field [" + getName() + "] is empty, but "
						+ "is mandatory in latest template field definition");
			}
			fieldUpdated = true;
		}

		return fieldUpdated;
	}

}
