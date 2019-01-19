package devices;

import exceptions.IllegalOperationException;
import exceptions.IncompatibleProtocolException;
import interfaces.Programmable;
import interfaces.Visitor;

//actuator example
public class DoorLock implements Programmable {

	private Device d;

	public DoorLock() {
		d = createInstance();
	}

	@Override
	public Device createInstance() {
		return new Device("DoorLock", this);
	}

	@Override
	public Device getDevice() {
		return d;
	}
	
	@Override
	public void setDevice(Device d) {
		this.d = d;
	}
	
	public void setOn() {
		d.setOn();
	}
	
	public void setOff() {
		d.setOff();
	}
	
	public String toString() {
		return d.toString();
	}
	
	public <T> T acceptVisitor(Visitor<T> v) throws IllegalOperationException, IncompatibleProtocolException { 
		return v.visitLock(this);
	}
	
	public Programmable copy() {
        DoorLock x = new DoorLock();
        x.setName(this.getName());
    	if (this.isOn()) x.getDevice().setOn();
    	x.setProtocol(this.getProtocol());
        
        return x;
    }

	
}
