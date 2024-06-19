package com.researchspace.model.inventory.field;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.validator.constraints.NotBlank;

import com.researchspace.model.field.ErrorList;

import lombok.Getter;
import lombok.Setter;

/**
 * Defines possible options and behaviour for ChoiceFields in Inventory objects
 */
@Entity
@Setter
public class InventoryChoiceFieldDef extends InventoryFieldDef implements Serializable {
	
	private static final long serialVersionUID = 1974158800405393625L;

	private Long id;
	
	@Getter
	private boolean multipleChoice;
	
	private String choiceOptions;

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	public Long getId() {
		return id;
	}
	
	/**
	 * Get choice options string, as stored in db.
	 * 
	 * Package-scoped for better encapsulation, the code 
	 * should normally call {@link #getChoiceOptionsList()}.  
	 */
	@Column(columnDefinition = "text")
	@NotBlank
	String getChoiceOptions() {
		return choiceOptions;
	}
	
	/**
	 * Set choice options string, as stored in db.
	 * 
	 * Package-scoped for better encapsulation, the code 
	 * should normally call {@link #setChoiceOptionsList(List)}.
	 */
	void setChoiceOptions(String choiceOptions) {
		this.choiceOptions = choiceOptions;
	}

	/**
	 * Get choice options string, as stored in db.
	 */
	@Transient
	public String getChoiceOptionsDBString() {
		return choiceOptions;
	}
	
	@Transient
	public List<String> getChoiceOptionsList() {
		return getOptionListFromString(choiceOptions);
	}
	
	@Transient
	public void setChoiceOptionsList(List<String> newChoices) {
		choiceOptions = getStringFromOptionList(newChoices);
	}

	@Transient
	public String getSummary() {
		StringBuffer sb = new StringBuffer();
		sb.append("Choices: [" + Arrays.toString(getChoiceOptionsList().toArray()) + "], ")
				.append("Multiple choice: " + isMultipleChoice() + " ");
		return sb.toString();
	}

	public InventoryChoiceFieldDef shallowCopy() {
		InventoryChoiceFieldDef copy = new InventoryChoiceFieldDef();
		copy.setMultipleChoice(isMultipleChoice());
		copy.setChoiceOptions(choiceOptions);
		return copy;
	}

	public ErrorList validate(String data) {
		ErrorList el = new ErrorList();
		if (StringUtils.isEmpty(data)) {
			return el;
		}
		List<String> values = getOptionListFromString(data);
		if(!CollectionUtils.isSubCollection(values, getChoiceOptionsList())) {
			el.addErrorMsg("Some supplied values are not allowed options");
		}
		return el;
	}

}
