package mmd.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mmd.factory.DmaFmtFactory;
import mmd.util.Util;

public class Module {
	
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
	
	private String name;
	private String[] data;
	private Module closestModule;
	private int distance;
	private Set<Module> clones = new HashSet<>();
	private List<Integer> differingFormatTypes = new ArrayList<>();
	private List<ModuleConnection> connections = new ArrayList<>();
	private boolean primaryClone;
	private boolean secondaryClone;
	private boolean visited;
	
	public Module(String name, String[] data) {
		this.name = name;
		this.data = data;
		distance = -1;
		primaryClone = false;
		secondaryClone = false;
		visited = false;
	}
	
	public String getName() {
		return name;
	}
	
	public String[] getData() {
		return data;
	}
	
	public Module getClsestModule() {
		return closestModule;
	}
	
	public void setClsestModule(Module clsestModule) {
		this.closestModule = clsestModule;
	}
	
	public int getDistance() {
		return distance;
	}
	
	public void setDistance(int distance) {
		this.distance = distance;
	}
	
	public boolean isPrimaryClone() {
		return primaryClone;
	}
	
	public boolean isSecondaryClone() {
		return secondaryClone;
	}
	
	public void setVisited(boolean visited) {
		this.visited = visited;
	}
	
	public boolean isVisited() {
		return visited;
	}
	
	public String getModuleDotText() {
		if (secondaryClone) {
			return "";
		} else {
			String shapeText = (primaryClone) ? "circle" : "box";
			return name + "[shape=" + shapeText + "];";
		}
	}
	
	public String getModuleConnectionDotText() {
		if (secondaryClone || closestModule == null) {
			return "";
		} else if (closestModule.visited && closestModule.closestModule == this) {
			// the other module has provided the details already
			return "";
		} else {
			visited = true;
			return String.format("%s--%s[label=%s];", name, closestModule.name, createCommaSeparatedDifferingFormatTypes());
		}
	}
	
	public void updateDistance(int newDistance, Module closestModule, List<Integer> differingFormatTypes) { 
		if (isSecondaryClone()) {
			return;
		} else if (newDistance == 0) {
			clones.add(closestModule);
			closestModule.secondaryClone = true;
			primaryClone = true;
		} else if (this.distance == -1 || distance > newDistance) {
			distance = newDistance;
			this.closestModule = closestModule;
			this.differingFormatTypes = differingFormatTypes;
		}
	}
	
	private String createCommaSeparatedDifferingFormatTypes() {
		StringBuffer formatTypes = new StringBuffer("\"");
		for (int index : differingFormatTypes) {
			formatTypes.append(DmaFmtFactory.getDistanceFormatName(index)).append(",");
		}
		formatTypes.append("\"");
		return formatTypes.deleteCharAt(formatTypes.length() - 2).toString();
	}
	
	public void createConnection(int connectionId) { 
		if (closestModule != null) {
			if (!(closestModule.visited && closestModule.closestModule == this)) {
				ModuleConnection connection = new ModuleConnection(connectionId, this);
				connections.add(connection);
				closestModule.connections.add(connection);
				visited = true;
			}
		}
	}
	
	public void createCloneGroupXml(StringBuffer xmlText) {
		xmlText.append(Util.createOpenXmlTag(CLONE_GROUP_XML_TAG));
		createCloneDataTag(xmlText);
		createCloneModule(this, xmlText);
		addSecondaryCloneModule(xmlText);
		addDistanceConnections(xmlText);
		xmlText.append(Util.createCloseXmlTag(CLONE_GROUP_XML_TAG));
	}
	
	private void addSecondaryCloneModule(StringBuffer xmlText) {
		for (Module module : clones) {
			createCloneModule(module, xmlText);
		}
	}

	private void createCloneModule(Module module, StringBuffer xmlText) {
		xmlText.append(Util.createOpenXmlTag(CLONE_MODULE_XML_TAG));
		createModuleIdTag(module, xmlText);
		xmlText.append(Util.createCloseXmlTag(CLONE_MODULE_XML_TAG));
	}

	private void createModuleIdTag(Module module, StringBuffer xmlText) {
		xmlText.append(Util.createOpenXmlTag(MODULE_ID_XML_TAG));
		xmlText.append(String.format("%s%s%s", Util.createOpenXmlTag(NAME_XML_TAG), module.name, Util.createCloseXmlTag(NAME_XML_TAG)));
		xmlText.append(Util.createCloseXmlTag(MODULE_ID_XML_TAG));
	}

