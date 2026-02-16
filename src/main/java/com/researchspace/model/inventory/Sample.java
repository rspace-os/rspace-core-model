package com.researchspace.model.inventory;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.Validate;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;

import com.researchspace.model.User;
import com.researchspace.model.audittrail.AuditDomain;
import com.researchspace.model.audittrail.AuditTrailData;
import com.researchspace.model.core.GlobalIdPrefix;
import com.researchspace.model.core.UniquelyIdentifiable;
import com.researchspace.model.inventory.field.ExtraField;
import com.researchspace.model.inventory.field.SampleField;
import com.researchspace.model.units.QuantityInfo;
import com.researchspace.model.units.QuantityUtils;
import com.researchspace.model.units.RSUnitDef;
import com.researchspace.model.units.ValidTemperature;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents RSpace Inventory Sample.
 */
@Entity
@Audited
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@AuditTrailData(auditDomain = AuditDomain.INV_SAMPLE)
@Indexed
public class Sample extends InventoryRecord implements Serializable, UniquelyIdentifiable {

	private static final long serialVersionUID = 1867269597891360704L;

	public static final int SUBSAMPLE_ALIAS_MAX_LENGTH = 30;

	private User owner;

	private QuantityInfo storageTempMin;
	private QuantityInfo storageTempMax;

	private List<SubSample> subSamples = new ArrayList<>();
	private int activeSubSamplesCount;

	@IndexedEmbedded
	private List<SampleField> fields = new ArrayList<>();
	
	@IndexedEmbedded(prefix = "fields.")
	private List<ExtraField> extraFields = new ArrayList<>();

	@IndexedEmbedded
	private List<Barcode> barcodes = new ArrayList<>();

	private List<DigitalObjectIdentifier> identifiers = new ArrayList<>();

	@IndexedEmbedded(prefix = "fields.")
	private List<InventoryFile> files = new ArrayList<>();

	private SampleSource sampleSource = SampleSource.LAB_CREATED;

	private LocalDate expiryDate;

	@Setter(value=AccessLevel.PRIVATE)
	private String subSampleName = SubSampleName.SUBSAMPLE.getDisplayName();
	@Setter(value=AccessLevel.PRIVATE)
	private String subSampleNamePlural = SubSampleName.SUBSAMPLE.getDisplayNamePlural();

	/** 1st field has index = 1 */
	private int currMaxColIndex = 0;
	
	/** Boolean flag as to whether this sample is a template or not */
	private boolean isTemplate = false;
	
	private Integer defaultUnitId;
	
	/** Template on which this sample is based on. */
	private Sample sTemplate;
	/** Version of the template on which this sample is based on. */
	@Setter(value=AccessLevel.PRIVATE)
	private Long sTemplateLinkedVersion;
	
	static final Set<String> RESERVED_FIELD_NAMES = Collections.unmodifiableSet( 
			Stream.concat(
				InventoryRecord.RESERVED_FIELD_NAMES.stream(), 
				Arrays.asList("source", "expiry date").stream())
			.collect(Collectors.toSet()));

	/** for hibernate, record factory & pagination criteria */
	public Sample() { }
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	@NotNull
	public SampleSource getSampleSource () {
		return sampleSource;
	}

	@ManyToOne
	@JoinColumn(nullable = false)
	@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
	@IndexedEmbedded
	public User getOwner() {
		return owner;
	}

	@Embedded
	@AttributeOverrides({ @AttributeOverride(name = "unitId", column = @Column(name = "storageTempMinUnitId")),
			@AttributeOverride(name = "numericValue", column = @Column(name = "storageTempMinNumericValue", precision = 19, scale = 3)) })
	@ValidTemperature
	public QuantityInfo getStorageTempMin() {
		return storageTempMin;
	}

	@Embedded
	@AttributeOverrides({ @AttributeOverride(name = "unitId", column = @Column(name = "storageTempMaxUnitId")),
			@AttributeOverride(name = "numericValue", column = @Column(name = "storageTempMaxNumericValue", precision = 19, scale = 3)) })
	@ValidTemperature
	public QuantityInfo getStorageTempMax() {
		return storageTempMax;
	}

	/**
	 * @return the list of SubSample being part of this Sample
	 */
	@OneToMany(mappedBy = "sample", cascade = CascadeType.ALL)
	@Size(min = 1)
	@OrderBy(value = "id")
	public List<SubSample> getSubSamples() {
		return subSamples;
	}
	
