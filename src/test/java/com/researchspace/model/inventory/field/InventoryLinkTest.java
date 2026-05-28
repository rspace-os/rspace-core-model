package com.researchspace.model.inventory.field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;

import org.junit.jupiter.api.Test;

import com.researchspace.model.core.GlobalIdPrefix;

class InventoryLinkTest {

	@Test
	void settersAndGettersRoundTrip() {
		InventoryLink link = new InventoryLink();
		link.setId(7L);
		link.setTargetGlobalId("SA123v3");
		link.setTargetPrefix(GlobalIdPrefix.SA);
		link.setTargetDbId(123L);
		link.setVersionPin(3L);
		link.setRelationType("IsCalibratedBy");
		link.setDeleted(true);

		assertEquals(7L, link.getId());
		assertEquals("SA123v3", link.getTargetGlobalId());
		assertEquals(GlobalIdPrefix.SA, link.getTargetPrefix());
		assertEquals(123L, link.getTargetDbId());
		assertEquals(3L, link.getVersionPin());
		assertEquals("IsCalibratedBy", link.getRelationType());
		assertTrue(link.isDeleted());
	}

	@Test
	void versionPinMayBeNullForLatest() {
		InventoryLink link = new InventoryLink();
		link.setTargetGlobalId("SA123");
		assertNull(link.getVersionPin());
	}

	@Test
	void prePersistSetsCreatedAtAndModifiedAt() {
		InventoryLink link = new InventoryLink();
		assertNull(link.getCreatedAt());
		assertNull(link.getModifiedAt());

		link.prePersist();

		assertNotNull(link.getCreatedAt());
		assertNotNull(link.getModifiedAt());
		assertEquals(link.getCreatedAt(), link.getModifiedAt());
	}

	@Test
	void prePersistPreservesExplicitlySetCreatedAt() {
		InventoryLink link = new InventoryLink();
		Date originalCreatedAt = new Date(1_700_000_000_000L);
		link.setCreatedAt(originalCreatedAt);

		link.prePersist();

		assertEquals(originalCreatedAt, link.getCreatedAt());
		assertNotNull(link.getModifiedAt());
	}

	@Test
	void preUpdateAdvancesModifiedAtWithoutTouchingCreatedAt() throws InterruptedException {
		InventoryLink link = new InventoryLink();
		link.prePersist();
		Date createdAt = link.getCreatedAt();
		Date firstModifiedAt = link.getModifiedAt();

		Thread.sleep(5);
		link.preUpdate();

		assertEquals(createdAt, link.getCreatedAt());
		assertFalse(link.getModifiedAt().before(firstModifiedAt));
	}

}
