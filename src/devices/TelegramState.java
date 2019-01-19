package devices;

import interfaces.LoggerState;

public class TelegramState implements LoggerState {

	private String serviceName;
	
	private TelegramState() { }

	// implementation of an actual network communication would be here
	public TelegramState(String key) {
		this.serviceName = "Telegram";
	}

	@Override
	public String getDestination() {
		return serviceName;
	}

	@Override
	public boolean log(String text) {
		// logging to telegram would go here
		return true;
	}
}