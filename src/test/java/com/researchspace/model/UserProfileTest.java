package com.researchspace.model;

import static com.researchspace.core.testutil.CoreTestUtils.getRandomName;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.researchspace.model.record.TestFactory;

public class UserProfileTest {

	User user;
	User other;

	@Before
	public void setUp() throws Exception {
		user = TestFactory.createAnyUser("any");
		other = TestFactory.createAnyUser("other");
	}

	@After
	public void tearDown() {
	}

	@Test
	public void testHashCodeEquals() {
		UserProfile profile = new UserProfile(user);
		UserProfile profile2 = new UserProfile(user);
		UserProfile otherProfile = new UserProfile(other);
		assertEquals(profile.hashCode(), profile2.hashCode());
		assertEquals(profile, profile2);
		assertFalse(otherProfile.hashCode() == profile.hashCode());
		assertFalse(otherProfile.equals(profile));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testUserProfileUserThrowsIAEIfNullUser() {
		new UserProfile(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSetOwner() {
		UserProfile profile = new UserProfile(user);
		profile.setOwner(null);
	}

	@Test
	public void testSetURLDisplayIsAbbreviatedIfTooLong() {
		UserProfile profile = new UserProfile(user);
		profile.setExternalLinkDisplay(getRandomName(UserProfile.MAX_FIELD_LENG + 1));
		assertEquals(UserProfile.MAX_FIELD_LENG, profile.getExternalLinkDisplay().length());
	}

	@Test
	public void testShortProfileTextIsAbbreviatedIfTooLong() {
		UserProfile profile = new UserProfile(user);
		profile.setProfileText(getRandomName(UserProfile.MAX_FIELD_LENG + 1));
		assertEquals(UserProfile.SHORT_PROFILE_TEXT_LEN, profile.getShortProfileText().length());
	}

	@Test
	public void testShortProfileTextIsEmptyIfProfileTextIsNotSet() {
		UserProfile profile = new UserProfile(user);
		profile.setProfileText(UserProfile.DEFAULT_PROFILE_TEXT);
		assertNull(profile.getShortProfileText());
	}

	@Test
	public void testSetProfileTextIsAbbreviatedIfTooLong() {
		UserProfile profile = new UserProfile(user);
		profile.setProfileText(getRandomName(UserProfile.MAX_PROFILE_TEXT_LEN + 1));
		assertEquals(UserProfile.MAX_PROFILE_TEXT_LEN, profile.getProfileText().length());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSetURLThrowsIAEIfTooLong() {
		UserProfile profile = new UserProfile(user);
		profile.setExternalLinkURL(getRandomName(UserProfile.MAX_FIELD_LENG + 1));
	}

	@Test
	public void testSetURL() {
		UserProfile profile = new UserProfile(user);
		String OK_LINK = "http://www.google.com";
		String OK_HTTPS_LINK = "https://www.google.com";
		// http
		profile.setExternalLinkURL(OK_LINK);
		assertEquals(OK_LINK, profile.getExternalLinkURL());

		// https prefix
		profile.setExternalLinkURL(OK_HTTPS_LINK);
		assertEquals(OK_HTTPS_LINK, profile.getExternalLinkURL());

		// set with missing prefix converted to http
		profile.setExternalLinkURL("www.google.com");
		assertEquals(OK_LINK, profile.getExternalLinkURL());

		// correct typos
		profile.setExternalLinkURL("http:/www.google.com");
		assertEquals(OK_LINK, profile.getExternalLinkURL());

		// correct typos 2
		profile.setExternalLinkURL("http//www.google.com");
		assertEquals(OK_LINK, profile.getExternalLinkURL());

		// correct typos 3
		profile.setExternalLinkURL("http//:www.google.com");
		assertEquals(OK_LINK, profile.getExternalLinkURL());

		// and now for HTTPS
		// correct typos
		profile.setExternalLinkURL("https:/www.google.com");
		assertEquals(OK_HTTPS_LINK, profile.getExternalLinkURL());

		// correct typos 2
		profile.setExternalLinkURL("https//www.google.com");
		assertEquals(OK_HTTPS_LINK, profile.getExternalLinkURL());

		// correct typos 3
		profile.setExternalLinkURL("https//:www.google.com");
		assertEquals(OK_HTTPS_LINK, profile.getExternalLinkURL());

		profile.setExternalLinkURL(null);
		assertNull(profile.getExternalLinkURL());
	}

}
