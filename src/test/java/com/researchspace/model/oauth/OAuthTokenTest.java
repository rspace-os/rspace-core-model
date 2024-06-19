package com.researchspace.model.oauth;

import static com.researchspace.core.testutil.CoreTestUtils.assertIllegalArgumentException;
import static com.researchspace.model.record.TestFactory.createAnyUser;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.researchspace.model.User;

class OAuthTokenTest {

	private User anyUser;

	@BeforeEach
	void before() {
		anyUser = createAnyUser("any");
	}

	@ParameterizedTest(name = "{index}: user:{0}, clientId:{1}, accessToken:{2})")
	@MethodSource("constructorValidationArguments")
	void constructorValidationNoNullArgs(User user, String clientId, String hashedToken) {
		assertIllegalArgumentException(() -> new OAuthToken(user, clientId, hashedToken));
	}

	void expiryTimeInFuture() {
		assertIllegalArgumentException(() -> new OAuthToken(anyUser, "anId", "token", inthePast()));
		OAuthToken valid = new OAuthToken(anyUser, "id", "token", inTheFuture());
		assertEquals("id", valid.getClientId());
	}

	static Stream<Arguments> constructorValidationArguments() throws Throwable {
		return Stream.of(Arguments.of(null, "clientid", "hashedToken"),
				Arguments.of(createAnyUser("any"), "", "hashedToken"),
				Arguments.of(createAnyUser("any"), null, "hashedToken"),
				Arguments.of(createAnyUser("any"), "clientId", " "),
				Arguments.of(null, null, null)
		);
	}

	private Instant inTheFuture() {
		return Instant.now().plus(10, ChronoUnit.SECONDS);
	}

	private Instant inthePast() {
		return Instant.now().minus(10, ChronoUnit.SECONDS);
	}

}
