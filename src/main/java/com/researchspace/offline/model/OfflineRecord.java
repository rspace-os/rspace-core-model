package com.researchspace.offline.model;

import java.util.ArrayList;
import java.util.List;

import com.researchspace.model.field.Field;
import com.researchspace.model.record.BaseRecord;
import com.researchspace.model.record.RSPath;
import com.researchspace.model.record.StructuredDocument;

import lombok.Getter;
import lombok.Setter;

public class OfflineRecord {

	@Getter
	@Setter
	private Long id;

	@Getter
	@Setter
	private Long clientId;

	@Getter
	@Setter
	private String name;
	
	@Getter
	private String path;
	
	private long lastSynchronisedModificationTime;

	@Getter
	@Setter
	private OfflineWorkType lockType;
	
	@Getter
	@Setter
	private long fieldId;
	
	@Getter
	@Setter
	private String content;

	@Getter
	private List<OfflineImage> images = new ArrayList<>();

	public OfflineRecord() { }
	
	public OfflineRecord(BaseRecord record) {
		id = record.getId();
		name = record.getName();
		lastSynchronisedModificationTime = record.getModificationDate();

		// assuming basic document
		if (record.isStructuredDocument()) {
			Field field = ((StructuredDocument) record).getFields().get(0);
			content = field.getFieldData();
			fieldId = field.getId();
		}
	}
	

	public void updatePath(RSPath recordPath) {
		String fullPath = recordPath.getPathAsString("/");
		String userName = recordPath.getFirstElement().get().getName();
		this.path = fullPath.substring(userName.length(), fullPath.lastIndexOf(name));
	}

	public long getLastSynchronisedModificationTime() {
		return lastSynchronisedModificationTime;
	}

	public void setLastSynchronisedModificationTime(long lastSynchronisedModificationTime) {
		this.lastSynchronisedModificationTime = lastSynchronisedModificationTime;
	}

	public void addImage(OfflineImage image) {
		images.add(image);
	}

}
