package com.researchspace.model.inventory.field;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;


public class ExtraTimeFieldTest {
  ExtraTimeField timeField;

  @BeforeEach
  public void setUp() {
    timeField = new ExtraTimeField();
  }

  @Test
  public void validTimeIsValid(){
    String result = timeField.validateNewData("00:00");
    assertEquals("", result);
  }

  @Test
  public void twentyFourHourTimeIsValid(){
    String result = timeField.validateNewData("23:11");
    assertEquals("", result);
  }

  @Test
  public void threeDigitTimeInvalid(){
    String result = timeField.validateNewData("9:30");
    String expected = "9:30 is an invalid 24hour time format. Valid format is 00:00.";
    assertEquals(expected, result);
  }

  @Test
  public void twelveHourTimeInvalid(){
    String result = timeField.validateNewData("09:30am");
    String expected = "09:30am is an invalid 24hour time format. Valid format is 00:00.";
    assertEquals(expected, result);
  }

  @Test
  public void randomStringInvalid(){
    String result = timeField.validateNewData("abc123");
    String expected = "abc123 is an invalid 24hour time format. Valid format is 00:00.";
    assertEquals(expected, result);
  }

}