	public void setSubSamples(List<SubSample> subSamples) {
		this.subSamples = subSamples;
		refreshActiveSubSamples();
	}

	private List<SubSample> activeSubSamples;
	
	@Transient
	public List<SubSample> getActiveSubSamples() {
		if (activeSubSamples == null) {
			activeSubSamples = subSamples.stream()
					.filter(ss -> !ss.isDeleted() || (isDeleted() && ss.isDeletedOnSampleDeletion()))
				.collect(Collectors.toList());
		}
		return activeSubSamples;
	}
	
	public void refreshActiveSubSamples() {
		activeSubSamples = null;
		getActiveSubSamples();
		activeSubSamplesCount = activeSubSamples.size();
	}
	
	@Transient
	public List<SubSample> getDeletedSubSamples() {
		return subSamples.stream().filter(ss -> ss.isDeleted())
				.collect(Collectors.toList());
	}

	public boolean hasExactlyOneSubSample() {
		return getActiveSubSamples().size() == 1;
	}
	
	/**
	 * If sample has just one subSample, retrieve it. Otherwise return empty optional.
	 */
	@Transient
	public Optional<SubSample> getOnlySubSample() {
		return hasExactlyOneSubSample() ? Optional.of(getActiveSubSamples().get(0)) : Optional.empty();
	}
	
