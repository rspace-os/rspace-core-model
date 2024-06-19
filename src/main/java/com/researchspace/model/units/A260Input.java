package com.researchspace.model.units;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@ValidA260Input
@AllArgsConstructor
@NoArgsConstructor
public class A260Input {
	@NotNull
	@DecimalMin(value="0", inclusive=false)
	@Max(100)
	private Double a260;
	
	@Min(0)
	@Max(100)
	private Double a280 ;
	
	@Min(0)
	@Max(100)
	private Double a320;
	
	@Pattern(regexp="(50)|(40)|(35)|(20)")
	private String natype;


}
