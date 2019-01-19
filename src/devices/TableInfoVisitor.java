package devices;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JCheckBox;

import exceptions.IllegalOperationException;
import exceptions.IncompatibleProtocolException;
import interfaces.Programmable;
import interfaces.Visitor;

public class TableInfoVisitor implements Visitor<Object[][]> {
	
	private String isOnToString(boolean b) {
		return b ? "ON" : "OFF";
	}
	
	private String booleanToYesNo(boolean b) {
		return b ? "Yes" : "No";
	}
	
	private Object[][] concatArrays(Object[][] first, Object[][] second) {
		Object[][] result = Arrays.copyOf(first, first.length + second.length);
		System.arraycopy(second, 0, result, first.length, second.length);
		return result;
	}

	@Override
	public Object[][] visitOther(Programmable d) throws IllegalOperationException, IncompatibleProtocolException {
		return new Object[][] { new Object[] {"Status", isOnToString(d.isOn())}
		};
	}
	
	@Override
	public Object[][] visitCustom(CustomUtility d)
			throws IllegalOperationException, IncompatibleProtocolException {	
		Device x = d.getDevice();
		List<Object[]> list = new ArrayList<>();	
		list.add(new Object[] {"Status", isOnToString(d.isOn())} );
		list.add(new Object[] {"Can send power on/off commands", booleanToYesNo(x.booleanSender)} );
		list.add(new Object[] {"Can receive power on/off commands", booleanToYesNo(x.booleanListener)} );
		list.add(new Object[] {"Can send numeric data", booleanToYesNo(x.doubleSender)} );
		list.add(new Object[] {"Can read numeric data", booleanToYesNo(x.doubleListener)} );
		list.add(new Object[] {"Can send textual messages", booleanToYesNo(x.stringSender)} );
		list.add(new Object[] {"Can log textual messages", booleanToYesNo(x.stringListener)} );
		if (x.booleanSender) list.add(new Object[] {"Connected to (power)", x.getBooleanListeners().toString()});
		if (x.booleanListener) list.add(new Object[] {"Receiving from (power)", x.getBooleanSenders().toString()});
		if (x.doubleSender) list.add(new Object[] {"Connected to (numeric)", x.getDoubleListeners().toString()});
		if (x.doubleListener) list.add(new Object[] {"Receiving from (numeric)", x.getDoubleSenders().toString()});
		if (x.stringSender) list.add(new Object[] {"Connected to (text)", x.getStringListeners().toString()});
		if (x.stringListener) list.add(new Object[] {"Receiving from (text)", x.getStringSenders().toString()});
		
		return list.toArray(new Object[list.size()][2]);
	}

	@Override
	public Object[][] visitLamp(Lamp d) throws IllegalOperationException, IncompatibleProtocolException {
		return new Object[][] { new Object[] {"Status", isOnToString(d.isOn())},
			new Object[] {"Switches", d.getDevice().getSenders().toString()}
		};
	}

	@Override
	public Object[][] visitSwitch(Switch d) throws IllegalOperationException, IncompatibleProtocolException {
		return new Object[][] { new Object[] {"Status", isOnToString(d.isOn())},
			new Object[] {"Connected to", d.getDevice().getListeners().toString()}
		};
	}

	@Override
	public Object[][] visitTempAgg(TemperatureAggregator d) throws IllegalOperationException, IncompatibleProtocolException {
		return new Object[][] { new Object[] {"Status", isOnToString(d.isOn())},
			new Object[] {"Switches", d.getDevice().getBooleanSenders().toString()},
			new Object[] {"Thermos", d.getDevice().getDoubleSenders().toString()}
		};
	}

	@Override
	public Object[][] visitThermo(Thermometer d) throws IllegalOperationException, IncompatibleProtocolException {
		return new Object[][] { new Object[] {"Status", isOnToString(d.isOn())},
			new Object[] {"Switches", d.getDevice().getBooleanSenders().toString()},
			new Object[] {"Aggregators", d.getDevice().getDoubleListeners().toString()}
		};
	}

	@Override
	public Object[][] visitLumiH(LuminosityHistory d) throws IllegalOperationException, IncompatibleProtocolException {
		return new Object[][] { new Object[] {"Status", isOnToString(d.isOn())},
			new Object[] {"Switches", d.getDevice().getBooleanSenders().toString()},
			new Object[] {"Luminosity Sensors", d.getDevice().getStringSenders().toString()}
		};
	}

