package hot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import devices.*;
import exceptions.*;
import interfaces.*;

//solitary class (singleton) responsible for self-healing
//and resource distribution (flyweight)
public class CommandCenter implements Iterable<Programmable> {

	private static CommandCenter INSTANCE;
	private Map<String, List<Programmable>> utilities;
	private List<Group> groups;
	private Logger logger;
	private List<Memento> saves;

	private CommandCenter() {
		utilities = new HashMap<>();
		groups = new ArrayList<>();
		saves = new ArrayList<>();
		logger = Logger.getInstance();
		logger.log("System is ready");
	}

	public static CommandCenter getInstance() {
		if (INSTANCE == null)
			INSTANCE = new CommandCenter();
		return INSTANCE;
	}

	// adds to device list
	public Programmable add(Programmable p) {
		List<Programmable> target;
		String type = p.getDevice().getType();

		if (!utilities.containsKey(type))
			target = new ArrayList<>();
		else
			target = utilities.get(type);

		if (!target.contains(p)) {
			while (nameExists(p.getName())) {
				p.setName(p.getName() + "b");
			}
			target.add(p);
		}

		utilities.put(type, target);
		logger.log("Added device '"+p.getName()+"' to memory");
		return p;

	}

	public Group addGroup(Group g) {
		if (!groups.contains(g)) {
			while (groupNameExists(g.getName())) {
				g.setName(g.getName() + "b");
			}
			groups.add(g);
		}
		
		logger.log("Added group '"+g.getName()+"' to memory");
		return g;
	}

	public Programmable addDeviceByType(String type) throws UnsupportedUtilityException {
		logger.log("Added device of type '"+type+"' to memory");
		return add(UtilityFactory.create(type));
	}

	public Programmable addCustomDevice(String protocol, boolean[] flags) {
		logger.log("Added custom device to memory");
		return add(UtilityFactory.createCustomDevice(protocol, flags));
	}

	public Group addGroupByType(int type) throws UnsupportedUtilityException {
		logger.log("Added group to memory");
		return addGroup(UtilityFactory.createGroup(type));
	}

	public void remove(Programmable p) {
		String type = p.getDevice().getType();

		if (!utilities.containsKey(type))
			return;

		List<Programmable> target = utilities.get(type);
		target.remove(p);
		utilities.put(type, target);
		logger.log("Removed '"+p.getName()+"' from memory");
	}

	public void deleteByName(String name) throws IllegalOperationException, IncompatibleProtocolException {
		String k = null;
		Programmable target = null;

		// kill device

		for (String key : utilities.keySet())
			for (Programmable p : utilities.get(key))
				if (p.getName().equals(name)) {
					k = key;
					target = p;
					break;
				}

		if (target == null)
			return;

		List<Programmable> list = utilities.get(k);
		list.remove(target);
		utilities.put(k, list);

		// remove from groups

		for (Group g : groups)
			if (g.getMembers().contains(target))
				g.remove(target);

		// remove from adapters

		AdapterVisitor v = new AdapterVisitor(target, AdapterVisitor.DELETE_CONNECTIONS);

		for (String key : utilities.keySet())
			for (Programmable pr : utilities.get(key))
				pr.acceptVisitor(v);

		logger.log("Deleted device '"+target.getName()+"'");
	}

	public void deleteGroupByName(String groupname) {
		Group group = null;

		for (Group g : groups)
			if (g.getName().equals(groupname)) {
				group = g;
				break;
			}

		if (group == null)
			return;

		groups.remove(group);

		for (int i = 0; i < groups.size(); i++) {
			Group g = groups.get(i);
			for (int k = 0; k < g.getMembers().size(); k++) {
				Programmable p = g.getMembers().get(k);
				if (p.getName().equals(groupname))
					g.remove(p);
			}
		}
		
		logger.log("Deleted group '"+group.getName()+"'");

	}

	public void removeFromGroupByName(String groupname, String name) {
		Group group = null;

		for (Group g : groups)
			if (g.getName().equals(groupname)) {
				group = g;
				break;
			}

		if (group == null)
			return;

		for (int i = 0; i < group.getMembers().size(); i++) {
			if (group.getMembers().get(i).getName().equals(name))
				group.remove(group.getMembers().get(i));
		}
		
		logger.log("Removed '"+name+"' from group'"+groupname+"'");

	}

	public List<Group> findSuitableGroups(Programmable p) {
		List<Group> list = new ArrayList<>();
		Device d = p.getDevice();

		for (Group g : groups) {
			if (g.getMembers().contains(p))
				continue;

			if (!Group.matchingProtocols(g, p))
				continue;

			if ((d.booleanListener && g.getDevice().booleanListener) || (d.doubleSender && g.getDevice().doubleSender)
					|| (d.stringSender && g.getDevice().stringSender))
				list.add(g);
		}

		return list;
	}

