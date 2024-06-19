package com.researchspace.model;

import static com.researchspace.core.util.LinkUtils.HTTPS_PREFIX;
import static com.researchspace.core.util.LinkUtils.HTTP_PREFIX;
import static com.researchspace.core.util.LinkUtils.modifyPrefix;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Stores profile information about a user.
 */
@Entity
@XmlType
@XmlAccessorType(XmlAccessType.NONE)
public class UserProfile implements Serializable, Comparable<UserProfile> {

	private static final long serialVersionUID = 6039997143520226780L;

	public static final int MAX_FIELD_LENG = 255;

	protected static final int MAX_PROFILE_TEXT_LEN = 2000;

	protected static final int SHORT_PROFILE_TEXT_LEN = 100;

	protected static final String DEFAULT_PROFILE_TEXT = "Optionally, add any information about you or your research.";

	private String profileText = DEFAULT_PROFILE_TEXT;

	private String externalLinkURL;

	private String externalLinkDisplay;

	private Long id;

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	public Long getId() {
		return id;
	}

	void setId(Long id) {
		this.id = id;
	}

	private ImageBlob profilePicture;

	public UserProfile(User user) {
		if (user == null) {
			throw new IllegalArgumentException("User cannot be null");
		}
		this.owner = user;
	}

	/**
	 * Default constructor
	 */
	public UserProfile() {

	}

	private User owner;

	@OneToOne(optional = false)
	@XmlElement
	@XmlIDREF
	@JsonIgnore
	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		if (owner == null) {
			throw new IllegalArgumentException("user cannot be null");
		}
		this.owner = owner;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((owner == null) ? 0 : owner.hashCode());
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
		UserProfile other = (UserProfile) obj;
		if (owner == null) {
			if (other.owner != null) {
				return false;
			}
		} else if (!owner.equals(other.owner)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "UserProfile for user [" + (owner == null ? owner : owner.getUsername()) + "] -  [profileText="
				+ StringUtils.abbreviate(profileText, 100) + ", externalLinkURL=" + externalLinkURL
				+ ", externalLinkDisplay=" + externalLinkDisplay + "]";
	}

	@Column(length = MAX_PROFILE_TEXT_LEN)
	@XmlElement
	public String getProfileText() {
		return profileText;
	}

	/**
	 * Sets profile text, abbreviating it to fit inside DB column size limit if
	 * too big
	 * 
	 * @param profileText
	 */
	public void setProfileText(String profileText) {
		this.profileText = StringUtils.abbreviate(profileText, MAX_PROFILE_TEXT_LEN);
	}

	/**
	 * Gets abbreviated profile text to be displayed in a listing of users, for
	 * example
	 * 
	 * @return abbreviated profile text, or null if profile text was not set by
	 *         the user
	 */
	@Transient
	public String getShortProfileText() {
		if (hasNonDefaultProfileText()) {
			return StringUtils.abbreviate(profileText, SHORT_PROFILE_TEXT_LEN);
		}
		return null;
	}

	private boolean hasNonDefaultProfileText() {
		return !DEFAULT_PROFILE_TEXT.equals(profileText);
	}

	@XmlElement
	public String getExternalLinkURL() {
		return externalLinkURL;
	}

	/**
	 * Sets the external link, prefixing with 'http://' if not already there.
	 * 
	 * @throws if
	 *             length of string > MAX_FIELD_LENG
	 * @param externalLinkURL
	 */
	public void setExternalLinkURL(String externalLinkURL) {
		if (externalLinkURL != null && isTooLong(externalLinkURL)) {
			throw new IllegalArgumentException(
					"Link URL is too long! Must be < " + (MAX_FIELD_LENG - HTTPS_PREFIX.length()) + " chars");
		}
		if (!StringUtils.isBlank(externalLinkURL)) {
			if (externalLinkURL.trim().startsWith(HTTP_PREFIX) || externalLinkURL.trim().startsWith(HTTPS_PREFIX)) {
				this.externalLinkURL = externalLinkURL.trim();

			} else {
				this.externalLinkURL = modifyPrefix(externalLinkURL.trim());
			}
		} else {
			this.externalLinkURL = externalLinkURL;
		}

	}

	@XmlElement()
	public String getExternalLinkDisplay() {
		return externalLinkDisplay;
	}

	public void setExternalLinkDisplay(String externalLinkDisplay) {
		this.externalLinkDisplay = StringUtils.abbreviate(externalLinkDisplay, MAX_FIELD_LENG);
	}

	/**
	 * 
	 * @return
	 */
	@OneToOne(cascade = CascadeType.ALL)
	public ImageBlob getProfilePicture() {
		return profilePicture;
	}

	public void setProfilePicture(ImageBlob profilePicture) {
		this.profilePicture = profilePicture;
	}

	@Transient
	public boolean isTooLong(String externalLinkInput) {
		if (externalLinkInput == null) {
			return false;
		}
		return externalLinkInput.length() >= MAX_FIELD_LENG - HTTPS_PREFIX.length();
	}

	@Override
	public int compareTo(UserProfile other) {
		return this.owner.compareTo(other.getOwner());
	}

}
