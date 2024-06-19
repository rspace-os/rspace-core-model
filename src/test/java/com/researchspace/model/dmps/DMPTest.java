package com.researchspace.model.dmps;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DMPTest {

    @Test
    @DisplayName("equality based on dmpId")
    void testEquals() {
        DMP dmp1 = new DMP("dmpID", "Title");
        DMP dmp2 = new DMP("dmpID", "Title");

        assertEquals(dmp1, dmp2);
        assertEquals(dmp1.hashCode(), dmp2.hashCode());
    }
}