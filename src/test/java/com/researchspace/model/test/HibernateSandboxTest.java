package com.researchspace.model.test;

import static com.researchspace.core.util.TransformerUtils.toList;
import static org.apache.commons.collections4.CollectionUtils.isEqualCollection;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.researchspace.model.EcatDocumentFile;
import com.researchspace.model.EcatImage;
import com.researchspace.model.FileProperty;
import com.researchspace.model.FileStoreRoot;
import com.researchspace.model.User;
import com.researchspace.model.core.GlobalIdPrefix;
import com.researchspace.model.dmps.DMP;
import com.researchspace.model.dmps.DMPUser;
import com.researchspace.model.elninventory.ListOfMaterials;
import com.researchspace.model.elninventory.MaterialUsage;
import com.researchspace.model.field.DateFieldForm;
import com.researchspace.model.field.Field;
import com.researchspace.model.field.FieldType;
import com.researchspace.model.field.TextFieldForm;
import com.researchspace.model.inventory.Basket;
import com.researchspace.model.inventory.Container;
import com.researchspace.model.inventory.Container.GridLayoutAxisLabelEnum;
import com.researchspace.model.inventory.ContainerLocation;
import com.researchspace.model.inventory.DigitalObjectIdentifier;
import com.researchspace.model.inventory.InventoryFile;
import com.researchspace.model.inventory.InventoryRecord.InventorySharingMode;
import com.researchspace.model.inventory.Sample;
import com.researchspace.model.inventory.SubSample;
import com.researchspace.model.inventory.SubSampleNote;
import com.researchspace.model.inventory.field.ExtraField;
import com.researchspace.model.inventory.field.ExtraNumberField;
import com.researchspace.model.inventory.field.ExtraTextField;
import com.researchspace.model.inventory.field.InventoryAttachmentField;
import com.researchspace.model.inventory.field.InventoryChoiceField;
import com.researchspace.model.inventory.field.InventoryChoiceFieldDef;
import com.researchspace.model.inventory.field.InventoryDateField;
import com.researchspace.model.inventory.field.InventoryNumberField;
import com.researchspace.model.inventory.field.InventoryRadioField;
import com.researchspace.model.inventory.field.InventoryRadioFieldDef;
import com.researchspace.model.inventory.field.InventoryReferenceField;
import com.researchspace.model.inventory.field.InventoryStringField;
import com.researchspace.model.inventory.field.InventoryTextField;
import com.researchspace.model.inventory.field.InventoryTimeField;
import com.researchspace.model.inventory.field.InventoryUriField;
import com.researchspace.model.inventory.field.SampleField;
import com.researchspace.model.netfiles.ExternalStorageLocation;
import com.researchspace.model.netfiles.NfsAuthenticationType;
import com.researchspace.model.netfiles.NfsClientType;
import com.researchspace.model.netfiles.NfsFileStore;
import com.researchspace.model.netfiles.NfsFileSystem;
import com.researchspace.model.permissions.PermissionType;
import com.researchspace.model.permissions.RecordSharingACL;
import com.researchspace.model.record.RSForm;
import com.researchspace.model.record.TestFactory;
import com.researchspace.model.units.QuantityInfo;
import com.researchspace.model.units.RSUnitDef;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.ConstraintViolationException;
import org.hibernate.LazyInitializationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Tests for model entity creation and behaviour.
 */
class HibernateSandboxTest extends HibernateTest {

	@Test
	void createForm() {
		User u = createAndSaveAnyUser();
		RSForm form = new RSForm("mydoc", "a doc", u);
		form = dao.save(form, RSForm.class);
	}

	@Test
	void testField() {
		User u = createAndSaveAnyUser();
		RSForm form = createAForm(u);

		form = dao.save(form, RSForm.class);
		DateFieldForm dff = TestFactory.createDateFieldForm();
		form.addFieldForm(dff);
		dao.save(dff, DateFieldForm.class);
		
		long dffid = dff.getId();
		DateFieldForm dff2 = dao.load(dffid, DateFieldForm.class);
		assertEquals("datefield", dff2.getName());
		
		Field dateField = dff2.createNewFieldFromForm();
		dateField = dao.save(dateField, Field.class);
		
		Field dateField2 = dao.load(dateField.getId(), Field.class);
		assertEquals(dateField2.getFieldData(), dateField.getFieldData());
		
		dateField2.setFieldData("2002-04-19");
		Field dateField3 = dao.save(dateField2, Field.class);
		assertEquals("2002-04-19", dao.load(dateField3.getId(), Field.class).getFieldData());
	}
	
