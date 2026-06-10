package com.researchspace.model.inventory.field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.researchspace.model.core.GlobalIdPrefix;
import com.researchspace.model.field.FieldType;
import org.junit.Test;

public class InventoryLinkFieldTest {

  @Test
  public void fromFieldTypeStringCreatesLinkField() {
    InventoryEntityField field = InventoryEntityField.fromFieldTypeString("link");
    assertTrue(field instanceof InventoryLinkField);
    assertEquals(FieldType.LINK, field.getType());
  }

  @Test
  public void shallowCopyCopiesAllowedRelationTypesAndDeepCopiesLink() {
    InventoryLinkField field = new InventoryLinkField();
    field.setName("Related items");
    field.setAllowedRelationTypes("References|IsDerivedFrom");
    InventoryLink link = new InventoryLink();
    link.setTargetGlobalId("SA10");
    link.setTargetPrefix(GlobalIdPrefix.SA);
    link.setTargetDbId(10L);
    link.setRelationType("References");
    link.setVersionPin(3L);
    link.setTargetRevisionId(99L);
    link.setDeleted(true);
    field.setLink(link);

    InventoryLinkField copy = field.shallowCopy();

    assertEquals("References|IsDerivedFrom", copy.getAllowedRelationTypes());
    // deep copy: a new InventoryLink row, never the same instance
    assertNotSame(link, copy.getLink());
    assertNull(copy.getLink().getId());
    assertEquals("SA10", copy.getLink().getTargetGlobalId());
    assertEquals(GlobalIdPrefix.SA, copy.getLink().getTargetPrefix());
    assertEquals(Long.valueOf(10), copy.getLink().getTargetDbId());
    assertEquals("References", copy.getLink().getRelationType());
    assertEquals(Long.valueOf(3), copy.getLink().getVersionPin());
    assertEquals(Long.valueOf(99), copy.getLink().getTargetRevisionId());
    // deletion state must survive the clone (same path as InventoryLink.shallowCopy())
    assertTrue(copy.getLink().isDeleted());
  }

  @Test
  public void shallowCopyLeavesLinkNullWhenSourceHasNoLink() {
    InventoryLinkField field = new InventoryLinkField();
    field.setName("Related items");
    field.setAllowedRelationTypes("References");

    InventoryLinkField copy = field.shallowCopy();

    assertNull(copy.getLink());
    assertEquals("References", copy.getAllowedRelationTypes());
  }
}
