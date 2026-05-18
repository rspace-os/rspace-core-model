package com.researchspace.model.inventory;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

  private static Set<String> union(Set<String> base, String... extras) {
    Set<String> result = new HashSet<>(base);
    for (String extra : extras) {
      result.add(extra);
    }
    return result;
  }
}
