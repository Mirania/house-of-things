package devices;

import exceptions.IllegalOperationException;
import exceptions.IncompatibleProtocolException;
import interfaces.Programmable;
import interfaces.Visitor;

public class AdapterVisitor implements Visitor<Boolean> {
	
	private Programmable p;
	private Device d;
	private String type;
	private int mode;
	
	public static int FIND_SUITABLE = 0;
	public static int DELETE_CONNECTIONS = 1;
	public static int FIND_USED = 2;
	
	private AdapterVisitor() {}
	
	public AdapterVisitor(Programmable p, String adptype, int mode) {
		this.p = p;
		this.d = p.getDevice();
		this.type = adptype;
		this.mode = mode;
	}
	
	public AdapterVisitor(Programmable p, int mode) {
		this.p = p;
		this.d = p.getDevice();
		this.mode = mode;
	}
	
	@Override
	public Boolean visitBooleanAdapter(BooleanAdapter bd)
			throws IllegalOperationException, IncompatibleProtocolException {
		if (mode == FIND_SUITABLE) {
			return type.equals("Powering on/off") && bd.getAdaptee()==null && 
			((d.booleanListener && bd.getDevice().booleanListener) || (d.booleanSender && bd.getDevice().booleanSender));
		}
		else if (mode == DELETE_CONNECTIONS) {
			if (bd.getAdaptee() == p) bd.reset();
			return true;
		}	
		else if (mode == FIND_USED) {
			if (bd.getAdaptee() == p)
			return true;
		}
		return false;
	}

	@Override
	public Boolean visitDoubleAdapter(DoubleAdapter bd) throws IllegalOperationException, IncompatibleProtocolException {
		if (mode == FIND_SUITABLE)
			return type.equals("Numeric Data") && bd.getAdaptee()==null &&
			((d.doubleListener && bd.getDevice().doubleListener) || (d.doubleSender && bd.getDevice().doubleSender));
		else if (mode == DELETE_CONNECTIONS) {
			if (bd.getAdaptee() == p) bd.reset();
			return true;
		}
		else if (mode == FIND_USED) {
			if (bd.getAdaptee() == p)
			return true;
		}
		return false;
	}

	@Override
	public Boolean visitStringAdapter(StringAdapter bd) throws IllegalOperationException, IncompatibleProtocolException {
		if (mode == FIND_SUITABLE)
			return type.equals("Messaging") && bd.getAdaptee()==null &&
			((d.stringListener && bd.getDevice().stringListener) || (d.stringSender && bd.getDevice().stringSender));
		else if (mode == DELETE_CONNECTIONS) {
			if (bd.getAdaptee() == p) bd.reset();
			return true;
		}
		else if (mode == FIND_USED) {
			if (bd.getAdaptee() == p)
			return true;
		}
		return false;
	}

	@Override
	public Boolean visitOther(Programmable d) throws IllegalOperationException, IncompatibleProtocolException {
		return false;
	}
	
	@Override
	public Boolean visitCustom(CustomUtility d) throws IllegalOperationException, IncompatibleProtocolException {
		return false;
	}
	
	@Override
	public Boolean visitLock(DoorLock d) throws IllegalOperationException, IncompatibleProtocolException {
		return false;
	}

	@Override
	public Boolean visitLamp(Lamp d) throws IllegalOperationException, IncompatibleProtocolException {
		return false;
	}

	@Override
	public Boolean visitSwitch(Switch d) throws IllegalOperationException, IncompatibleProtocolException {
		return false;
	}

	@Override
	public Boolean visitTempAgg(TemperatureAggregator d)
			throws IllegalOperationException, IncompatibleProtocolException {
		return false;
	}

	@Override
	public Boolean visitThermo(Thermometer d) throws IllegalOperationException, IncompatibleProtocolException {
		return false;
	}

	@Override
	public Boolean visitLumiH(LuminosityHistory d) throws IllegalOperationException, IncompatibleProtocolException {
		return false;
	}

	@Override
	public Boolean visitLumiS(LuminositySensor d) throws IllegalOperationException, IncompatibleProtocolException {
		return false;
	}

	@Override
	public Boolean visitBooleanGroup(BooleanGroup d) throws IllegalOperationException, IncompatibleProtocolException {
		return false;
	}

	@Override
	public Boolean visitDoubleGroup(DoubleGroup d) throws IllegalOperationException, IncompatibleProtocolException {
		return false;
	}

	@Override
	public Boolean visitStringGroup(StringGroup d) throws IllegalOperationException, IncompatibleProtocolException {
		return false;
	}

}
