package devices;

import java.util.ArrayList;

import exceptions.IllegalOperationException;
import exceptions.IncompatibleProtocolException;
import interfaces.*;

public class Switch implements Programmable {
	
	private Device d;

	public Switch() {
		d = createInstance();
	}

	@Override
	public Device createInstance() {
		return new BooleanSender(new Device("Switch", this)).getDevice();
	}

	@Override
	public Device getDevice() {
		return d;
	}
	
	@Override
	public void setDevice(Device d) {
		this.d = d;
	}
	
	public boolean isOn() {
		return d.isOn();
	}

	@Override
	public void setOn() throws IllegalOperationException {
		d.setOn();
		notifyListeners(true);
	}
	
	@Override
	public void setOff() throws IllegalOperationException {
		d.setOff();
		notifyListeners(false);
	}
	
	private void notifyListeners(boolean b) throws IllegalOperationException {
		if (!d.booleanSender) throw new IllegalOperationException(getName(), "Notify target devices");
		d.notifyListeners(b);
	}
	
	public <T> T acceptVisitor(Visitor<T> v) throws IllegalOperationException, IncompatibleProtocolException { 
		return v.visitSwitch(this);
	}
	
	public Programmable copy() {
        Switch x = new Switch();
        try {
        	x.setName(this.getName());
        	if (this.isOn()) x.getDevice().setOn();
        	x.setProtocol(this.getProtocol());
			x.setListeners(new ArrayList<Programmable>(this.getListeners()));
		} catch (IllegalOperationException e) {
			return null;
		}
        
        return x;
    }
	
	public String toString() {
		return d.toString();
	}

}