	@Test
	void testSampleField() {
		final int initialFieldCount = dao.count("SampleField").intValue();
		final int expectedFieldCount = initialFieldCount +10;
		
		User u = createAndSaveAnyUser();
		
		InventoryRadioFieldDef rff = createRadioFieldDef();	
		dao.save(rff, InventoryRadioFieldDef.class);
		
		InventoryChoiceFieldDef cff = createChoiceFieldDef();
		dao.save(cff, InventoryChoiceFieldDef.class);
	
		// create Fields
		//create a sample first
		Sample sample = TestFactory.createBasicSampleWithSubSamples(u, 2);
		sample = saveSampleInContainer(sample);
		final List<FieldType> expectedFieldOrder = toList(FieldType.NUMBER, FieldType.DATE,  FieldType.STRING, 
				FieldType.TEXT, FieldType.CHOICE, FieldType.RADIO, FieldType.TIME, FieldType.REFERENCE,
				FieldType.ATTACHMENT,   FieldType.URI);
		
		SampleField numberField = new InventoryNumberField("number");
		sample.addSampleField(numberField);
			
		SampleField dateField = new InventoryDateField("date");
		sample.addSampleField(dateField);
		
		SampleField stringField = new InventoryStringField("string");
		sample.addSampleField(stringField);
		
		SampleField textField = new InventoryTextField("text");
		sample.addSampleField(textField);	

		SampleField choiceField = new InventoryChoiceField(cff, "choice");
		sample.addSampleField(choiceField);
		
		SampleField radioField = new InventoryRadioField(rff, "radio");
		sample.addSampleField(radioField);
		
		SampleField timeField = new InventoryTimeField( "time");
		sample.addSampleField(timeField);
		
		SampleField refField = new InventoryReferenceField( "ref");
		sample.addSampleField(refField);
		
		SampleField attachField = new InventoryAttachmentField( "att");
		sample.addSampleField(attachField);
	
		SampleField uriField = new InventoryUriField("uri");
		sample.addSampleField(uriField);
		sample = dao.update(sample,Sample.class);
		assertEquals(expectedFieldCount, dao.count("SampleField"));
		assertEquals(expectedFieldCount, getAllSampleFields().size());
		
		
		List<FieldType> actualTypes = getOrderedFieldTypes(getAllSampleFields(), sample.getId());
		assertTrue(isEqualCollection(actualTypes, expectedFieldOrder));
		
		// and sorting is by column order, maintains ordering 
		List<SampleField> items = getAllSampleFields();
		Collections.sort(items);
		assertTrue(isEqualCollection(getOrderedFieldTypes(items, sample.getId()), expectedFieldOrder));
		
	}

	private List<SampleField> getAllSampleFields() {
		return dao.getAll(SampleField.class, "SampleField");
	}

	private List<FieldType> getOrderedFieldTypes(List<SampleField> items, Long sampleId) {
		return getAllSampleFields().stream()
				.filter(f->f.getSample().getId().equals(sampleId))
				.map(SampleField::getType)
				.collect(Collectors.toList());
	}

	private InventoryChoiceFieldDef createChoiceFieldDef() {
		InventoryChoiceFieldDef cff = new InventoryChoiceFieldDef();
		cff.setChoiceOptionsList(Arrays.asList("2", "3"));
		return cff;
	}

	private InventoryRadioFieldDef createRadioFieldDef() {
		InventoryRadioFieldDef rff = new InventoryRadioFieldDef();
		rff.setRadioOptionsList(Arrays.asList("1", "2", "3"));
		return rff;
	}

	private RSForm createAForm(User u) {
		RSForm form = TestFactory.createAnyForm();
		form.setOwner(u);
		form.removeFieldForm(form.getFieldForms().get(0));
		return form;
	}
	
	@Test()
	@DisplayName("Deep copy of complex sample copies fields") 
	void complexsampleCopy() throws IOException {
		Sample complexSample = TestFactory.createComplexSampleInContainer(testUser);
		assertEquals(1, complexSample.getActiveFields().get(0).getColumnIndex());
		assertEquals(9, complexSample.getActiveFields().get(8).getColumnIndex());
		saveParentTemplateForSample(complexSample);
		// create a copy
		Sample copy = complexSample.copy(testUser);
		copy.getSubSamples().get(0).moveToNewParent(complexSample.getSubSamples().get(0).getParentContainer());
		// save a sample
		saveRadioAndChoiceDefinitions(dao, complexSample);
		saveSampleInContainer(complexSample);
		// save a copy
		copy = saveSampleInContainer(copy);
		assertEquals(complexSample.getActiveFields().size(), copy.getActiveFields().size());
		for (int i = 0; i< complexSample.getActiveFields().size(); i++) {
			SampleField origF = complexSample.getActiveFields().get(i);
			SampleField copyF = copy.getActiveFields().get(i);
			assertEquals(origF.getFieldData(), copyF.getFieldData());
			assertFalse(origF.getId().equals(copyF.getId()));
			assertEquals(origF.getColumnIndex(), copyF.getColumnIndex());
		}
    }

	private Sample saveSampleInContainer(Sample sample) {
		Container container = sample.getSubSamples().get(0).getParentContainer();
		dao.update(container, Container.class);
		if (sample.getId() == null) {
			sample = dao.save(sample, Sample.class);
		} else {
			sample = dao.update(sample, Sample.class);
		}
	
		for (InventoryFile invFile : sample.getAttachedFiles()) {
			dao.update(invFile, InventoryFile.class);
		}
		return sample;
	}

	@Test()
	@DisplayName("Deep copy of sample excludes subsamples")
	void sampleCopy() throws IOException {
		User user = createAndSaveAnyUser();
		Sample sample = TestFactory.createSampleWithSubSamplesAndEverything(user, 1);
		saveAssociatedEntities(sample);
		sample = saveSampleInContainer(sample);
		Container parentContainer = sample.getSubSamples().get(0).getParentContainer();
		
		Sample copy = sample.copy(testUser);
		copy.getSubSamples().get(0).moveToNewParent(parentContainer);
		dao.update(parentContainer, Container.class);
		copy = dao.save(copy, Sample.class);
		
		Sample reloaded = dao.load(copy.getId(), Sample.class);
		assertFalse(reloaded.getId().equals(sample.getId()));
		assertEquals(sample.getTotalQuantity().getNumericValue().doubleValue(), 
				reloaded.getTotalQuantity().getNumericValue().doubleValue());
		assertEquals(1, reloaded.getSubSamples().size());
	}
	
