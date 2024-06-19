package com.researchspace.offline.model;

import java.util.Base64;

import com.researchspace.model.EcatImage;
import com.researchspace.model.EcatImageAnnotation;
import com.researchspace.model.RSChemElement;

/**
 * Transport class storing image details for various types of image 
 * (raw, annotated, sketches, chemicals) that are transferred 
 * between RSpace server and offline app.
 */
public class OfflineImage {

	private Long id;

	private Long clientId;

	private String type;
	
	private String annotation;
	
	private byte[] data;
	
	public OfflineImage() { }
	
	public OfflineImage(EcatImage image, byte[] data) {
		id = image.getId();
		this.data =  data;
		type = "image";
	}

	public OfflineImage(EcatImageAnnotation annotation) {
		id = annotation.getId();
		this.annotation = annotation.getAnnotations();
		data =  annotation.getData();
		type = annotation.isSketch() ? "sketch" : "annotation";
	}

	public OfflineImage(RSChemElement chemElem) {
		id = chemElem.getId();
		data =  chemElem.getDataImage();
		type = "chem";
	}

	/** image id assigned on a server. for downloaded images it should never null. */
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	/** image id assigned on offline device. for uploaded images it should never be  null. */
	public Long getClientId() {
		return clientId;
	}

	public void setClientId(Long clientId) {
		this.clientId = clientId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getAnnotation() {
		return annotation;
	}

	public void setAnnotation(String annotation) {
		this.annotation = annotation;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}
	
	public String getBase64ImageData() {
		return "data:image/png;base64," + Base64.getEncoder().encodeToString(data);
	}

}
