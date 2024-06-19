package com.researchspace.model.units;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@ValidCellDoublingInput
@AllArgsConstructor
@NoArgsConstructor
public class CellDoublingTimeInput {
	@NotNull
	@Min(0)
	private Double initConc;

	@NotNull
	@Min(0)
	private Double finalConc;
	
	@NotNull
	@Min(0)
	private Double duration;
	

}
