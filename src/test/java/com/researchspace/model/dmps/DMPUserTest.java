package com.researchspace.model.dmps;

import org.junit.jupiter.api.Test;

import static com.researchspace.core.testutil.CoreTestUtils.getRandomName;
import static com.researchspace.model.record.TestFactory.createAnyUser;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DMPUserTest {

    @Test
    void timestampAutoCreated() {
        DMPUser dmpUser = new DMPUser(createAnyUser(getRandomName(10)),
                new DMP("dmpID", "Title"));
        assertNotNull(dmpUser.getTimestamp());
    }
}