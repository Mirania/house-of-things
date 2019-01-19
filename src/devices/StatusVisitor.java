package devices;

import exceptions.IllegalOperationException;
import exceptions.IncompatibleProtocolException;
import interfaces.Programmable;
import interfaces.Visitor;

public class StatusVisitor implements Visitor<String> {
	
	private String isOnToString(boolean b) {
		return b ? "ON" : "OFF";
	}
	
	@Override
	public String visitOther(Programmable d) throws IllegalOperationException, IncompatibleProtocolException {
		return String.format("('%s' %s) %s", d.getProtocol(), d.getDevice().getType(), isOnToString(d.isOn()));
	}
	
	@Override
	public String visitCustom(CustomUtility d) throws IllegalOperationException, IncompatibleProtocolException {
		return visitOther(d);
	}


	@Override
	public String visitLamp(Lamp d) throws IllegalOperationException, IncompatibleProtocolException {
		return String.format("('%s' %s) %s, %d switch(es)", d.getProtocol(), d.getDevice().getType(), isOnToString(d.isOn()), d.getDevice().getSenders().size());
	}

	@Override
	public String visitSwitch(Switch d) throws IllegalOperationException, IncompatibleProtocolException {
		return String.format("('%s' %s) %s, %d target(s)", d.getProtocol(), d.getDevice().getType(), isOnToString(d.isOn()), d.getDevice().getListeners().size());
	}

	@Override
	public String visitTempAgg(TemperatureAggregator d) throws IllegalOperationException, IncompatibleProtocolException {
		return String.format("('%s' %s) %s, %d switch(es), %d thermo(s)", 
				d.getProtocol(), 
				d.getDevice().getType(), 
				isOnToString(d.isOn()), 
				d.getDevice().getBooleanSenders().size(),
				d.getDevice().getDoubleSenders().size());
	}

	@Override
	public String visitThermo(Thermometer d) throws IllegalOperationException, IncompatibleProtocolException {
		return String.format("('%s' %s) %s, %d switch(es), %d target(s)", 
				d.getProtocol(), 
				d.getDevice().getType(), 
				isOnToString(d.isOn()), 
				d.getDevice().getBooleanSenders().size(),
				d.getDevice().getDoubleListeners().size());
	}

	@Override
	public String visitLumiH(LuminosityHistory d) throws IllegalOperationException, IncompatibleProtocolException {
		return String.format("('%s' %s) %s, %d switch(es), %d sender(s)", 
				d.getProtocol(), 
				d.getDevice().getType(), 
				isOnToString(d.isOn()), 
				d.getDevice().getBooleanSenders().size(),
				d.getDevice().getStringSenders().size());
	}

	@Override
	public String visitLumiS(LuminositySensor d) throws IllegalOperationException, IncompatibleProtocolException {
		return String.format("('%s' %s) %s, %d switch(es), %d+%d target(s)", 
				d.getProtocol(), 
				d.getDevice().getType(), 
				isOnToString(d.isOn()), 
				d.getDevice().getBooleanSenders().size(),
				d.getDevice().getStringListeners().size(),
				d.getDevice().getDoubleListeners().size());
	}

	@Override
	public String visitBooleanAdapter(BooleanAdapter d)
			throws IllegalOperationException, IncompatibleProtocolException {
		if (d.getAdaptee()==null) {
			if (d.getDevice().booleanSender) return "Unused power adapter for power controllers";
			else return "Unused power adapter for power receivers";
		}
		
		return String.format("Power adapter for %s (%s)", 
				d.getAdaptee().getName(),
				d.getAdaptee().getDevice().getType());
	}

	@Override
	public String visitDoubleAdapter(DoubleAdapter d) throws IllegalOperationException, IncompatibleProtocolException {
		if (d.getAdaptee()==null) {
			if (d.getDevice().doubleSender) return "Unused numeric adapter for number senders";
			else return "Unused numeric adapter for number loggers";
		}
		
		return String.format("Number adapter for %s (%s)", 
				d.getAdaptee().getName(),
				d.getAdaptee().getDevice().getType());
	}

	@Override
	public String visitStringAdapter(StringAdapter d) throws IllegalOperationException, IncompatibleProtocolException {
		if (d.getAdaptee()==null) {
			if (d.getDevice().stringSender) return "Unused message adapter for text senders";
			else return "Unused message adapter for text loggers";
		}
		
		return String.format("Message adapter for %s (%s)", 
				d.getAdaptee().getName(),
				d.getAdaptee().getDevice().getType());
	}

	@Override
	public String visitBooleanGroup(BooleanGroup d) throws IllegalOperationException, IncompatibleProtocolException {
		return String.format("'%s' Power listener group (affects %d device(s))",
				d.getProtocol(), 
				d.getCount());
	}

	@Override
	public String visitDoubleGroup(DoubleGroup d) throws IllegalOperationException, IncompatibleProtocolException {
		return String.format("'%s' Number sender group (affects %d device(s))", 
				d.getProtocol(), 
				d.getCount());
	}

	@Override
	public String visitStringGroup(StringGroup d) throws IllegalOperationException, IncompatibleProtocolException {
		return String.format("'%s' Message sender group (affects %d device(s))", 
				d.getProtocol(), 
				d.getCount());
	}

	@Override
	public String visitLock(DoorLock d) throws IllegalOperationException, IncompatibleProtocolException {
		return String.format("('%s' %s) %s", d.getProtocol(), d.getDevice().getType(), isOnToString(d.isOn()));
	}

}
