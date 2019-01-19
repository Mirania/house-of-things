package devices;

import java.util.ArrayList;
import java.util.Random;

import exceptions.IllegalOperationException;
import exceptions.IncompatibleProtocolException;
import interfaces.Programmable;
import interfaces.Visitor;

public class LuminositySensor implements Programmable {

	private Device d;
	private Random random;

	public LuminositySensor() {
		d = createInstance();
	}

	public void sendValue() throws IllegalOperationException {
		if (!d.isOn()) throw new IllegalOperationException(d.getName(), "Send data while OFF");
		notifyListeners(d.getName(), computeValue());
	}
	
	public void sendMessage() throws IllegalOperationException {
		if (!d.isOn()) throw new IllegalOperationException(d.getName(), "Send data while OFF");
		notifyListeners(d.getName(), computeReadableValue());
	}
	
	private double computeValue() {
		random = new Random();
		return random.nextDouble(); // [0.0, 1.0[
	}
	
	private String computeReadableValue() {
		double v = computeValue();
		if (v<0.2) return "Very dark";
		if (v<0.4) return "Dark";
		if (v<0.6) return "Normal";
		if (v<0.8) return "Bright";
		return "Very bright";
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
	
	private void notifyListeners(String name, String msg) throws IllegalOperationException {
		if (!d.stringSender) throw new IllegalOperationException(getName(), "Notify target devices");
		d.notifyListeners(name, msg);
	}

	@Override
	public Device createInstance() {
		return new StringSender(new BooleanListener(new DoubleSender((new Device("LumiSensor", this))))).getDevice();
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
		return v.visitLumiS(this);
	}
	
	public Programmable copy() {
        LuminositySensor x = new LuminositySensor();
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
