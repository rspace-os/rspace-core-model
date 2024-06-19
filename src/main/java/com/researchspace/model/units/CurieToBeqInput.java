package com.researchspace.model.units;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import lombok.Data;
@Data
public class CurieToBeqInput {
	
	@NotNull
	@Min(0)
	private Double value;
	
	@Pattern(regexp="(Ci)|(mCi)", message="Possible units are Ci or mCi")
	private String unit;

}
