package com.researchspace.model.netfiles;

/**
 * Enum representing authentication methods supported by external file systems.
 */
public enum NfsAuthenticationType {

	/**
	 * username/password type of authentication
	 */
	PASSWORD,
	
	/**
	 * public/private key authentication
	 */
	PUBKEY;
	
	/**
	 * Get enum object for linked string value
	 */
	public static NfsAuthenticationType fromString(String authTypeString) {
		if ("password".equals(authTypeString)) {
			return PASSWORD;
		}
		if ("pubKey".equals(authTypeString)) {
			return PUBKEY;
		}
		throw new IllegalArgumentException("unrecognised authentication type: " + authTypeString);
	}

}
