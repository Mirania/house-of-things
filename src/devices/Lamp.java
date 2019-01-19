package devices;

import java.util.ArrayList;

import exceptions.IllegalOperationException;
import exceptions.IncompatibleProtocolException;
import interfaces.Programmable;
import interfaces.Visitor;

public class Lamp implements Programmable {
	
	private Device d;

	public Lamp() {
		d = createInstance();
	}

	@Override
	public Device createInstance() {
		return new BooleanListener(new Device("Lamp", this)).getDevice();
	}

	@Override
	public Device getDevice() {
		return d;
	}
	
	@Override
	public void setDevice(Device d) {
		this.d = d;
	}
	
	@Override
	public void update(boolean b) {
		if (b) d.setOn();
		else d.setOff();
	}
	
	public <T> T acceptVisitor(Visitor<T> v) throws IllegalOperationException, IncompatibleProtocolException { 
		return v.visitLamp(this);
	}
	
	public Programmable copy() {
        Lamp x = new Lamp();
        try {
        	x.setName(this.getName());
        	if (this.isOn()) x.getDevice().setOn();
        	x.setProtocol(this.getProtocol());
			x.setSenders(new ArrayList<Programmable>(this.getSenders()));
		} catch (IllegalOperationException e) {
			return null;
		}
        
        return x;
    }
	
	public String toString() {
		return d.toString();
	}

}
