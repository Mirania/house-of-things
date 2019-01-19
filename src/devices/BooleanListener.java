package devices;

import interfaces.Permission;

public class BooleanListener implements Permission {
	
	Device d;

	private BooleanListener() { }
	
	public BooleanListener(Permission d) {
		this.d = d.getDevice();
		this.d.booleanListener = true;
	}
	
	public Device getDevice() {
		return d;
	}
}
