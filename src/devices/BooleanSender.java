package devices;

import interfaces.Permission;

public class BooleanSender implements Permission {
	
	Device d;

	private BooleanSender() { }
	
	public BooleanSender(Permission d) {
		this.d = d.getDevice();
		this.d.booleanSender = true;
	}
	
	public Device getDevice() {
		return d;
	}
}
