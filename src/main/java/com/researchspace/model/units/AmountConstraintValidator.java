package com.researchspace.model.units;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Amount constraint validator for Quantifiable objects annotated with @ValidAmount
 */
public class AmountConstraintValidator implements ConstraintValidator<ValidAmount, Quantifiable> {

	@Override
	public void initialize(ValidAmount constraintAnnotation) {
		
	}

	@Override
	public boolean isValid(Quantifiable value, ConstraintValidatorContext context) {
		return AmountValidator.validate(value);
	} 

}
