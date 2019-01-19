package interfaces;

import java.util.List;
import devices.Device;
import exceptions.IllegalOperationException;
import exceptions.IncompatibleProtocolException;

//a proxy for the device class
public interface Programmable {
	
	public Device createInstance();
	
	public void setDevice(Device d);
	
	public Device getDevice();
	
	public default void update(boolean b) throws IllegalOperationException {
		 throw new IllegalOperationException(getName(), "Update power status");
	}
	
	public default void update(String key, double val) throws IllegalOperationException {
		 throw new IllegalOperationException(getName(), "Update numeric values");	
	};
	
	public default void update(String key, String msg) throws IllegalOperationException {
		throw new IllegalOperationException(getName(), "Update message history");	
	};
	
	public default void setOn() throws IllegalOperationException { };
	
	public default void setOff() throws IllegalOperationException { };
	
	public default void sendValue() throws IllegalOperationException { };
	
	public default void sendMessage() throws IllegalOperationException { };
	
	public default <T> T acceptVisitor(Visitor<T> v) throws IllegalOperationException, IncompatibleProtocolException {
		return v.visitOther(this);
	}
	
	//factory method
	public default Programmable copy() { return null; };
	
	public default void attach(Programmable p) throws IncompatibleProtocolException, IllegalOperationException {
		Device d = getDevice();
		
		if (!(d.booleanSender && p.getDevice().booleanListener) && !(d.doubleSender && p.getDevice().doubleListener)
				&& !(d.stringSender && p.getDevice().stringListener))
			throw new IllegalOperationException(getName(), "Attach to device");
		
		if (!Programmable.matchingProtocols(this, p))
			throw new IncompatibleProtocolException(getName(), getProtocol(), p.getName(), p.getProtocol());
		
		d.attach(p);
			
	}
	
	public default void disattach(Programmable p) throws IllegalOperationException {
		getDevice().disattach(p);
	}
	
	public default List<Programmable> getSenders() throws IllegalOperationException {
		Device d = getDevice();
		
		if (d.booleanListener || d.doubleListener || d.stringListener)
			return d.getSenders();
		
		else throw new IllegalOperationException(getName(), "Show attached devices");
	}
	
	public default List<Programmable> getListeners() throws IllegalOperationException {
		Device d = getDevice();
		
		if (d.booleanSender || d.doubleSender || d.stringSender)
			return d.getListeners();
		
		else throw new IllegalOperationException(getName(), "Show target devices");
	}
	
	public default List<Programmable> getBooleanSenders() throws IllegalOperationException {
		Device d = getDevice();	
		if (d.booleanListener) return d.getBooleanSenders();		
		else throw new IllegalOperationException(getName(), "Get attached devices");
	}
	
	public default List<Programmable> getBooleanListeners() throws IllegalOperationException {
		Device d = getDevice();	
		if (d.booleanSender) return d.getBooleanListeners();		
		else throw new IllegalOperationException(getName(), "Get target devices");
	}
	
	public default List<Programmable> getDoubleSenders() throws IllegalOperationException {
		Device d = getDevice();	
		if (d.doubleListener) return d.getDoubleSenders();		
		else throw new IllegalOperationException(getName(), "Get attached devices");
	}
	
	public default List<Programmable> getDoubleListeners() throws IllegalOperationException {
		Device d = getDevice();	
		if (d.doubleSender) return d.getDoubleListeners();		
		else throw new IllegalOperationException(getName(), "Get target devices");
	}
	
	public default List<Programmable> getStringSenders() throws IllegalOperationException {
		Device d = getDevice();	
		if (d.stringListener) return d.getStringSenders();		
		else throw new IllegalOperationException(getName(), "Get attached devices");
	}
	
	public default List<Programmable> getStringListeners() throws IllegalOperationException {
		Device d = getDevice();	
		if (d.stringSender) return d.getStringListeners();		
		else throw new IllegalOperationException(getName(), "Get target devices");
	}
	
	public default List<Programmable> getFullMembers() {
		return getDevice().getFullMembers();
	}
	
	//**** shortcut methods below *****************************************
	
	public default boolean isOn() {
		return getDevice().isOn();
	}
	
	public default void setName(String name) {
		getDevice().setName(name);
	}
	
	public default String getName() {
		return getDevice().getName();
	}
	
	public default void setProtocol(String protocol) {
		getDevice().setProtocol(protocol);
	}
	
	public default String getProtocol() {
		return getDevice().getProtocol();
	}
	
	public default void setListeners(List<Programmable> listeners) {
		getDevice().setListeners(listeners);
	}
	
	public default void setSenders(List<Programmable> senders) {
		getDevice().setSenders(senders);
	}
	
	public static boolean matchingProtocols(Programmable p1, Programmable p2) {
		return p1.getProtocol().equals("Universal") || p2.getProtocol().equals("Universal") ||
				p1.getProtocol().equals(p2.getProtocol());
	}
	
}