	@Test()
	@DisplayName("Deep copy of subsample")
	void subsampleCopy() throws IOException {
		User user = createAndSaveAnyUser();
		Sample sample = TestFactory.createSampleWithSubSamplesAndEverything(user, 1);
		SubSample originalSS = sample.getActiveSubSamples().get(0);
		saveAssociatedEntities(sample);
		sample = saveSampleInContainer(sample);
		assertEquals(10, sample.getTotalQuantity().getNumericValue().intValue());

		SubSample copy = originalSS.copy(user);
		copy.moveToNewParent(sample.getSubSamples().get(0).getParentContainer());
		dao.update(copy.getParentContainer(), Container.class);
		copy = dao.save(copy, SubSample.class);
		sample = dao.update(sample, Sample.class);
		assertEquals(sample, copy.getSample());
		assertTrue(copy.getActiveExtraFields().stream().allMatch(ef -> ef.getId() != null));
		assertTrue(copy.getNotes().stream().allMatch(note -> note.getId() != null));
		// ids are different
		Set<Long> originalNotes = originalSS.getNotes().stream().map(SubSampleNote::getId).collect(Collectors.toSet());
		assertTrue(copy.getNotes().stream().noneMatch(n -> originalNotes.contains(n.getId())));

		Set<Long> originalFields = originalSS.getActiveExtraFields().stream().map(ExtraField::getId)
				.collect(Collectors.toSet());
		assertTrue(copy.getActiveExtraFields().stream().noneMatch(n -> originalFields.contains(n.getId())));
		// saving name of original does not alter new name
		String originalName = originalSS.getName();
		originalSS.setName("newname");
		originalSS = dao.update(originalSS, SubSample.class);
		copy = dao.load(copy.getId(), SubSample.class);
		assertEquals(originalName + "_COPY", copy.getName());

		Sample reloaded = dao.load(copy.getSample().getId(), Sample.class);
		assertEquals(20, reloaded.getTotalQuantity().getNumericValue().intValue());
	}

	@Test
	void testExtraFields() {
		User u = createAndSaveAnyUser();
		
		// create a sample with default subsample
		Sample sample = TestFactory.createBasicSampleInContainer(u);
		saveSampleInContainer(sample);
		SubSample subSample = sample.getSubSamples().get(0);

		/*
		 * Numeric field
		 */
		// connected to sample, no value
		ExtraNumberField ahnf = TestFactory.createExtraNumberField("", u, sample);
		dao.save(ahnf, ExtraNumberField.class);
		ExtraNumberField loaded = dao.load(ahnf.getId(), ExtraNumberField.class);
		assertEquals(ahnf, loaded);

		// connected to sample, with value
		ExtraNumberField ahnf2 = TestFactory.createExtraNumberField("", u, sample);
		ahnf2.setData("3.14");
		dao.save(ahnf2, ExtraNumberField.class);
		loaded = dao.load(ahnf2.getId(), ExtraNumberField.class);
		assertEquals(ahnf2, loaded);
		
		// try setting unparseable value
		ExtraNumberField ahnf3 = TestFactory.createExtraNumberField("", u, sample);
		assertThrows(IllegalArgumentException.class, ()->ahnf3.setData("3.asdf"));
		
		/*
		 * String field
		 */
		
		// connected to subsample, no value
		ExtraTextField ahsf = TestFactory.createExtraTextField("", u, subSample);
		ahsf = dao.save(ahsf, ExtraTextField.class);
		ExtraTextField loaded2 = dao.load(ahsf.getId(), ExtraTextField.class);
		assertEquals(ahsf, loaded2);

		// connected to subsample, with value
		ExtraTextField ahsf2 = TestFactory.createExtraTextField("", u, subSample);
		ahsf2.setData("test content");
		dao.save(ahsf2, ExtraTextField.class);
		loaded2 = dao.load(ahsf2.getId(), ExtraTextField.class);
		assertEquals(ahsf2, loaded2);
		
		/* 
		 * either sample or subsample field must not be null
		 */
		
		// when updating existing extra field
		ahsf2.setSubSample(null);
		assertThrows(ConstraintViolationException.class, ()->dao.save(ahsf2, ExtraTextField.class));
		
		// when saving new extra field
		ExtraTextField unconnectedExtraField = TestFactory.createExtraTextField("", u, sample);
		unconnectedExtraField.setSample(null);
		assertThrows(ConstraintViolationException.class, ()->dao.save(unconnectedExtraField, ExtraTextField.class)); // no parents
		unconnectedExtraField.setSample(sample);
		unconnectedExtraField.setSubSample(subSample);
		assertThrows(ConstraintViolationException.class, ()->dao.save(unconnectedExtraField, ExtraTextField.class)); // two parents
		// should save fine now
		unconnectedExtraField.setSample(null);
		dao.save(unconnectedExtraField, ExtraTextField.class);
	}
	
	@Test
	@DisplayName("save sample temperature units")
	public void testSaveSampleValidateTemp() {
		User u = createAndSaveAnyUser();

		Sample sample = TestFactory.createBasicSampleWithSubSamples(u, 2);
		sample.setStorageTempMin(QuantityInfo.of(BigDecimal.valueOf(5), RSUnitDef.GRAM));
		assertThrows(ConstraintViolationException.class, ()-> saveSampleInContainer(sample));
		
		sample.setStorageTempMin(QuantityInfo.of(BigDecimal.valueOf(5), RSUnitDef.KELVIN));
		assertNotNull(sample.getId());
	}

