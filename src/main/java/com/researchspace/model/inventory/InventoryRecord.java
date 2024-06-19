package com.researchspace.model.inventory;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import com.researchspace.model.FileProperty;
import com.researchspace.model.User;
import com.researchspace.model.audittrail.AuditTrailIdentifier;
import com.researchspace.model.audittrail.AuditTrailProperty;
import com.researchspace.model.core.GlobalIdPrefix;
import com.researchspace.model.core.GlobalIdentifier;
import com.researchspace.model.inventory.field.ExtraField;
import com.researchspace.model.permissions.ACLElement;
import com.researchspace.model.permissions.RecordSharingACL;
import com.researchspace.model.record.EditInfo;
import com.researchspace.model.record.IActiveUserStrategy;
import com.researchspace.model.units.Quantifiable;
import com.researchspace.model.units.QuantityInfo;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;
import org.hibernate.search.annotations.Field;

@MappedSuperclass
@Getter
@Setter
@EqualsAndHashCode(of = {"id", "editInfo"})
@Audited
public abstract class InventoryRecord implements Quantifiable {
	
	private Long id;
	private EditInfo editInfo;
	protected boolean deleted;
	private Date deletedDate;

	private Long iconId = -1L;
	private QuantityInfo quantityInfo;
	/**
	 * tags field is redundant as all info is in tagMetaData but the field is present to allow efficient database searches.
	 */
	private String tags;
	/**
	 * tagMetaData is the source of truth for tag data - the tags field should now be derived from tagMetaData.
	 */
	private String tagMetaData;
	
	/* user-visible version, part of the versioned global id */
	@Setter(AccessLevel.PRIVATE)
	private Long version = 1L;
	
	private FileProperty imageFileProperty;
	private FileProperty thumbnailFileProperty;

	public enum InventorySharingMode {
		OWNER_GROUPS, WHITELIST, OWNER_ONLY
	}
	
	private InventorySharingMode sharingMode = InventorySharingMode.OWNER_GROUPS;
	private RecordSharingACL sharingACL;
	
	public enum InventoryRecordType {
		SAMPLE, SUBSAMPLE, CONTAINER
	}

	@Transient
	public List<String> getSharedWithUniqueNames() {
		if (getSharingACL() == null) {
			return Collections.EMPTY_LIST;
		}
		return getSharingACL().getAclElements().stream().map(ACLElement::getUserOrGrpUniqueName).collect(Collectors.toList()); 
	}

	@Field(name = "sharedWith")
	@Transient
	public String getSharedWithUniqueNamesString() {
		if (getSharingACL() == null) {
			return null;
		}
		return getSharingACL().getAclElements().stream().map(ACLElement::getUserOrGrpUniqueName).collect(Collectors.joining(","));
	}
 	
	static final Set<String> RESERVED_FIELD_NAMES = Set.of("name", "description", "tags");

	/**
	 * Comparator used to order inventory record list by name (asc/desc).
	 */
	public static final Comparator<InventoryRecord> NAME_COMPARATOR = new Comparator<>() {
		@Override
		public int compare(InventoryRecord invRec1, InventoryRecord invRec2) {
			return invRec1.getName().toLowerCase().compareTo(invRec2.getName().toLowerCase());
		}
	};
	
	public static final Comparator<InventoryRecord> TYPE_COMPARATOR = new Comparator<>() {
		@Override
		public int compare(InventoryRecord invRec1, InventoryRecord invRec2) {
			return invRec1.getType().toString().compareTo(invRec2.getType().toString());
		}
	};

	public static final Comparator<InventoryRecord> GLOBAL_ID_PREFIX_COMPARATOR = new Comparator<>() {
		@Override
		public int compare(InventoryRecord invRec1, InventoryRecord invRec2) {
			return invRec1.getType().toString().compareTo(invRec2.getType().toString());
		}
	};
	
	public static final Comparator<InventoryRecord> ID_COMPARATOR = 
			Comparator.comparing(InventoryRecord::getId);

	public static final Comparator<InventoryRecord> CREATION_DATE_COMPARATOR = 
			Comparator.comparing(InventoryRecord::getCreationDate);

	public static final Comparator<InventoryRecord> MODIFICATION_DATE_COMPARATOR = 
			Comparator.comparing(InventoryRecord::getModificationDate); 

	public InventoryRecord() {
		editInfo = new EditInfo();
		setCreationDate(new Date());
		setModificationDate(new Date());
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	public Long getId() {
		return id;
	}

	@Embedded
	public EditInfo getEditInfo() {
		return editInfo;
	}

	@Transient
	@AuditTrailProperty(name = "name")
	@Field
	public String getName() {
		return getEditInfo().getName();
	}

	public void setName(String name) {
		getEditInfo().setName(name);
	}

	/**
	 * Gets when an item was deleted. Can be null (if not deleted, or was deleted and restored).
	 * <br/>
	 * Returns a copy of the stored date object for better encapsulation
	 * @return
	 */
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDeletedDate() {
		return (deletedDate != null) ? new Date(deletedDate.getTime()) : null;
	}

	/** For hibernate. When deleting don't try setting the flag directly, call {@link #setRecordDeleted(boolean)} */
	void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}
	
