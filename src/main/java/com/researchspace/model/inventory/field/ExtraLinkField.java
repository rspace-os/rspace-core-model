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
import lombok.Setter;

/**
 * Represents an extra field of type 'link' in inventory items. Links to another InventoryItem.
 */
@Entity
@Audited
@Setter
@EqualsAndHashCode(callSuper = true)
@DiscriminatorValue("link")
public class ExtraLinkField extends ExtraField {

	private static final long serialVersionUID = 1L;
	private static final String DEFAULT_NAME = "Link";

	// Annotations placed on getter (not field) because the ExtraField hierarchy
	// uses PROPERTY access (@Id is on getId()). With field-level annotations
	// Hibernate ignores @JoinColumn and falls back to the property name as the
	// column name (yielding `link` instead of `link_id`).
	private InventoryLink link;

	public ExtraLinkField() {
		setName(DEFAULT_NAME);
	}

	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "link_id", unique = true)
	public InventoryLink getLink() {
		return link;
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
		if (link != null) {
			copy.setLink(link.shallowCopy());
		}
		return copy;
	}

}
