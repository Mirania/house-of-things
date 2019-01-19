package devices;

import java.util.ArrayList;
import java.util.List;
import exceptions.*;
import interfaces.*;

//group of booleanlisteners
public class BooleanGroup implements Programmable, Group {

	private Device d;
	private List<Programmable> members;
	private String protocol; //dictated by first non-universal member

	public BooleanGroup() {
		members = new ArrayList<>();
		d = createInstance();
		protocol = "Universal";
	}

	public void add(Programmable member) throws IncompatibleProtocolException, IllegalOperationException {
		
		if (!member.getDevice().booleanListener)
			throw new IllegalOperationException(member.getName(), "Add to boolean group");

		protocol = calcProtocol(); //refresh
		if (!Programmable.matchingProtocols(this, member))
			throw new IncompatibleProtocolException(getName(), getProtocol(), member.getName(), member.getProtocol());

		members.add(member);
		
		protocol = calcProtocol(); //calc again
	}

	public void remove(Programmable member) {
		members.remove(member);
		protocol = calcProtocol();
	}

	public void add(Programmable... ps) throws IncompatibleProtocolException, IllegalOperationException {
		for (Programmable p : ps)
			add(p);
	}

	public void remove(Programmable... ps) {
		for (Programmable p : ps)
			remove(p);
	}

	@Override
	public void update(boolean b) throws IllegalOperationException {
		for (Programmable member : members)
			member.update(b);
	}

	private String calcProtocol() {
		for (Programmable member: members)
			if (!member.getProtocol().equals("Universal"))
				return member.getProtocol();
		
		return "Universal";		
	}

	@Override
	public String getProtocol() {
		return protocol;
	}

	public String toString() {
		return getName();
	}
	
	public void setMembers(List<Programmable> members) {
		this.members = members;
	}

	public List<Programmable> getMembers() {
		return members;
	}
	
	@Override
	public List<Programmable> getFullMembers() {
		List<Programmable> list = new ArrayList<>();

		for (Programmable member : members) {
			list.add(member);
			list.addAll(member.getFullMembers());
		}
		return list;
	}

	public int getCount() {
		return getMembers().size();
	}

	public int count() {
		return members.size();
	}

	@Override
	public Device createInstance() {
		return new BooleanListener(new Device("BooleanGroup", this)).getDevice();
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
		return v.visitBooleanGroup(this);
	}
	
	public Group copyGroup() {
        BooleanGroup x = new BooleanGroup();
        x.setName(this.getName());
    	x.setProtocol(this.getProtocol());
		x.setMembers(new ArrayList<Programmable>(this.getMembers()));
        
        return x;
	}
	
	public Programmable copy() {
		return copyGroup().getDevice().getParent();
    }

	@Override
	public String getName() {
		return d.getName();
	}

	public void setName(String name) {
		d.setName(name);
	}

}
