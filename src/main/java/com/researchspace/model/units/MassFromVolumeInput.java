package com.researchspace.model.units;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MassFromVolumeInput implements VolumeInput, RfmInput {
	@NotNull
	@Min(value = 0, message = "{valid.rfm.msg}")
	private Double rfm;

	@NotNull
	@Min(value = 0)
	@Max(value = 1000_000_000)
	private Double concValue;

	@NotNull
	private String concUnit;

	@NotNull
	@Min(value = 0)
	@Max(value = 1000_000_000)
	private Double volValue;

	@NotNull
	private String volUnit;

}
