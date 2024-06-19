package com.researchspace.model.netfiles;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.LinkedHashMap;

/** 
 * DTO object for NfsFileStore information that user may want to see in the UI. 
 */

@Data
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
public class NfsFileSystemInfo implements Serializable {

	private static final long serialVersionUID = -657284186777466823L;

	private Long id;
	private String name;
	private String url;
	
	private String clientType;
	private String authType;

	private LinkedHashMap<String, String> options = new LinkedHashMap<>(); 
	
	// (optional) if user is logged into the File System the field may store the username
	private String loggedAs;

	public NfsFileSystemInfo(NfsFileSystem nfsFileSystem) {
		id = nfsFileSystem.getId();
		name = nfsFileSystem.getName();
		url = nfsFileSystem.getUrl();
		clientType = nfsFileSystem.getClientType().toString();
		authType = nfsFileSystem.getAuthType().toString();

		if ("SMBJ".equals(clientType)) {
			options.put(NfsFileSystemOption.SAMBA_SHARE_NAME.toString(), 
					nfsFileSystem.getClientOption(NfsFileSystemOption.SAMBA_SHARE_NAME));
		} else if (nfsFileSystem.fileSystemRequiresUserRootDirs()) {
			options.put(NfsFileSystemOption.USER_DIRS_REQUIRED.toString(),
					nfsFileSystem.getClientOption(NfsFileSystemOption.USER_DIRS_REQUIRED));
		}
	}

}
