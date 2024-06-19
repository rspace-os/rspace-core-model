package com.researchspace.model.inventory;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.Test;

public class SampleSeriesHelperTest {

	@Test
	public void testSampleSubSampleNaming() {

		assertEquals("sampleName-05", SampleSeriesHelper2.getSerialNameForSample("sampleName", 5, 5));
		assertEquals("sampleName-05", SampleSeriesHelper2.getSerialNameForSample("sampleName", 5, 25));
		assertEquals("sampleName-15", SampleSeriesHelper2.getSerialNameForSample("sampleName", 15, 99));
		assertEquals("sampleName-015", SampleSeriesHelper2.getSerialNameForSample("sampleName", 15, 100));
		assertEquals("sampleName-0015", SampleSeriesHelper2.getSerialNameForSample("sampleName", 15, 1500));
		
		assertEquals("sampleName.05", SampleSeriesHelper2.getSerialNameForSubSample("", "sampleName", 5, 5));
		assertEquals("sampleName-05.05", SampleSeriesHelper2.getSerialNameForSubSample("", "sampleName-05", 5, 5));
		assertEquals("customSubSampleName", SampleSeriesHelper2.getSerialNameForSubSample("customSubSampleName", "sampleName", 5, 5));
	}

}
