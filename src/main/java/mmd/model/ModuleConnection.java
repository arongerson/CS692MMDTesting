package mmd.model;

public class ModuleConnection {
	
	private int connectionId;
	private Module owner;
	
	public ModuleConnection(int connectionId, Module owner) {
		this.connectionId = connectionId;
		this.owner = owner;
	}

	public int getConnectionId() {
		return connectionId;
	}

	public Module getOwner() {
		return owner;
	}

}
