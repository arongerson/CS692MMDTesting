package mmd.factory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import mmd.model.FormatTypes;

public class DmaFmtFactory {
	
	private static List<FormatTypes.DmaFmt> formatTypes = new ArrayList<>();
	private static int moduleNameIndex = -1;
	public static int numberOfFields = -1;
	private static StringBuffer distanceIndicesText = new StringBuffer();
	public static int[] distanceIndices;
	private static boolean metricExists = false;
	
	private DmaFmtFactory() {	
	}
	
	public static void parse(List<String> dmaFileFmtLines) throws Exception {  
		numberOfFields = dmaFileFmtLines.size();
		for (int i = 0; i < dmaFileFmtLines.size(); i++) {
			createDmaFmt(dmaFileFmtLines.get(i), i);
		}
		// remove the trailing comma in the distance indices
		distanceIndicesText.deleteCharAt(distanceIndicesText.length() - 1);
		// create distance indices array
		distanceIndices = Arrays.stream(distanceIndicesText.toString().split(",")).mapToInt(Integer::parseInt).toArray();
		distanceIndicesText = null;
	}
	
	private static void createDmaFmt(String dmaFmtLine, int index) throws Exception {
		String[] tokens = dmaFmtLine.split(" ");
		checkBlankLine(tokens);
		String firstToken = tokens[0].trim();
		if (firstToken.equalsIgnoreCase(FormatTypes.NAME)) {
			addNameFormat(tokens, index);
		} else if (firstToken.equalsIgnoreCase(FormatTypes.METRIC)) {
			addMetricType(tokens, index);
			distanceIndicesText.append(index).append(",");
			metricExists = true;
		} else if (firstToken.equalsIgnoreCase(FormatTypes.XMLTAG)) {
			addMetricXmlTag(tokens, index);
			distanceIndicesText.append(index).append(",");
		} else if (isInteger01Type(tokens[0])) {
			addInteger01Format(tokens, index);
		} else if (isIntegerType(tokens[0])) {
			addIntegerFormat(tokens, index);
		}  else {
			// other
			addOtherFormat(tokens, index);
		}
	}

	private static void addInteger01Format(String[] tokens, int index) throws Exception {
		if (tokens.length != 1) {
			throw new Exception("incorrect format in the line with the name format type");
		} else if (formatTypeExists(tokens[0].trim())) {
			throw new Exception("the format type already exists");
		}
		FormatTypes.DmaFmt dmaFmt = new FormatTypes.Integer01Format(tokens[0].trim(), index);
		formatTypes.add(dmaFmt);
	}
	
	private static void addIntegerFormat(String[] tokens, int index) throws Exception {
		if (tokens.length != 1) {
			throw new Exception("incorrect format in the line with the name format type");
		} else if (formatTypeExists(tokens[0].trim())) {
			throw new Exception("the format type already exists");
		}
		FormatTypes.DmaFmt dmaFmt = new FormatTypes.IntegerFormat(tokens[0].trim(), index);
		formatTypes.add(dmaFmt);
	}

	private static boolean isInteger01Type(String token) {
		return token.equalsIgnoreCase(FormatTypes.CLONE_MIXED_TYPE) || token.equalsIgnoreCase(FormatTypes.CLONE_TYPE) || token.equalsIgnoreCase(FormatTypes.CLONE)
				|| token.equalsIgnoreCase(FormatTypes.VULNERABILITY);
	}
	
	private static boolean isIntegerType(String token) {
		return token.equalsIgnoreCase(FormatTypes.INT) || token.equalsIgnoreCase(FormatTypes.ENDING_LINENO) || token.equalsIgnoreCase(FormatTypes.CWE)
				|| token.equalsIgnoreCase(FormatTypes.CHANGE) || token.equalsIgnoreCase(FormatTypes.EFFORT) || token.equalsIgnoreCase(FormatTypes.BEGINNING_LINENO);
	}

	private static void addOtherFormat(String[] tokens, int index) throws Exception { 
		if (tokens.length != 1) {
			throw new Exception("incorrect format in the line with the name format type");
		} else if (formatTypeExists(tokens[0].trim())) {
			throw new Exception("the format type already exists");
		}
		FormatTypes.DmaFmt dmaFmt = new FormatTypes.OtherFormat(tokens[0].trim(), index);
		formatTypes.add(dmaFmt);
	}

	private static void addMetricXmlTag(String[] tokens, int index) throws Exception { 
		if (tokens.length != 2) {
			throw new Exception("incorrect format in the line with the name format type");
		} else if (xmlTagExists( tokens[1])) {
			throw new Exception("the format type already exists");
		}
		FormatTypes.DmaFmt dmaFmt = new FormatTypes.XmlFormat(tokens[0].trim(), tokens[1].trim(), index);
		formatTypes.add(dmaFmt);
	}

	private static boolean xmlTagExists(String value) {
		for (FormatTypes.DmaFmt dmaFmt : formatTypes) {
			if (dmaFmt instanceof FormatTypes.XmlFormat) {
				if (dmaFmt.getValue().equalsIgnoreCase(value)) {
					return true;
				}
			}
		}
		return false;
	}

	private static void addMetricType(String[] tokens, int index) throws Exception {
		if (tokens.length != 2) {
			throw new Exception("incorrect format in the line with the name format type");
		} else if (metricExists( tokens[1].trim())) {
			throw new Exception("the format type already exists");
		}
		FormatTypes.DmaFmt dmaFmt = new FormatTypes.MetricFormat(tokens[0].trim(), tokens[1].trim(), index);
		formatTypes.add(dmaFmt);
	}

	private static boolean metricExists(String value) {
		for (FormatTypes.DmaFmt dmaFmt : formatTypes) {
			if (dmaFmt instanceof FormatTypes.MetricFormat) {
				if (dmaFmt.getValue().equalsIgnoreCase(value)) {
					return true;
				}
			}
		}
		return false;
	}

	private static void addNameFormat(String[] tokens, int index) throws Exception { 
		if (tokens.length != 1) {
			throw new Exception("incorrect format in the line with the name format type");
		} else if (moduleNameIndex != -1) {
			throw new Exception("the format type already exists");
		}
		FormatTypes.DmaFmt dmaFmt = new FormatTypes.NameFormat(tokens[0].trim(), index);
		moduleNameIndex = index;
		formatTypes.add(dmaFmt);
	}

	private static void checkBlankLine(String[] tokens) throws Exception {
		// check if the line is blank
		if (tokens.length == 0) {
			throw new Exception("Blank line not allowed");
		}
	}
	
	private static boolean formatTypeExists(String formatType) {
		for (FormatTypes.DmaFmt dmaFma : formatTypes) {
			if (dmaFma.getKey().equalsIgnoreCase(formatType)) {
				return true;
			}
		}
		return false;
	}
	
	public static void validate(int index, String value) throws Exception { 
		formatTypes.get(index).validate(value); 
	}
	
	public static int getModuleNameIndex() {
		return moduleNameIndex;
	}
	
	public static boolean moduleNameFormatExists() {
		return moduleNameIndex != -1;
	}
	
	public static boolean metricFormatExists() {
		return metricExists;
	}

	public static String getDistanceFormatName(int index) {
		FormatTypes.DmaFmt dmaFmt = formatTypes.get(index);
		return dmaFmt.getValue();
	}
}
