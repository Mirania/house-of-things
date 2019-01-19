package devices;

import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import exceptions.*;
import hot.*;
import interfaces.*;
import ui.UiFactory;
import static ui.UiFactory.alert;
import static ui.UiFactory.scrollAlert;

public class ActionVisitor implements Visitor<List<JButton>> {

	private JFrame frame;
	private CommandCenter c;

	private ActionVisitor() {
	}

	public ActionVisitor(JFrame frame) {
		this.frame = frame;
		c = CommandCenter.getInstance();
	}

	@Override
	public List<JButton> visitOther(Programmable d) throws IllegalOperationException, IncompatibleProtocolException {
		return new ArrayList<>();
	}
	
	@Override
	public List<JButton> visitCustom(CustomUtility d) throws IllegalOperationException, IncompatibleProtocolException {
		Device x = d.getDevice();
		
		JButton xbtn = UiFactory.createStandardButton("Set On/Off");
		xbtn.addActionListener(e -> {
			sendBooleanProcedure(d);
		});	
		
		JButton groupbtn = UiFactory.createStandardButton("Add to Group");
		groupbtn.addActionListener(e -> {
			addToGroupProcedure(d);
		});
		
		JButton ybtn = UiFactory.createStandardButton("Manage Adapters");
		ybtn.addActionListener(e -> {
			adapterProcedure(d, "custom device");
		});
		
		JButton zbtn = UiFactory.createStandardButton("Attach/Disattach");
		zbtn.addActionListener(e -> {
			attachDisattachProcedure(d, "custom device");
		});
		
		JButton kbtn = UiFactory.createStandardButton("Display Numeric Data");
		kbtn.addActionListener(e -> {
			try {
				scrollAlert(d.printAllNumericData());
			} catch (IllegalOperationException e1) {
				alert(e1.getMessage());
			}
		});
		
		JButton abtn = UiFactory.createStandardButton("Send Numeric Data");
		abtn.addActionListener(e -> {
			sendDoubleProcedure(d);
		});
		
		JButton bbtn = UiFactory.createStandardButton("Display Logs");
		bbtn.addActionListener(e -> {
			try {
				scrollAlert(d.printAllTextualData());
			} catch (IllegalOperationException e1) {
				alert(e1.getMessage());
			}
		});
		
		JButton cbtn = UiFactory.createStandardButton("Send Log Entry");
		cbtn.addActionListener(e -> {
			sendStringProcedure(d);
		});

		List<JButton> list = new ArrayList<>();
		if (x.booleanSender || (!x.booleanSender && !x.booleanListener)) list.add(xbtn);
		if (x.booleanListener || x.doubleSender || x.stringSender) list.add(groupbtn);
		if (!(!x.booleanListener && !x.booleanSender && !x.doubleListener &&
				!x.doubleSender && !x.stringListener && !x.stringSender) &&
				!(x.booleanListener && x.booleanSender) &&
				!(x.doubleListener && x.doubleSender) &&
				!(x.stringListener && x.stringSender)) list.add(ybtn);
		if (x.booleanSender || x.doubleSender || x.stringSender) list.add(zbtn);
		if (x.doubleListener) list.add(kbtn);
		if (x.doubleSender) list.add(abtn);
		if (x.stringListener) list.add(bbtn);
		if (x.stringSender) list.add(cbtn);
		return list;
	}
	
	@Override
	public List<JButton> visitLock(DoorLock d) throws IllegalOperationException, IncompatibleProtocolException {
		JButton xbtn = UiFactory.createStandardButton("Set On/Off");
		xbtn.addActionListener(e -> {
			sendBooleanProcedure(d);
		});

		List<JButton> list = new ArrayList<>();
		list.add(xbtn);
		return list;
	}

