package mmd.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Util {
	
	private Util() {}
	
	
	public static List<String> readFile(String dir, String file) throws IOException { 
		Path path = Paths.get(dir, file);
		return Files.readAllLines(path);
	}
	
}
