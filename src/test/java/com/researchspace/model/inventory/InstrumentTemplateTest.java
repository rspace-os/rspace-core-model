package com.researchspace.model.inventory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.researchspace.model.User;
import com.researchspace.model.core.GlobalIdPrefix;
import com.researchspace.model.inventory.field.InventoryEntityField;
import com.researchspace.model.inventory.field.InventoryTextField;
import com.researchspace.model.record.TestFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class InstrumentTemplateTest {

  private static final String TEMPLATE_MOVE_NOT_ALLOWED =
      "InstrumentTemplate cannot be moved or attached to containers";

  private InstrumentTemplate template;
  private User anyUser;

  @BeforeEach
  public void setUp() {
    anyUser = TestFactory.createAnyUser("any");
    Instrument instrument = TestFactory.createBasicInstrumentOutsideContainer(anyUser);
    template = (InstrumentTemplate) instrument.copyToTemplate(anyUser);
    template.setId(6L);
  }

  @Test
  public void testInitialProperties() {
    assertNotNull(template.getModificationDate());
    assertNotNull(template.getCreationDate());
    assertFalse(template.isDeleted());
    assertTrue(template.isTemplate());
    assertEquals(InventoryRecord.InventoryRecordType.INSTRUMENT_TEMPLATE, template.getType());
    assertEquals("NT" + template.getId(), template.getOid().toString());
    assertEquals("NT" + template.getId() + "v1", template.getOidWithVersion().toString());
  }

  @Test
  @DisplayName("Use OID to distinguish instrument templates from instruments")
  public void globalId() {
    assertTrue(template.getGlobalIdentifier().startsWith("NT"));
    assertEquals(GlobalIdPrefix.NT, template.getOid().getPrefix());

    Instrument instrumentFromTemplate = (Instrument) template.copyFromTemplate(anyUser);
    instrumentFromTemplate.setId(7L);

    assertTrue(instrumentFromTemplate.getGlobalIdentifier().startsWith("IN"));
    assertEquals(GlobalIdPrefix.IN, instrumentFromTemplate.getOid().getPrefix());
  }

  @Test
  public void copy() {
    addField(template, buildTextField(1L, 1, "manufacturer", "Acme"));

    InstrumentTemplate copied = (InstrumentTemplate) template.copy(anyUser);
    assertTrue(copied.isTemplate());
    assertNull(copied.getGlobalIdentifier());
    assertEquals(template.getName() + "_COPY", copied.getName());
    assertEquals(1, copied.getActiveFields().size());
    assertEquals("manufacturer", copied.getActiveFields().get(0).getName());
    assertEquals("Acme", copied.getActiveFields().get(0).getFieldData());
    assertNull(copied.getParentLocation());
    assertNull(copied.getParentId());
  }

  @Test
  @DisplayName("InstrumentTemplate cannot be copied to template")
  public void copyToTemplateNotAllowed() {
    IllegalArgumentException iae =
        assertThrows(IllegalArgumentException.class, () -> template.copyToTemplate(anyUser));
    assertEquals("Only an Instrument can be copied into an InstrumentTemplate", iae.getMessage());
  }

  @Test
  @DisplayName("Make instrument from template")
  public void copyFromTemplate() {
    template.increaseVersion();

    Instrument copiedInstrument = (Instrument) template.copyFromTemplate(anyUser);
    assertFalse(copiedInstrument.isTemplate());
    assertEquals(template, copiedInstrument.getInstrumentTemplate());
    assertEquals(template.getVersion(), copiedInstrument.getTemplateLinkedVersion());
    assertEquals(1L, copiedInstrument.getVersion());
  }

  @Test
  public void movementOperationsNotAllowed() {
    Container container = Container.createListContainer(true, true, true);
    ContainerLocation location = new ContainerLocation(container);

    IllegalArgumentException iae =
        assertThrows(IllegalArgumentException.class, () -> template.moveToNewParent(container));
    assertEquals(TEMPLATE_MOVE_NOT_ALLOWED, iae.getMessage());

    iae =
        assertThrows(
            IllegalArgumentException.class,
            () -> template.moveToNewParentWithCoords(container, 1, 1));
    assertEquals(TEMPLATE_MOVE_NOT_ALLOWED, iae.getMessage());

    iae =
        assertThrows(
            IllegalArgumentException.class,
            () -> template.moveToNewParentAndLocation(container, location));
    assertEquals(TEMPLATE_MOVE_NOT_ALLOWED, iae.getMessage());

    iae = assertThrows(IllegalArgumentException.class, () -> template.removeFromCurrentParent());
    assertEquals(TEMPLATE_MOVE_NOT_ALLOWED, iae.getMessage());

    iae =
        assertThrows(
            IllegalArgumentException.class, () -> template.setLastNonWorkbenchParent(container));
    assertEquals(TEMPLATE_MOVE_NOT_ALLOWED, iae.getMessage());
  }

  private InventoryTextField buildTextField(Long id, Integer columnIndex, String name, String data) {
    InventoryTextField field = new InventoryTextField(name);
    field.setId(id);
    field.setColumnIndex(columnIndex);
    field.setFieldData(data);
    return field;
  }

  private void addField(InstrumentEntity instrumentEntity, InventoryEntityField field) {
    field.setInventoryRecord(instrumentEntity);
    instrumentEntity.getFields().add(field);
    instrumentEntity.refreshActiveFieldsAndColumnIndex();
  }
}

