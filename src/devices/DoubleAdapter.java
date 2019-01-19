package devices;

import java.util.ArrayList;
import java.util.List;

import exceptions.IllegalOperationException;
import exceptions.IncompatibleProtocolException;
import interfaces.*;

public class DoubleAdapter implements Programmable {
	
	private Device d;
	private Programmable listener;
	private Programmable sender;
	
	private DoubleAdapter() { }
	
	public DoubleAdapter(Programmable p) throws IllegalOperationException {
		if (p.getDevice().doubleListener) listener = p;
		else if (p.getDevice().doubleSender) sender = p;
		else throw new IllegalOperationException(p.getName(), "Create number adapter");
		
		d = createInstance();
	}
	
	public void connect(Programmable p) throws IllegalOperationException {
		if (p.getDevice().doubleListener) listener = p;
		else if (p.getDevice().doubleSender) sender = p;
		else throw new IllegalOperationException(p.getName(), "Create number adapter");
	}
	
	@Override
	public void attach(Programmable p) throws IncompatibleProtocolException, IllegalOperationException {
		if (sender==null) throw new IllegalOperationException(getName()+" (Sender)", "Attach to device");
		d.getListeners().add(p);
		p.getDevice().setSender(this);
	}
	
	@Override
	public void disattach(Programmable p) throws IllegalOperationException {
		if (sender==null) throw new IllegalOperationException(getName()+" (Sender)", "Disattach from device");
		d.getListeners().remove(p);
		p.getDevice().removeSender(this);
	}

	@Override
	public void sendValue() throws IllegalOperationException {
		if (sender==null) throw new IllegalOperationException(getName()+" (Sender)", "Send data");
		List<Programmable> adapteeListeners = sender.getListeners();
		sender.setListeners(d.getListeners());
		sender.sendValue();
		sender.setListeners(adapteeListeners);
	}

	@Override
	public void update(String name, double d) throws IllegalOperationException {
		if (listener==null) throw new IllegalOperationException(getName()+" (Listener)", "Read data");
		listener.update(name, d);
	}

	@Override
	public String getProtocol() {
		return "Universal";
	}
	
	public String toString() {
		return getName();
	}
	
	public Programmable getAdaptee() {
		if (listener==null) return sender;
		else if (sender==null) return listener;
		else return null;
	}
	
	@Override
	public boolean isOn() {
		if (listener==null) return sender.isOn();
		else if (sender==null) return listener.isOn();
		else return false;
	}
	
	public void reset() {
		listener = null;
		sender = null;
	}

	@Override
	public Device createInstance() {
		if (listener!=null) return new DoubleListener(new Device("DoubleAdapter", this)).getDevice();
		else return new DoubleSender(new Device("DoubleAdapter", this)).getDevice();
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
		return v.visitDoubleAdapter(this);
	}
	
	public Programmable copy() {
        DoubleAdapter x = null;
        try {
        	x = new DoubleAdapter(this.getAdaptee());
        	x.setName(this.getName());
            if (this.sender!=null)
        		x.setListeners(new ArrayList<Programmable>(this.getListeners()));

		} catch (IllegalOperationException e) {
			return null;
		}
        
        return x;
    }

}
