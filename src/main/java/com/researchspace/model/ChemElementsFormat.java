package com.researchspace.model;
/**
 * The format that the 'chemElements' column is stored in.
 */
public enum ChemElementsFormat {
	
	
	
	/**
	 * Mol format
	 */
	MOL("mol"),
	
	/**
	 * Marvin XML format
	 */
	MRV("mrv"),
	
	/**
	 * Ketcher format
	 */
	KET("ket");
	
	public String getLabel() {
		return label;
	}

	private String label;

	private ChemElementsFormat(String label) {
		this.label = label;
	}
	

}
