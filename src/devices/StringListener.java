package devices;

import interfaces.Permission;

public class StringListener implements Permission {

	Device d;

	private StringListener() { }
	
	public StringListener(Permission d) {
		this.d = d.getDevice();
		this.d.stringListener = true;
	}
	
	public Device getDevice() {
		return d;
	}
	
}