	/**
	 * @return the list of fields of this Sample
	 */
	@OneToMany(mappedBy = "sample", cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderBy(value = "columnIndex")
	protected List<SampleField> getFields() {
		return fields;
	}

	protected void setFields(List<SampleField> fields) {
		this.fields = fields;
	}

	private List<SampleField> activeFields;
	
	/**
	 * @return list of non-deleted fields belonging to this Sample, sorted by columnIndex
	 */
	@Transient
	public List<SampleField> getActiveFields() {
		if (activeFields == null) {
			activeFields = getFields().stream().filter(sf -> !sf.isDeleted())
				.sorted().collect(Collectors.toList());
		}
		return activeFields;
	}
	
	/**
	 * Appends a new SampleField to the list of this sample's fields, incrementing {@code currMaxColIndex} as a side-effect.
	 * @param toAdd
	 */
	public void addSampleField(SampleField toAdd) {
		verifyFieldNameAllowed(toAdd.getName());
		currMaxColIndex++;
		if (toAdd.getColumnIndex() == null) {
			toAdd.setColumnIndex(currMaxColIndex);
		}
		toAdd.setSample(this);
		fields.add(toAdd);
		refreshActiveFields();
	}

	/**
	 * Resets column index property for active fields, 
	 * so they start from 1 and end with currMaxColIndex.
	 */
	public void refreshActiveFieldsAndColumnIndex() {
		currMaxColIndex = 0;
		activeFields = null;
		for (SampleField sf: getActiveFields()) {
			sf.setColumnIndex(++currMaxColIndex);
		}
	}
	
	public void deleteSampleField(SampleField toDelete, boolean deleteOnSampleUpdate) {
		if (!isTemplate()) {
			throw new IllegalArgumentException("Cannot directly delete field from sample, only from template");
		}
		Optional<SampleField> fieldToDeleteOpt = getFieldById(toDelete.getId());
		if (!fieldToDeleteOpt.isPresent()) {
			throw new IllegalArgumentException("Trying to delete a field not belonging to current sample");
		}
		SampleField fieldToDelete = fieldToDeleteOpt.get();
		fieldToDelete.setDeleted(true);
		fieldToDelete.setDeleteOnSampleUpdate(deleteOnSampleUpdate);
		refreshActiveFields();
	}

	public Optional<SampleField> getFieldById(Long id) {
		return getFields().stream().filter(sf -> id != null && id.equals(sf.getId())).findFirst();
	}

	private Optional<SampleField> getFieldByTemplateFieldId(Long id) {
		return getFields().stream()
				.filter(sf -> id != null && sf.getTemplateField() != null && id.equals(sf.getTemplateField().getId()))
				.findFirst();
	}

	public List<SampleField> refreshActiveFields() {
		activeFields = null;
		return getActiveFields();
	}

	@NotNull
	@Column(length = SUBSAMPLE_ALIAS_MAX_LENGTH)
	private String getSubSampleName() {
		return subSampleName;
	}

	@NotNull
	@Column(length = SUBSAMPLE_ALIAS_MAX_LENGTH)
	private String getSubSampleNamePlural() {
		return subSampleNamePlural;
	}

	@Transient
	public String getSubSampleAlias() {
		return getSubSampleName();
	}

	@Transient
	public String getSubSampleAliasPlural() {
		return getSubSampleNamePlural();
	}

	@Transient
	public void setSubSampleAliases(String alias, String aliasPlural) {
		Validate.notBlank(alias, "SubSample alias cannot be blank");
		Validate.notBlank(aliasPlural, "SubSample alias (plural) cannot be blank");
		setSubSampleName(StringUtils.trim(alias));
		setSubSampleNamePlural(StringUtils.trim(aliasPlural));
	}

	@Transient
	public void setSubSampleAliases(SubSampleName name) {
		setSubSampleName(name.getDisplayName());
		setSubSampleNamePlural(name.getDisplayNamePlural());
	}

	/**
	 * Retrieve total quantity of the Sample. The total value is a sum of all subsample quantities, 
	 * rounded to 3 fraction digits.
	 */
	@Transient
	public QuantityInfo getTotalQuantity() {
		return super.getQuantityInfo();
	}
	
	/**
	 * Set total quantity of the Sample. Can be called only if sample has a single subsample,
	 * throws exception otherwise.
	 */
	public void setTotalQuantity(QuantityInfo quantityInfo) {
		if (hasExactlyOneSubSample()) {
			super.setQuantityInfo(quantityInfo);
			getActiveSubSamples().get(0).setQuantityInfo(quantityInfo);
		} else {
			throw new IllegalStateException("Can't save total quantity directly in Sample having multiple SubSamples");
		}
	}
	
	public void recalculateTotalQuantity() {
		QuantityUtils quantityUtils = new QuantityUtils();
		List<QuantityInfo> subSampleQuantities = getActiveSubSamples().stream()
				.filter(ss -> ss.getQuantity() != null)
				.map(ss -> ss.getQuantity()).collect(Collectors.toList());

		if (subSampleQuantities.isEmpty()) {
			setQuantityInfo(null);
			return;
		}
		if (subSampleQuantities.size() == 1) {
			setQuantityInfo(subSampleQuantities.get(0));
			return;
		}
		
		QuantityInfo totalQuantity = quantityUtils.sum(subSampleQuantities);
		setQuantityInfo(totalQuantity);
	}

	/**
	 * @return the list of extra fields of this Sample, including deleted fields.
	 */
	@OneToMany(mappedBy = "sample", cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderBy(value = "id")
	@Override
	protected List<ExtraField> getExtraFields() {
		return extraFields;
	}

	protected void setExtraFields(List<ExtraField> extraFields) {
		this.extraFields = extraFields;
		refreshActiveExtraFields();
	}

	/**
	 * @return the list of barcodes of this SubSample, including deleted fields.
	 */
	@OneToMany(mappedBy = "sample", cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderBy(value = "id")
	@Override
	protected List<Barcode> getBarcodes() {
		return barcodes;
	}

	protected void setBarcodes(List<Barcode> barcodes) {
		this.barcodes = barcodes;
		refreshActiveBarcodes();
	}

	@OneToMany(mappedBy = "sample", cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderBy(value = "id")
	@Override
	protected List<DigitalObjectIdentifier> getIdentifiers() {
		return identifiers;
	}

	protected void setIdentifiers(List<DigitalObjectIdentifier> identifiers) {
		this.identifiers = identifiers;
		refreshActiveIdentifiers();
	}

	/**
	 * @return the list of files attached to this Sample
	 */
	@OneToMany(mappedBy = "sample", cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderBy(value = "id")
	protected List<InventoryFile> getFiles() {
		return files;
	}
	
	protected void setFiles(List<InventoryFile> files) {
		this.files = files;
		refreshActiveAttachedFiles();
	}
	
	@Transient
	@Override
	public GlobalIdPrefix getGlobalIdPrefix() {
		return isTemplate ? GlobalIdPrefix.IT : GlobalIdPrefix.SA;
	}

	@Transient
	@Override
	public InventoryRecord.InventoryRecordType getType() {
		return InventoryRecordType.SAMPLE;
	}

    /**
     * Adds subsample setting both sides of the relationship and refreshing active subsamples.
     * @param subSampleToAdd
     */
	public void addSubSample(SubSample subSampleToAdd) {
		this.subSamples.add(subSampleToAdd);
		subSampleToAdd.setSample(this);
		refreshActiveSubSamples();
	}

	Sample shallowCopy() {
		Sample copy = new Sample();
		shallowCopyBasicFields(copy);
		return copy;
	}
	
	/**
	 * Convenience copy method to make a normal sample from a template.
	 * Validates that this object <em>is</em> a template, 
	 * also sets the template and its version into copied sample.
	 * 
	 * @return a copy of a template, a regular sample.
	 * @throws IllegalArgumentException if this Sample is not a template
	 */
	public Sample copyFromTemplate(User currentUser) {
		Validate.isTrue(isTemplate(), templateErrMsg(this, true));
		Sample copy = this.copy(false, currentUser);
		copy.setSTemplate(this);
		copy.setSTemplateLinkedVersion(getVersion());
		return copy;
	}
	
	/**
	 * Convenience copy method to make a template from a sample, or template. 
	 * This is the inverse operation to {@code copyFromTemplate}
	 * 
	 * @return a copy of the sample, as a Template.
	 * @throws IllegalArgumentException if this Sample <em>is</em> a template
	 */
	public Sample copyToTemplate(User currentUser) {
		return this.copy(true, currentUser);
	}
	
	private String templateErrMsg(Sample template, boolean shouldBeTemplate) {
		return String.format("the sample argument (id=%d) is ", template.getId()) + (shouldBeTemplate?"not":"") + " a template";
	}
	
	/**
	 * Duplicates this sample (or template), including fields, core properties, icons and images.
	 * Does not copy subsamples, but creates a single sample with same quantity as original.
	 *
	 * @param currentUser user to set as a creator and owner of the copy
	 * 
	 * @return the newly duplicated sample.
	 */
	@Override
	public Sample copy(User currentUser) {
		return this.copy(isTemplate(), currentUser);
	}
		
	private Sample copy(boolean toTemplate, User currentUser) {
		return copy(this::defaultNameCopy, toTemplate, currentUser);
	}
	
	/**
	 * 
	 * @param nameMapper A custom name-mapper to generate name for the new copy.
	 * @return
	 * @see Sample#copy(User)
	 */
	private Sample copy(Function<Sample, String> nameMapper, boolean toTemplate, User currentUser) {
		QuantityInfo toCopyTotal = this.getTotalQuantity();
		Sample sampleCopy = shallowCopy();
		sampleCopy.setExpiryDate(getExpiryDate());
		sampleCopy.setName(nameMapper.apply(this));
		sampleCopy.setImageFileProperty(getImageFileProperty());
		sampleCopy.setThumbnailFileProperty(getThumbnailFileProperty());
		sampleCopy.setCreatedBy(currentUser.getUsername());
		sampleCopy.setModifiedBy(currentUser.getUsername());
		sampleCopy.setOwner(currentUser);
		sampleCopy.setStorageTempMax(getStorageTempMax());
		sampleCopy.setStorageTempMin(getStorageTempMin());
		sampleCopy.setSubSampleName(getSubSampleName());
		sampleCopy.setSubSampleNamePlural(getSubSampleNamePlural());
		sampleCopy.setDefaultUnitId(getDefaultUnitId());
		sampleCopy.setSampleSource(getSampleSource());
		// don't need to set currMaxColIndexIndex as is set by adding fields
	
		sampleCopy.setTemplate(toTemplate);
		if (!toTemplate) {
			sampleCopy.setSampleTemplate(getSTemplate());
			sampleCopy.setSTemplateLinkedVersion(getSTemplateLinkedVersion());
		}
		
		for (SampleField field: getFields()) {
			sampleCopy.copyAndAddSampleField(field);
		}
		createSubSample(nameMapper, toCopyTotal, sampleCopy);
		return sampleCopy;
	}

	private SampleField copyAndAddSampleField(SampleField field) {
		SampleField copiedField = field.shallowCopy();
		/* if copying into a sample set connection to original template field */
		if (!isTemplate()) {
			/* if copying a field belonging to a template, set that field as a template field,
			 * but if copying a field belonging to a non-template (i.e. sample), set 
			 * the original template field as a template field */
			copiedField.setTemplateField(field.getSample().isTemplate() ? field : field.getTemplateField());
		}
		addSampleField(copiedField);
		return copiedField;
	}
	
	/**
	 * If this sample was created from a template, returns the template, else {@code null}.
	 * <p>
	 * This will be {@code null} if any of the following are true:
	 * <ul>
	 * <li> This Sample <em>is</em> a template
	 * <li> This Sample is a free-form sample created from scratch, not using a template. 
	 * </ul>
	 * This association is lazy-loaded.
	 * @return
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
	public Sample getSTemplate() {
		return sTemplate;
	}
	
	// for hibernate, does not perform validation so as to support setting lazy proxies with null values.
	void setSTemplate(Sample template) {
		this.sTemplate = template;
	}

	/**
	 * Public setter for the  template used to create this sample
	 * @param template
	 * @throws  IllegalArgumentException if {@code template} is not a template (i.e {@code isTemplate()==false} ).
	 */
	public void setSampleTemplate(Sample template) {
		if (template != null) {
			Validate.isTrue(template.isTemplate(), templateErrMsg(template, true));
		}
		this.sTemplate = template;
	}

	@Transient
	@Field
	public Long getParentTemplateId() {
		if (getSTemplate() != null) {
			return getSTemplate().getId();
		}
		return null;
	}

	@NotNull
	@Column(nullable = false)
	public Integer getDefaultUnitId() {
		return defaultUnitId;
	}
	
	private void createSubSample(Function<Sample, String> nameMapper, QuantityInfo toCopyTotal, Sample sample) {
		SubSample singleSS = new SubSample(sample);
		sample.addSubSample(singleSS);
		String ssName = InventorySeriesNamingHelper.getSerialNameForSubSample(nameMapper.apply(this), 1, 1);
		singleSS.setName(ssName);

		QuantityInfo newQuantity = toCopyTotal != null ? toCopyTotal.copy() 
				: QuantityInfo.zero(RSUnitDef.getUnitById(getDefaultUnitId()));
		singleSS.setQuantity(newQuantity);
		singleSS.setCreatedBy(sample.getCreatedBy());
		singleSS.setModifiedBy(sample.getModifiedBy());
	}

	@Override
	@Transient
	public Set<String> getReservedFieldNames() {
		return RESERVED_FIELD_NAMES;
	}

	@Override
	protected void assertCanStoreAttachments() {
		if (isTemplate()) {
			throw new IllegalArgumentException("Sample Templates don't support file attachments yet");
		}
	}
	
	public boolean updateToLatestTemplateVersion() {
		if (isTemplate()) {
			throw new IllegalStateException("Update to latest template version is only supported for samples, not templates");
		}
		if (getSTemplate() == null) {
			throw new IllegalStateException("The sample is not based on any template");
		}
		Long latestTemplateVersion = getSTemplate().getVersion();
		if (getSTemplateLinkedVersion().equals(latestTemplateVersion)) {
			return false; // sample already pointing to latest template version 
		}
		
		boolean sampleUpdated = false;

		// 1. update some sample properties directly
		if (!isSubSampleAliasEqualTo(getSTemplate().getSubSampleAlias(), getSTemplate().getSubSampleAliasPlural())) {
			setSubSampleAliases(getSTemplate().getSubSampleAlias(), getSTemplate().getSubSampleAliasPlural());
			sampleUpdated = true;
		}
		
		// 2. for existing sample fields - ask field to update to latest definition  
		for (SampleField sampleField : getActiveFields()) {
			if (sampleField.getTemplateField() != null) {
				boolean fieldUpdated = sampleField.updateToLatestTemplateDefinition();
				if (fieldUpdated) {
					sampleField.setModificationDate(new Date().getTime());
					sampleUpdated = true;
				}
			}
		}
		
		// 3. check for new fields in template, create them in the sample 
		for (SampleField templateField : getSTemplate().getActiveFields()) {
			if (!getFieldByTemplateFieldId(templateField.getId()).isPresent()) {
				SampleField addedField = copyAndAddSampleField(templateField);
				/* fields added through update to latest template version shouldn't have pre-set value */ 
				addedField.setFieldData(null);
				sampleUpdated = true;
			}
		}
		
		if (sampleUpdated) {
			refreshActiveFieldsAndColumnIndex();
		}
		setSTemplateLinkedVersion(latestTemplateVersion);
		return sampleUpdated;
	}

	/**
	 * Checks if provided alias singular and plural are equal to ones set in this Sample.
	 */
	public boolean isSubSampleAliasEqualTo(String subSampleAlias, String subSampleAliasPlural) {
		return getSubSampleAlias().equals(subSampleAlias) && getSubSampleAliasPlural().equals(subSampleAliasPlural);
	}

}