	@Override
	public List<JButton> visitLamp(Lamp d) throws IllegalOperationException, IncompatibleProtocolException {
		JButton groupbtn = UiFactory.createStandardButton("Add to Group");
		groupbtn.addActionListener(e -> {
			addToGroupProcedure(d);
		});
		
		JButton ybtn = UiFactory.createStandardButton("Manage Adapters");
		ybtn.addActionListener(e -> {
			adapterProcedure(d, "lamp");
		});

		List<JButton> list = new ArrayList<>();
		list.add(groupbtn);
		list.add(ybtn);
		return list;
	}

	@Override
	public List<JButton> visitSwitch(Switch d) throws IllegalOperationException, IncompatibleProtocolException {
		JButton xbtn = UiFactory.createStandardButton("Manage Adapters");
		xbtn.addActionListener(e -> {
			adapterProcedure(d, "switch");
		});

		JButton ybtn = UiFactory.createStandardButton("Attach/Disattach");
		ybtn.addActionListener(e -> {
			attachDisattachProcedure(d, "switch");
		});

		JButton zbtn = UiFactory.createStandardButton("Set On/Off");
		zbtn.addActionListener(e -> {
			sendBooleanProcedure(d);
		});

		List<JButton> list = new ArrayList<>();
		list.add(xbtn);
		list.add(ybtn);
		list.add(zbtn);
		return list;
	}

	@Override
	public List<JButton> visitTempAgg(TemperatureAggregator d)
			throws IllegalOperationException, IncompatibleProtocolException {
		JButton groupbtn = UiFactory.createStandardButton("Add to Group");
		groupbtn.addActionListener(e -> {
			addToGroupProcedure(d);
		});
		
		JButton xbtn = UiFactory.createStandardButton("Manage Adapters");
		xbtn.addActionListener(e -> {
			adapterProcedure(d, "lamp");
		});

		JButton ybtn = UiFactory.createStandardButton("Display Temperatures");
		ybtn.addActionListener(e -> {
			try {
				scrollAlert(d.printAvgTemp()+"\n\n"+d.printAll());
			} catch (IllegalOperationException e1) {
				alert(e1.getMessage());
			}
		});

		List<JButton> list = new ArrayList<>();
		list.add(groupbtn);
		list.add(xbtn);
		list.add(ybtn);
		return list;
	}

	@Override
	public List<JButton> visitThermo(Thermometer d) throws IllegalOperationException, IncompatibleProtocolException {
		JButton groupbtn = UiFactory.createStandardButton("Add to Group");
		groupbtn.addActionListener(e -> {
			addToGroupProcedure(d);
		});
		
		JButton xbtn = UiFactory.createStandardButton("Manage Adapters");
		xbtn.addActionListener(e -> {
			adapterProcedure(d, "sensor");
		});

		JButton ybtn = UiFactory.createStandardButton("Attach/Disattach");
		ybtn.addActionListener(e -> {
			attachDisattachProcedure(d, "sensor");
		});
		
		JButton zbtn = UiFactory.createStandardButton("Send Data");
		zbtn.addActionListener(e -> {
			sendDoubleProcedure(d);
		});

		List<JButton> list = new ArrayList<>();
		list.add(groupbtn);
		list.add(xbtn);
		list.add(ybtn);
		list.add(zbtn);
		return list;
	}

	@Override
	public List<JButton> visitLumiH(LuminosityHistory d)
			throws IllegalOperationException, IncompatibleProtocolException {
		JButton groupbtn = UiFactory.createStandardButton("Add to Group");
		groupbtn.addActionListener(e -> {
			addToGroupProcedure(d);
		});
		
		JButton xbtn = UiFactory.createStandardButton("Manage Adapters");
		xbtn.addActionListener(e -> {
			adapterProcedure(d, "lamp");
		});

		JButton ybtn = UiFactory.createStandardButton("Display Logs");
		ybtn.addActionListener(e -> {
			try {
				scrollAlert(d.printAll());
			} catch (IllegalOperationException e1) {
				alert(e1.getMessage());
			}
		});

		List<JButton> list = new ArrayList<>();
		list.add(groupbtn);
		list.add(xbtn);
		list.add(ybtn);
		return list;
	}

