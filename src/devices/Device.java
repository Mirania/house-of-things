package devices;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import exceptions.IllegalOperationException;
import exceptions.IncompatibleProtocolException;
import interfaces.Permission;
import interfaces.Programmable;
import interfaces.Visitor;

//consider device to be the chip inside an object
public class Device implements Permission, Programmable {

	private boolean on;
	private String protocol;
	private String name;
	private String type;
	private List<Programmable> senders;
	private List<Programmable> listeners;
	private Programmable parent;
	//flags
	public boolean booleanListener;
	public boolean booleanSender;
	public boolean doubleListener;
	public boolean doubleSender;
	public boolean stringListener;
	public boolean stringSender;
	
	private Device() { }
	
	public Device(Programmable parent) {
		init(parent);
		generateName();
	}
	
	public Device(String type, Programmable parent) {		
		init(parent);
		this.type = type;	
		generateName();
	}
	
	private void init(Programmable parent) {
		this.type = "Device";
		this.protocol = "Universal";
		this.senders = new ArrayList<>();
		this.listeners = new ArrayList<>();
		this.parent = parent;
	}
	
	private void generateName() {
		int num = new Random().nextInt(100000);
		this.name = type+"-"+num;	
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setParent(Programmable p) {
		this.parent = p;
	}
	
	public Programmable getParent() {
		return parent;
	}
	
	//implementation of this method is dictated by parent object
	public void update(boolean b) throws IllegalOperationException {
		parent.update(b);
	}
	
	//implementation of this method is dictated by parent object
	public void update(String name, double val) throws IllegalOperationException {
		parent.update(name, val);
	}
	
	//implementation of this method is dictated by parent object
	public void update(String name, String msg) throws IllegalOperationException {
		parent.update(name, msg);
	}
	
	public void setSender(Programmable p) throws IllegalOperationException {
		senders.add(p);
	}
	
	public void removeSender(Programmable p) throws IllegalOperationException {
		senders.remove(p);
	}
	
	public List<Programmable> getSenders() throws IllegalOperationException {
		return senders;
	}
	
	public List<Programmable> getListeners() throws IllegalOperationException {
		return listeners;
	}
	
	public void setSenders(List<Programmable> senders) {
		this.senders = senders;
	}
	
	public void setListeners(List<Programmable> listeners) {
		this.listeners = listeners;
	}
	
	private boolean nameExists(List<Programmable> array, String name) {
		for (Programmable p: array)
			if (p.getName().equals(name)) return true;
		return false;
	}
	
	public void attach(Programmable p) throws IncompatibleProtocolException, IllegalOperationException {
		Device d = p.getDevice();
		if (!nameExists(listeners, p.getName())) {
			listeners.add(p);
			d.setSender(this.getParent());
		}
	}

	public void disattach(Programmable p) throws IllegalOperationException {
		Device d = p.getDevice();
		listeners.remove(p);
		d.removeSender(this.getParent());
	}
	
	public void notifyListeners(boolean b) throws IllegalOperationException {
		for (Programmable p: listeners)
			if (p.getDevice().booleanListener) p.update(b);
	}
	
	public void notifyListeners(String name, double val) throws IllegalOperationException {
		for (Programmable p: listeners)
			if (p.getDevice().doubleListener) p.update(name, val);
	}
	
	public void notifyListeners(String name, String msg) throws IllegalOperationException {
		for (Programmable p: listeners)
			if (p.getDevice().stringListener) p.update(name, msg);
	}
	
	public boolean isOn() {
		return on;
	}
	
	public void setOn() {
		on = true;
	}
	
	public void setOff() {
		on = false;
	}

	public List<Programmable> getBooleanSenders() throws IllegalOperationException {
		List<Programmable> s = new ArrayList<>();
		for (Programmable p: senders) {
			if (p.getDevice().booleanSender) s.add(p);
		}
		
		return s;
	}
	
	public List<Programmable> getBooleanListeners() throws IllegalOperationException {
		List<Programmable> s = new ArrayList<>();
		for (Programmable p: listeners) {
			if (p.getDevice().booleanListener) s.add(p);
		}
		
		return s;
	}
	
	public List<Programmable> getDoubleSenders() throws IllegalOperationException {
		List<Programmable> s = new ArrayList<>();
		for (Programmable p: senders) {
			if (p.getDevice().doubleSender) s.add(p);
		}
		
		return s;
	}
	
	public List<Programmable> getDoubleListeners() throws IllegalOperationException {
		List<Programmable> s = new ArrayList<>();
		for (Programmable p: listeners) {
			if (p.getDevice().doubleListener) s.add(p);
		}
		
		return s;
	}
	
	public List<Programmable> getStringSenders() throws IllegalOperationException {
		List<Programmable> s = new ArrayList<>();
		for (Programmable p: senders) {
			if (p.getDevice().stringSender) s.add(p);
		}
		
		return s;
	}
	
	public List<Programmable> getStringListeners() throws IllegalOperationException {
		List<Programmable> s = new ArrayList<>();
		for (Programmable p: listeners) {
			if (p.getDevice().stringListener) s.add(p);
		}
		
		return s;
	}
	
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}
	
	public String getProtocol() {
		return protocol;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public String getType() {
		return type;
	}
	
	public String toString() {
		return getName();
	}

	@Override
	public Device getDevice() {
		return this;
	}
	
	public List<Programmable> getFullMembers() {
		List<Programmable> l = new ArrayList<>();
		l.add(parent);
		return l;
	}

	@Override
	public Device createInstance() {
		return new Device();
	}

	@Override
	public void setDevice(Device d) {
		//no-op
	}
	
} 
