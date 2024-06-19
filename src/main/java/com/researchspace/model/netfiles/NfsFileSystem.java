package com.researchspace.model.netfiles;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Entity class storing external net file system details 
 */
@Entity
@org.hibernate.annotations.Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
public class NfsFileSystem implements Serializable {
	
	private static final long serialVersionUID = 1405744614537665778L;

	private Long id; 
	private String name; // user-friendly name of the file system
	private String url; // filesystem url (may start with smb:// or sftp://)
	
	private NfsClientType clientType;
	private NfsAuthenticationType authType;

	// options are stored as string in the database 
	private LinkedHashMap<String, String> clientOptions = new LinkedHashMap<>(); 
	private LinkedHashMap<String, String> authOptions = new LinkedHashMap<>();

	private boolean disabled; // is the system available for users
	
	public NfsFileSystem() { }

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Enumerated(EnumType.STRING)
	public NfsClientType getClientType() {
		return clientType;
	}

	@Transient
	public boolean fileSystemRequiresUserRootDirs(){
		return "true".equals(getClientOption(NfsFileSystemOption.USER_DIRS_REQUIRED));
	}

	public void setClientType(NfsClientType clientType) {
		this.clientType = clientType;
	}

	@Lob
	public String getClientOptions() {
		return convertOptionsMapToString(clientOptions);
	}
	
	public void setClientOptions(String clientOptionsString) {
		putOptionsFromStringToMap(clientOptions, clientOptionsString);
	}

	@Enumerated(EnumType.STRING)
	public NfsAuthenticationType getAuthType() {
		return authType;
	}

	public void setAuthType(NfsAuthenticationType authType) {
		this.authType = authType;
	}

	@Lob
	public String getAuthOptions() {
		return convertOptionsMapToString(authOptions);
	}

	public void setAuthOptions(String authOptionsString) {
		putOptionsFromStringToMap(authOptions, authOptionsString);
	}
	
	public boolean isDisabled() {
		return disabled;
	}

	public void setDisabled(boolean isDisabled) {
		this.disabled = isDisabled;
	}

	@Transient
	public boolean isEnabled() {
		return !disabled;
	}
	
	// for managing client options
	@Transient
	public String getClientOption(NfsFileSystemOption option) {
		return clientOptions.get(option.toString());
	}

	public void setClientOption(NfsFileSystemOption option, String value) {
		clientOptions.put(option.toString(), value);
	}
	
	// for managing authorisation options
	@Transient
	public String getAuthOption(NfsFileSystemOption option) {
		return authOptions.get(option.toString());
	}

	public void setAuthOption(NfsFileSystemOption option, String value) {
		authOptions.put(option.toString(), value);
	}
	
	private String convertOptionsMapToString(Map<String, String> map) {
		StringBuilder sb = new StringBuilder();
		if (map != null) {
			for (Entry<String, String> entry : map.entrySet()) {
				sb.append(entry.getKey() + "=" + entry.getValue() + "\n");
			}
		}
		return sb.toString();
	}

	private void putOptionsFromStringToMap(LinkedHashMap<String, String> map, String optionsString) {
		map.clear();
		if (optionsString != null) {
			String[] rows = optionsString.split("\n");
			for (String row : rows) {
				if (row.contains("=")) {
					String[] keyValue = row.split("=", 2);
					map.put(keyValue[0], keyValue[1]);
				}
			}
		}
	}

	public NfsFileSystemInfo toFileSystemInfo() {
		return new NfsFileSystemInfo(this);
	}
	
	@Override
	public int hashCode() {
		return id != null ? id.intValue() : 0;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof NfsFileSystem)) {
			return false;
		}
		NfsFileSystem other = (NfsFileSystem) obj;
		if (id != null && id.equals(other.id)) {
			return true;
		}
		return false;
	}

	// for debug
	public String toString() {
		StringBuffer sbf = new StringBuffer();
		sbf.append("filesystem id: ");
		sbf.append(id);
		sbf.append(", name: ");
		sbf.append(name);
		sbf.append(", url: ");
		sbf.append(url);
		sbf.append(", disabled: ");
		sbf.append(disabled);
		return sbf.toString();
	}

}
