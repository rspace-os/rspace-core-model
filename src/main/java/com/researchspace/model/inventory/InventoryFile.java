
package com.researchspace.model.inventory;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;
import org.hibernate.search.annotations.Field;

import com.researchspace.model.FileProperty;
import com.researchspace.model.core.GlobalIdPrefix;
import com.researchspace.model.core.GlobalIdentifier;
import com.researchspace.model.inventory.field.SampleField;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Basic model used to represent all files added as inventory attachments
 * 
 */
@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Audited
public class InventoryFile extends InventoryRecordConnectedEntity implements Serializable {

	private static final long serialVersionUID = -5314995639182094423L;

	private Long id;
	
	// indexing filename together with field data
	@Field(name = "fieldData") 
	private String fileName;

	private Date creationDate;
	private String createdBy;

	private String extension;
	private FileProperty fileProperty;
	private long size;
	private InventoryFileType fileType = InventoryFileType.GENERAL;
	private String contentMimeType;
	private boolean deleted;
	
	private SampleField sampleField; 
	
	public enum InventoryFileType {
		GENERAL, CHEMICAL
	}

	public InventoryFile(String fileName, FileProperty fileProperty) {
		setFileName(fileName);
		setFileProperty(fileProperty);
		if (fileProperty != null) {
			setSize(Long.parseLong(fileProperty.getFileSize()));
		}
		setCreationDate(new Date());
	}

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	public Long getId() {
		return id;
	}
	
	@Transient
	public GlobalIdentifier getOid() {
		return new GlobalIdentifier(GlobalIdPrefix.IF, id);
	}
	
	/**
	 * Date of entity creation, i.e. date of uploading inventory file to RSpace.
	 * Returns a copy of the stored date object for better encapsulation
	 */
	@Column(nullable = false, updatable = false)
	@Temporal(TemporalType.TIMESTAMP)
	public Date getCreationDate() {
		return creationDate == null ? null : new Date(creationDate.getTime());
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
	public FileProperty getFileProperty() {
		return fileProperty;
	}

	public void setFileProperty(FileProperty fileProperty) {
		this.fileProperty = fileProperty;
	}

	@ManyToOne(cascade = CascadeType.MERGE)
	private SampleField getSampleField() {
		return sampleField;
	}

	@Transient
	@Override
	public GlobalIdentifier getConnectedRecordOid() {
		return sampleField != null ? sampleField.getOid() : super.getConnectedRecordOid();
	}

	@Transient
	@Override
	protected int getNonInventoryRecordParentCount() {
		return sampleField == null ? 0 : 1;
	}
	
	/**
	 * Performs shallow copy of the attachment with copied reference to FileProperty. 
	 * Does not set InventoryRecord relation.
	 */
	public InventoryFile shallowCopy() {
		InventoryFile copy = new InventoryFile(getFileName(), getFileProperty());
		copy.setCreatedBy(getCreatedBy());
		copy.setExtension(getExtension());
		copy.setSize(getSize());
		copy.setFileType(getFileType());
		copy.setContentMimeType(getContentMimeType());
		copy.setDeleted(isDeleted());
		return copy;
	}
	
}
