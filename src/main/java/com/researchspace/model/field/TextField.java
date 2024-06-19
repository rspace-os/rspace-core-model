package com.researchspace.model.field;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Transient;

import org.hibernate.envers.Audited;

@Entity
@DiscriminatorValue("text")
@Audited
public class TextField extends Field {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1757280226055607179L;

	private String rtfData;

	private TextFieldForm fieldForm;

	public TextField() {
	}

	public TextField(TextFieldForm template) {
		setFieldForm(template);
	}

	@Transient
	@Override
	public String getData() {
		return getRtfData();
	}

	@Override
	public void setData(String fieldData) {
		setRtfData(fieldData);
	}

	@Lob
	String getRtfData() {
		return rtfData;
	}

	void setRtfData(String rtfData) {
		this.rtfData = rtfData;
	}

	@Transient
	public String getDefaultValue() {
		return _getFieldForm().getDefaultValue();
	}

	@Override
	public TextField shallowCopy() {
		TextField copy = new TextField(fieldForm);
		copyFields(copy);
		copy.setFieldData(getFieldData());
		copy.setRtfData(getRtfData());
		return copy;
	}

	@Transient
	public TextFieldForm _getFieldForm() {
		return fieldForm;
	}

	void _setFieldForm(TextFieldForm fieldForm) {
		this.fieldForm = fieldForm;
	}

	@Override
	protected void _setFieldForm(IFieldForm ft) {		
		_setFieldForm(TextFieldBehaviour.realOrProxy(ft));
	}

}
