package com.researchspace.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlType;

import com.researchspace.model.preference.Preference;
import com.researchspace.model.preference.SettingsType;

/**
 * Persists user preferences.
 */
@Entity
@XmlType()
@XmlAccessorType(XmlAccessType.NONE)
public class UserPreference implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -575396996158819503L;

	private Preference preference;

	private User user;

	private Long id;

	private String value;

	/**
	 * Convenience constructor. Does fail-fast checking of <code>value</code>
	 * which should be:
	 * <ul>
	 * <li>"true" or "false" if boolean
	 * <li>Parseable into a Double if number
	 * <li>less than 255 characters length if String.
	 * </ul>
	 * 
	 * @param preference
	 * @param user
	 * @param value
	 *            - should be parsable into preference type. Any String should
	 *            be &lt; 255 characters.
	 * @throws if
	 *             <code>value</code> is incompatible with its preference type.
	 *             This ensures that no invalid data is stored in the database.
	 *             m
	 */
	public UserPreference(Preference preference, User user, String value) {
		super();
		checkValue(preference, value);
		this.preference = preference;
		this.user = user;
		this.value = value;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((preference == null) ? 0 : preference.hashCode());
		result = prime * result + ((user == null) ? 0 : user.hashCode());
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
		UserPreference other = (UserPreference) obj;
		if (preference != other.preference) {
			return false;
		}
		if (user == null) {
			if (other.user != null) {
				return false;
			}
		} else if (!user.equals(other.user)) {
			return false;
		}
		return true;
	}

	private void checkValue(Preference preference, String value) {
		SettingsType.validate(preference.getPrefType(), value);

		// for ENUM preference type run the validator to confirm that string
		// value is correct
		if (preference.getPrefType().equals(SettingsType.ENUM)) {
			String validationMsg = preference.getInvalidErrorMessageForValue(value);
			if (validationMsg != null) {
				throw new IllegalArgumentException(validationMsg);
			}
		}
	}

	@Override
	public String toString() {
		return "UserPreference [preference=" + preference + ", user=" + (user == null ? user : user.getUniqueName())
				+ ", value=" + value + "]";
	}

	/**
	 * For hibernate
	 */
	public UserPreference() {
		super();
	}

	@XmlElement
	public Preference getPreference() {
		return preference;
	}

	public void setPreference(Preference preference) {
		this.preference = preference;
	}

	@ManyToOne()
	@XmlIDREF
	@XmlElement
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	public Long getId() {
		return id;
	}

	void setId(Long id) {
		this.id = id;
	}

	@XmlElement
	public String getValue() {
		if (value == null && preference != null) {
			value = preference.getDefaultValue();
		}
		return value;
	}

	/**
	 * @throws IllegalArgumentException
	 *             if <code>value</code> is incompatible with its preference
	 *             type.
	 */
	public void setValue(String value) {
		if (preference != null && value != null) {
			checkValue(preference, value);
		}
		this.value = value;
	}

	@Transient
	public boolean isBooleanType() {
		return SettingsType.BOOLEAN.equals(this.preference.getPrefType());
	}

	/**
	 * Convenience getter for a boolean-type preference value
	 * 
	 * @return
	 * @throws if
	 *             is not a {@link Boolean} type-preference
	 */
	@Transient
	public boolean getValueAsBoolean() {
		if (!isBooleanType()) {
			throw new IllegalStateException(" Not a boolean type");
		}
		return Boolean.valueOf(getValue());
	}

	/**
	 * Convenience getter for a numeric-type preference value
	 * 
	 * @return
	 * @throws IllegalStateException
	 *             if is not a {@link Boolean} type-preference
	 */
	@Transient
	public Number getValueAsNumber() {
		if (!isNumeric()) {
			throw new IllegalStateException(" Not a numeric type");
		}
		return Double.valueOf(getValue());
	}

	@Transient
	public boolean isNumeric() {
		return SettingsType.NUMBER.equals(this.preference.getPrefType());
	}

}
