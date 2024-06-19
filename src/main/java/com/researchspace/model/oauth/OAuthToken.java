package com.researchspace.model.oauth;

import static org.apache.commons.lang.StringUtils.isBlank;

import java.io.Serializable;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
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
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "clientId"}))
@EqualsAndHashCode(of = {"clientId", "user", "hashedAccessToken", "scope", "expiryTime", "hashedRefreshToken"})
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@NoArgsConstructor
public class OAuthToken implements Serializable, AuthenticationToken {

	private static final long serialVersionUID = 1L;
	private static final int TOKEN_LENGTH = 64; // SHA-256 hash in hex

	/**
	 * "all"
	 */
	public static final String DEFAULT_SCOPE = "all";

	/**
	 * Default value of expiry time, approx 40 years in future
	 */
	public static final Instant DEFAULT_EXPIRY_TIME = Instant.now().plus(Integer.MAX_VALUE, ChronoUnit.SECONDS);

	/**
	 * Public constructor to make a valid OAuthToken with all required fields. Scope will be set to default 'all' scope
	 *
	 * @param clientId          - non-empty string
	 * @param hashedAccessToken - non-empty string
	 */
	public OAuthToken(User user, String clientId, String hashedAccessToken, Instant expiryTime) {
		Validate.noNullElements(new Object[]{user, clientId}, "No null arguments");
		Validate.isTrue(expiryTime == null || expiryTime.isAfter(Instant.now()), "expiry time must be in the future ");
		Validate.isTrue(!isBlank(hashedAccessToken) || hashedAccessToken == null, "access token must be non-empty");
		Validate.isTrue(!isBlank(clientId), "client ID  must be non-empty");
		this.user = user;
		this.clientId = clientId;
		this.expiryTime = expiryTime;
		this.scope = DEFAULT_SCOPE;
		this.hashedAccessToken = hashedAccessToken;
	}

	/**
	 * Public constructor to make a valid OAuthToken with all required fields.
	 * <br>Default expiry time will be set to now + Integer.Maxvalue (approximately 40 years from now).
	 */
	public OAuthToken(User user, String clientId, String hashedAccessToken) {
		this(user, clientId, hashedAccessToken, DEFAULT_EXPIRY_TIME);
	}

	@Id
	@Getter
	@Setter(AccessLevel.PRIVATE)
	@GeneratedValue(strategy = GenerationType.TABLE)
	private Long id;

	@Getter
	@Setter(AccessLevel.PRIVATE)
	@Column(nullable = false)
	private String clientId;

	@ManyToOne(optional = false)
	@Getter
	@Setter(AccessLevel.PRIVATE)
	private User user;

	/**
	 * Can be null if only JWT tokens are requested.
	 * Use expiry time to test the validity of the token.
	 */
	@Getter
	@Setter
	@Size(min = TOKEN_LENGTH, max = TOKEN_LENGTH)
	@Column(name = "accessToken", length = TOKEN_LENGTH, unique = true)
	private String hashedAccessToken;

	@Getter
	@Setter
	@Column(nullable = false)
	private String scope;

	@Getter
	@Setter
	@Type(type = "org.hibernate.type.InstantType")
	private Instant expiryTime;

	/**
	 * <code>null</code> means access token can't be refreshed
	 */
	@Getter
	@Setter
	@Size(min = TOKEN_LENGTH, max = TOKEN_LENGTH)
	@Column(name = "refreshToken", length = TOKEN_LENGTH, unique = true)
	private String hashedRefreshToken;

	/**
	 * To identify the user
	 */
	@Override
	public Object getPrincipal() {
		return user.getUsername();
	}

	@Override
	public Object getCredentials() {
		return hashedAccessToken;
	}
}
