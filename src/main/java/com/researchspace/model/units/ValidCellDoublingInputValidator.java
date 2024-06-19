package com.researchspace.model.units;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ValidCellDoublingInputValidator
		implements ConstraintValidator<ValidCellDoublingInput, CellDoublingTimeInput> {

	@Override
	public void initialize(ValidCellDoublingInput constraintAnnotation) {
	}

	@Override
	public boolean isValid(CellDoublingTimeInput cellDoubleTime, ConstraintValidatorContext context) {
		if (cellDoubleTime == null) {
			return true;
		}
		return cellDoubleTime.getFinalConc() > cellDoubleTime.getInitConc();

	}

}
