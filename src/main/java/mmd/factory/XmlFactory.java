package mmd.factory;

import java.util.List;
import java.util.Set;

import mmd.model.Module;
import mmd.model.ModuleConnection;
import mmd.util.Util;

public class XmlFactory {
	
	private static final String FILENAME_XML_TAG = "filename";
	private static final String MMD_XML_TAG = "mmd"; 
	private static final String CLONE_GROUP_XML_TAG = "clone_group";
	private static final String CLONE_DATA_XML_TAG = "clone_data";
	private static final String CLONE_GROUP_ID_XML_TAG = "clone_group_id";
	private static final String COUNT_OF_CLONE_GROUP_XML_TAG = "count_of_clone_group";
	private static final String CLONE_GROUP_PROCESS_DATA = "clone_group_process_data";
	private static final String CLONE_MODULE_XML_TAG = "clone_module";
	private static final String MODULE_ID_XML_TAG = "module_id";
	private static final String NAME_XML_TAG = "name";
	private static final String DISTANCE_CONNECTIONS_XML_TAG = "distance_connections";
	private static final String CONNECTION_ID_XML_TAG = "connection_id";
	private static final String MODULE_XML_TAG = "module";
	private static final String CONNECTION_XML_TAG = "connection";
	private static final String LIST_OF_METRICS_XML_TAG = "list_of_metrics";
	private static final String METRIC_XML_TAG = "metric";
	private static final String METRIC_NAME__XML_TAG = "metric_name";
	
	private static DmaFmtFactory dmaFmtFactory;
	
	public static StringBuffer generateXml(String filename, List<Module> modules, DmaFmtFactory dmaFmtFactory) {
		XmlFactory.dmaFmtFactory = dmaFmtFactory;
		StringBuffer xmlText = new StringBuffer(Util.createOpenXmlTag(MMD_XML_TAG)); 
		xmlText.append(String.format("%s%s%s", Util.createOpenXmlTag(FILENAME_XML_TAG), filename, Util.createCloseXmlTag(FILENAME_XML_TAG))); 
		createCloneGroupsXml(xmlText, modules);
		createModuleXml(xmlText, modules);
		createConnectionXml(xmlText, modules);
		xmlText.append(Util.createCloseXmlTag(MMD_XML_TAG));
		return xmlText;
	}
	
	private static void createConnectionXml(StringBuffer xmlText, List<Module> modules) {
		for (Module module : modules) {
			if (!module.isSecondaryClone()) {
				createConnectionTag(xmlText, module);
			}
		}
	}

	private static void createModuleXml(StringBuffer xmlText, List<Module> modules) {
		for (Module module : modules) {
			if (!(module.isPrimaryClone() || module.isSecondaryClone())) {
				createModuleXmlTag(xmlText, module);
			}
		}
	}

	private static void createCloneGroupsXml(StringBuffer xmlText, List<Module> modules) {
		for (Module module : modules) {
			if (module.isPrimaryClone()) {
				createCloneGroupXml(xmlText, module);
			}
		}
	}
	
	private static void createCloneGroupXml(StringBuffer xmlText, Module module) {
		xmlText.append(Util.createOpenXmlTag(CLONE_GROUP_XML_TAG));
		createCloneDataTag(xmlText, module);
		createCloneModule(module, xmlText);
		addSecondaryCloneModule(xmlText, module);
		addDistanceConnections(xmlText, module); 
		xmlText.append(Util.createCloseXmlTag(CLONE_GROUP_XML_TAG));
	}
	
	private static void addSecondaryCloneModule(StringBuffer xmlText, Module module) {
		Set<Module> clones = module.getClones();
		for (Module theModule : clones) {
			createCloneModule(theModule, xmlText);
		}
	}
	
	private static void createCloneModule(Module module, StringBuffer xmlText) {
		xmlText.append(Util.createOpenXmlTag(CLONE_MODULE_XML_TAG));
		createModuleIdTag(module, xmlText);
		xmlText.append(Util.createCloseXmlTag(CLONE_MODULE_XML_TAG));
	}
	
