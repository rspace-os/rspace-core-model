package com.researchspace.model.inventory.field;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import org.hibernate.envers.Audited;

import com.researchspace.model.field.FieldType;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Audited
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@DiscriminatorValue("link")
/**
 * Represents an extra field of type 'link' in inventory items. Links to another InventoryItem.
 */
public class ExtraLinkField extends ExtraField {

	private static final long serialVersionUID = 1L;
	private static final String DEFAULT_NAME = "Link";

	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "link_id", unique = true)
	private InventoryLink link;

	public ExtraLinkField() {
		setName(DEFAULT_NAME);
	}

	@Transient
	@Override
	public FieldType getType() {
		return FieldType.LINK;
	}

	@Override
	public String validateNewData(String data) {
		return null;
	}

	@Override
	public ExtraLinkField shallowCopy() {
		ExtraLinkField copy = new ExtraLinkField();
		copyProperties(copy);
		return copy;
	}

}
