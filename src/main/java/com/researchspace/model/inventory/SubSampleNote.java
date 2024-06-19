package com.researchspace.model.inventory;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;
import org.hibernate.search.annotations.Field;

import com.researchspace.model.User;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Audited
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = {"id", "content", "creationDateMillis"})
public class SubSampleNote implements Serializable {

	private static final long serialVersionUID = 284160459745885323L;

	private Long id;
	
	private Long creationDateMillis;
	private User createdBy;

	// indexing notes together with field data
	@Field(name = "fieldData")
	private String content;
	
	private SubSample subSample;

	public SubSampleNote(String content, User creator) {
		setContent(content);
		setCreationDateMillis(new Date().getTime());
		setCreatedBy(creator);
		setSubSample(subSample);
	}

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	public Long getId() {
		return id;
	}
	
	@ManyToOne
	@JoinColumn(nullable = false)
	@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
	public User getCreatedBy() {
		return createdBy;
	}

	@ManyToOne(cascade = CascadeType.MERGE)
	@JoinColumn(nullable = false)
	private SubSample getSubSample() {
		return subSample;
	}
	/*
	 * Copies properties (except id, not related properties)
	 */
	SubSampleNote shallowCopy() {
		SubSampleNote copy = new SubSampleNote(content, null);
		copy.setCreationDateMillis(creationDateMillis);
		return copy;
	}
	
	@Column(nullable = false)
	public Long getCreationDateMillis() {
		return creationDateMillis ;
	}
	private void setCreationDateMillis(Long millis) {
		this.creationDateMillis = millis;
	}
	/**
	 * Content length for subsample note
	 * @return
	 */
	@Column(length = 2000)
	public String getContent() {
		return content;
	}

}
