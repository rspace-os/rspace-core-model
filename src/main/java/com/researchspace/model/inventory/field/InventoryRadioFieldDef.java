package com.researchspace.model.inventory.field;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.NotBlank;

import com.researchspace.model.field.ErrorList;

import lombok.Setter;
import lombok.ToString;

/**
 * Defines possible options and behaviour of RadioFields in Inventory objects
 */
@Entity
@Setter
@ToString
public class InventoryRadioFieldDef extends InventoryFieldDef implements Serializable {
	
	private static final long serialVersionUID = -8400847914215893324L;

	private Long id;
	
	private String radioOptions;

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	public Long getId() {
		return id;
	}
	
	/**
	 * Get radio options string, as stored in db.
	 * 
	 * Package-scoped for better encapsulation, the code 
	 * should normally call {@link #getRadioOptionsList()}.
	 */
	@Column(columnDefinition = "text")
	@NotBlank
	String getRadioOptions() {
		return radioOptions;
	}
	
	/**
	 * Set radio options string, as stored in db.
	 * 
	 * Package-scoped for better encapsulation, the code 
	 * should normally call {@link #setRadioOptionsList(List)}.
	 */
	void setRadioOptions(String radioOptions) {
		this.radioOptions = radioOptions;
	}
	
	/**
	 * Get radio options string, as stored in db.
	 */
	@Transient
	public String getRadioOptionsDBString() {
		return radioOptions;
	}

	@Transient
	public List<String> getRadioOptionsList() {
		return getOptionListFromString(radioOptions);
	}

	@Transient
	public void setRadioOptionsList(List<String> newOptions) {
		radioOptions = getStringFromOptionList(newOptions);
	}

	public InventoryRadioFieldDef shallowCopy() {
		InventoryRadioFieldDef copy = new InventoryRadioFieldDef();
		copy.setRadioOptions(radioOptions);
		return copy;
	}
	
	public ErrorList validate(String data) {
		ErrorList el = new ErrorList();
		
		if(!StringUtils.isEmpty(data) && !getRadioOptionsList().contains(data)) {
			el.addErrorMsg("Some supplied values are not allowed options");
		}
		return el;
	}

}
