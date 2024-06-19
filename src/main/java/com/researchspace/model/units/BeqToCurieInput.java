package com.researchspace.model.units;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import lombok.Data;
@Data
public class BeqToCurieInput {
	
	@NotNull
	@Min(0)
	private Double value;
	
	@Pattern(regexp="(MBq)|(kBq)|(Bq)|(mBq)", message="Possible units are MBq, kBq, Bq, or mBq")
	private String unit;

}
