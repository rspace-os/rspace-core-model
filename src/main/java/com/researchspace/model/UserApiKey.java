package com.researchspace.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.NaturalId;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * UserApiKey object Must be an alphanumeric string of between 16 and 32
 * characters.
 */
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class UserApiKey implements Serializable {

	public static final String APIKEY_REGEX = "[A-Za-z_0-9]+";
	private static final long serialVersionUID = 5989574626306310911L;

	private String apiKey;
	private Date created;
	private Long id;
	private User user;

	@OneToOne(optional = false)
	@JsonIgnore
	public User getUser() {
		return user;
	}

	protected void setUser(User user) {
		this.user = user;
	}

	@Id()
	@GeneratedValue(strategy = GenerationType.AUTO)
	@JsonIgnore
	public Long getId() {
		return id;
	}

	protected void setId(Long id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "UserApiKey [key=" + apiKey + ", created=" + created + "]";
	}

	public UserApiKey(User user, String apiKey) {
		super();
		this.apiKey = apiKey;
		this.user = user;
		this.created = new Date();
	}

	@Column(nullable = false, length = 32, unique = true)
	@Size(min = 16, max = 32)
	@Pattern(regexp = APIKEY_REGEX)
	@NaturalId(mutable = true)// can be  reset
	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((apiKey == null) ? 0 : apiKey.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserApiKey other = (UserApiKey) obj;
		if (apiKey == null) {
			if (other.apiKey != null)
				return false;
		} else if (!apiKey.equals(other.apiKey))
			return false;
		return true;
	}

	@Column(nullable = false)
	public Date getCreated() {
		return (created == null)?created:new Date(created.getTime());
	}

	public void setCreated(Date created) {
		this.created = created;
		if(created != null) {
			this.created = new Date(created.getTime());
		}		 
	}
}
