package devices;

import interfaces.LoggerState;

public class FacebookState implements LoggerState {

	private String serviceName;
	
	private FacebookState() { }

	// implementation of an actual network communication would be here
	public FacebookState(String user, String pword) {
		this.serviceName = "Facebook";
	}

	@Override
	public String getDestination() {
		return serviceName;
	}

	@Override
	public boolean log(String text) {
		// logging to facebook would go here
		return true;
	}
}