	@Override
	public List<JButton> visitLumiS(LuminositySensor d)
			throws IllegalOperationException, IncompatibleProtocolException {
		JButton groupbtn = UiFactory.createStandardButton("Add to Group");
		groupbtn.addActionListener(e -> {
			addToGroupProcedure(d);
		});
		
		JButton xbtn = UiFactory.createStandardButton("Manage Adapters");
		xbtn.addActionListener(e -> {
			adapterProcedure(d, "sensor");
		});

		JButton ybtn = UiFactory.createStandardButton("Attach/Disattach");
		ybtn.addActionListener(e -> {
			attachDisattachProcedure(d, "sensor");
		});
		
		JButton zbtn = UiFactory.createStandardButton("Send Data");
		zbtn.addActionListener(e -> {
			sendDoubleProcedure(d);
		});
		
		JButton kbtn = UiFactory.createStandardButton("Send Log Entry");
		kbtn.addActionListener(e -> {
			sendStringProcedure(d);
		});

		List<JButton> list = new ArrayList<>();
		list.add(groupbtn);
		list.add(xbtn);
		list.add(ybtn);
		list.add(zbtn);
		list.add(kbtn);
		return list;
	}

	@Override
	public List<JButton> visitBooleanAdapter(BooleanAdapter d)
			throws IllegalOperationException, IncompatibleProtocolException {
		JButton groupbtn = UiFactory.createStandardButton("Add to Group");
		groupbtn.addActionListener(e -> {
			addToGroupProcedure(d);
		});
		
		JButton xbtn = UiFactory.createStandardButton("Disconnect");
		xbtn.addActionListener(e -> {
			d.reset();
			UiFactory.nav(frame, new DevicePage(d.getName()));
		});
		
		JButton ybtn = UiFactory.createStandardButton("Set On/Off");
		ybtn.addActionListener(e -> {
			sendBooleanProcedure(d);
		});
		
		JButton zbtn = UiFactory.createStandardButton("Attach/Disattach");
		zbtn.addActionListener(e -> {
			attachDisattachProcedure(d, "power adapter");
		});
		
		List<JButton> list = new ArrayList<>();
		if (d.getAdaptee()!=null && d.getDevice().booleanListener) list.add(groupbtn);
		list.add(xbtn);
		if (d.getAdaptee()!=null && d.getDevice().booleanSender) list.add(ybtn);
		if (d.getAdaptee()!=null && d.getDevice().booleanSender) list.add(zbtn);
		return list;
	}

	@Override
	public List<JButton> visitDoubleAdapter(DoubleAdapter d)
			throws IllegalOperationException, IncompatibleProtocolException {
		JButton groupbtn = UiFactory.createStandardButton("Add to Group");
		groupbtn.addActionListener(e -> {
			addToGroupProcedure(d);
		});
		
		JButton xbtn = UiFactory.createStandardButton("Disconnect");
		xbtn.addActionListener(e -> {
			d.reset();
			UiFactory.nav(frame, new DevicePage(d.getName()));
		});
		
		JButton ybtn = UiFactory.createStandardButton("Send Data");
		ybtn.addActionListener(e -> {
			sendDoubleProcedure(d);
		});
		
		JButton zbtn = UiFactory.createStandardButton("Attach/Disattach");
		zbtn.addActionListener(e -> {
			attachDisattachProcedure(d, "numeric adapter");
		});
		
		List<JButton> list = new ArrayList<>();
		if (d.getAdaptee()!=null && d.getDevice().doubleSender) list.add(groupbtn);
		list.add(xbtn);
		if (d.getAdaptee()!=null && d.getDevice().doubleSender) list.add(ybtn);
		if (d.getAdaptee()!=null && d.getDevice().doubleSender) list.add(zbtn);
		return list;
	}

