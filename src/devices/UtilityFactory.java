package devices;

import exceptions.IllegalOperationException;
import exceptions.UnsupportedUtilityException;
import interfaces.Group;
import interfaces.LoggerState;
import interfaces.Programmable;

//simple factory
public class UtilityFactory {

	public static Programmable create(String type) throws UnsupportedUtilityException {
		Programmable p;

		switch (type) {
		case "Lamp":
			p = new Lamp();
			break;
		case "Door Lock":
			p = new DoorLock();
			break;
		case "Switch":
			p = new Switch();
			break;
		case "Temperature Logger":
			p = new TemperatureAggregator();
			break;
		case "Temperature Sensor":
			p = new Thermometer();
			break;
		case "Luminosity Logger":
			p = new LuminosityHistory();
			break;
		case "Luminosity Sensor":
			p = new LuminositySensor();
			break;
		case "Custom":
			p = new CustomUtility();
			break;
		default:
			throw new UnsupportedUtilityException(type);
		}

		return p;
	}

	public static Programmable createCustomDevice(String protocol, boolean[] flags) {
		Programmable p = new CustomUtility();
		Device d = p.getDevice();
		d.setProtocol(protocol);
		if (flags.length != 6)
			return p; // not a recognised format
		if (flags[0])
			d = new BooleanSender(d).getDevice();
		if (flags[1])
			d = new BooleanListener(d).getDevice();
		if (flags[2])
			d = new DoubleSender(d).getDevice();
		if (flags[3])
			d = new DoubleListener(d).getDevice();
		if (flags[4])
			d = new StringSender(d).getDevice();
		if (flags[5])
			d = new StringListener(d).getDevice();
		return p;
	}

	public static Programmable createCustomDevice(String protocol, String name, boolean[] flags) {
		Programmable p = createCustomDevice(protocol, flags);
		p.setName(name);
		return p;
	}

	public static Programmable create(String type, String name) throws UnsupportedUtilityException {
		Programmable p = create(type);
		p.setName(name);
		return p;
	}

	public static Programmable createWithProtocol(String type, String protocol) throws UnsupportedUtilityException {
		Programmable p = create(type);
		p.setProtocol(protocol);
		return p;
	}

	public static Programmable createWithProtocol(String type, String protocol, String name)
			throws UnsupportedUtilityException {
		Programmable p = create(type, name);
		p.setProtocol(protocol);
		return p;
	}

	public static LoggerState createLoggerState(String type) throws UnsupportedUtilityException {
		switch (type) {
		case "Slack":
			return new SlackState("example user key");
		case "Telegram":
			return new TelegramState("example user key");
		case "Facebook":
			return new FacebookState("example username", "example password");
		case "Offline":
			return new OfflineState();
		default:
			throw new UnsupportedUtilityException("'" + type + "' logger");
		}
	}

	public static Programmable createAdapterForDevice(Programmable p, String type)
			throws IllegalOperationException, UnsupportedUtilityException {
		switch (type) {
		case "Powering on/off":
			return new BooleanAdapter(p);
		case "Numeric Data":
			return new DoubleAdapter(p);
		case "Messaging":
			return new StringAdapter(p);
		default:
			throw new UnsupportedUtilityException("Adapter for " + p.getName());
		}

	}

	public static Group createGroup(int modalOption) throws UnsupportedUtilityException {
		switch (modalOption) {
		case 0:
			return new BooleanGroup();
		case 1:
			return new DoubleGroup();
		case 2:
			return new StringGroup();
		default:
			throw new UnsupportedUtilityException("Group of unknown type");
		}
	}

	public static Programmable createNewAdapterConnection(Programmable device, Programmable adapter, String type)
			throws IllegalOperationException {
		Device d = adapter.getDevice();
		switch (type) {
		case "Powering on/off":
			adapter = new BooleanAdapter(device);
			adapter.setDevice(d);
			d.setParent(adapter);
			break;
		case "Numeric Data":
			adapter = new DoubleAdapter(device);
			adapter.setDevice(d);
			d.setParent(adapter);
			break;
		case "Messaging":
			adapter = new StringAdapter(device);
			adapter.setDevice(d);
			d.setParent(adapter);
			break;
		}
		return adapter;
	}

}
