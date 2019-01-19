package devices;

import java.util.ArrayList;
import java.util.Random;
import exceptions.IllegalOperationException;
import exceptions.IncompatibleProtocolException;
import interfaces.*;

public class Thermometer implements Programmable {

	private Device d;
	private Random random;

	public Thermometer() {
		d = createInstance();
	}

	public void sendValue() throws IllegalOperationException {
		if (!d.isOn()) throw new IllegalOperationException(d.getName(), "Send data while OFF");
		notifyListeners(d.getName(), computeValue());
	}
	
	private double computeValue() {
		random = new Random();
		return random.nextDouble()*40; // [0.0, 40.0[
	}
	
	@Override
	public void update(boolean b) {
		if (b) d.setOn();
		else d.setOff();
	}
	
	private void notifyListeners(String name, double val) throws IllegalOperationException {
		if (!d.doubleSender) throw new IllegalOperationException(getName(), "Notify target devices");
		d.notifyListeners(name, val);
	}

	@Override
	public Device createInstance() {
		return new BooleanListener(new DoubleSender((new Device("Thermometer", this)))).getDevice();
	}
	
	@Override
	public Device getDevice() {
		return d;
	}
	
	@Override
	public void setDevice(Device d) {
		this.d = d;
	}
	
	public <T> T acceptVisitor(Visitor<T> v) throws IllegalOperationException, IncompatibleProtocolException { 
		return v.visitThermo(this);
	}
	
	public Programmable copy() {
        Thermometer x = new Thermometer();
        try {
        	x.setName(this.getName());
        	if (this.isOn()) x.getDevice().setOn();
        	x.setProtocol(this.getProtocol());
			x.setListeners(new ArrayList<Programmable>(this.getListeners()));
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
