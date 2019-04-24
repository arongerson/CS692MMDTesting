package mmd.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Util {
	
	private Util() {}
	
	
	public static List<String> readFile(String dir, String filename) throws IOException { 
		File file = new File(dir + filename);
		if (file.exists()) {
			Path path = Paths.get(dir, filename);
			return Files.readAllLines(path);
		} else {
			throw new IOException(getFileExtension(filename) + " file does not exist.");
		}
	}
	
	public static String getFileExtension(String filename) {
		int index = filename.lastIndexOf(".");
		return filename.substring(index + 1);
	}

	public static String createOpenXmlTag(String xmlTagName) {
		return "<" + xmlTagName + ">";
	}
	
	public static String createCloseXmlTag(String xmlTagName) {
		return "</" + xmlTagName + ">";
	}
	
	public static void createTag(StringBuffer xmlText, String xmlTagName, String content) {
		xmlText.append(Util.createOpenXmlTag(xmlTagName));
		xmlText.append(content);
		xmlText.append(Util.createCloseXmlTag(xmlTagName));
	}
	
}
