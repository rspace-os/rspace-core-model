package com.researchspace.model.oauth;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class OAuthAppTest {
	@Test
	void constructorValidationNoNullArgs() {
		assertThrows(NullPointerException.class, () -> new OAuthApp(null, null, null, null));
	}
}
