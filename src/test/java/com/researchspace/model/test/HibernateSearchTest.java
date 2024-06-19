package com.researchspace.model.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.researchspace.model.inventory.Barcode;
import com.researchspace.model.permissions.ACLElement;
import com.researchspace.model.permissions.ConstraintBasedPermission;
import com.researchspace.model.permissions.PermissionDomain;
import com.researchspace.model.permissions.PermissionType;
import com.researchspace.model.permissions.RecordSharingACL;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.hibernate.search.FullTextQuery;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.junit.jupiter.api.Test;

import com.researchspace.model.FileProperty;
import com.researchspace.model.inventory.Container;
import com.researchspace.model.inventory.InventoryFile;
import com.researchspace.model.inventory.InventoryRecord;
import com.researchspace.model.inventory.Sample;
import com.researchspace.model.inventory.SubSample;
import com.researchspace.model.inventory.field.ExtraTextField;
import com.researchspace.model.record.TestFactory;

/**
 * Tests lucene indexing and search.
 * 
 */
class HibernateSearchTest extends HibernateTest {

	@SuppressWarnings("unchecked")
	@Test
	public void searchSamples() throws InterruptedException, IOException {

		// save three samples
		Sample sample = TestFactory.createBasicSampleInContainer(testUser);
		sample.setName("uniquename one");
		sample.setTags("tag1, tag2, tag3");
		sample.setDescription("description1");
		
		Sample sample2 = TestFactory.createBasicSampleInContainer(testUser);
		sample2.setName("uniquename two");
		sample2.setTags("tag4, tag5, tag6");
		sample2.setDescription("description2");
		ExtraTextField extraField = TestFactory.createExtraTextField("extraField", testUser, sample2);
		final String testExtraFieldData = "extrafielddata";
		extraField.setData(testExtraFieldData);
		sample2.addExtraField(extraField);
		FileProperty fp =  TestFactory.createAnyTransientFileProperty(testUser);
		String testAttachmentName = "myAttachment.txt";
		InventoryFile invFile = rf.createInventoryFile(testAttachmentName, fp, testUser);
		sample2.addAttachedFile(invFile);
		
		Sample sample3 = TestFactory.createComplexSampleInContainer(testUser);
		sample3.setName("uniquename three");
		sample3.setTags("tag7, tag8, tag9");
		sample3.setDescription("description3");
		sample3.setSharingACL(new RecordSharingACL());
		sample3.getSharingACL().addACLElement(new ACLElement("group1", new ConstraintBasedPermission(PermissionDomain.RECORD, PermissionType.CREATE)));
		final String testFieldData = "textfielddata";
		sample3.getActiveFields().get(5).setData(testFieldData);
		sample3.addBarcode(new Barcode("Barcode123", null));

		sample = saveSampleInContainer(sample);
		saveAssociatedEntities(sample2);
		sample2 = saveSampleInContainer(sample2);
		sample3 = saveComplexSample(sample3);

		Session session = null;
		Transaction transaction = null;
		try {
			session = sf.getCurrentSession();
			transaction = session.getTransaction();
			transaction.begin();

			// build lucene index and query
			FullTextSession fullTextSession = Search.getFullTextSession(sf.getCurrentSession());
			fullTextSession.createIndexer().startAndWait();
			QueryBuilder qb = fullTextSession.getSearchFactory().buildQueryBuilder().forEntity(Sample.class).get();

			// try various searches
			
			// search that doesn't match any sample
			org.apache.lucene.search.Query lucenceQuery = qb.keyword().onFields("name", "name").matching("asdf")
					.createQuery();
			Query<Sample> query = fullTextSession.createFullTextQuery(lucenceQuery, Sample.class);
			List<Sample> samples = query.getResultList();

			assertNotNull(samples);
			assertEquals(0, samples.size());

			// find by name
			lucenceQuery = qb.keyword().onFields("name", "tags", "description").matching("one").createQuery();
			query = fullTextSession.createFullTextQuery(lucenceQuery, Sample.class);
			samples = query.getResultList();
			assertNotNull(samples);
			assertEquals(1, samples.size());
			assertEquals("uniquename one", samples.get(0).getName());

			// by tag
			lucenceQuery = qb.keyword().onFields("name", "tags", "description").matching("tag4").createQuery();
			query = fullTextSession.createFullTextQuery(lucenceQuery, Sample.class);
			samples = query.getResultList();
			assertNotNull(samples);
			assertEquals(1, samples.size());
			assertEquals("uniquename two", samples.get(0).getName());

			// by description
			lucenceQuery = qb.keyword().onFields("name", "tags", "description").matching("description3").createQuery();
			query = fullTextSession.createFullTextQuery(lucenceQuery, Sample.class);
			samples = query.getResultList();
			assertNotNull(samples);
			assertEquals(1, samples.size());
			assertEquals("uniquename three", samples.get(0).getName());
			
			// by field content
			lucenceQuery = qb.keyword().onFields("fields.fieldData").matching(testFieldData).createQuery();
			query = fullTextSession.createFullTextQuery(lucenceQuery, Sample.class);
			samples = query.getResultList();
			assertNotNull(samples);
			assertEquals(1, samples.size());
			assertEquals("uniquename three", samples.get(0).getName());
			
			// by extra field content
			lucenceQuery = qb.keyword().onFields("fields.fieldData").matching(testExtraFieldData).createQuery();
			query = fullTextSession.createFullTextQuery(lucenceQuery, Sample.class);
			samples = query.getResultList();
			assertNotNull(samples);
			assertEquals(1, samples.size());
			assertEquals("uniquename two", samples.get(0).getName());

			// by attachment name
			lucenceQuery = qb.keyword().onFields("fields.fieldData").matching(testAttachmentName).createQuery();
			query = fullTextSession.createFullTextQuery(lucenceQuery, Sample.class);
			samples = query.getResultList();
			assertNotNull(samples);
			assertEquals(1, samples.size());
			assertEquals("uniquename two", samples.get(0).getName());
			assertEquals(1, samples.get(0).getAttachedFiles().size());

			// by barcode
			lucenceQuery = qb.keyword().onFields("barcodes.barcodeData").matching("Barcode123").createQuery();
			query = fullTextSession.createFullTextQuery(lucenceQuery, Sample.class);
			samples = query.getResultList();
			assertNotNull(samples);
			assertEquals(1, samples.size());
			assertEquals("uniquename three", samples.get(0).getName());
			
			// by shared with
			lucenceQuery = qb.keyword().onFields("sharedWith").matching("group1").createQuery();
			query = fullTextSession.createFullTextQuery(lucenceQuery, Sample.class);
			samples = query.getResultList();
			assertNotNull(samples);
			assertEquals(1, samples.size());
			assertEquals("uniquename three", samples.get(0).getName());

			transaction.commit();

		} catch (Exception e) {
			if (transaction != null) {
				transaction.rollback();
			}
			throw e;
		} finally {
			if (session != null) {
				session.close();
			}
		}
	}
	