	@Test
	public void testSaveSampleWithSubSamples() {

		User u = createAndSaveAnyUser();
		Sample sample = TestFactory.createBasicSampleWithSubSamples(u, 2);
		
		Long initSubSampleCount = dao.count("SubSample");
		Long initFieldsCount = dao.count("SampleField");

		Sample savedSample = saveSampleInContainer(sample);
		assertNotNull(savedSample);
		assertNotNull(savedSample.getId());
		assertEquals("test sample", savedSample.getName());
		
		Long newSubSampleCount = dao.count("SubSample");
		assertEquals(initSubSampleCount + 2, newSubSampleCount);
		Long newFieldsCount = dao.count("SampleField"); // default form has no fields
		assertEquals(initFieldsCount, newFieldsCount);
		
		Sample retrievedSample = dao.load(savedSample.getId(), Sample.class);
		assertEquals(savedSample, retrievedSample);
	}
	
	@Test
	public void testSubSampleQuantities() {

		User u = createAndSaveAnyUser();
		Sample sample = TestFactory.createBasicSampleWithSubSamples(u, 2);

		// check quantity settings
		QuantityInfo quantity5L = QuantityInfo.of(BigDecimal.valueOf(5), RSUnitDef.LITRE);
		QuantityInfo quantity100ML = QuantityInfo.of(BigDecimal.valueOf(100), RSUnitDef.MILLI_LITRE);

		// set subsample volumes to 0
		sample.getSubSamples().get(0).setQuantity(QuantityInfo.zero(RSUnitDef.LITRE));
		sample.getSubSamples().get(1).setQuantity(QuantityInfo.zero(RSUnitDef.LITRE));
		// assert sample with more than one subsample cannot have total quantity set directly
		assertThrows(IllegalStateException.class, () -> sample.setTotalQuantity(quantity5L));
		// try setting quantity of individual subsamples instead
		
		sample.getSubSamples().get(0).setQuantity(quantity5L);
		assertEquals(quantity5L.getNumericValue().doubleValue(),
				sample.getTotalQuantity().getNumericValue().doubleValue(), 0.001);
		sample.getSubSamples().get(1).setQuantity(quantity100ML);
		assertEquals("5.1 l", sample.getTotalQuantity().toPlainString());
		// retrieve
		Sample sampleWithQuantity = saveSampleInContainer(sample);
		assertEquals("5.1 l", sampleWithQuantity.getTotalQuantity().toPlainString());
	}
	
	@Test
	public void testSubSampleNotes() {

		User u = createAndSaveAnyUser();
		Sample sample = TestFactory.createBasicSampleWithSubSamples(u, 1);
		SubSample subSample = sample.getSubSamples().get(0);
		
		// add note
		subSample.addNote("test note", u);
		
		Sample savedSample = saveSampleInContainer(sample);
		SubSample savedSubSample = savedSample.getSubSamples().get(0);
		assertEquals(1, savedSubSample.getNotes().size());
		SubSampleNote savedNote = savedSubSample.getNotes().get(0);
		assertEquals("test note", savedNote.getContent());
		assertEquals(u, savedNote.getCreatedBy());
	}
	
	@Test
	public void requireSampleToHaveAtLeastOneSubSample() {
		User user = createAndSaveAnyUser();
		Sample sampleWithoutSubSamples = TestFactory.createBasicSampleInContainer(user);
		SubSample defaultSubSample = sampleWithoutSubSamples.getSubSamples().remove(0);
		assertNull(sampleWithoutSubSamples.getId());
		
		// try saving the sample without subsamples
		assertThrows(ConstraintViolationException.class, ()->dao.save(sampleWithoutSubSamples, Sample.class));
		// for some reason sample's id is not null anymore, which causes problems with saving later
		sampleWithoutSubSamples.setId(null);
		
		// confirm can save the sample with defaultSubSample added back
		sampleWithoutSubSamples.getSubSamples().add(defaultSubSample);
		saveSampleInContainer(sampleWithoutSubSamples);
	}
	
	@Test 
	public void checkQuantitySetupForSampleAndSubSamples() {
		User user = createAndSaveAnyUser();
		Sample sample = TestFactory.createBasicSampleInContainer(user);
		QuantityInfo quantity = QuantityInfo.of(BigDecimal.valueOf(10), RSUnitDef.LITRE);
		sample.setTotalQuantity(quantity);

		Sample savedSample = saveSampleInContainer(sample);
		assertEquals(quantity, savedSample.getTotalQuantity());
		assertTrue(savedSample.hasExactlyOneSubSample());
		assertEquals(quantity, savedSample.getOnlySubSample().get().getQuantity());
	}
	
	@Test
	public void createContainersHierarchy() {
		User user = createAndSaveAnyUser();

		int initContainersCount = dao.getAll(Container.class, "Container").size();
		
		Container container = rf.createListContainer("test container", user);
		ExtraTextField extraField = TestFactory.createExtraTextField("extraField", testUser, container);
		container.addExtraField(extraField);
		Container subContainer1 = rf.createListContainer("test subcontainer #1", user);
		Container subContainer2 = rf.createListContainer("test subcontainer #2", user);
		container.addToNewLocation(subContainer1);
		container.addToNewLocation(subContainer2);
		Container savedContainer = dao.save(container, Container.class);
		Container savedSubContainer1 = dao.save(subContainer1, Container.class);
		dao.save(subContainer2, Container.class);

		// verify all created
		List<Container> allContainers = dao.getAll(Container.class, "Container");
		assertEquals(initContainersCount + 3, allContainers.size());
		
		// load container and try checking the content
		Container loadedContainer = dao.load(savedContainer.getId(), Container.class);
		assertNotNull(loadedContainer.getId());
		assertEquals("test container", loadedContainer.getName());
		assertEquals(2, loadedContainer.getContentCount());
		assertNull(loadedContainer.getParentContainer());
		// trying to access lazy-loaded locations should throw an exception
		assertThrows(LazyInitializationException.class, ()-> loadedContainer.getLocations().size());
		
		// load subcontainer and try checking the parent
		Container loadedSubContainer1 = dao.load(savedSubContainer1.getId(), Container.class);
		assertEquals("test subcontainer #1", loadedSubContainer1.getName());
		assertEquals("test container", loadedSubContainer1.getParentContainer().getName()); // parent is loaded
		assertThrows(LazyInitializationException.class, ()-> loadedSubContainer1.getParentContainer().getLocations().size()); // but not parent's locations
	}

