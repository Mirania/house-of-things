package devices;

import interfaces.LoggerState;

public class SlackState implements LoggerState {

	private String serviceName;
	
	private SlackState() { }

	// implementation of an actual network communication would be here
	public SlackState(String key) {
		this.serviceName = "Slack";
	}

	@Override
	public String getDestination() {
		return serviceName;
	}

	@Override
	public boolean log(String text) {
		// logging to slack would go here
		return true;
	}
}