package mmd.model;

import java.util.List;
import mmd.factory.DmaFmtFactory;
import mmd.util.Util;

public class MMDAlgo {
	
	public static final String DMA_EXTENSION = ".dma";
	public static final String DMA_FMT_EXTENSION = ".dma_fmt";
	public static final String INPUT_DIR = "src/main/resources/test/input/";
	public static final String OUTPUT_DIR = "resources/test/output/";
	
	
	
	private String filename;
	
	public MMDAlgo(String filename) {
		this.filename = filename;
	}
	
	public void run() throws Exception {
		List<String> dmaFileLines = Util.readFile(INPUT_DIR, getDmaFile());
		List<String> dmaFileFmtLines = Util.readFile(INPUT_DIR, getDmaFmtFile());
		DmaFmtFactory.parse(dmaFileFmtLines);
		checkRequiredData();
		ModuleFactory.parse(dmaFileLines);
		ModuleFactory.updateModuleDistances();
		// ModuleFactory.printModuleInfo();
		ModuleFactory.generateDotFileText();
		ModuleFactory.updateModuleConnections();
		ModuleFactory.createXmlFile(filename); 
	}

	private void checkRequiredData() throws Exception {
		if (!DmaFmtFactory.moduleNameFormatExists()) {
			throw new Exception("module name format missing in dma_fmt file.");
		} else if (!DmaFmtFactory.metricFormatExists()) {
			throw new Exception("no metric data exists in the file.");
		}
	}

	private String getDmaFile() {
		return filename + DMA_EXTENSION;
	}
	
	private String getDmaFmtFile() {
		return filename + DMA_FMT_EXTENSION;
	}
	
}
