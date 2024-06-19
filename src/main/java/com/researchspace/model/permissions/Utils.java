package com.researchspace.model.permissions;

class Utils {
	static void replaceTRailingSeparator(StringBuffer sb, String separator) {
		int indx = sb.lastIndexOf(separator);
		if (indx == sb.length() - 1) {
			sb.deleteCharAt(indx);
		}
	}
	
	static void replaceTRailingSeparator(StringBuilder sb, String separator) {
		int indx = sb.lastIndexOf(separator);
		if (indx == sb.length() - 1) {
			sb.deleteCharAt(indx);
		}
	}
}
