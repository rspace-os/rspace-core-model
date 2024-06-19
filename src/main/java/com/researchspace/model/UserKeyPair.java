package com.researchspace.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * Entity class for storing ssh key (public and private part) of the user.
 */
@Entity
public class UserKeyPair implements Serializable {

	private static final long serialVersionUID = 384078875624523136L;

	private Long id;
	private User user; // owner of the key

	private String privateKey; // private key part
	private String publicKey; // public key part

	public UserKeyPair() {
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@ManyToOne
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Column(length = 1000)
	public String getPrivateKey() {
		return privateKey;
	}

	public void setPrivateKey(String privateKey) {
		this.privateKey = privateKey;
	}

	@Column
	public String getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}

	// for debug
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("\nuser = " + user);
		sb.append("\npublicKey = " + publicKey);
		return sb.toString();
	}

}