	private static void createCloneDataTag(StringBuffer xmlText, Module module) {
		xmlText.append(Util.createOpenXmlTag(CLONE_DATA_XML_TAG)); 
		xmlText.append(String.format("%s%s%s", Util.createOpenXmlTag(CLONE_GROUP_ID_XML_TAG), module.getName(), Util.createCloseXmlTag(CLONE_GROUP_ID_XML_TAG)));
		xmlText.append(String.format("%s%s%s", Util.createOpenXmlTag(COUNT_OF_CLONE_GROUP_XML_TAG), 1 + module.getClones().size(), Util.createCloseXmlTag(COUNT_OF_CLONE_GROUP_XML_TAG)));
		xmlText.append(String.format("%s%s%s", Util.createOpenXmlTag(CLONE_GROUP_PROCESS_DATA), "***", Util.createCloseXmlTag(CLONE_GROUP_PROCESS_DATA)));
		xmlText.append(Util.createCloseXmlTag(CLONE_DATA_XML_TAG));
	}
	
	private static void addDistanceConnections(StringBuffer xmlText, Module module) {
		List<ModuleConnection> connections = module.getConnections();
		xmlText.append(Util.createOpenXmlTag(DISTANCE_CONNECTIONS_XML_TAG));
		for (ModuleConnection connection : connections) {
			createDistanceConnectionXmlTag(xmlText, connection);
		}
		xmlText.append(Util.createCloseXmlTag(DISTANCE_CONNECTIONS_XML_TAG));
	}
	
	private static void createDistanceConnectionXmlTag(StringBuffer xmlText, ModuleConnection connection) { 
		xmlText.append(String.format("%s%s%s", Util.createOpenXmlTag(CONNECTION_ID_XML_TAG), connection.getConnectionId(), Util.createCloseXmlTag(CONNECTION_ID_XML_TAG)));
	}
	
	public static void createModuleXmlTag(StringBuffer xmlText, Module module) {
		xmlText.append(Util.createOpenXmlTag(MODULE_XML_TAG));
		createModuleIdTag(module, xmlText);
		createProcessDataTag(xmlText);
		addDistanceConnections(xmlText, module);
		xmlText.append(Util.createCloseXmlTag(MODULE_XML_TAG));
	}
	
	private static void createModuleIdTag(Module module, StringBuffer xmlText) {
		xmlText.append(Util.createOpenXmlTag(MODULE_ID_XML_TAG));
		xmlText.append(String.format("%s%s%s", Util.createOpenXmlTag(NAME_XML_TAG), module.getName(), Util.createCloseXmlTag(NAME_XML_TAG)));
		xmlText.append(Util.createCloseXmlTag(MODULE_ID_XML_TAG));
	}

	private static void createProcessDataTag(StringBuffer xmlText) {
		// -- module names should be different
	}
	
	public static void createConnectionTag(StringBuffer xmlText, Module module) {
		ModuleConnection primaryConnection = module.getPrimaryModuleConnection();
		if (primaryConnection != null) {
			xmlText.append(Util.createOpenXmlTag(CONNECTION_XML_TAG));
			createDistanceConnectionXmlTag(xmlText, primaryConnection);
			createListOfMetricsTag(xmlText, module);
			xmlText.append(Util.createCloseXmlTag(CONNECTION_XML_TAG));
		}
	}
	
	private static void createListOfMetricsTag(StringBuffer xmlText, Module module) {
		xmlText.append(Util.createOpenXmlTag(LIST_OF_METRICS_XML_TAG));
		addDifferingMetricTags(xmlText, module);
		xmlText.append(Util.createCloseXmlTag(LIST_OF_METRICS_XML_TAG));
	}

	private static void addDifferingMetricTags(StringBuffer xmlText, Module module) {
		List<Integer> differingFormatTypes = module.getDifferingFormatTypes();
		for (int index : differingFormatTypes) {
			createMetricTag(xmlText, index, module);
		}
	}

	private static void createMetricTag(StringBuffer xmlText, int index, Module module) {
		Module closestModule = module.getClsestModule();
		xmlText.append(Util.createOpenXmlTag(METRIC_XML_TAG));
		xmlText.append(String.format("%s%s%s", Util.createOpenXmlTag(METRIC_NAME__XML_TAG), dmaFmtFactory.getDistanceFormatName(index), Util.createCloseXmlTag(METRIC_NAME__XML_TAG))); 
		createMetricValueTag(xmlText, module, index);
		createMetricValueTag(xmlText, closestModule, index);
		xmlText.append(Util.createCloseXmlTag(METRIC_XML_TAG));
	}

	private static void createMetricValueTag(StringBuffer xmlText, Module module, int index) {
		xmlText.append(String.format("<value module=\"%s\">%s</value>", module.getName(), module.getData()[index]));
	}
	
}