	private void createCloneDataTag(StringBuffer xmlText) {
		xmlText.append(Util.createOpenXmlTag(CLONE_DATA_XML_TAG)); 
		xmlText.append(String.format("%s%s%s", Util.createOpenXmlTag(CLONE_GROUP_ID_XML_TAG), name, Util.createCloseXmlTag(CLONE_GROUP_ID_XML_TAG)));
		xmlText.append(String.format("%s%s%s", Util.createOpenXmlTag(COUNT_OF_CLONE_GROUP_XML_TAG), 1 + clones.size(), Util.createCloseXmlTag(COUNT_OF_CLONE_GROUP_XML_TAG)));
		xmlText.append(String.format("%s%s%s", Util.createOpenXmlTag(CLONE_GROUP_PROCESS_DATA), "***", Util.createCloseXmlTag(CLONE_GROUP_PROCESS_DATA)));
		xmlText.append(Util.createCloseXmlTag(CLONE_DATA_XML_TAG));
	}
	
	private void addDistanceConnections(StringBuffer xmlText) {
		xmlText.append(Util.createOpenXmlTag(DISTANCE_CONNECTIONS_XML_TAG));
		for (ModuleConnection connection : connections) {
			createDistanceConnectionXmlTag(xmlText, connection);
		}
		xmlText.append(Util.createCloseXmlTag(DISTANCE_CONNECTIONS_XML_TAG));
	}

	private void createDistanceConnectionXmlTag(StringBuffer xmlText, ModuleConnection connection) { 
		xmlText.append(String.format("%s%s%s", Util.createOpenXmlTag(CONNECTION_ID_XML_TAG), connection.getConnectionId(), Util.createCloseXmlTag(CONNECTION_ID_XML_TAG)));
	}
	
	public void createModuleXmlTag(StringBuffer xmlText) {
		xmlText.append(Util.createOpenXmlTag(MODULE_XML_TAG));
		createModuleIdTag(this, xmlText);
		createProcessDataTag(xmlText);
		addDistanceConnections(xmlText);
		xmlText.append(Util.createCloseXmlTag(MODULE_XML_TAG));
	}

	private void createProcessDataTag(StringBuffer xmlText) {
		// -- module names should be different
	}
	
	public void createConnectionTag(StringBuffer xmlText) {
		ModuleConnection primaryConnection = getPrimaryModuleConnection();
		if (primaryConnection != null) {
			xmlText.append(Util.createOpenXmlTag(CONNECTION_XML_TAG));
			createDistanceConnectionXmlTag(xmlText, primaryConnection);
			createListOfMetricsTag(xmlText);
			xmlText.append(Util.createCloseXmlTag(CONNECTION_XML_TAG));
		}
	}
	
	private void createListOfMetricsTag(StringBuffer xmlText) {
		xmlText.append(Util.createOpenXmlTag(LIST_OF_METRICS_XML_TAG));
		addDifferingMetricTags(xmlText);
		xmlText.append(Util.createCloseXmlTag(LIST_OF_METRICS_XML_TAG));
	}

	private void addDifferingMetricTags(StringBuffer xmlText) {
		for (int index : differingFormatTypes) {
			createMetricTag(xmlText, index);
		}
	}

	private void createMetricTag(StringBuffer xmlText, int index) {
		xmlText.append(Util.createOpenXmlTag(METRIC_XML_TAG));
		xmlText.append(String.format("%s%s%s", Util.createOpenXmlTag(METRIC_NAME__XML_TAG), DmaFmtFactory.getDistanceFormatName(index), Util.createCloseXmlTag(METRIC_NAME__XML_TAG))); 
		createMetricValueTag(xmlText, this, index);
		createMetricValueTag(xmlText, closestModule, index);
		xmlText.append(Util.createCloseXmlTag(METRIC_XML_TAG));
	}

	private void createMetricValueTag(StringBuffer xmlText, Module module, int index) {
		xmlText.append(String.format("<value module=\"%s\">%s</value>", module.getName(), module.data[index]));
	}

	private ModuleConnection getPrimaryModuleConnection() {
		for (ModuleConnection moduleConnection : connections) {
			if (moduleConnection.getOwner() == this) {
				return moduleConnection;
			}
		}
		return null;
	}

	public String toString() {
		String closetModuleName = (closestModule != null) ? closestModule.name : "--";
		return String.format("%s, [%s, %d]", name, closetModuleName, distance);
	}
	
}