	@Test
	public void checkConstraintValidationOnSave() {
		
		User user = createAndSaveAnyUser();

		// create a container with location
		Container container = rf.createListContainer("test container", user);
		Container subContainer = rf.createListContainer("test subcontainer #1", user);
		container.addToNewLocation(subContainer);
		Container savedContainer = dao.save(container, Container.class);
		Container savedSubContainer = dao.save(subContainer, Container.class);
		Container loadedSubContainer = dao.load(savedSubContainer.getId(), Container.class);

		// create a sample with default subsample
		Sample sample = TestFactory.createBasicSampleInContainer(user);
		sample = saveSampleInContainer(sample);
		SubSample subSample = sample.getSubSamples().get(0);

		/*
		 *  do a few checks on location with content
		 */
		ContainerLocation containerLocation = loadedSubContainer.getParentLocation();
		assertTrue(containerLocation.getStoredRecord() != null);
		
		// try setting subsample where there is already container
		containerLocation.setStoredSubSample(subSample); 
		assertThrows(ConstraintViolationException.class, ()-> dao.save(containerLocation, ContainerLocation.class));
		
		// try updating coords to illegal value (1, 0) (valid coords start at (1,1))
		containerLocation.setStoredSubSample(null);
		containerLocation.setCoordX(1);
		containerLocation.setCoordY(0);
		assertThrows(ConstraintViolationException.class, ()-> dao.save(containerLocation, ContainerLocation.class));
		
		/*
		 *  try updating canStoreSamples/canStoreContainers flags
		 */
		// one flag can be set to false fine
		Container onlySamplesContainer = rf.createListContainer("test container", user);
		onlySamplesContainer.setCanStoreContainers(false);
		savedContainer = dao.save(onlySamplesContainer, Container.class);
		assertFalse(savedContainer.isCanStoreContainers());
		assertTrue(savedContainer.isCanStoreSamples());
		
		// both can't be set to false
		Container noValidContentContainer = rf.createListContainer("test container", user);
		noValidContentContainer.setCanStoreContainers(false);
		noValidContentContainer.setCanStoreSamples(false);
		ConstraintViolationException cve = assertThrows(ConstraintViolationException.class, ()-> dao.save(noValidContentContainer, Container.class));
		assertEquals("Container cannot have both canStoreSamples and canStoreContainers set to false", cve.getMessage());
		
		// create container which holds both sample and container
		Container allContentContainer =  rf.createListContainer("test container", user);
		allContentContainer.addToNewLocation(savedContainer);
		allContentContainer.addToNewLocation(subSample);
		dao.save(allContentContainer, Container.class);
		assertEquals(2, allContentContainer.getLocations().size());
		
		// try disabling either canStoreSamples or canStoreContainers flag
		allContentContainer.setCanStoreSamples(false);
		cve = assertThrows(ConstraintViolationException.class, ()-> dao.save(allContentContainer, Container.class));
		assertEquals("Container has canStoreSamples set to false, but also has a stored subsample", cve.getMessage());
		allContentContainer.setCanStoreSamples(true);
		allContentContainer.setCanStoreContainers(false);
		cve = assertThrows(ConstraintViolationException.class, ()-> dao.save(allContentContainer, Container.class));
		assertEquals("Container has canStoreContainers set to false, but also has a stored container", cve.getMessage());
	}

	@Test
	public void createGridContainer() {
		User user = createAndSaveAnyUser();
		
		Container container = rf.createListContainer("test grid container", user);
		container.configureAsGridLayoutContainer(2, 1);
		container.setGridLayoutColumnsLabelType(GridLayoutAxisLabelEnum.CBA);
		container.setGridLayoutRowsLabelType(GridLayoutAxisLabelEnum.N321);
		Container savedContainer = dao.save(container, Container.class);
		
		Container loadedContainer = dao.load(savedContainer.getId(), Container.class);
		assertTrue(loadedContainer.isGridLayoutContainer());
		assertEquals(GridLayoutAxisLabelEnum.CBA, loadedContainer.getGridLayoutColumnsLabelType());
		assertEquals(GridLayoutAxisLabelEnum.N321, loadedContainer.getGridLayoutRowsLabelType());
		
		// check grid layout parameter validation
		Container invalidContainer = rf.createListContainer("invalid grid params container", user);
		invalidContainer.configureAsGridLayoutContainer(2, -1); // negative incorrect
		assertThrows(ConstraintViolationException.class, () -> dao.save(invalidContainer, Container.class));
		
		invalidContainer.configureAsGridLayoutContainer(25, 2); // more than size limit 24x24
		assertThrows(ConstraintViolationException.class, () -> dao.save(invalidContainer, Container.class));

		invalidContainer.configureAsGridLayoutContainer(24, 2); // should work now
		Container savedContainer2 = dao.save(invalidContainer, Container.class);
		assertTrue(savedContainer2.isGridLayoutContainer()); 
	}
	