	public List<String> findAttachableDevices(Programmable p) throws IllegalOperationException {
		List<String> list = new ArrayList<>();
		Device d = p.getDevice();

		for (Programmable pr : this) { // check devices
			Device dr = pr.getDevice();

			if (!Programmable.matchingProtocols(pr, p))
				continue; // skip

			if (d.booleanSender && dr.booleanListener && !dr.getSenders().contains(p))
				list.add(pr.getName());
			else if (d.doubleSender && dr.doubleListener && !dr.getSenders().contains(p))
				list.add(pr.getName());
			else if (d.stringSender && dr.stringListener && !dr.getSenders().contains(p))
				list.add(pr.getName());
		}

		for (Group g : groups) { // check groups
			Device dr = g.getDevice();

			if (!Group.matchingProtocols(g, p))
				continue; // skip

			if (d.booleanSender && dr.booleanListener && !g.getMembers().contains(p))
				list.add(g.getName());
			else if (d.doubleSender && dr.doubleListener && !g.getMembers().contains(p))
				list.add(g.getName());
			else if (d.stringSender && dr.stringListener && !g.getMembers().contains(p))
				list.add(g.getName());
		}

		return list;
	}

	public List<String> findAttachableDevicesGroup(Group g) throws IllegalOperationException {
		List<String> list = new ArrayList<>();
		Device d = g.getDevice();

		for (Programmable pr : this) {
			Device dr = pr.getDevice();

			if (!Group.matchingProtocols(g, pr))
				continue; // skip

			if (d.booleanSender && dr.booleanListener)
				list.add(pr.getName());
			else if (d.doubleSender && dr.doubleListener)
				list.add(pr.getName());
			else if (d.stringSender && dr.stringListener)
				list.add(pr.getName());
		}

		return list;
	}

	public List<String> findPotentialGroupMembers(Group g) throws IllegalOperationException {
		List<String> list = new ArrayList<>();

		for (Programmable p : this) {

			if (!Group.matchingProtocols(g, p))
				continue; // skip

			if (g.getMembers().contains(p))
				continue; // skip

			if (g.getDevice().booleanListener && p.getDevice().booleanListener)
				list.add(p.getName());
			else if (g.getDevice().doubleSender && p.getDevice().doubleSender)
				list.add(p.getName());
			else if (g.getDevice().stringSender && p.getDevice().stringSender)
				list.add(p.getName());
		}

		for (Group gr : groups) {
			if (gr == g)
				continue;

			if (isCircularGroupReference(g, gr))
				continue; // skip

			if (!Group.matchingProtocols(g, gr))
				continue; // skip

			if (g.getMembers().contains(gr.getDevice().getParent()))
				continue; // skip

			if (g.getDevice().booleanListener && gr.getDevice().booleanListener)
				list.add(gr.getName());
			else if (g.getDevice().doubleSender && gr.getDevice().doubleSender)
				list.add(gr.getName());
			else if (g.getDevice().stringSender && gr.getDevice().stringSender)
				list.add(gr.getName());
		}

		return list;
	}

	private boolean isCircularGroupReference(Group base, Group toAdd) {
		for (Programmable p : toAdd.getDevice().getParent().getFullMembers()) { // return true if toAdd contains a
																				// reference to base
			if (p == base)
				return true;
		}

		return false;
	}

	public Group addToGroupByName(String groupname, Programmable p)
			throws IncompatibleProtocolException, IllegalOperationException {
		Group group = null;

		for (Group g : groups)
			if (g.getName().equals(groupname)) {
				group = g;
				break;
			}

		group.add(p);

		logger.log("Added '"+p.getName()+"' to group '"+groupname+"'");
		return group;
	}

	public Programmable getDeviceByName(String name) throws NoSuchElementException {
		for (Programmable p : this)
			if (p.getName().equals(name))
				return p;

		throw new NoSuchElementException(name);
	}

	public Group getGroupByName(String name) throws NoSuchElementException {
		for (Group g : groups) {
			if (g.getName().equals(name))
				return g;
		}

		throw new NoSuchElementException(name);
	}

	public List<Programmable> findAdaptersUsedByDevice(Programmable p)
			throws IllegalOperationException, IncompatibleProtocolException {
		AdapterVisitor v = new AdapterVisitor(p, AdapterVisitor.FIND_USED);
		List<Programmable> adp = new ArrayList<>();

		for (Programmable pr : this)
			if (pr.acceptVisitor(v)) {
				adp.add(pr);
			}

		return adp;
	}

	public Programmable findSuitableAdapter(Programmable p, String type)
			throws IllegalOperationException, IncompatibleProtocolException {
		AdapterVisitor v = new AdapterVisitor(p, type, AdapterVisitor.FIND_SUITABLE);
		Programmable adp = null;

		for (Programmable pr : this) {
			if (pr.acceptVisitor(v)) {
				adp = pr;
				break;
			}
		}

		return adp;
	}

	public Programmable connectAdapter(Programmable device, Programmable adapter, String choice)
			throws IllegalOperationException, IncompatibleProtocolException {
		Programmable adp = UtilityFactory.createNewAdapterConnection(device, adapter, choice);
		deleteByName(adp.getName());
		add(adp);
		logger.log("Connected device '"+device.getName()+"' to adapter '"+adp.getName()+"'");
		return adp;
	}

