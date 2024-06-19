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
public class HalfLifeInput {
  
  @NotNull
  @Min(value=0, message="Please supply a halflife > 0")
  private Double halfLife;
  
  @NotNull
  @Pattern(regexp="(min)|h|(day)|(year)|", message="{valid.time.message}")
  private String halfLifeTimeUnit;
  
  @NotNull
  @Min(value=0, message="Please supply a decay time > 0")
  private Double decayTime;
  
  @NotNull
  @Pattern(regexp="(min)|h|(day)|(year)|", message="{valid.time.message}")
  private String decayTimeUnit;
  
  
}