	@Test()
	@DisplayName("Deep copy of image container and locations")
	void containerCopy() throws IOException {
		User user = createAndSaveAnyUser();
		Container container = TestFactory.createImageContainer(user);
		saveImageFileProperties(container);
		container = dao.save(container, Container.class);
		
		Container copy = container.copy(testUser);
		copy = dao.save(copy, Container.class);
		assertFalse(copy.getId().equals(container.getId()));
		assertTrue(copy.getLocations().size() > 0);
		
		assertEquals (container.getLocations().size(), copy.getLocations().size());
	}

	@Test
	@DisplayName("Create list of materials for field")
	void listOfMaterialsCreation() throws IOException {
		
		User user = createAndSaveAnyUser();
		RSForm form = createAForm(user);
		form = dao.save(form, RSForm.class);
		TextFieldForm tff = TestFactory.createTextFieldForm();
		form.addFieldForm(tff);
		dao.save(tff, TextFieldForm.class);
		
		Field textField = tff.createNewFieldFromForm();
		textField = dao.save(textField, Field.class);

		// create lom with sample and subsample
		Sample sample = TestFactory.createBasicSampleInContainer(user);
		SubSample subSample = sample.getActiveSubSamples().get(0);
		sample = saveSampleInContainer(sample);

		ListOfMaterials lom = rf.createListOfMaterials("test list", textField);
		lom.addMaterial(sample, null);
		lom.addMaterial(subSample, subSample.getQuantity());
		ListOfMaterials savedLom = dao.save(lom, ListOfMaterials.class);
		assertNotNull(savedLom.getId());
		assertEquals(GlobalIdPrefix.LM, savedLom.getOid().getPrefix());
		assertEquals(2, savedLom.getMaterials().size());
		assertEquals(GlobalIdPrefix.SA, savedLom.getMaterials().get(0).getInventoryRecord().getOid().getPrefix());
		assertEquals(GlobalIdPrefix.SS, savedLom.getMaterials().get(1).getInventoryRecord().getOid().getPrefix());
		assertNotNull(savedLom.getMaterials().get(1).getUsedQuantity());
		
		// try saving material usage
		Container container = rf.createListContainer("test container", user);
		Container savedContainer = dao.save(container, Container.class);
		MaterialUsage mu = rf.createMaterialUsage(savedLom, savedContainer, null);
		MaterialUsage savedMu = dao.save(mu, MaterialUsage.class);
		assertNotNull(savedMu.getId());

		// check container usage is saved
		ListOfMaterials reloadedLom = dao.load(savedLom.getId(), ListOfMaterials.class,  lomToInit -> { lomToInit.getMaterials().size(); });
		assertEquals(3, reloadedLom.getMaterials().size());
		assertEquals(GlobalIdPrefix.IC, reloadedLom.getMaterials().get(2).getInventoryRecord().getOid().getPrefix());

		// remove one of the usages
		reloadedLom.getMaterials().remove(1);
		dao.update(reloadedLom, ListOfMaterials.class);
		reloadedLom = dao.load(savedLom.getId(), ListOfMaterials.class,  lomToInit -> { lomToInit.getMaterials().size(); });
		assertEquals(2, reloadedLom.getMaterials().size());

		// try saving incorrect material usage - linked to both sample and container
		MaterialUsage incorrectMU = rf.createMaterialUsage(savedLom, savedContainer, null);
		incorrectMU.setInventoryRecord(sample);
		ConstraintViolationException cve = assertThrows(ConstraintViolationException.class, 
				()-> dao.save(incorrectMU, MaterialUsage.class));
		assertEquals("MaterialUsage must be connected to exactly one inventory record", cve.getMessage());
	}
	
	@Test
	public void createWorkbenchContainer() {
		User user = createAndSaveAnyUser();
		
		Container workbench = rf.createWorkbench(user);
		Container subContainer = rf.createListContainer("test subcontainer #1", user);
		subContainer.moveToNewParent(workbench);
		Container savedWorkbench = dao.save(workbench, Container.class);
		
		Container loadedWorkbench = dao.load(savedWorkbench.getId(), Container.class);
		assertEquals("WB " + user.getUsername(), loadedWorkbench.getName());
		assertEquals(1, loadedWorkbench.getContentCount());
	}
	
	@Test 
	public void createSampleWithAttachedFiles() throws IOException {
		User u = createAndSaveAnyUser();

		// save gallery document file
		FileProperty galleryFileFP = TestFactory.createAnyTransientFileProperty(u);
		dao.save(galleryFileFP.getRoot(), FileStoreRoot.class);
		galleryFileFP = dao.save(galleryFileFP, FileProperty.class);
		EcatDocumentFile connectedMediaFile = TestFactory.createEcatDocument(10L, u);
		connectedMediaFile.setFileProperty(galleryFileFP);
		connectedMediaFile = dao.save(connectedMediaFile, EcatDocumentFile.class);

		Sample sample = TestFactory.createBasicSampleInContainer(u);
		// attach a standalone file
		FileProperty standaloneFileFP = TestFactory.createAnyTransientFileProperty(u);
		InventoryFile standaloneInvFile = rf.createInventoryFile("testFileName.txt", standaloneFileFP, u);
		sample.addAttachedFile(standaloneInvFile);

		// attach a gallery file
		InventoryFile attachedGalleryFile = new InventoryFile(connectedMediaFile);
		sample.addAttachedFile(attachedGalleryFile);

		// save sample
		saveAssociatedEntities(sample);
		sample = saveSampleInContainer(sample);

		// verify
		assertNotNull(sample.getId());
		assertEquals(2, sample.getAttachedFiles().size());
		InventoryFile savedInvFile = sample.getAttachedFiles().get(0);
		assertNotNull(savedInvFile.getId());
		assertNull(savedInvFile.getMediaFileGlobalIdentifier());
		InventoryFile savedGalleryAttachmentFile = sample.getAttachedFiles().get(1);
		assertNotNull(savedGalleryAttachmentFile.getId());
		assertNotNull(savedGalleryAttachmentFile.getMediaFileGlobalIdentifier());
	}

