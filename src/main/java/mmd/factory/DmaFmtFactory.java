package mmd.factory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import mmd.model.FormatTypes;

public class DmaFmtFactory {
	
	private List<FormatTypes.DmaFmt> formatTypes = new ArrayList<>();
	public int numberOfFields = -1;
	private StringBuffer distanceIndicesText = new StringBuffer();
	public int[] distanceIndices;
	private boolean metricExists = false;
	
	private int typeCount = 0;
	private int type2Count = 0;
	private int xmiIdCount = 0;
	private int enumCount = 0;
	private int enum2Count = 0;
	private int cloneMixedTypeCount = 0;
	private int endingLinenoCount = 0;
	private int cweCount = 0;
	private int testNumberCount = 0;
	private int cloneTypeCount = 0;
	private int cloneIdCount = 0;
	private int nameCount = 0;
	private int idCount = 0;
	private int filenameCount = 0;
	private int changeCount = 0;
	private int categoryCount = 0;
	private int signatureCount = 0;
	private int dmaIdCount = 0;
	private int effortCount = 0;
	private int cloneCount = 0;
	private int beginningLinenoCount = 0;
	private int vulnerabilityCount = 0;
	private int benchmarkFilenameCount = 0;
	private int benchmarkVersionCount = 0;
	
	private int xmiIdIndex = -1;
	private int testNumberIndex = -1;
	private int idIndex = -1;
	private int cloneIdIndex = -1;
	private int dmaIdIndex = -1;
	private int moduleNameIndex = -1;
	private int vulnerabilityIndex = -1;
	
	public DmaFmtFactory(List<String> dmaFileFmtLines) throws Exception {	 
		parse(dmaFileFmtLines);
	}
	
	public final void parse(List<String> dmaFileFmtLines) throws Exception {  
		numberOfFields = dmaFileFmtLines.size();
		for (int i = 0; i < dmaFileFmtLines.size(); i++) {
			createDmaFmt(dmaFileFmtLines.get(i), i);
		}
		createDistanceIndicesArray();
	}

	private void createDistanceIndicesArray() {
		if (!distanceIndicesText.toString().isEmpty()) {
			// remove the trailing comma in the distance indices
			distanceIndicesText.deleteCharAt(distanceIndicesText.length() - 1);
			// create distance indices array
			distanceIndices = Arrays.stream(distanceIndicesText.toString().split(",")).mapToInt(Integer::parseInt).toArray();
			distanceIndicesText = null;
		}
	}
	
