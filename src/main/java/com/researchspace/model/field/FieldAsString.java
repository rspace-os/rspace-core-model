package com.researchspace.model.field;

import javax.persistence.Column;
import javax.persistence.Entity;

import org.hibernate.envers.Audited;

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

	public void setData(String data) {
		this.data = data;
	}

}
