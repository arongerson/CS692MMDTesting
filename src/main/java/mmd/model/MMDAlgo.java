package mmd.model;

import java.io.IOException;
import java.util.List;

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
	
	public void run() throws IOException {
		List<String> dmaFileLines = Util.readFile(INPUT_DIR, getDmaFile());
		List<String> dmaFileFmtLines = Util.readFile(INPUT_DIR, getDmaFmtFile());
	}
	
	private String getDmaFile() {
		return filename + DMA_EXTENSION;
	}
	
	private String getDmaFmtFile() {
		return filename + DMA_FMT_EXTENSION;
	}
	
}
