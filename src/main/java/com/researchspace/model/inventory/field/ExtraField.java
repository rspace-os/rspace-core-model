package com.researchspace.model.inventory.field;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Transient;

import org.hibernate.envers.Audited;
import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Analyzer;
import org.hibernate.search.annotations.Store;

import com.researchspace.model.audittrail.AuditTrailProperty;
import com.researchspace.model.core.GlobalIdPrefix;
import com.researchspace.model.core.GlobalIdentifier;
import com.researchspace.model.field.FieldType;
import com.researchspace.model.inventory.InventoryRecordConnectedEntity;
import com.researchspace.model.record.EditInfo;
import com.researchspace.model.record.IActiveUserStrategy;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Sample field is used to hold field data for Samples.
 */
@Entity
@Getter
@Setter
@EqualsAndHashCode(of = {"id", "editInfo"}, callSuper = false)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Audited
public abstract class ExtraField extends InventoryRecordConnectedEntity implements Serializable {

	private static final long serialVersionUID = 2062310963640792742L;

	private Long id;
	private EditInfo editInfo;
	protected boolean deleted;

	public ExtraField() {
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
	public String getName() {
		return getEditInfo().getName();
	}

	public void setName(String name) {
		getEditInfo().setName(name);
	}

	@Transient
	@org.hibernate.search.annotations.Field(analyzer = @Analyzer(definition = "structureAnalyzer"),
			analyze = Analyze.YES, name = "fieldData", store = Store.NO)
	public String getData() {
		return getEditInfo().getDescription();
	}

	public void setData(String description) {
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

	/**
	 * @return type of the field
	 */
	@Transient
	public abstract FieldType getType();

	/** 
	 * Validates provided data, returns error message if invalid  
	 */
	@Transient
	public abstract String validateNewData(String data);

	@Transient
	public GlobalIdentifier getOid() {
		return new GlobalIdentifier(GlobalIdPrefix.EF, getId());
	}

	/*
	 * Performs shallow copy of data and getInfo fields. Does not set InventoryRecordRelation
	 */
	public abstract ExtraField shallowCopy();
	
	void copyProperties(ExtraField copy) {
		copy.setEditInfo(getEditInfo().shallowCopy());
		copy.setData(getData());
		copy.setDeleted(isDeleted());
	}

}
