package com.researchspace.model;

import java.io.Serializable;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Stores meta-data about version/state of application
 */
@Entity
public class RSMetaData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 261971692772281729L;

	private Version version;

	private boolean isInitialized;

	private Long id;

	public RSMetaData() {
		this.version = new Version(0L);
	}

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Embedded
	public Version getDBVersion() {
		return version;
	}

	public void setDBVersion(Version internalVersion) {
		this.version = internalVersion;
	}

	public boolean isInitialized() {
		return isInitialized;
	}

	public void setInitialized(boolean isInitialized) {
		this.isInitialized = isInitialized;
	}

}
