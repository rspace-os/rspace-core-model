package com.researchspace.model.oauth;

import static org.apache.commons.lang.StringUtils.isBlank;

import java.io.Serializable;
import java.time.Instant;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.Validate;
import org.apache.shiro.authc.AuthenticationToken;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;

import com.researchspace.model.User;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@EqualsAndHashCode(of = {"clientId", "user", "hashedAccessToken", "tokenType", "expiryTime", "hashedRefreshToken"})
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OAuthToken implements Serializable, AuthenticationToken {

	private static final long serialVersionUID = 1234L;
	private static final int TOKEN_LENGTH = 64; // SHA-256 hash in hex

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	@Setter(AccessLevel.PRIVATE)
	private Long id;

	@Column(nullable = false)
	@Setter(AccessLevel.PRIVATE)
	private String clientId;

	@ManyToOne(optional = false)
	@Setter(AccessLevel.PRIVATE)
	private User user;

	@Column(nullable = false)
	private Date created;

	@Column(nullable = false)
	@Type(type = "org.hibernate.type.InstantType")
	private Instant expiryTime;

	/**
	 * Can be null if only JWT tokens are requested.
	 * Use expiry time to test the validity of the token.
	 */
	@Size(min = TOKEN_LENGTH, max = TOKEN_LENGTH)
	@Column(name = "accessToken", length = TOKEN_LENGTH, unique = true)
	private String hashedAccessToken;

	/**
	 * <code>null</code> means access token can't be refreshed
	 */
	@Size(min = TOKEN_LENGTH, max = TOKEN_LENGTH)
	@Column(name = "refreshToken", length = TOKEN_LENGTH, unique = true)
	private String hashedRefreshToken;

	@Column(nullable = false)
	private OAuthTokenType tokenType;

	/**
	 * Public constructor with all required fields.
	 */
	public OAuthToken(User user, String clientId, OAuthTokenType tokenType) {
		Validate.noNullElements(new Object[]{user, clientId, tokenType}, "userId/clientId/tokenType cannot be null");
		Validate.isTrue(!isBlank(clientId), "client ID must be non-empty");
		this.user = user;
		this.clientId = clientId;
		this.tokenType = tokenType;
		this.created = new Date();
	}

	public void setHashedAccessToken(String hashedAccessToken) {
		Validate.isTrue(!isBlank(hashedAccessToken) || hashedAccessToken == null, "access token must be non-empty");
		this.hashedAccessToken = hashedAccessToken;
	}

	/**
	 * To identify the user
	 */
	@Override
	@Transient
	public Object getPrincipal() {
		return user.getUsername();
	}

	@Override
	@Transient
	public Object getCredentials() {
		return hashedAccessToken;
	}
}
