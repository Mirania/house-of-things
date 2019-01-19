package interfaces;

import java.util.List;

import devices.Device;
import exceptions.IllegalOperationException;
import exceptions.IncompatibleProtocolException;

public interface Group {

	public void add(Programmable member) throws IncompatibleProtocolException, IllegalOperationException;

	public void remove(Programmable member);

	public void add(Programmable... ps) throws IncompatibleProtocolException, IllegalOperationException;

	public void remove(Programmable... ps);

	public Device getDevice();

	public int getCount();

	public String getName();

	public void setName(String name);

	public String getProtocol();

	public List<Programmable> getMembers();

	public <T> T acceptVisitor(Visitor<T> v) throws IllegalOperationException, IncompatibleProtocolException;
	
	//factory method
	public default Group copyGroup() { return null; };

	public static boolean matchingProtocols(Group g, Programmable p) {
		return g.getProtocol().equals("Universal") || p.getProtocol().equals("Universal")
				|| g.getProtocol().equals(p.getProtocol());
	}
	
	public static boolean matchingProtocols(Group g1, Group g2) {
		return g1.getProtocol().equals("Universal") || g2.getProtocol().equals("Universal")
				|| g1.getProtocol().equals(g2.getProtocol());
	}
}
