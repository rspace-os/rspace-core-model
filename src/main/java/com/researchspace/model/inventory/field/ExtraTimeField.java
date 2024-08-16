package com.researchspace.model.inventory.field;

import com.researchspace.model.field.FieldType;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;
import lombok.EqualsAndHashCode;
import org.hibernate.envers.Audited;

@Entity
@Audited
@EqualsAndHashCode(callSuper = true)
@DiscriminatorValue("time")
public class ExtraTimeField extends ExtraField {

  @Transient
  @Override
  public FieldType getType() {
    return FieldType.TIME;
  }

  @Override
  public String validateNewData(String time) {
    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm").withResolverStyle(
        ResolverStyle.STRICT);
    try{
      LocalTime.parse(time, timeFormatter);
    } catch (DateTimeParseException e) {
      return String.format("%s is an invalid 24hour time format. Valid format is 00:00.", time);
    }
    return "";
  }

  @Override
  public ExtraTimeField shallowCopy() {
    ExtraTimeField copy = new ExtraTimeField();
    copyProperties(copy);
    return copy;
  }
}