	@Override
	public List<JButton> visitStringAdapter(StringAdapter d)
			throws IllegalOperationException, IncompatibleProtocolException {
		JButton groupbtn = UiFactory.createStandardButton("Add to Group");
		groupbtn.addActionListener(e -> {
			addToGroupProcedure(d);
		});
		
		JButton xbtn = UiFactory.createStandardButton("Disconnect");
		xbtn.addActionListener(e -> {
			d.reset();
			UiFactory.nav(frame, new DevicePage(d.getName()));
		});
		
		JButton ybtn = UiFactory.createStandardButton("Send Log Entry");
		ybtn.addActionListener(e -> {
			sendStringProcedure(d);
		});
		
		JButton zbtn = UiFactory.createStandardButton("Attach/Disattach");
		zbtn.addActionListener(e -> {
			attachDisattachProcedure(d, "message adapter");
		});
		
		List<JButton> list = new ArrayList<>();
		if (d.getAdaptee()!=null && d.getDevice().stringSender) list.add(groupbtn);
		list.add(xbtn);
		if (d.getAdaptee()!=null && d.getDevice().stringSender) list.add(ybtn);
		if (d.getAdaptee()!=null && d.getDevice().stringSender) list.add(zbtn);
		return list;
	}

	@Override
	public List<JButton> visitBooleanGroup(BooleanGroup d)
			throws IllegalOperationException, IncompatibleProtocolException {
		return new ArrayList<>();
	}

	@Override
	public List<JButton> visitDoubleGroup(DoubleGroup d)
			throws IllegalOperationException, IncompatibleProtocolException {
		JButton xbtn = UiFactory.createStandardButton("Attach/Disattach");
		xbtn.addActionListener(e -> {
			attachDisattachProcedureGroup(d, "numeric data group");
		});
		
		JButton ybtn = UiFactory.createStandardButton("Send Data");
		ybtn.addActionListener(e -> {
			sendDoubleProcedure(d);
		});
		
		List<JButton> list = new ArrayList<>();
		list.add(xbtn);
		list.add(ybtn);
		return list;
	}

	@Override
	public List<JButton> visitStringGroup(StringGroup d)
			throws IllegalOperationException, IncompatibleProtocolException {
		JButton xbtn = UiFactory.createStandardButton("Attach/Disattach");
		xbtn.addActionListener(e -> {
			attachDisattachProcedureGroup(d, "text data group");
		});
		
		JButton ybtn = UiFactory.createStandardButton("Send Log Entry");
		ybtn.addActionListener(e -> {
			sendStringProcedure(d);
		});
		
		List<JButton> list = new ArrayList<>();
		list.add(xbtn);
		list.add(ybtn);
		return list;
	}

	// -------- private methods
	// ---------------------------------------------------------------

	private int adapterModal(List<Object> opts, Programmable p) {
		Object[] options = opts.toArray();
		return JOptionPane.showOptionDialog(frame, "What should the adapter be created for?", "Adapter Configuration",
				JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, null);

	}

