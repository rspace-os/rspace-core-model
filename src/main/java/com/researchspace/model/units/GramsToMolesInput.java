package com.researchspace.model.units;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GramsToMolesInput implements MassInput, RfmInput {
	
	@NotNull
	@Min(value=0, message="{valid.min.rfm.msg}")
	private Double rfm;
	
	@NotNull
	@Min(value = 0, message="{valid.min.mass.msg}")
	private Double massValue;
	
	@NotNull
	@Pattern(regexp = "(k|m|\u00B5|m|n|p|a|f)?g")
	private String massUnit;
	

}