	@Test 
	public void createSampleWithFieldAttachment() throws IOException {
		User u = createAndSaveAnyUser();

		FileProperty f1 = TestFactory.createAnyTransientFileProperty(u);
		InventoryFile invFile = rf.createInventoryFile("testFileName.txt", f1, u);

		Sample sample = TestFactory.createComplexSampleInContainer(u);
		saveParentTemplateForSample(sample);
		
		InventoryAttachmentField attachmentField = (InventoryAttachmentField) sample.getActiveFields().get(6);
		assertNull(attachmentField.getAttachedFile());
		attachmentField.setFieldData("attachment description");
		attachmentField.setAttachedFile(invFile);

		// save sample
		saveAssociatedEntities(sample);
		sample = saveSampleInContainer(sample);
		
		assertNotNull(sample.getId());
		InventoryFile savedInvFile = sample.getActiveFields().get(6).getAttachedFile();
		assertNotNull(savedInvFile);
		assertNotNull(savedInvFile.getId());
	}

	@Test
	public void createSampleWithDoiIdentifier() throws IOException {
		User u = createAndSaveAnyUser();

		Sample sample = TestFactory.createBasicSampleInContainer(u);
		DigitalObjectIdentifier igsnIdentifier = rf.createDoiIdentifier("IGSN01");
		sample.addIdentifier(igsnIdentifier);

		// save sample
		saveAssociatedEntities(sample);
		sample = saveSampleInContainer(sample);

		assertNotNull(sample.getId());
		assertEquals(1, sample.getActiveIdentifiers().size());
		DigitalObjectIdentifier savedIgsn = sample.getActiveIdentifiers().get(0);
		assertNotNull(savedIgsn.getId());
		assertEquals(DigitalObjectIdentifier.IdentifierType.DATACITE_IGSN, savedIgsn.getType());
		assertNull(savedIgsn.getOtherData(DigitalObjectIdentifier.IdentifierOtherProperty.PUBLISHER));
	}	
	
	@Test
	public void cannotSaveSampleAttachmentFieldWithTwoActiveAttachments() throws IOException {
		User u = createAndSaveAnyUser();

		FileProperty f1 = TestFactory.createAnyTransientFileProperty(u);
		InventoryFile invFile = rf.createInventoryFile("testFileName.txt", f1, u);
		FileProperty f2 = TestFactory.createAnyTransientFileProperty(u);
		InventoryFile invFile2 = rf.createInventoryFile("testFileName2.txt", f2, u);
		
		Sample sample = TestFactory.createComplexSampleInContainer(u);
		saveParentTemplateForSample(sample);
		
		InventoryAttachmentField attachmentField = (InventoryAttachmentField) sample.getActiveFields().get(6);
		assertNull(attachmentField.getAttachedFile());
		attachmentField.setFieldData("attachment description");
		attachmentField.setAttachedFile(invFile);
		attachmentField.setAttachedFile(invFile2);
		
		// save connected entities
		dao.save(invFile.getFileProperty().getRoot(), FileStoreRoot.class);
		dao.save(invFile.getFileProperty(), FileProperty.class);
		saveAssociatedEntities(sample);

		// for a test, change 1st attachment to be undeleted
		assertTrue(invFile.isDeleted());
		invFile.setDeleted(false);

		// try saving the sample with field having 2 non-deleted attachments
		ConstraintViolationException cve = assertThrows(ConstraintViolationException.class, () -> saveSampleInContainer(sample));
		assertEquals("Inventory attachment field can link only one attachment", cve.getMessage());
	}
	
	@Test
	public void createDMP() throws IOException {
		User u = createAndSaveAnyUser();
		DMP dmp = new DMP("https://doi/dmp/12345","A title");
		DMPUser dmpUser = new DMPUser(u, dmp);
		DMPUser saved = dao.save(dmpUser, DMPUser.class);
		assertNotNull(saved.getId());
	}