	private int confirmAdapterModal(Programmable adapter) {
		return JOptionPane.showConfirmDialog(frame,
				"A free adapter '" + adapter.getName() + "' was detected. Connect to this one?",
				"Adapter Configuration", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

	}

	private int attachModeModal() {
		Object[] options = { "Attach", "Disattach" };
		return JOptionPane.showOptionDialog(frame, "Which action should be performed?", "Connection Configuration",
				JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, null);

	}

	private int adapterModeModal() {
		Object[] options = { "Connect to Adapter", "Disconnect from Adapter" };
		return JOptionPane.showOptionDialog(frame, "Which action should be performed?", "Device Configuration",
				JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, null);

	}
	
	private void sendStringProcedure(Programmable d) {
		try {
			d.sendMessage();
			alert("The manual request has been processed.");
		} catch (IllegalOperationException e1) {
			alert(e1.getMessage());
		}
	}
	
	private void sendDoubleProcedure(Programmable d) {
		try {
			d.sendValue();
			alert("The manual request has been processed.");
		} catch (IllegalOperationException e1) {
			alert(e1.getMessage());
		}
	}

	private void sendBooleanProcedure(Programmable d) {
		try {
			if (d.isOn())
				d.setOff();
			else
				d.setOn();
			UiFactory.nav(frame, new DevicePage(d.getName()));
		} catch (IllegalOperationException e1) {
			alert(e1.getMessage());
		}
	}
	
	private void addToGroupProcedure(Programmable p) {
		List<Object> opts = new ArrayList<>();
		for (Group g: c.findSuitableGroups(p))
			opts.add(g.getName());

		Object[] options = opts.toArray();

		String choice = (String) JOptionPane.showInputDialog(frame, "Which group?", "Adding to Group",
				JOptionPane.PLAIN_MESSAGE, null, options, null);

		if (choice == null)
			return; // ignore because the user closed the modal or no group was selected

		try {
			Group g = c.addToGroupByName(choice, p);
			JOptionPane.showMessageDialog(null, "Added to group '" + choice + "'.");
			UiFactory.nav(frame, new GroupPage(g.getName()));
		} catch (IncompatibleProtocolException e) {
			alert("Upon recalculation, it was noticed that this group and device do not have compatible protocols.");
		} catch (IllegalOperationException e1) {
			alert(e1.getMessage());
		}
	}
	
	private boolean isAlreadyListener(Programmable p, Programmable target) throws IllegalOperationException {
		for (Programmable lis: p.getDevice().getListeners()) {
			if (lis.getName().equals(target.getName())) return true;
		}
		return false;
	}
	
	private void attachDisattachProcedureGroup(Group g, String id) {
		int mode = attachModeModal();
		
		switch (mode) {
		case 0: // attach
			try {
				Object[] options = c.findAttachableDevicesGroup(g).toArray();
				String choice = (String) JOptionPane.showInputDialog(frame, "Attach to which device?",
						"Group Configuration", JOptionPane.PLAIN_MESSAGE, null, options, null);
				if (choice == null)
					return;
				
				Programmable target = c.getDeviceByName(choice);
				for (Programmable p: g.getMembers()) {
					if (!isAlreadyListener(p,target)) p.attach(target);
				}

				alert("All members of this " + id + " are now attached to '" + target.getName() + "'.");
			} catch (IllegalOperationException | NoSuchElementException | IncompatibleProtocolException e1) {
				alert(e1.getMessage());
			}
			break;
		case 1: // dis
			try {
				Object[] options = c.findAttachableDevicesGroup(g).toArray();
				String choice = (String) JOptionPane.showInputDialog(frame, "Disattach from which device?",
						"Group Configuration", JOptionPane.PLAIN_MESSAGE, null, options, null);
				if (choice == null)
					return;
				
				Programmable target = c.getDeviceByName(choice);
				for (Programmable p: g.getMembers()) {
					p.disattach(target);
				}

				alert("All members of this " + id + " are now disattached from '" + target.getName() + "'.");
			} catch (IllegalOperationException | NoSuchElementException e3) {
				alert(e3.getMessage());
			}
			break;
		case JOptionPane.CLOSED_OPTION:
			return;
		}
	}

	private void attachDisattachProcedure(Programmable d, String id) {
		int mode = attachModeModal();

		switch (mode) {
		case 0: // attach
			try {
				Object[] options = c.findAttachableDevices(d).toArray();
				String choice = (String) JOptionPane.showInputDialog(frame, "Attach to which device?",
						"Device Configuration", JOptionPane.PLAIN_MESSAGE, null, options, null);
				if (choice == null)
					return;

				Programmable target = null;
				try {
					target = c.getDeviceByName(choice);
				} catch (NoSuchElementException e2) {
					try {
						target = c.getGroupByName(choice).getDevice().getParent();
					} catch (NoSuchElementException e) {
						alert(e2.getMessage());
						return;
					}
				}
				
				d.attach(target);
				alert("This " + id + " is now attached to '" + target.getName() + "'.");
				UiFactory.nav(frame, new DevicePage(d.getName()));
			} catch (IllegalOperationException | IncompatibleProtocolException e1) {
				alert(e1.getMessage());
			}
			break;
		case 1: // dis
			try {
				List<String> opts = new ArrayList<>();
				for (int i = 0; i < d.getDevice().getListeners().size(); i++) {
					opts.add(d.getDevice().getListeners().get(i).getName());
				}
				Object[] options = opts.toArray();
				String choice = (String) JOptionPane.showInputDialog(frame, "Disattach from which device?",
						"Device Configuration", JOptionPane.PLAIN_MESSAGE, null, options, null);
				if (choice == null)
					return;
				Programmable target;
				try {
					target = c.getDeviceByName(choice);
				} catch (NoSuchElementException e1) {
					try {
						target = c.getGroupByName(choice).getDevice().getParent();
					} catch (NoSuchElementException e2) {
						alert(e2.getMessage());
						return;
					}
				}

				d.disattach(target);
				alert("This " + id + " is no longer attached to '" + target.getName() + "'.");
				UiFactory.nav(frame, new DevicePage(d.getName()));
			} catch (IllegalOperationException e3) {
				alert(e3.getMessage());
			}
			break;
		case JOptionPane.CLOSED_OPTION:
			return;
		}
	}

	private void adapterProcedure(Programmable p, String id) {
		int mode = adapterModeModal();

		switch (mode) {
		case 0: // connect
			try {
				Device d = p.getDevice();
				List<Object> objs = new ArrayList<>();

				if (d.booleanListener || d.booleanSender)
					objs.add("Powering on/off");
				if (d.doubleListener || d.doubleSender)
					objs.add("Numeric Data");
				if (d.stringListener || d.stringSender)
					objs.add("Messaging");

				if (objs.size() == 0) {
					alert("This " + id + " is incapable of communicating with other devices.");
					return;
				}
				int choice = adapterModal(objs, p);
				if (choice == JOptionPane.CLOSED_OPTION)
					return;

				Programmable adp;
				adp = c.findSuitableAdapter(p, objs.get(choice).toString());

				if (adp != null) { // can reuse
					int add = confirmAdapterModal(adp);
					if (add == JOptionPane.OK_OPTION) { // reuse=yes
						adp = c.connectAdapter(p, adp, objs.get(choice).toString());
						alert("The adapter '" + adp.getName() + "' was updated.");
						UiFactory.nav(this.frame, new DevicePage(adp.getName()));

					} else if (add == JOptionPane.NO_OPTION) { // reuse=no
						adp = c.createSuitableAdapter(p, objs.get(choice).toString());
						alert("The adapter '" + adp.getName() + "' was created.");
						UiFactory.nav(this.frame, new DevicePage(adp.getName()));
					}

				} else { // no chance to reuse
					adp = c.createSuitableAdapter(p, objs.get(choice).toString());
					alert("The adapter '" + adp.getName() + "' was created.");
					UiFactory.nav(this.frame, new DevicePage(adp.getName()));
				}
			} catch (IllegalOperationException | UnsupportedUtilityException | IncompatibleProtocolException e1) {
				alert(e1.getMessage());
			}
			break;
		case 1: // dis
			try {
				List<String> opts = new ArrayList<>();
				List<Programmable> adps;
				adps = c.findAdaptersUsedByDevice(p);
				for (int i = 0; i < adps.size(); i++) {
					opts.add(adps.get(i).getName());
				}
				Object[] options = opts.toArray();
				String choice = (String) JOptionPane.showInputDialog(frame, "Disconnect from which adapter?",
						"Adapter Configuration", JOptionPane.PLAIN_MESSAGE, null, options, null);
				if (choice == null)
					return;
				Programmable adp = c.getDeviceByName(choice);
				AdapterVisitor v = new AdapterVisitor(p, AdapterVisitor.DELETE_CONNECTIONS);
				adp.acceptVisitor(v);
				alert("This " + id + " is no longer attached to '" + adp.getName() + "'.");
				UiFactory.nav(frame, new DevicePage(p.getName()));
			} catch (IllegalOperationException | IncompatibleProtocolException | NoSuchElementException e) {
				alert(e.getMessage());
			}
			break;
		case JOptionPane.CLOSED_OPTION:
			return;
		}
	}

}
