package devices;

import interfaces.Permission;

public class StringSender implements Permission {

	Device d;

	private StringSender() { }
	
	public StringSender(Permission d) {
		this.d = d.getDevice();
		this.d.stringSender = true;
	}
	
	public Device getDevice() {
		return d;
	}
}
