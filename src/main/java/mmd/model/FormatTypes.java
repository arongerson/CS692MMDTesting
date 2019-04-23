package mmd.model;

public class FormatTypes {
	
	public static final String NAME = "NAME";
	public static final String METRIC = "METRIC"; 
	public static final String XMLTAG = "XMLTAG";
	public static final String XMI_ID = "XMI_ID";
	public static final String INT = "INT";
	public static final String ENUM = "ENUM";
	public static final String ENUM2 = "ENUM2";
	public static final String CLONE_MIXED_TYPE = "CLONE_MIXED_TYPE";
	public static final String ENDING_LINENO = "ENDING_LINENO";
	public static final String CWE = "CWE";
	public static final String TEST_NUMBER = "TEST_NUMBER";
	public static final String CLONE = "CLONE";
	public static final String CLONE_ID = "CLONE_ID";
	public static final String CLONE_TYPE = "CLONE_TYPE";
	public static final String ID = "ID";
	public static final String FILENAME = "FILENAME";
	public static final String CHANGE = "CHANGE";
	public static final String CATEGORY = "CATEGORY";
	public static final String SIGNATURE = "SIGNATURE";
	public static final String TYPE = "TYPE";
	public static final String TYPE2 = "TYPE2";
	public static final String DMA_ID = "DMA_ID";
	public static final String CHAR = "CHAR";
	public static final String EFFORT = "EFFORT";
	public static final String BEGINNING_LINENO = "BEGINNING_LINENO";
	public static final String VULNERABILITY = "VULNERABILITY";
	public static final String BENCHMARK_FILENAME = "BENCHMARK_FILENAME";
	public static final String BENCHMARK_VERSION = "BENCHMARK_VERSION";
	public static final String OTHER = ""; 
	
	public static abstract class DmaFmt {
		
		protected String key;
		protected String value;
		protected int index;
		
		public DmaFmt(String key, String value, int index) {
			this.key = key;
			this.value = value;
			this.index = index;
		}
		
		public String getKey() {
			return key;
		}
		
		public String getValue() {
			return value;
		}
		
		public int getIndex() {
			return index;
		}
		
		public abstract void validate(String token) throws Exception ;
		
		public static void validateCount() throws Exception {}
	}
	
	public static class Integer01Format extends DmaFmt {
		public Integer01Format(String key, int index) {
			super(key, "", index); // the empty string is for value which for other formats is empty
		}
		
		public void validate(String text) throws Exception {
			int value = Integer.parseInt(text);
			if (value < 0 || value > 1) {
				throw new Exception("Only integers between 0 and 1 allowed.");
			}
		}
	}
	
	public static class IntegerFormat extends DmaFmt {
		public IntegerFormat(String key, String value, int index) {
			super(key, value, index); // the empty string is for value which for other formats is empty
		}
		
		public void validate(String text) throws Exception {
			int value = Integer.parseInt(text);
			if (value < 0 ) {
				throw new Exception("negative numbers not allowed.");
			}
		}
	}
	
	public static class StringFormat extends DmaFmt {
		public StringFormat(String key, int index) {
			super(key, "", index); // the empty string is for value which for other formats is empty
		}
		
		public void validate(String text) throws Exception {
			
		}
	}
	
	public static class NameFormat extends StringFormat {
		
		public NameFormat(String key, int index) {
			super(key, index);
		}
	}
	
	public static class MetricFormat extends IntegerFormat {
		
		public MetricFormat(String key, String value, int index) {
			super(key, value, index);
		}
		
		public void validate(String token) throws Exception {
			int metric = Integer.parseInt(token);
			if (metric < 0) {
				throw new Exception("invalid metric");
			}
		}
	}
	
	public static class XmlFormat extends IntegerFormat {
		
		public XmlFormat(String key, String value, int index) {
			super(key, value, index);
		}
		
		public void validate(String token) throws Exception {
			int metric = Integer.parseInt(token);
			if (metric < 0) {
				throw new Exception("invalid metric");
			}
		}
	}
}
