package com.researchspace.model.units;

import static org.junit.Assert.assertEquals;

import java.util.Set;

import javax.measure.MetricPrefix;
import javax.measure.Unit;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Volume;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.junit.BeforeClass;
import org.junit.Test;

import tech.units.indriya.quantity.Quantities;
import tech.units.indriya.unit.BaseUnit;
import tech.units.indriya.unit.Units;


public class HalfLifeInputTest {
	private static Validator validator;

	@BeforeClass
	public static void setUpValidator() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
	}

	@Test
	public void test() {
		Set<ConstraintViolation<HalfLifeInput>> constraintViolations = validator.validate(new HalfLifeInput(3.1, "day",2.0,"h"));
		assertEquals(0, constraintViolations.size());
		constraintViolations = validator.validate(new HalfLifeInput(3d, "min", 5.0, "year"));
		assertEquals(0, constraintViolations.size());
	}
	
	@Test
	public void test2() {
	BaseUnit<Dimensionless> PEOPLE = new BaseUnit<>("people");
	javax.measure.Quantity<Dimensionless> pop = Quantities.getQuantity(100, PEOPLE);
	Unit<Volume> sq_km = (Unit<Volume>) MetricPrefix.KILO(Units.METRE).multiply(MetricPrefix.KILO(Units.METRE))
			    .multiply(MetricPrefix.KILO(Units.METRE));
	javax.measure.Quantity<Volume> a = Quantities.getQuantity(200, sq_km);
	System.out.println(a);
	System.out.println(pop);
	System.out.println(pop.divide(a));

	}

}
