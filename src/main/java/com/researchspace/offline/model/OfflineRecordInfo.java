package com.researchspace.offline.model;

import com.researchspace.model.record.BaseRecord;

public class OfflineRecordInfo {

	private long id;
	
	private OfflineWorkType lockType;
	
	private long lastModificationTime;
	
	public OfflineRecordInfo(BaseRecord record, OfflineWorkType lockType) {
		id = record.getId();
		lastModificationTime = record.getModificationDate();
		this.lockType = lockType;
	}
	
	public OfflineRecordInfo(OfflineRecordUser offlineRecordUser) {
		id = offlineRecordUser.getRecord().getId();
		lastModificationTime = offlineRecordUser.getRecord().getModificationDate();
		lockType = offlineRecordUser.getWorkType();
	}
	
	public long getId() {
		return id;
	}

	public OfflineWorkType getLockType() {
		return lockType;
	}

	public long getLastModificationTime() {
		return lastModificationTime;
	}

}