	private Sample saveComplexSample(Sample sample3) {
		HibernateSandboxTest t = new HibernateSandboxTest();
		t.saveRadioAndChoiceDefinitions(dao, sample3.getSTemplate());
		dao.save(sample3.getSTemplate(), Sample.class);
		t.saveRadioAndChoiceDefinitions(dao, sample3);
		return saveSampleInContainer(sample3);
	}
	
	private Sample saveSampleInContainer(Sample sample) {
		Container container = sample.getSubSamples().get(0).getParentContainer();
		dao.update(container, Container.class);
		return dao.save(sample, Sample.class);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void searchSubSamples() throws InterruptedException {

		// save a sample with 3 subsamples
		Sample sample = TestFactory.createBasicSampleWithSubSamples(testUser, 3);
		SubSample subSample1 = sample.getSubSamples().get(0);
		subSample1.setName("test subSample #1");
		subSample1.setTags("tag1");
		SubSample subSample2 = sample.getSubSamples().get(1);
		String noteContent = "note1";
		subSample2.setName("test subSample #2");
		subSample2.addNote(noteContent, testUser);
		SubSample subSample3 = sample.getSubSamples().get(2);
		subSample3.setName("test subSample #3");
		ExtraTextField extraField = TestFactory.createExtraTextField("extraField", testUser, subSample3);
		final String testExtraFieldData = "extrafielddata";
		extraField.setData(testExtraFieldData);
		subSample3.addExtraField(extraField);
		subSample3.addBarcode(new Barcode("B1234", null));
		sample = saveSampleInContainer(sample);

		Session session = null;
		Transaction transaction = null;
		try {
			session = sf.getCurrentSession();
			transaction = session.getTransaction();
			transaction.begin();

			// build lucene index and query
			FullTextSession fullTextSession = Search.getFullTextSession(sf.getCurrentSession());
			fullTextSession.createIndexer().startAndWait();
			QueryBuilder qb = fullTextSession.getSearchFactory().buildQueryBuilder().forEntity(SubSample.class).get();

			// try various searches
			
			// search that doesn't match any sample
			org.apache.lucene.search.Query lucenceQuery = qb.keyword().onFields("name", "name").matching("asdf")
					.createQuery();
			Query<SubSample> query = fullTextSession.createFullTextQuery(lucenceQuery, SubSample.class);
			List<SubSample> subSamples = query.getResultList();
			assertNotNull(subSamples);
			assertEquals(0, subSamples.size());

			// find by name
			lucenceQuery = qb.keyword().onFields("name", "tags", "description").matching("subSample").createQuery();
			query = fullTextSession.createFullTextQuery(lucenceQuery, SubSample.class);
			subSamples = query.getResultList();
			assertNotNull(subSamples);
			assertEquals(3, subSamples.size());
			assertEquals("test subSample #1", subSamples.get(0).getName());

			// by tag
			lucenceQuery = qb.keyword().onFields("name", "tags", "description").matching("tag1").createQuery();
			query = fullTextSession.createFullTextQuery(lucenceQuery, SubSample.class);
			subSamples = query.getResultList();
			assertNotNull(subSamples);
			assertEquals(1, subSamples.size());
			assertEquals("test subSample #1", subSamples.get(0).getName());

			// by note content
			lucenceQuery = qb.keyword().onFields("fields.fieldData").matching(noteContent).createQuery();
			query = fullTextSession.createFullTextQuery(lucenceQuery, SubSample.class);
			subSamples = query.getResultList();
			assertNotNull(subSamples);
			assertEquals(1, subSamples.size());
			assertEquals("test subSample #2", subSamples.get(0).getName());
			
			// by extra field content
			lucenceQuery = qb.keyword().onFields("fields.fieldData").matching(testExtraFieldData).createQuery();
			query = fullTextSession.createFullTextQuery(lucenceQuery, SubSample.class);
			subSamples = query.getResultList();
			assertNotNull(subSamples);
			assertEquals(1, subSamples.size());
			assertEquals("test subSample #3", subSamples.get(0).getName());

			// by barcode
			lucenceQuery = qb.keyword().onFields("barcodes.barcodeData").matching("B1234").createQuery();
			query = fullTextSession.createFullTextQuery(lucenceQuery, SubSample.class);
			subSamples = query.getResultList();
			assertNotNull(subSamples);
			assertEquals(1, subSamples.size());
			assertEquals("test subSample #3", subSamples.get(0).getName());			
			
			// by parent sample id 
			lucenceQuery = qb.keyword().onFields("parentSampleId").matching(sample.getId()).createQuery();
			query = fullTextSession.createFullTextQuery(lucenceQuery, SubSample.class);
			subSamples = query.getResultList();
			assertNotNull(subSamples);
			assertEquals(3, subSamples.size());
			assertEquals("test subSample #1", subSamples.get(0).getName());

			transaction.commit();

		} catch (Exception e) {
			if (transaction != null) {
				transaction.rollback();
			}
			throw e;
		} finally {
			if (session != null) {
				session.close();
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void searchContainers() throws InterruptedException {

		Container container = rf.createListContainer("test container", testUser);
		ExtraTextField extraField = TestFactory.createExtraTextField("extraField", testUser, container);
		extraField.setData("myData");
		container.addExtraField(extraField);
		Container subContainer1 = rf.createListContainer("test subcontainer #1", testUser);
		subContainer1.setTags("tag1");
		Container subContainer2 = rf.createListContainer("test subcontainer #2", testUser);
		subContainer2.setTags("tag2");
		subContainer2.addBarcode(new Barcode("B456", null));
		container.addToNewLocation(subContainer1);
		container.addToNewLocation(subContainer2);
		Container topContainer = dao.save(container, Container.class);
		dao.save(subContainer1, Container.class);
		dao.save(subContainer2, Container.class);

		Session session = null;
		Transaction transaction = null;
		try {
			session = sf.getCurrentSession();
			transaction = session.getTransaction();
			transaction.begin();

			// build lucene index and query
			FullTextSession fullTextSession = Search.getFullTextSession(sf.getCurrentSession());
			fullTextSession.createIndexer().startAndWait();
			QueryBuilder qb = fullTextSession.getSearchFactory().buildQueryBuilder().forEntity(Container.class).get();

			// try various searches
			
			// search that doesn't match any container
			org.apache.lucene.search.Query lucenceQuery = qb.keyword().onFields("name", "name").matching("asdf")
					.createQuery();
			Query<Container> query = fullTextSession.createFullTextQuery(lucenceQuery, Container.class);
			List<Container> containers = query.getResultList();
			assertNotNull(containers);
			assertEquals(0, containers.size());

			// find by name
			lucenceQuery = qb.keyword().onFields("name", "tags", "description").matching("subcontainer").createQuery();
			query = fullTextSession.createFullTextQuery(lucenceQuery, Container.class);
			containers = query.getResultList();
			assertNotNull(containers);
			assertEquals(2, containers.size());
			assertEquals("test subcontainer #1", containers.get(0).getName());
			assertEquals("test subcontainer #2", containers.get(1).getName());

			// by tag
			lucenceQuery = qb.keyword().onFields("name", "tags", "description").matching("tag2").createQuery();
			query = fullTextSession.createFullTextQuery(lucenceQuery, Container.class);
			containers = query.getResultList();
			assertNotNull(containers);
			assertEquals(1, containers.size());
			assertEquals("test subcontainer #2", containers.get(0).getName());

			// by extra field content
			lucenceQuery = qb.keyword().onFields("fields.fieldData").matching("myData").createQuery();
			query = fullTextSession.createFullTextQuery(lucenceQuery, Container.class);
			containers = query.getResultList();
			assertNotNull(containers);
			assertEquals(1, containers.size());
			assertEquals("test container", containers.get(0).getName());

			// by barcode
			lucenceQuery = qb.keyword().onFields("barcodes.barcodeData").matching("B456").createQuery();
			query = fullTextSession.createFullTextQuery(lucenceQuery, Container.class);
			containers = query.getResultList();
			assertNotNull(containers);
			assertEquals(1, containers.size());
			assertEquals("test subcontainer #2", containers.get(0).getName());	
			
			// by parent id 
			lucenceQuery = qb.keyword().onFields("parentId").matching(topContainer.getId()).createQuery();
			query = fullTextSession.createFullTextQuery(lucenceQuery, Container.class);
			containers = query.getResultList();
			assertNotNull(containers);
			assertEquals(2, containers.size());
			assertEquals("test subcontainer #1", containers.get(0).getName());
			assertEquals("test subcontainer #2", containers.get(1).getName());

			transaction.commit();

		} catch (Exception e) {
			if (transaction != null) {
				transaction.rollback();
			}
			throw e;
		} finally {
			if (session != null) {
				session.close();
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void combinedInventorySearch() throws InterruptedException {

		// save a sample with 2 subsamples
		Sample sample = TestFactory.createBasicSampleWithSubSamples(testUser, 3);
		sample.setTags("tag11, tag12");
		SubSample subSample1 = sample.getSubSamples().get(0);
		subSample1.setName("test subSample #1");
		subSample1.setTags("tag11");
		subSample1.addBarcode(new Barcode("b11", null));
		SubSample subSample2 = sample.getSubSamples().get(1);
		String noteContent = "note1";
		subSample2.setName("test subSample #2");
		subSample2.addNote(noteContent, testUser);
		sample = saveSampleInContainer(sample);

		// and three containers
		Container container = rf.createListContainer("test container", testUser);
		Container subContainer1 = rf.createListContainer("test subcontainer #1", testUser);
		subContainer1.setTags("tag11");
		subContainer1.addBarcode(new Barcode("b11", null));
		Container subContainer2 = rf.createListContainer("test subcontainer #2", testUser);
		subContainer2.setTags("tag12");
		subContainer2.addBarcode(new Barcode("b12", null));
		container.addToNewLocation(subContainer1);
		container.addToNewLocation(subContainer2);
		dao.save(container, Container.class);
		dao.save(subContainer1, Container.class);
		dao.save(subContainer2, Container.class);
		
		Session session = null;
		Transaction transaction = null;
		try {
			session = sf.getCurrentSession();
			transaction = session.getTransaction();
			transaction.begin();

			// build lucene index and query
			FullTextSession fullTextSession = Search.getFullTextSession(sf.getCurrentSession());
			fullTextSession.createIndexer().startAndWait();
			QueryBuilder qb = fullTextSession.getSearchFactory().buildQueryBuilder().forEntity(Sample.class).get();

			// try various searches
			// search that doesn't match any sample
			org.apache.lucene.search.Query lucenceQuery = qb.keyword().onFields("name", "name").matching("asdf")
					.createQuery();
			FullTextQuery query = fullTextSession.createFullTextQuery(lucenceQuery, Sample.class, SubSample.class, Container.class);
			List<InventoryRecord> foundInvRecords = query.getResultList();
			assertNotNull(foundInvRecords);
			assertEquals(0, foundInvRecords.size());

			// search by tag, should match a sample and a first subsample 
			lucenceQuery = qb.keyword().onFields("tags").matching("tag11").createQuery();
			query = fullTextSession.createFullTextQuery(lucenceQuery, Sample.class, SubSample.class, Container.class);
			foundInvRecords = query.getResultList();
			assertNotNull(foundInvRecords);
			assertEquals(3, foundInvRecords.size());
			Collections.sort(foundInvRecords, (ir1, ir2) -> ir1.getName().compareTo(ir2.getName()));
			assertEquals("test sample", foundInvRecords.get(0).getName());
			assertEquals("test subSample #1", foundInvRecords.get(1).getName());
			assertEquals("test subcontainer #1", foundInvRecords.get(2).getName());

			// search by barcode, should match a subsample and a container 
			lucenceQuery = qb.keyword().onFields("barcodes.barcodeData").matching("B11").createQuery();
			query = fullTextSession.createFullTextQuery(lucenceQuery, Sample.class, SubSample.class, Container.class);
			foundInvRecords = query.getResultList();
			assertNotNull(foundInvRecords);
			assertEquals(2, foundInvRecords.size());
			
			// search by owner, should match everything
			lucenceQuery = qb.keyword().onFields("owner.username").matching(testUser.getUsername()).createQuery();
			query = fullTextSession.createFullTextQuery(lucenceQuery, Sample.class, SubSample.class, Container.class);
			foundInvRecords = query.getResultList();
			assertNotNull(foundInvRecords);
			assertEquals(8, foundInvRecords.size());

			transaction.commit();

		} catch (Exception e) {
			if (transaction != null) {
				transaction.rollback();
			}
			throw e;
		} finally {
			if (session != null) {
				session.close();
			}
		}
	}

}
