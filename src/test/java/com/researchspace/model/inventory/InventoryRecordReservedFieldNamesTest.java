package com.researchspace.model.inventory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.researchspace.model.inventory.field.ExtraTextField;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class InventoryRecordReservedFieldNamesTest {

  private static final Set<String> BASE_RESERVED =
      Set.of(
          "name", "description", "tags",
          "Name", "Description", "Preview Image", "Tags", "Attachments");

  @Test
  void containerReservedFieldNames() {
    Container container = new Container(Container.ContainerType.LIST);
    assertEquals(
        union(BASE_RESERVED, "Can Store", "Type", "Locations Image", "Grid Dimensions"),
        container.getReservedFieldNames());
  }

  @Test
  void subSampleReservedFieldNames() {
    SubSample subSample = new SubSample();
    assertEquals(
        union(BASE_RESERVED, "Quantity", "Sample", "Notes"),
        subSample.getReservedFieldNames());
  }

  @Test
  void sampleReservedFieldNames() {
    Sample sample = new Sample();
    assertEquals(
        union(
            BASE_RESERVED,
            "source",
            "expiry date",
            "Sample Template",
            "Expiry Date",
            "Source",
            "Storage Temperature",
            "Total Quantity",
            "Subsamples"),
        sample.getReservedFieldNames());
  }

  @Test
  void sampleTemplateReservedFieldNames() {
    Sample template = new Sample();
    template.setTemplate(true);
    assertEquals(
        union(
            BASE_RESERVED,
            "source",
            "expiry date",
            "Subsample Alias",
            "Quantity Units",
            "Fields",
            "Samples"),
        template.getReservedFieldNames());
  }

  @Test
  void instrumentReservedFieldNames() {
    Instrument instrument = new Instrument();
    assertEquals(BASE_RESERVED, instrument.getReservedFieldNames());
  }

  @Test
  void instrumentTemplateReservedFieldNames() {
    InstrumentTemplate instrumentTemplate = new InstrumentTemplate();
    assertEquals(BASE_RESERVED, instrumentTemplate.getReservedFieldNames());
  }

  @Test
  void containerAddExtraFieldRejectsDisplayedLabel() {
    Container container = new Container(Container.ContainerType.LIST);
    ExtraTextField field = new ExtraTextField();
    field.setName("Type");

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
    field.setName("Notes");

    assertThrows(IllegalArgumentException.class, () -> subSample.addExtraField(field));
  }

  @Test
  void sampleAddExtraFieldRejectsBaseDisplayedLabel() {
    Sample sample = new Sample();
    ExtraTextField field = new ExtraTextField();
    field.setName("Preview Image");

    assertThrows(IllegalArgumentException.class, () -> sample.addExtraField(field));
  }

  @Test
  void verifyFieldNameAllowedKeepsExistingReservedNameRejection() {
    Container container = new Container(Container.ContainerType.LIST);
    ExtraTextField field = new ExtraTextField();
    field.setName("description");

    assertThrows(IllegalArgumentException.class, () -> container.addExtraField(field));
  }

  @Test
  void verifyFieldNameAllowedIsCaseSensitiveForDisplayedLabels() {
    // Title-Case-only displayed labels (e.g. "Type") are NOT lowercased into the set, so a
    // lower-case form of an extra-only displayed label is still allowed.
    Container container = new Container(Container.ContainerType.LIST);
    ExtraTextField field = new ExtraTextField();
    field.setName("type");

    container.addExtraField(field);
  }

  private static Set<String> union(Set<String> base, String... extras) {
    Set<String> result = new HashSet<>(base);
    for (String extra : extras) {
      result.add(extra);
    }
    return result;
  }
}
