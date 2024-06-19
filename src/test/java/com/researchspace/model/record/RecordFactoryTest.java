package com.researchspace.model.record;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.researchspace.model.Group;
import com.researchspace.model.Role;
import com.researchspace.model.User;
import com.researchspace.model.core.RecordType;
import com.researchspace.model.inventory.Sample;


public class RecordFactoryTest {

	IRecordFactory factoryAPI;
	private User user, otherUser;
	@Before
	public void setUp() throws Exception {
		factoryAPI = new RecordFactory();
		user = TestFactory.createAnyUser("user");
		otherUser = TestFactory.createAnyUser("otherUser");
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	(expected=IllegalArgumentException.class)
	public void testCreateRichTextDocumentFailsIfARgsNull() {
		factoryAPI.createFolder(null, user);		
	}

	@Test
	public void testCreateStructuredDocument() {
		RSForm form = factoryAPI.createExperimentForm("name", "desc", TestFactory.createAnyUser("user"));
		StructuredDocument structuredDoc = factoryAPI.createStructuredDocument("\n notroot\n  \n", user,form);
		assertFalse(structuredDoc.hasType(RecordType.ROOT));
		assertNotNull(structuredDoc.getForm());
		assertEquals(form.getNumAllFields(), structuredDoc.getFields().size());	
		assertCoreDataNotNull(structuredDoc);
		//control chars and newlines are stripped.
		assertEquals("notroot", structuredDoc.getName());
	}

    @Test
    public void testCreateSnippet() {
       
        String testContent = "test content";
        Snippet snippet = factoryAPI.createSnippet("testSnippet", testContent, user);
        assertEquals(testContent, snippet.getContent());
        assertFalse(snippet.hasType(RecordType.ROOT));
        assertCoreDataNotNull(snippet);
    }

	@Test
	public void testCreateSsytemCreatedFolder() {
		Folder folder = factoryAPI.createSystemCreatedFolder("notroot", user);
		assertTrue(folder.isSystemFolder());
		assertEquals(0,folder.getChildren().size());
		assertCoreDataNotNull(folder);		
	}
	
	@Test
	public void testCreateFolder() {
		Folder folder = factoryAPI.createFolder("notroot", user);
		assertFalse(folder.hasType(RecordType.ROOT));
		assertEquals(0,folder.getChildren().size());
		assertCoreDataNotNull(folder);		
	}

	private void assertCoreDataNotNull(BaseRecord baseRecord) {
		assertNotNull(baseRecord.getCreationDate());
		assertNotNull(baseRecord.getCreatedBy());
		assertNotNull(baseRecord.getName());
	}

	@Test
	public void testCreateRootFolder() {
		Folder folder = factoryAPI.createRootFolder("notroot", user);
		assertTrue(folder.hasType(RecordType.ROOT));
		assertTrue(folder.isRootFolder());
		assertTrue(folder.isRootFolderForUser(user));
		assertEquals(0,folder.getChildren().size());
		assertCoreDataNotNull(folder);
	}
	
	@Test
	public void testCreateCommunalGrpFolder() {
		user.addRole(Role.PI_ROLE);
		Group grp = TestFactory.createAnyGroup(user, new User[]{});
		Folder f = factoryAPI.createCommunalGroupFolder(grp, otherUser);
		assertTrue(f.hasType(RecordType.SHARED_GROUP_FOLDER_ROOT));
	}
	
	@Test
	public void testCreateNewSample() {
		Sample sample = factoryAPI.createSample("test sample", user);
		assertNotNull(sample.getCreationDate());
		assertEquals(1, sample.getSubSamples().size());
		assertEquals(1, sample.getActiveSubSamples().size());
		assertEquals(0, sample.getActiveFields().size());
	}
	
}
