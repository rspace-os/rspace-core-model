package com.researchspace.model.inventory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.researchspace.model.inventory.field.ExtraTextField;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class InventoryRecordDisplayedFieldNamesTest {

  private static final Set<String> BASE =
      Set.of("Name", "Description", "Preview Image", "Tags", "Attachments");

  @Test
  void containerDisplayedFieldNames() {
    Container container = new Container(Container.ContainerType.LIST);
    assertEquals(
        union(BASE, "Can Store", "Type", "Locations Image", "Grid Dimensions"),
        container.getDisplayedFieldNames());
  }

  @Test
  void subSampleDisplayedFieldNames() {
    SubSample subSample = new SubSample();
    assertEquals(
        union(BASE, "Quantity", "Sample", "Notes"),
        subSample.getDisplayedFieldNames());
  }

  @Test
  void sampleDisplayedFieldNames() {
    Sample sample = new Sample();
    assertEquals(
        union(
            BASE,
            "Sample Template",
            "Expiry Date",
            "Source",
            "Storage Temperature",
            "Total Quantity",
            "Subsamples"),
        sample.getDisplayedFieldNames());
  }

  @Test
  void sampleTemplateDisplayedFieldNames() {
    Sample template = new Sample();
    template.setTemplate(true);
    assertEquals(
        union(BASE, "Subsample Alias", "Quantity Units", "Fields", "Samples"),
        template.getDisplayedFieldNames());
  }

  @Test
  void instrumentDisplayedFieldNames() {
    Instrument instrument = new Instrument();
    assertEquals(BASE, instrument.getDisplayedFieldNames());
  }

  @Test
  void instrumentTemplateDisplayedFieldNames() {
    InstrumentTemplate instrumentTemplate = new InstrumentTemplate();
    assertEquals(BASE, instrumentTemplate.getDisplayedFieldNames());
  }

  // RSDEV-1066: verifyFieldNameAllowed must reject names that collide with a displayed label
  // (case-sensitive, in addition to the existing case-insensitive reserved-name check).

  @Test
  void containerAddExtraFieldRejectsDisplayedLabel() {
    Container container = new Container(Container.ContainerType.LIST);
    ExtraTextField field = new ExtraTextField();
    field.setName("Type"); // Container's displayed-label set includes "Type"

    IllegalArgumentException iae =
        assertThrows(IllegalArgumentException.class, () -> container.addExtraField(field));
    assertTrue(
        iae.getMessage().contains("'Type'"),
        "Expected message to mention 'Type', got: " + iae.getMessage());
  }

  @Test
  void subSampleAddExtraFieldRejectsDisplayedLabel() {
    SubSample subSample = new SubSample();
    ExtraTextField field = new ExtraTextField();
    field.setName("Notes"); // SubSample's displayed-label set includes "Notes"

    assertThrows(IllegalArgumentException.class, () -> subSample.addExtraField(field));
  }

  @Test
  void sampleAddExtraFieldRejectsBaseDisplayedLabel() {
    Sample sample = new Sample();
    ExtraTextField field = new ExtraTextField();
    field.setName("Preview Image"); // base displayed-label set includes "Preview Image"

    assertThrows(IllegalArgumentException.class, () -> sample.addExtraField(field));
  }

  @Test
  void verifyFieldNameAllowedKeepsExistingReservedNameRejection() {
    // The reserved-name (lowercase, case-insensitive) check still fires for legacy cases.
    Container container = new Container(Container.ContainerType.LIST);
    ExtraTextField field = new ExtraTextField();
    field.setName("description"); // lowercase form of legacy reserved name

    assertThrows(IllegalArgumentException.class, () -> container.addExtraField(field));
  }

  @Test
  void verifyFieldNameAllowedIsCaseSensitiveForDisplayedLabels() {
    // The displayed-labels check is case-sensitive (mirrors UI's `new Set` semantics). A
    // lower-cased form of an extra-only displayed label (one not also in the lowercase reserved
    // set) is allowed by this method.
    Container container = new Container(Container.ContainerType.LIST);
    ExtraTextField field = new ExtraTextField();
    field.setName("type"); // lowercase — not in displayed labels (which are Title Case)

    container.addExtraField(field); // should not throw
  }

  private static Set<String> union(Set<String> base, String... extras) {
    Set<String> result = new HashSet<>(base);
    for (String extra : extras) {
      result.add(extra);
    }
    return result;
  }
}