	public void setRecordDeleted(boolean isDeleted) {
		setDeleted(isDeleted);
		// if isDeleted is false, we're restoring.
		setDeletedDate(isDeleted ? new Date() : null);
	}

	@Field
	public String getTags() {
		return tags;
	}

	@Lob
	public String getTagMetaData() {
		return tagMetaData;
	}
	
	@Transient
	@Field
	public String getDescription() {
		return getEditInfo().getDescription();
	}

	public void setDescription(String description) {
		getEditInfo().setDescription(description);
	}
	
	@Transient
	public Date getCreationDate() {
		return getEditInfo().getCreationDate();
	}

	@Transient
	void setCreationDate(Date creationDate) {
		getEditInfo().setCreationDate(creationDate);
	}

	@Transient
	public Date getModificationDate() {
		return getEditInfo().getModificationDate();
	}

	@Transient
	public void setModificationDate(Date modificationDate) {
		getEditInfo().setModificationDate(modificationDate);
	}

	@Transient
	public String getCreatedBy() {
		return getEditInfo().getCreatedBy();
	}
	
	public void setCreatedBy(String createdBy) {
		getEditInfo().setCreatedBy(createdBy);
	}

	@Transient
	public String getModifiedBy() {
		return getEditInfo().getModifiedBy();
	}
	
	public void setModifiedBy(String modifiedBy) {
		getEditInfo().setModifiedBy(modifiedBy);
	}

	public void setModifiedBy(String modifiedBy, IActiveUserStrategy modifyByStategy) {
		modifiedBy = modifyByStategy.getOriginalUser(modifiedBy);
		getEditInfo().setModifiedBy(modifiedBy);
	}

	@Transient
	public abstract User getOwner();
	
	@Transient
	public abstract InventoryRecordType getType();

	@Transient 
	public boolean isSample() {
		return InventoryRecordType.SAMPLE.equals(getType());
	}

	@Transient 
	public boolean isSubSample() {
		return InventoryRecordType.SUBSAMPLE.equals(getType());
	}
	
	@Transient 
	public boolean isContainer() {
		return InventoryRecordType.CONTAINER.equals(getType());
	}
	
	@Embedded
	@AttributeOverrides({
		@AttributeOverride(name = "unitId", column = @Column(name = "quantityUnitId")),
		@AttributeOverride(name = "numericValue", column = @Column(name = "quantityNumericValue", precision = 19, scale = 3))
	})
	public QuantityInfo getQuantityInfo() {
		return quantityInfo;
	}

	/* marked 'protected' so concrete subclass methods are used by the code outside */
	protected void setQuantityInfo(QuantityInfo quantityInfo) {
		if (quantityInfo != null && quantityInfo.getUnitId() != null) {
			if (BigDecimal.ZERO.compareTo(quantityInfo.getNumericValue()) > 0) {
				throw new IllegalArgumentException("Trying to set negative record quantity: " + quantityInfo.getNumericValuePlainString());
			}
		}
		this.quantityInfo = quantityInfo;
	}
	
	@Override
	@Transient
	public Integer getUnitId() {
		return getQuantityInfo().getUnitId();
	}

	@Override
	@Transient
	public BigDecimal getNumericValue() {
		return getQuantityInfo().getNumericValue();
	}

	@Transient
	protected abstract List<ExtraField> getExtraFields();

	public void addExtraField(ExtraField toAdd) {
		List<ExtraField> extraFields = getExtraFields();
		if (!extraFields.contains(toAdd)) {
			verifyFieldNameAllowed(toAdd.getName());
			toAdd.setInventoryRecord(this);
			extraFields.add(toAdd);
			refreshActiveExtraFields();
		}
	}
	
	protected void verifyFieldNameAllowed(String fieldName) {
		if (fieldName != null && getReservedFieldNames().contains(fieldName.toLowerCase())) {
			throw new IllegalArgumentException(String.format("'%s' is not a valid name for a field, "
						+ "as there is a default property with this name.", fieldName));
		}
	}

	private List<ExtraField> activeExtraFields;
	
	/**
	 * @return non-deleted extra fields attached to this inventory record
	 */
	@Transient
	public List<ExtraField> getActiveExtraFields() {
		if (activeExtraFields == null) {
			activeExtraFields = getExtraFields().stream().filter(ef -> !ef.isDeleted())
				.collect(Collectors.toList());
		}
		return activeExtraFields;
	}

	public void refreshActiveExtraFields() {
		activeExtraFields = null;
		getActiveExtraFields();
	}

	@Transient
	protected abstract List<InventoryFile> getFiles();

	private List<InventoryFile> attachedFiles;

	public void addAttachedFile(InventoryFile toAdd) {
		assertCanStoreAttachments();
		
		List<InventoryFile> files = getFiles();
		if (!files.contains(toAdd)) {
			toAdd.setInventoryRecord(this);
			files.add(toAdd);
			refreshActiveAttachedFiles();
		}
	}

