package com.researchspace.model.record;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.researchspace.model.User;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FormUserMenu implements Serializable {

	private static final long serialVersionUID = -7394248632989885038L;

	private User user;

	private String formStableId;

	private Long id;

	public FormUserMenu(User toAdd, RSForm form) {
		this.user = toAdd;
		this.formStableId = form.getStableID();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((formStableId == null) ? 0 : formStableId.hashCode());
		result = prime * result + ((user == null) ? 0 : user.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FormUserMenu other = (FormUserMenu) obj;
		if (formStableId == null) {
			if (other.formStableId != null)
				return false;
		} else if (!formStableId.equals(other.formStableId))
			return false;
		if (user == null) {
			if (other.user != null)
				return false;
		} else if (!user.equals(other.user))
			return false;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "FormUserMenu [user=" + user + ", formStableId=" + formStableId + ", id=" + id + "]";
	}

	/**
	 * @return the user
	 */
	@ManyToOne
	public User getUser() {
		return user;
	}

	/**
	 * @param user
	 *            the user to set
	 */
	public void setUser(User user) {
		this.user = user;
	}

	/**
	 * This will maintain a refernece to a form even if the form is edited and a
	 * new version created.
	 * 
	 * @return the formStableId
	 */
	@Column(nullable = false)
	public String getFormStableId() {
		return formStableId;
	}

	/**
	 * @param formStableId
	 *            the formStableId to set
	 */
	public void setFormStableId(String formStableId) {
		this.formStableId = formStableId;
	}

	/**
	 * @return the id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	public Long getId() {
		return id;
	}

	/*
	 * For hibernate /testing only
	 * 
	 * @param id the id to set
	 */
	void setId(Long id) {
		this.id = id;
	}

}
