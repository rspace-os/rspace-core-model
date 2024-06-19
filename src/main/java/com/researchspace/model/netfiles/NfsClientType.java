package com.researchspace.model.netfiles;

/**
 * Enum representing types of external file systems that can be connected.
 */
public enum NfsClientType {

	/**
	 * samba/cifs client type
	 */
	SAMBA,
	
	/**
	 * samba2/smbj client type
	 */
	SMBJ,
	
	/**
	 * sftp client type
	 */
	SFTP,

	/**
	 * iRODS client type
	 */
	IRODS;
	
	/**
	 * Get enum object for linked string value
	 */
	public static NfsClientType fromString(String clientTypeString) {
		if ("samba".equalsIgnoreCase(clientTypeString)) {
			return SAMBA;
		}
		if ("smbj".equalsIgnoreCase(clientTypeString)) {
			return SMBJ;
		}
		if ("sftp".equalsIgnoreCase(clientTypeString)) {
			return SFTP;
		}
		throw new IllegalArgumentException("unrecognised client type: " + clientTypeString);
	}

}
