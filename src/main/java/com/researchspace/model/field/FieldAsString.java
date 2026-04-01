package com.researchspace.model.field;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Transient;

import org.hibernate.envers.Audited;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexingDependency;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.ObjectPath;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.PropertyValue;

/**
 * Subclasses of this Field class store Field data as a plain String.
 */
@Entity
@Audited
public abstract class FieldAsString extends Field {

	private static final long serialVersionUID = 1L;

	private String data;

	@Column(name = "data", length = 1000)
	public String getData() {
		return data;
	}

	@Override
	@Transient
	@FullTextField(analyzer = "structureAnalyzer", name = "fieldData")
	@IndexingDependency(derivedFrom = @ObjectPath(@PropertyValue(propertyName = "data")))
	public String getFieldData() {
		return super.getFieldData();
	}

	public void setData(String data) {
		this.data = data;
	}

}
