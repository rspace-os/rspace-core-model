package com.researchspace.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import lombok.NoArgsConstructor;
import org.hibernate.annotations.FilterDef;
import org.hibernate.envers.Audited;

import com.researchspace.model.field.Field;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;

/**
 * Join table for Field/Media link associations. This needs to be an entity,
 * rather than defined as ManyToMAny since otherwise the revision history
 * mechanism fails. 
 * <p>
 * Also these associations can be marked deleted.
 */
@Entity
@Audited
@FilterDef(name = "fieldAttachmentNotDeleted", defaultCondition = "deleted = 0")
@Data
@EqualsAndHashCode(of = { "mediaFile", "field" })
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FieldAttachment implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	@Setter(AccessLevel.PACKAGE)
	private Long id;

	@ManyToOne
	private Field field;

	@ManyToOne
	private EcatMediaFile mediaFile;

	/**
	 * Boolean flag for whether this attachment is marked as deleted/
	 */
	private boolean deleted = false;

	/**
	 * 
	 */
	private static final long serialVersionUID = 8912412332370429772L;

	public FieldAttachment(Field field, EcatMediaFile mediaFile) {
		super();
		this.field = field;
		this.mediaFile = mediaFile;
	}
	/**
	 * 
	 * @param deleted
	 * @return this for method chaining
	 */
	public FieldAttachment setDeleted (boolean deleted) {
		this.deleted = deleted;
		return this;
	}

}
