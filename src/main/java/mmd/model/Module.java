package mmd.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mmd.factory.DmaFmtFactory;

public class Module {
	
	private String name;
	private String[] data;
	private Module closestModule;
	private int distance;
	private Set<Module> clones = new HashSet<>();
	private List<Integer> differingFormatTypes = new ArrayList<>();
	private List<ModuleConnection> connections = new ArrayList<>();
	private DmaFmtFactory dmaFmtFactory;
	private boolean primaryClone;
	private boolean secondaryClone;
	private boolean visited;
	
	public Module(String name, String[] data, DmaFmtFactory dmaFmtFactory) {
		this.name = name;
		this.data = data;
		distance = -1;
		primaryClone = false;
		secondaryClone = false;
		visited = false;
		this.dmaFmtFactory = dmaFmtFactory;
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
	
	public List<ModuleConnection> getConnections() {
		return connections;
	}
	
	public List<Integer> getDifferingFormatTypes() {
		return differingFormatTypes;
	}
	
	public Set<Module> getClones() { 
		return clones;
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
			formatTypes.append(dmaFmtFactory.getDistanceFormatName(index)).append(",");
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

	public ModuleConnection getPrimaryModuleConnection() {
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
