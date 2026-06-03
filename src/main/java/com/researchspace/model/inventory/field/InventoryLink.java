package com.researchspace.model.inventory.field;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import org.hibernate.envers.Audited;

import com.researchspace.model.core.GlobalIdPrefix;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Audited
@Getter
@Setter
@EqualsAndHashCode(of = {"id"})
public class InventoryLink implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "target_globalid", nullable = false, length = 64)
	private String targetGlobalId;

	@Column(name = "target_prefix", nullable = false, length = 8)
	@Enumerated(EnumType.STRING)
	private GlobalIdPrefix targetPrefix;

	@Column(name = "target_db_id", nullable = false)
	private Long targetDbId;

	@Column(name = "version_pin")
	private Long versionPin;

	/**
	 * The Envers revision (REV) this link's pin resolved to, captured at pin time. Null means the
	 * link is "latest" and resolves dynamically to the newest revision in the target's audit table.
	 * Paired with {@link #versionPin}: versionPin is the user-facing version for display, this is the
	 * exact audit-row key used to load the snapshot.
	 */
	@Column(name = "target_revision_id")
	private Long targetRevisionId;

	@Column(name = "relation_type", nullable = false, length = 64)
	private String relationType;

	@Column(name = "created_at", nullable = false)
	private Date createdAt;

	@Column(name = "modified_at", nullable = false)
	private Date modifiedAt;

	@Column(nullable = false)
	private boolean deleted;

	@PrePersist
	void prePersist() {
		Date now = new Date();
		if (createdAt == null) {
			createdAt = now;
		}
		modifiedAt = now;
	}

	@PreUpdate
	void preUpdate() {
		modifiedAt = new Date();
	}

}
