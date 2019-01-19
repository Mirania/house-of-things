package devices;

import interfaces.*;

import java.util.ArrayList;
import java.util.List;

import exceptions.*;

public class BooleanAdapter implements Programmable {
	
	private Device d;
	private Programmable listener;
	private Programmable sender;
	
	private BooleanAdapter() { }
	
	public BooleanAdapter(Programmable p) throws IllegalOperationException {
		if (p.getDevice().booleanListener) listener = p;
		else if (p.getDevice().booleanSender) sender = p;
		else throw new IllegalOperationException(p.getName(), "Create power adapter");

		d = createInstance();
	}
	
	public void connect(Programmable p) throws IllegalOperationException {
		if (p.getDevice().booleanListener) listener = p;
		else if (p.getDevice().booleanSender) sender = p;
		else throw new IllegalOperationException(p.getName(), "Create power adapter");
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
	public void setOn() throws IllegalOperationException {
		if (sender==null) throw new IllegalOperationException(getName()+" (Sender)", "Set status");
		List<Programmable> adapteeListeners = sender.getListeners();
		sender.setListeners(d.getListeners());
		sender.setOn();
		sender.setListeners(adapteeListeners);
	}

	@Override
	public void setOff() throws IllegalOperationException {
		if (sender==null) throw new IllegalOperationException(getName()+" (Sender)", "Set status");
		List<Programmable> adapteeListeners = sender.getListeners();
		sender.setListeners(d.getListeners());
		sender.setOff();
		sender.setListeners(adapteeListeners);
	}

	@Override
	public void update(boolean b) throws IllegalOperationException {
		if (listener==null) throw new IllegalOperationException(getName()+" (Listener)", "Set status");
		listener.update(b);
	}
	
	@Override
	public boolean isOn() {
		if (listener==null) return sender.isOn();
		else if (sender==null) return listener.isOn();
		else return false;
	}

	@Override
	public String getProtocol() {
		return d.getProtocol();
	}
	
	public Programmable getAdaptee() {
		if (listener==null) return sender;
		else if (sender==null) return listener;
		else return null;
	}
	
	public void reset() {
		listener = null;
		sender = null;
	}

	public String toString() {
		return getName();
    }

	@Override
	public Device createInstance() {
		if (listener!=null) return new BooleanListener(new Device("BooleanAdapter", this)).getDevice();
		else return new BooleanSender(new Device("BooleanAdapter", this)).getDevice();
	}
	
	@Override
	public void setDevice(Device d) {
		this.d = d;
	}

	@Override
	public Device getDevice() {
		return d;
	}
	
	public <T> T acceptVisitor(Visitor<T> v) throws IllegalOperationException, IncompatibleProtocolException { 
		return v.visitBooleanAdapter(this);
	}
	
	public Programmable copy() {
		BooleanAdapter x = null;
        try {
        	x = new BooleanAdapter(this.getAdaptee());
        	x.setName(this.getName());
            if (this.sender!=null)
        		x.setListeners(new ArrayList<Programmable>(this.getListeners()));

		} catch (IllegalOperationException e) {
			e.printStackTrace();
			return null;
		}
        
        return x;
    }

}
