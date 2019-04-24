package mmd.model;

import java.util.List;
import mmd.factory.DmaFmtFactory;
import mmd.util.Util;

public class MMDAlgo {
	
	public static final String DMA_EXTENSION = ".dma";
	public static final String DMA_FMT_EXTENSION = ".dma_fmt";
	public String input;
	public String output;
	
	
	
	private String filename;
	
	public MMDAlgo(String input, String filename, String output) {
		this.input = input;
		this.filename = filename;
		this.output = output;
		start();
	}
	
	private void start() {
		try {
			run();
		} catch (Exception e) {
			// show error message 
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
	}

	private final void run() throws Exception {
		List<String> dmaFileLines = Util.readFile(input, getDmaFile());
		validateFile(".dma", dmaFileLines);
		List<String> dmaFileFmtLines = Util.readFile(input, getDmaFmtFile());
		validateFile(".dma_fmt", dmaFileFmtLines);
		DmaFmtFactory dmaFmtFactory = new DmaFmtFactory(dmaFileFmtLines);
		validateDmaFmtFile(dmaFmtFactory);
		ModuleFactory.parse(dmaFileLines, dmaFmtFactory);
		ModuleFactory.updateModuleDistances();
		// ModuleFactory.printModuleInfo();
		ModuleFactory.generateDotFileText();
		ModuleFactory.updateModuleConnections();
		ModuleFactory.createXmlFile(filename); 
	}
	
	private void removeExtraBlankLines(List<String> dmaFileLines) {
		for (int i = dmaFileLines.size() - 1; i >= 0; i--) {
			if (dmaFileLines.get(i).trim().isEmpty()) {
				dmaFileLines.remove(i);
			} else {
				break;
			}
		}
	}

	private void validateFile(String fileType, List<String> lines) throws Exception {
		removeExtraBlankLines(lines);
		verifyFileNotEmpty(fileType, lines);
		// all the trailing blank lines have been removed, any other blank line is between lines with content, this should be an error
		for (String line: lines) {
			if (line.trim().isEmpty()) {
				throw new Exception(fileType + " has blank lines in it.");
			}
		}
	}

	private void verifyFileNotEmpty(String fileType, List<String> lines) throws Exception { 
		if (lines.isEmpty()) {
			throw new Exception(fileType + " is empty");
		}
	}

	private void validateDmaFmtFile(DmaFmtFactory dmaFmtFactory) throws Exception {
		if (!dmaFmtFactory.moduleIdentifierFieldExists()) {
			throw new Exception("module identifier format missing in dma_fmt file.");
		} else if (!dmaFmtFactory.metricFormatExists()) {
			throw new Exception("no metric data exists in the file.");
		} else if (!dmaFmtFactory.isValidType2()) {
			throw new Exception("TYPE2 attribute cannot exist without TYPE attribute.");
		} else {
			dmaFmtFactory.validateAttributeCount();
		}
	}

	private String getDmaFile() {
		return filename + DMA_EXTENSION;
	}
	
	private String getDmaFmtFile() {
		return filename + DMA_FMT_EXTENSION;
	}
	
}
