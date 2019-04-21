package mmd.model;

import java.util.ArrayList;
import java.util.List;

import mmd.factory.DmaFmtFactory;
import mmd.factory.XmlFactory;

public class ModuleFactory {
	private static List<Module> modules = new ArrayList<Module>();

	public static void parse(List<String> dmaFileLines) throws Exception {
		for (String line : dmaFileLines) {
			processModuleText(line.trim());
		}
	}

	private static void processModuleText(String line) throws Exception {
		line = line.replace("\t", " ");
		String[] tokens = line.split("\\s+");
		for (int i = 0; i < tokens.length; i++) {
			DmaFmtFactory.validate(i, tokens[i].trim());
		}
		Module module = new Module(tokens[DmaFmtFactory.getModuleNameIndex()].trim(), tokens);
		modules.add(module);
	}

	public static void updateModuleDistances() {
		for (int i = 0; i < modules.size() - 1; i++) {
			if (!modules.get(i).isSecondaryClone()) {
				for (int j = i + 1; j < modules.size(); j++) {
					List<Integer> differingFormatTypes = new ArrayList<Integer>();
					int distance = calculateDistance(differingFormatTypes, i, j);
					updateDistance(differingFormatTypes, i, j, distance);
				}
			}
		}
	}

	private static void updateDistance(List<Integer> differingFormatTypes, int i, int j, int distance) {
		modules.get(i).updateDistance(distance, modules.get(j), differingFormatTypes);
		modules.get(j).updateDistance(distance, modules.get(i), differingFormatTypes);
	}

	private static int calculateDistance(List<Integer> differingFormatTypes, int i, int j) {
		int[] distanceIndices = DmaFmtFactory.distanceIndices;
		int distance = 0;
		String[] data1 = modules.get(i).getData();
		String[] data2 = modules.get(j).getData();
		for (int k = 0; k < distanceIndices.length; k++) {
			int index = distanceIndices[k];
			if (!data1[index].trim().equals(data2[index].trim())) {
				differingFormatTypes.add(index);
				distance++;
			}
		}
		return distance;
	}

	public static void generateDotFileText() {
		StringBuffer dotFileText = new StringBuffer("strict graph G {\n");
		updateDotFileText(dotFileText, true);
		updateDotFileText(dotFileText, false);
		dotFileText.append("}");
		System.out.println(dotFileText.toString());
	}

	private static void updateDotFileText(StringBuffer dotFileText, boolean isModuleNames) {
		for (Module module : modules) {
			String text = (isModuleNames) ? module.getModuleDotText() : module.getModuleConnectionDotText();
			if (!text.isEmpty()) {
				dotFileText.append(text).append("\n");
			}
		}
	}

	private static void resetModuleVisited() {
		for (Module module : modules) {
			module.setVisited(false);
		}
	}

	public static void updateModuleConnections() {
		resetModuleVisited();
		int connectionId = 100;
		for (Module module : modules) {
			if (!module.isSecondaryClone()) {
				module.createConnection(connectionId);
				connectionId++;
			}
		}
	}

	public static void createXmlFile(String filename) {
		StringBuffer xmlText = XmlFactory.generateXml(filename, modules);
		System.out.println(xmlText.toString());
	}

	public static void printModuleInfo() {
		for (Module module : modules) {
			if (!module.isSecondaryClone()) {
				System.out.println(module);
			}
		}
	}

}