	/**
	 * Whether this record accepts attaching inventory files
	 * 
	 * @throws IllegalArgumentException if files cannot be attached to this record
	 */
	protected void assertCanStoreAttachments() {
		; // no problem unless subclass decides otherwise
	}

	/**
	 * @return non-deleted inventory files attached to this record
	 */
	@Transient
	public List<InventoryFile> getAttachedFiles() {
		if (attachedFiles == null) {
			attachedFiles = getFiles().stream().filter(ef -> !ef.isDeleted())
				.collect(Collectors.toList());
		}
		return attachedFiles;
	}

	public void refreshActiveAttachedFiles() {
		attachedFiles = null;
		getAttachedFiles();
	}

	@Transient
	protected abstract List<Barcode> getBarcodes();

	public void addBarcode(Barcode toAdd) {
		List<Barcode> barcodes = getBarcodes();
		if (!barcodes.contains(toAdd)) {
			toAdd.setInventoryRecord(this);
			barcodes.add(toAdd);
			refreshActiveBarcodes();
		}
	}

	private List<Barcode> activeBarcodes;
	
	/**
	 * @return non-deleted extra fields attached to this inventory record
	 */
	@Transient
	public List<Barcode> getActiveBarcodes() {
		if (activeBarcodes == null) {
			activeBarcodes = getBarcodes().stream().filter(ef -> !ef.isDeleted())
					.collect(Collectors.toList());
		}
		return activeBarcodes;
	}

	public void refreshActiveBarcodes() {
		activeBarcodes = null;
		getActiveBarcodes();
	}

	@Transient
	protected abstract List<DigitalObjectIdentifier> getIdentifiers();

	public void addIdentifier(DigitalObjectIdentifier toAdd) {
		List<DigitalObjectIdentifier> identifiers = getIdentifiers();
		if (!identifiers.contains(toAdd)) {
			toAdd.setInventoryRecord(this);
			identifiers.add(toAdd);
			refreshActiveIdentifiers();
		}
	}
	private List<DigitalObjectIdentifier> activeIdentifiers;

	/**
	 * @return non-deleted identifiers attached to this inventory record
	 */
	@Transient
	public List<DigitalObjectIdentifier> getActiveIdentifiers() {
		if (activeIdentifiers == null) {
			activeIdentifiers = getIdentifiers().stream().filter(i -> !i.isDeleted())
					.collect(Collectors.toList());
		}
		return activeIdentifiers;
	}

	public void refreshActiveIdentifiers() {
		activeIdentifiers = null;
		getActiveIdentifiers();
	}	
	
	public void increaseVersion() {
		version++;
	}
	
	@Transient
	public GlobalIdentifier getOid() {
		return new GlobalIdentifier(getGlobalIdPrefix(), getId());
	}
	
	@Transient
	public GlobalIdentifier getOidWithVersion() {
		return new GlobalIdentifier(getGlobalIdPrefix(), getId(), version);
	}
	
	@Transient
	public abstract GlobalIdPrefix getGlobalIdPrefix();
	
	@AuditTrailIdentifier
	@Transient
	public String getGlobalIdentifier() {
		return getId() != null ? getOid().toString() : null;
	}
	
	@ManyToOne
	@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
	public FileProperty getImageFileProperty() {
		return imageFileProperty;
	}

	@ManyToOne
	@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
	public FileProperty getThumbnailFileProperty() {
		return thumbnailFileProperty;
	}

	@Transient
	public Set<String> getReservedFieldNames() {
		return RESERVED_FIELD_NAMES;
	}

	/**
	 * Copy the inventory item.
	 *
	 * @param currentUser user to set as a creator and owner of the copy
	 */
	abstract InventoryRecord copy(User currentUser);

	/*
	 * Copies properties except ID and relations/collections
	 * subclasses can override. When copy is supported by all subclasses we can make this abstract
	 */
	abstract InventoryRecord shallowCopy();
	
	void shallowCopyBasicFields(InventoryRecord copy) {
		copy.setEditInfo(getEditInfo().shallowCopy());
		copy.setTags(getTags());
		copy.setTagMetaData(getTagMetaData());
		if(getQuantityInfo()!=null) {
			copy.setQuantityInfo(getQuantityInfo().copy());
		}
		copy.setIconId(getIconId());
		copy.setDeleted(deleted);
		copy.setDeletedDate(deletedDate);
		copy.setSharingMode(sharingMode);
		copy.setSharingACL(sharingACL);
		
		for (ExtraField ef: getActiveExtraFields()) {
			copy.addExtraField(ef.shallowCopy());
		}
		for (Barcode barcode: getActiveBarcodes()) {
			copy.addBarcode(barcode.shallowCopy());
		}
		for (InventoryFile invFile: getAttachedFiles()) {
			copy.addAttachedFile(invFile.shallowCopy());
		}
	}
	
	String defaultNameCopy(InventoryRecord original) {
			return original.getName() + "_COPY";
	}

}