	private void createDmaFmt(String dmaFmtLine, int index) throws Exception {
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
		} else if (isInteger01Type(tokens[0], index)) {
			addInteger01Format(tokens, index);
		} else if (isIntegerType(tokens[0])) {
			addIntegerFormat(tokens, index);
		}  else if (isStringType(tokens[0], index)) {
			addOtherFormat(tokens, index);
		} else {
			addOtherFormat(tokens, index);
		}
	}

	private void addInteger01Format(String[] tokens, int index) throws Exception {
		if (tokens.length != 1) {
			throw new Exception("incorrect format in the line with the name format type");
		} else if (formatTypeExists(tokens[0].trim())) {
			throw new Exception("the format type already exists");
		}
		FormatTypes.DmaFmt dmaFmt = new FormatTypes.Integer01Format(tokens[0].trim(), index);
		formatTypes.add(dmaFmt);
	}
	
	private void addIntegerFormat(String[] tokens, int index) throws Exception {
		if (tokens.length != 1) {
			throw new Exception("incorrect format in the line with the name format type");
		} else if (formatTypeExists(tokens[0].trim())) {
			throw new Exception("the format type already exists");
		}
		FormatTypes.DmaFmt dmaFmt = new FormatTypes.IntegerFormat(tokens[0].trim(), "", index);
		formatTypes.add(dmaFmt);
	}

	private boolean isInteger01Type(String token, int index) {
		if (token.equalsIgnoreCase(FormatTypes.CLONE_MIXED_TYPE)) {
			cloneMixedTypeCount++;
		} else if ( token.equalsIgnoreCase(FormatTypes.CLONE_TYPE)) {
			cloneTypeCount++;
		} else if (token.equalsIgnoreCase(FormatTypes.CLONE)) {
			cloneCount++;
		} else if (token.equalsIgnoreCase(FormatTypes.VULNERABILITY)) {
			vulnerabilityCount++;
			vulnerabilityIndex = index;
		} else {
			return false;
		}
		return true;
	}
	
	private boolean isIntegerType(String token) {
		if (token.equalsIgnoreCase(FormatTypes.ENDING_LINENO)) {
			endingLinenoCount++;
		} else if (token.equalsIgnoreCase(FormatTypes.CWE)) {
			cweCount++;
		} else if (token.equalsIgnoreCase(FormatTypes.CHANGE)) {
			changeCount++;
		} else if (token.equalsIgnoreCase(FormatTypes.EFFORT)) {
			effortCount++;
		} else if (token.equalsIgnoreCase(FormatTypes.BEGINNING_LINENO)) {
			beginningLinenoCount++;
		} else {
			return false;
		}
		return true;
	}
	
	private boolean isStringType(String token, int index) {
		if (token.equalsIgnoreCase(FormatTypes.TYPE)) {
			typeCount++;
		} else if (token.equalsIgnoreCase(FormatTypes.TYPE2)) {
			type2Count++;
		} else if (token.equalsIgnoreCase(FormatTypes.XMI_ID)) {
			xmiIdCount++;
			xmiIdIndex = index;
		} else if (token.equalsIgnoreCase(FormatTypes.ENUM)) {
			enumCount++;
		} else if (token.equalsIgnoreCase(FormatTypes.ENUM2)) {
			enum2Count++;
		} else if (token.equalsIgnoreCase(FormatTypes.TEST_NUMBER)) {
			testNumberCount++;
			testNumberIndex = index;
		} else if (token.equalsIgnoreCase(FormatTypes.ID)) {
			idCount++;
			idIndex = index;
		} else if (token.equalsIgnoreCase(FormatTypes.FILENAME)) {
			filenameCount++;
		} else if (token.equalsIgnoreCase(FormatTypes.CLONE_ID)) {
			cloneIdCount++;
			cloneIdIndex = index;
		} else if (token.equalsIgnoreCase(FormatTypes.CATEGORY)) {
			categoryCount++;
		} else if (token.equalsIgnoreCase(FormatTypes.SIGNATURE)) {
			signatureCount++;
		} else if (token.equalsIgnoreCase(FormatTypes.DMA_ID)) {
			dmaIdCount++;
			dmaIdIndex = index;
		} else if (token.equalsIgnoreCase(FormatTypes.BENCHMARK_FILENAME)) {
			benchmarkFilenameCount++;
		} else if (token.equalsIgnoreCase(FormatTypes.BENCHMARK_VERSION)) {
			benchmarkVersionCount++;
		} else {
			return false;
		}
		return true;
	}


	private void addOtherFormat(String[] tokens, int index) throws Exception { 
		if (tokens.length != 1) {
			throw new Exception("incorrect format in the line with the name format type");
		} else if (formatTypeExists(tokens[0].trim())) {
			throw new Exception("the format type already exists");
		}
		FormatTypes.DmaFmt dmaFmt = new FormatTypes.StringFormat(tokens[0].trim(), index);
		formatTypes.add(dmaFmt);
	}

	private void addMetricXmlTag(String[] tokens, int index) throws Exception { 
		if (tokens.length != 2) {
			throw new Exception("incorrect format in the line with the name format type");
		} else if (xmlTagExists( tokens[1])) {
			throw new Exception("the format type already exists");
		}
		FormatTypes.DmaFmt dmaFmt = new FormatTypes.XmlFormat(tokens[0].trim(), tokens[1].trim(), index);
		formatTypes.add(dmaFmt);
	}

	private boolean xmlTagExists(String value) {
		for (FormatTypes.DmaFmt dmaFmt : formatTypes) {
			if (dmaFmt instanceof FormatTypes.XmlFormat) {
				if (dmaFmt.getValue().equalsIgnoreCase(value)) {
					return true;
				}
			}
		}
		return false;
	}

	private void addMetricType(String[] tokens, int index) throws Exception {
		if (tokens.length != 2) {
			throw new Exception("incorrect format in the line with the name format type");
		} else if (metricExists( tokens[1].trim())) {
			throw new Exception("metric " + tokens[1] + " exists more than once in the dma_fmt file.");
		}
		FormatTypes.DmaFmt dmaFmt = new FormatTypes.MetricFormat(tokens[0].trim(), tokens[1].trim(), index);
		formatTypes.add(dmaFmt);
	}

	private boolean metricExists(String value) {
		for (FormatTypes.DmaFmt dmaFmt : formatTypes) {
			if (dmaFmt instanceof FormatTypes.MetricFormat) {
				if (dmaFmt.getValue().equalsIgnoreCase(value)) {
					return true;
				}
			}
		}
		return false;
	}

	private void addNameFormat(String[] tokens, int index) throws Exception { 
		if (tokens.length != 1) {
			throw new Exception("incorrect format in the line with the name format type");
		} else if (moduleNameIndex != -1) {
			throw new Exception(String.format("the format type %s already exists", "NAME"));
		}
		FormatTypes.DmaFmt dmaFmt = new FormatTypes.NameFormat(tokens[0].trim(), index);
		moduleNameIndex = index;
		formatTypes.add(dmaFmt);
		nameCount++;
	}

	private void checkBlankLine(String[] tokens) throws Exception {
		// check if the line is blank
		if (tokens.length == 0) {
			throw new Exception("Blank line not allowed");
		}
	}
	
	private boolean formatTypeExists(String formatType) {
		for (FormatTypes.DmaFmt dmaFma : formatTypes) {
			if (dmaFma.getKey().equalsIgnoreCase(formatType)) {
				return true;
			}
		}
		return false;
	}
	
	public void validate(int index, String value) throws Exception { 
		formatTypes.get(index).validate(value); 
	}
	
	public int getModuleNameIndex() {
		return moduleNameIndex;
	}
	
	public boolean moduleIdentifierFieldExists() {
		return moduleNameIndex != -1 || xmiIdIndex != -1 || testNumberIndex != -1 || idIndex != -1 || cloneIdIndex != -1 || dmaIdIndex != -1;
	}
	
	public boolean isValidType2() {
		System.out.println(String.format("typeCount: %s, typeCount2: %s", typeCount, type2Count));
		return (type2Count == 0) ? true : typeCount != 0;
	}
	
	public boolean metricFormatExists() {
		return metricExists;
	}

	public String getDistanceFormatName(int index) {
		FormatTypes.DmaFmt dmaFmt = formatTypes.get(index);
		return dmaFmt.getValue();
	}

	public void validateAttributeCount() throws Exception {
		int[] counts = {
				typeCount, type2Count, xmiIdCount, enumCount, enum2Count, cloneMixedTypeCount, endingLinenoCount, cweCount, testNumberCount, cloneTypeCount,
				nameCount, idCount, filenameCount, changeCount, cloneIdCount, categoryCount, signatureCount, 
				dmaIdCount, effortCount, cloneCount, beginningLinenoCount, vulnerabilityCount, benchmarkFilenameCount, benchmarkVersionCount};
		String[] attributeNames = {
				FormatTypes.TYPE, FormatTypes.TYPE2, FormatTypes.XMI_ID, FormatTypes.ENUM, FormatTypes.ENUM2, FormatTypes.CLONE_MIXED_TYPE, FormatTypes.ENDING_LINENO, FormatTypes.CWE,
				FormatTypes.TEST_NUMBER, FormatTypes.CLONE_TYPE, FormatTypes.NAME, FormatTypes.ID, FormatTypes.FILENAME, FormatTypes.CHANGE, FormatTypes.CLONE_ID, FormatTypes.CATEGORY,
				FormatTypes.SIGNATURE, FormatTypes.DMA_ID, FormatTypes.EFFORT, FormatTypes.CLONE, FormatTypes.BEGINNING_LINENO, FormatTypes.VULNERABILITY, FormatTypes.BENCHMARK_FILENAME,
				FormatTypes.BENCHMARK_VERSION
				};
		for (int i = 0; i < counts.length; i++) {
			if (counts[i] > 1) {
				throw new Exception("You have more than one " + attributeNames[i] + " attribute");
			}
		}
	}
	
	public int getVulnerabilityIndex() {
		return vulnerabilityIndex;
	}

}
