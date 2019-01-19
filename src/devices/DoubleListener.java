package devices;

import interfaces.Permission;

public class DoubleListener implements Permission {
	
	Device d;

	private DoubleListener() { }
	
	public DoubleListener(Permission d) {
		this.d = d.getDevice();
		this.d.doubleListener = true;
	}
	
	public Device getDevice() {
		return d;
	}
}