	@Test
	@DisplayName("Create inventory basket, add and remove items")
	void basketOperations() throws IOException {

		User user = createAndSaveAnyUser();
		Sample sample = TestFactory.createBasicSampleInContainer(user);
		SubSample subSample = sample.getActiveSubSamples().get(0);
		sample = saveSampleInContainer(sample);

		Basket basket = rf.createBasket("test basket", user);
		basket.addInventoryItem(sample);
		Basket savedBasket = dao.save(basket, Basket.class);
		assertNotNull(savedBasket.getId());
		assertEquals(GlobalIdPrefix.BA, savedBasket.getOid().getPrefix());
		assertEquals(1, savedBasket.getItems().size());
		assertEquals(1, savedBasket.getItemCount());
		assertEquals(GlobalIdPrefix.SA, savedBasket.getItems().get(0).getInventoryRecord().getOid().getPrefix());

		// try adding another item
		boolean addResult = basket.addInventoryItem(sample.getSubSamples().get(0));
		assertTrue(addResult);
		dao.update(basket, Basket.class);
		
		// check new item is present in basket
		Basket reloadedBasket = dao.load(savedBasket.getId(), Basket.class, basketToInit -> { basketToInit.getItems().size(); });
		assertEquals(2, reloadedBasket.getItems().size());
		assertEquals(2, reloadedBasket.getItemCount());
		assertEquals(GlobalIdPrefix.SA, reloadedBasket.getItems().get(0).getInventoryRecord().getOid().getPrefix());
		assertEquals(GlobalIdPrefix.SS, reloadedBasket.getItems().get(1).getInventoryRecord().getOid().getPrefix());

		// remove first item
		boolean removeResult = reloadedBasket.removeInventoryItem(sample);
		assertTrue(removeResult);
		dao.update(reloadedBasket, Basket.class);
		reloadedBasket = dao.load(savedBasket.getId(), Basket.class, basketToInit -> { basketToInit.getItems().size(); });
		assertEquals(1, reloadedBasket.getItems().size());
		assertEquals(1, reloadedBasket.getItemCount());
		assertEquals(GlobalIdPrefix.SS, reloadedBasket.getItems().get(0).getInventoryRecord().getOid().getPrefix());
	}

	@Test
	@DisplayName("Save and read sharing permissions")
	void sharingModeAndPermissions() throws IOException {

		User user = createAndSaveAnyUser();
		Sample sample = TestFactory.createBasicSampleInContainer(user);
		sample = saveSampleInContainer(sample);
		assertEquals(InventorySharingMode.OWNER_GROUPS, sample.getSharingMode());
		assertNull(sample.getSharingACL());
		// subsample inherits sharing config of sample
		assertEquals(InventorySharingMode.OWNER_GROUPS, sample.getSubSamples().get(0).getSharingMode());
		assertEquals(sample.getSharingACL(), sample.getSubSamples().get(0).getSharingACL());
		
		sample.setSharingMode(InventorySharingMode.WHITELIST);
		sample.setSharingACL(RecordSharingACL.createACLForUserOrGroup(user.asUser(), PermissionType.WRITE));
		dao.update(sample, Sample.class);

		Sample reloadedSample = dao.load(sample.getId(), Sample.class);
		assertEquals(InventorySharingMode.WHITELIST, reloadedSample.getSharingMode());
		assertNotNull(reloadedSample.getSharingACL());
		assertEquals(user.getUsername() + "=RECORD:WRITE:", reloadedSample.getSharingACL().getString());
		assertEquals(1, reloadedSample.getSharedWithUniqueNames().size());
		assertEquals(user.getUsername(), reloadedSample.getSharedWithUniqueNames().get(0));
		assertEquals(user.getUsername(), reloadedSample.getSharedWithUniqueNamesString());
		// changing sample permissions is reflected on subsample permissions
		assertEquals(InventorySharingMode.WHITELIST, reloadedSample.getSubSamples().get(0).getSharingMode());
		assertEquals(reloadedSample.getSharingACL(), reloadedSample.getSubSamples().get(0).getSharingACL());

		sample.setSharingMode(InventorySharingMode.OWNER_ONLY);
		sample.setSharingACL(reloadedSample.getSharingACL());
		dao.update(sample, Sample.class);
		reloadedSample = dao.load(sample.getId(), Sample.class);
		assertEquals(InventorySharingMode.OWNER_ONLY, reloadedSample.getSharingMode());
		// changing sample permissions is reflected on subsample permissions
		assertEquals(InventorySharingMode.OWNER_ONLY, reloadedSample.getSubSamples().get(0).getSharingMode());
		assertEquals(reloadedSample.getSharingACL(), reloadedSample.getSubSamples().get(0).getSharingACL());
	}

	@Test
	@DisplayName("Save and read external storage location")
	void createExternalStorageLocation() {
		User operationUser = createAndSaveAnyUser();

		String testClientOptionsString =
				"IRODS_ZONE=tempZone\nIRODS_HOME_DIR=/tempZone/home/alice\nIRODS_PORT=1247\n";
		NfsFileSystem fileSystem = new NfsFileSystem();
		fileSystem.setName("fileSystem-name");
		fileSystem.setClientOptions(testClientOptionsString);
		fileSystem.setClientType(NfsClientType.IRODS);
		fileSystem.setUrl("Http://url.com");
		fileSystem.setAuthType(NfsAuthenticationType.PASSWORD);
		fileSystem.setDisabled(false);
		dao.save(fileSystem, NfsFileSystem.class);

		NfsFileStore fileStore = new NfsFileStore();
		fileStore.setDeleted(false);
		fileStore.setPath("/root/path");
		fileStore.setName("fileStore-name");
		fileStore.setUser(operationUser);
		fileStore.setFileSystem(fileSystem);
		dao.save(fileStore, NfsFileStore.class);



		EcatImage connectedMediaFile = TestFactory.createEcatImage(10L);
		connectedMediaFile.setOwner(operationUser);
		dao.save(connectedMediaFile, EcatImage.class);

		ExternalStorageLocation externalStorageLocation = new ExternalStorageLocation();
		externalStorageLocation.setExternalStorageId(1L);
		externalStorageLocation.setFileStore(fileStore);
		externalStorageLocation.setConnectedMediaFile(connectedMediaFile);
		externalStorageLocation.setOperationUser(operationUser);
		externalStorageLocation = dao.save(externalStorageLocation, ExternalStorageLocation.class);
		assertNotNull(externalStorageLocation.getId());
	}
	
}

