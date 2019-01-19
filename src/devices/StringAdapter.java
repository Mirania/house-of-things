package devices;

import java.util.ArrayList;
import java.util.List;

import exceptions.IllegalOperationException;
import exceptions.IncompatibleProtocolException;
import interfaces.Programmable;
import interfaces.Visitor;

public class StringAdapter implements Programmable {
	
	private Device d;
	private Programmable listener;
	private Programmable sender;
	
	private StringAdapter() { }
	
	public StringAdapter(Programmable p) throws IllegalOperationException {
		if (p.getDevice().stringListener) listener = p;
		else if (p.getDevice().stringSender) sender = p;
		else throw new IllegalOperationException(p.getName(), "Create message adapter");
		
		d = createInstance();
	}
	
	public void connect(Programmable p) throws IllegalOperationException {
		if (p.getDevice().stringListener) listener = p;
		else if (p.getDevice().stringSender) sender = p;
		else throw new IllegalOperationException(p.getName(), "Create message adapter");
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
	public void sendMessage() throws IllegalOperationException {
		if (sender==null) throw new IllegalOperationException(getName()+" (Sender)", "Send data");
		List<Programmable> adapteeListeners = sender.getListeners();
		sender.setListeners(d.getListeners());
		sender.sendMessage();
		sender.setListeners(adapteeListeners);
	}

	@Override
	public void update(String name, String msg) throws IllegalOperationException {
		if (listener==null) throw new IllegalOperationException(getName()+" (Listener)", "Read data");
		listener.update(name, msg);
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
	
	public void reset() {
		listener = null;
		sender = null;
	}
	
	@Override
	public boolean isOn() {
		if (listener==null) return sender.isOn();
		else if (sender==null) return listener.isOn();
		else return false;
	}

	@Override
	public Device createInstance() {
		if (listener!=null) return new StringListener(new Device("StringAdapter", this)).getDevice();
		else return new StringSender(new Device("StringAdapter", this)).getDevice();
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
		return v.visitStringAdapter(this);
	}
	
	public Programmable copy() {
        StringAdapter x = null;
        try {
        	x = new StringAdapter(this.getAdaptee());
        	x.setName(this.getName());
            if (this.sender!=null)
        		x.setListeners(new ArrayList<Programmable>(this.getListeners()));

		} catch (IllegalOperationException e) {
			return null;
		}
        
        return x;
    }

}