	@Override
	public Object[][] visitLumiS(LuminositySensor d) throws IllegalOperationException, IncompatibleProtocolException {
		return new Object[][] { new Object[] {"Status", isOnToString(d.isOn())},
			new Object[] {"Switches", d.getDevice().getBooleanSenders().toString()},
			new Object[] {"Loggers", d.getDevice().getStringListeners().toString()},
			new Object[] {"Aggregators", d.getDevice().getDoubleListeners().toString()},
		};
	}

	@Override
	public Object[][] visitBooleanAdapter(BooleanAdapter d)
			throws IllegalOperationException, IncompatibleProtocolException {
		Programmable a = d.getAdaptee();
		
		if (a==null)
			return new Object[][] { new Object[] {"Connected to", "-"}, new Object[] {"Type", "Power"} };
			
		List<Object[]> list = new ArrayList<>();	
		list.add(new Object[] {"Connected to", a.getName()});
		list.add(new Object[] {"Type", "Power"});
		if (a.getDevice().booleanListener) list.add(new Object[] {"Adapter-only senders", d.getSenders()});
		if (a.getDevice().booleanSender) list.add(new Object[] {"Adapter-only receivers", d.getListeners()});
		Object[][] adp = list.toArray(new Object[list.size()][2]);
		
		Object[][] actual = a.acceptVisitor(this);
		
		return concatArrays(adp, actual);
		
	}

	@Override
	public Object[][] visitDoubleAdapter(DoubleAdapter d)
			throws IllegalOperationException, IncompatibleProtocolException {
		Programmable a = d.getAdaptee();
		
		if (a==null)
			return new Object[][] { new Object[] {"Connected to", "-"}, new Object[] {"Type", "Numeric"} };
			
		List<Object[]> list = new ArrayList<>();	
		list.add(new Object[] {"Connected to", a.getName()});
		list.add(new Object[] {"Type", "Numeric"});
		if (a.getDevice().doubleListener) list.add(new Object[] {"Adapter-only senders", d.getSenders()});
		if (a.getDevice().doubleSender) list.add(new Object[] {"Adapter-only receivers", d.getListeners()});
		Object[][] adp = list.toArray(new Object[list.size()][2]);
		
		Object[][] actual = a.acceptVisitor(this);
		
		return concatArrays(adp, actual);
	}

	@Override
	public Object[][] visitStringAdapter(StringAdapter d)
			throws IllegalOperationException, IncompatibleProtocolException {
		Programmable a = d.getAdaptee();
		
		if (a==null)
			return new Object[][] { new Object[] {"Connected to", "-"}, new Object[] {"Type", "Text"} };
			
		List<Object[]> list = new ArrayList<>();	
		list.add(new Object[] {"Connected to", a.getName()});
		list.add(new Object[] {"Type", "Text"});
		if (a.getDevice().stringListener) list.add(new Object[] {"Adapter-only senders", d.getSenders()});
		if (a.getDevice().stringSender) list.add(new Object[] {"Adapter-only receivers", d.getListeners()});
		Object[][] adp = list.toArray(new Object[list.size()][2]);
		
		Object[][] actual = a.acceptVisitor(this);
		
		return concatArrays(adp, actual);
	}

	@Override
	public Object[][] visitBooleanGroup(BooleanGroup d)
			throws IllegalOperationException, IncompatibleProtocolException {
		Object[][] rows = new Object[d.getMembers().size()][2];
		
		for (int i=0; i<rows.length; i++)
			rows[i] = new Object[] {d.getMembers().get(i), "Remove"};
		
		return rows;
	}

	@Override
	public Object[][] visitDoubleGroup(DoubleGroup d) throws IllegalOperationException, IncompatibleProtocolException {
Object[][] rows = new Object[d.getMembers().size()][2];
		
		for (int i=0; i<rows.length; i++)
			rows[i] = new Object[] {d.getMembers().get(i), "Remove"};
		
		return rows;
	}

	@Override
	public Object[][] visitStringGroup(StringGroup d) throws IllegalOperationException, IncompatibleProtocolException {
Object[][] rows = new Object[d.getMembers().size()][2];
		
		for (int i=0; i<rows.length; i++)
			rows[i] = new Object[] {d.getMembers().get(i), "Remove"};
		
		return rows;
	}

	@Override
	public Object[][] visitLock(DoorLock d) throws IllegalOperationException, IncompatibleProtocolException {
		return new Object[][] { new Object[] {"Status", isOnToString(d.isOn())}
		};
	}

}