	public Programmable createSuitableAdapter(Programmable p, String choice)
			throws IllegalOperationException, UnsupportedUtilityException {
		Programmable adp = UtilityFactory.createAdapterForDevice(p, choice);
		add(adp);
		logger.log("Created adapter '"+adp.getName()+"' for device '"+p.getName()+"'");
		return adp;
	}

	public void save(String name) {
		Memento mem = new Memento(name);
		saves.add(mem);
		logger.log("Created a restore point at "+mem.getDate()+" ("+mem.getName()+")");
	}

	public void load(int i) throws ArrayIndexOutOfBoundsException {
		if (i>=saves.size()) throw new ArrayIndexOutOfBoundsException();
		
		Memento mem = saves.get(i);
		restore(mem);
		logger.log("Loaded restore point from "+mem.getDate()+" ("+mem.getName()+")");
	}

	public int getTotalUtilities() {
		int count = 0;
		for (Programmable p : this) {
			count++;
		}

		return count;
	}

	public void createUtility(String type, String name) throws UnsupportedUtilityException {
		if (nameIsValid(name))
			add(UtilityFactory.create(type, name));
		else
			add(UtilityFactory.create(type));
		
		logger.log("Created device '"+name+"'");
	}

	public void createUtilityWithProtocol(String type, String protocol, String name)
			throws UnsupportedUtilityException {
		if (nameIsValid(name))
			add(UtilityFactory.createWithProtocol(type, protocol, name));
		else
			add(UtilityFactory.createWithProtocol(type, protocol));
		
		logger.log("Created device '"+name+"'");
	}

	public boolean nameIsValid(String name) {
		return name != null && !name.equals("") && !nameExists(name);
	}
	
	public boolean groupNameIsValid(String name) {
		return name != null && !name.equals("") && !groupNameExists(name);
	}

	private boolean nameExists(String name) {
		for (Programmable p : this)
			if (p.getName().equals(name))
				return true;
		return false;
	}
	
	private boolean groupNameExists(String name) {
		for (Group p: groups)
			if (p.getName().equals(name))
				return true;
		return false;
	}

	// should only be used for testing
	public void clear() {
		utilities.clear();
		groups.clear();
	}

	public List<Group> getGroups() {
		return groups;
	}

	@Override
	public Iterator<Programmable> iterator() {
		return new DeviceIterator();
	}

	private void restore(Memento mem) {
		this.utilities = mem.getSavedUtilities();
		this.groups = mem.getSavedGroups();
		logger.setLogs(mem.getSavedLogs());
		this.saves.remove(mem);
	}
	
	public List<Memento> getRestorePoints() {
		return saves;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer("Acknowledged utilities:\n");

		if (getTotalUtilities() == 0)
			return sb.append("None.").toString();

		for (String k : utilities.keySet()) {
			if (utilities.get(k).size() == 0)
				continue;

			sb.append("- " + k + " - ");
			sb.append(utilities.get(k));
			sb.append("\n");
		}

		return sb.toString();
	}

	public class Memento {

		private String name;
		private String date;
		private Map<String, List<Programmable>> savedUtilities;
		private List<Group> savedGroups;
		private List<String> savedLogs;

		public Memento(String name) {
			this.name = name;
			date = addDate();
			savedUtilities = new HashMap<>();
			savedGroups = new ArrayList<>();
			savedLogs = new ArrayList<>(logger.getLogs());
			
			for (String key: utilities.keySet()) {
				List<Programmable> entrylist = new ArrayList<>();
				for (Programmable p: utilities.get(key)) {
					entrylist.add(p.copy());
				}
				savedUtilities.put(key, entrylist);
			}

			for (Group g: groups) {
				savedGroups.add(g.copyGroup());
			}
			
		}

		public Map<String, List<Programmable>> getSavedUtilities() {
			return this.savedUtilities;
		}

		public List<Group> getSavedGroups() {
			return this.savedGroups;
		}
		
		public List<String> getSavedLogs() {
			return this.savedLogs;
		}
		
		public String getName() {
			return name;
		}
		
		public String getDate() {
			return date;
		}
		
		private String addDate() {
			SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM d yyyy HH:mm");
			return sdf.format(new Date());
		}

	}

	private class DeviceIterator implements Iterator<Programmable> {

		List<String> keyset;
		int keyidx;
		int validx;

		public DeviceIterator() {
			keyset = new ArrayList<>(utilities.keySet());
			keyidx = 0;
			validx = 0;
		}

		@Override
		public boolean hasNext() {
			if (keyset.size()<=keyidx)
				return false;

			return utilities.get(keyset.get(keyidx)).size()>validx;
		}

		@Override
		public Programmable next() {
			Programmable next = utilities.get(keyset.get(keyidx)).get(validx);
			
			if (utilities.get(keyset.get(keyidx)).size()>validx+1) { // checking list
				validx++;
			} else { // checking map
				validx = 0;
				keyidx++;
			}
			
			return next;

		}

	}

}
