package com.researchspace.model.inventory;

import com.researchspace.model.User;
import com.researchspace.model.core.GlobalIdPrefix;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Transient;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;

/**
 * Represents RSpace Inventory Instrument Template
 */
@Entity
@DiscriminatorValue("InstrumentTemplate")
@Audited
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@Indexed
public class InstrumentTemplate extends InstrumentEntity {

  private static final String TEMPLATE_MOVE_NOT_ALLOWED =
      "InstrumentTemplate cannot be moved or attached to containers";

  public InstrumentTemplate() {
    super();
  }

  protected InstrumentTemplate(Instrument originInstrument, User currentuser) {
    this();
    shallowCopyBasicFields(originInstrument, this);
    copy(originInstrument, this, this::defaultNameCopy, currentuser);
  }


  @Override
  public InstrumentEntity copyToTemplate(User currentUser) {
    throw new IllegalArgumentException(
        "Only an Instrument can be copied into an InstrumentTemplate");
  }

  @Override
  public InstrumentEntity copyFromTemplate(User currentUser) {
    Instrument copy = new Instrument(this, currentUser);
    copy.setInstrumentTemplate(this);
    copy.setTemplateLinkedVersion(this.getVersion());
    return copy;
  }

  @Transient
  @Override
  public boolean isTemplate() {
    return true;
  }

  @Transient
  @Override
  public GlobalIdPrefix getGlobalIdPrefix() {
    return GlobalIdPrefix.NT;
  }


  @Transient
  @Override
  public InventoryRecordType getType() {
    return InventoryRecordType.INSTRUMENT_TEMPLATE;
  }


  protected InstrumentEntity shallowCopy() {
    InstrumentEntity copy = new InstrumentTemplate();
    shallowCopyBasicFields(copy);
    return copy;
  }

  @Override
  public InstrumentEntity copy(User currentUser) {
    return super.copy(this::defaultNameCopy, currentUser);
  }

  @Override
  public void moveToNewParent(Container targetParent) {
    throw new IllegalArgumentException(TEMPLATE_MOVE_NOT_ALLOWED);
  }

  @Override
  public void moveToNewParentWithCoords(Container targetParent, Integer coordX, Integer coordY) {
    throw new IllegalArgumentException(TEMPLATE_MOVE_NOT_ALLOWED);
  }

  @Override
  public void moveToNewParentAndLocation(Container targetParent, ContainerLocation targetLocation) {
    throw new IllegalArgumentException(TEMPLATE_MOVE_NOT_ALLOWED);
  }

  @Override
  public void removeFromCurrentParent() {
    throw new IllegalArgumentException(TEMPLATE_MOVE_NOT_ALLOWED);
  }

  @Override
  public void setLastNonWorkbenchParent(Container lastNonWorkbenchParent) {
    if (this.getLastNonWorkbenchParent() != null || lastNonWorkbenchParent != null) {
      throw new IllegalArgumentException(TEMPLATE_MOVE_NOT_ALLOWED);
    }
    super.setLastNonWorkbenchParent(null);
  }


}