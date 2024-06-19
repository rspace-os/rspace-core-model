package com.researchspace.model.system;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;

import com.researchspace.model.Community;
import com.researchspace.model.preference.SettingsType;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SystemPropertyValue implements Serializable {

	private static final long serialVersionUID = -4926049568292799225L;

	private SystemProperty property;

	private String value;

	private Community community;

	private Long id;

	/**
	 * 
	 * @param sysprop A non-null {@link SystemProperty}
	 */
	public SystemPropertyValue(SystemProperty sysprop) {
	 Validate.notNull(sysprop);
	 this.property = sysprop;
	}

    /**
     * 
     * @param sysprop
     * @param value
     */
	public SystemPropertyValue(SystemProperty sysprop, String value) {
		this(sysprop);
		this.value = value;
	}

	public SystemPropertyValue(SystemProperty systemProperty, String value, Community community) {
		this(systemProperty, value);
		this.community = community;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	void setId(Long id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "SystemPropertyValue [property=" + property.getName() + ", value=" + value + ", id=" + id + ", community=" +
				((community != null) ? community.getUniqueName() : "null") + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((property == null) ? 0 : property.hashCode());
		if (community != null)
			result = prime * result + community.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		SystemPropertyValue other = (SystemPropertyValue) obj;
		if (property == null) {
			if (other.property != null) {
				return false;
			}
		} else if (!property.equals(other.property)) {
			return false;
		}
		if (community == null) {
			if (other.community != null) {
				return false;
			}
		} else if (!community.equals(other.community)) {
			return false;
		}
		return true;
	}

	@OneToOne(cascade = CascadeType.ALL)
	public SystemProperty getProperty() {
		return property;
	}

	public void setProperty(SystemProperty property) {
		this.property = property;
	}

	@Column(nullable = false)
	public String getValue() {
		return value;
	}

	@ManyToOne()
	public Community getCommunity() {
		return community;
	}

	public void setCommunity(Community community) {
		this.community = community;
	}

	/**
	 * Setter, performs basic validation using the underlying type of this property.
	 * @param value
	 * @throws IllegalArgumentException if value is not compatible with the underlying {@link SettingsType}
	 *  of this property
	 */
	public void setValue(String value) {
		if (property != null && !StringUtils.isEmpty(value)) {
			SettingsType.validate(property.getType(), value);
		}
		this.value = value;
	}

}
