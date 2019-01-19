package hot;

import java.util.ArrayList;
import java.util.List;

import devices.UtilityFactory;
import exceptions.UnsupportedUtilityException;
import interfaces.LoggerState;
import interfaces.Programmable;

//singleton class
public class Logger {

	private static Logger INSTANCE;
	private LoggerState loggerState;
	private List<String> logs;
	
	private Logger() { 
		try {
			this.loggerState = UtilityFactory.createLoggerState("Offline");
			this.logs = new ArrayList<>();
		} catch (UnsupportedUtilityException e) {
			// won't happen
		}
	}
	
	public static Logger getInstance() {
		if (INSTANCE == null)
			INSTANCE = new Logger();
		return INSTANCE;
	}
	
	public void setLogs(List<String> logs) {
		this.logs = logs;
	}
	
	public List<String> getLogs() {
		return logs;
	}
	
	public boolean log(String text) {
		logs.add("("+loggerState.getDestination()+") "+text);
		return loggerState.log(text);		
	}

	public boolean log(String text, Programmable p) {
		logs.add("("+loggerState.getDestination()+") "+text);
		return loggerState.log(text);
	}

	public void setState(LoggerState loggerState) {
		this.loggerState = loggerState;
	}
	
	//testing only
	public void clear() {
		logs.clear();
	}
}