package com.researchspace.offline.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.researchspace.model.User;
import com.researchspace.model.record.BaseRecord;

/**
 * Stores information about offline Records selected by Users.
 */

@Entity
public class OfflineRecordUser implements Serializable {
	
	private static final long serialVersionUID = 7796743283137030639L;

	private Long id;

	private BaseRecord record;

	private User user;

	private OfflineWorkType workType;
	
	private Date creationDate;
	
	public OfflineRecordUser() { }
	
	public OfflineRecordUser(BaseRecord record, User user) {
		this.record = record;
		this.user = user;
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@ManyToOne()
	@JoinColumn(nullable = false)	
	public BaseRecord getRecord() {
		return record;
	}
	
	public void setRecord(BaseRecord record) {
		this.record = record;
	}

	@ManyToOne
	@JoinColumn(nullable = false)	
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Column(nullable = false)	
	public OfflineWorkType getWorkType() {
		return workType;
	}

	public void setWorkType(OfflineWorkType workType) {
		this.workType = workType;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

}
