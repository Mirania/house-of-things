package devices;

import interfaces.Permission;

public class DoubleSender implements Permission {
	
	Device d;

	private DoubleSender() { }
	
	public DoubleSender(Permission d) {
		this.d = d.getDevice();
		this.d.doubleSender = true;
	}
	
	public Device getDevice() {
		return d;
	}
}
