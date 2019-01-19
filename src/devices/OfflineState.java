package devices;

import interfaces.LoggerState;

public class OfflineState implements LoggerState {

	private String serviceName;

	// implementation of an actual network communication would be here
	public OfflineState() {
		this.serviceName = "no destination";
	}

	@Override
	public String getDestination() {
		return serviceName;
	}

	@Override
	public boolean log(String text) {
		// empty method
		return true;
	}
}