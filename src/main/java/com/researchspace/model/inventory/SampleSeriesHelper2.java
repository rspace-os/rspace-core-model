package com.researchspace.model.inventory;

import org.apache.commons.lang.StringUtils;
/**
 * Utility class for naming series of samples and subsamples.
 */
public class SampleSeriesHelper2 {
	
	/**  
	 * Returns subSampleName if provided, otherwise takes sampleName and add counter as a suffix 
	 */
	public static String getSerialNameForSubSample(String subSampleName, String sampleName, int currentCount, Integer totalCount) {
		if (StringUtils.isNotBlank(subSampleName)) {
			return subSampleName;
		}
		int suffixLength = Math.max(2, String.valueOf(totalCount).length()); 
		return String.format("%s.%0" +  suffixLength + "d", sampleName, currentCount);
	}

	/**  
	 * Returns sampleName with counter added as a suffix 
	 */
	public static String getSerialNameForSample(String sampleName, int currentCount, Integer totalCount) {
		int suffixLength = Math.max(2, String.valueOf(totalCount).length()); 
		return String.format("%s-%0" +  suffixLength + "d", sampleName, currentCount);
	}
}