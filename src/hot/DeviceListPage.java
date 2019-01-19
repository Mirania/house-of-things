package hot;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import devices.StatusVisitor;
import exceptions.IllegalOperationException;
import exceptions.IncompatibleProtocolException;
import exceptions.UnsupportedUtilityException;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import ui.ButtonEditor;
import ui.ButtonRenderer;
import ui.UiFactory;
import hot.CommandCenter;
import interfaces.Programmable;
import static ui.UiFactory.alert;

public class DeviceListPage extends Page {

	private JFrame frame;
	private GridBagConstraints cn;
	private StatusVisitor v;
	private CommandCenter c;
	private DefaultTableModel model;

	@Override
	public void setTarget(JFrame frame) {
		this.frame = frame;
		cn = new GridBagConstraints();
		v = new StatusVisitor();
		c = CommandCenter.getInstance();
	}

	@Override
	public void collectInformation() {
		model = new DefaultTableModel();

		ArrayList<Object[]> rows = new ArrayList<>();

		for (Programmable d : c) {
			try {
				rows.add(new Object[] { d.getName(), d.acceptVisitor(v) });
			} catch (IllegalOperationException | IncompatibleProtocolException e1) {
				rows.add(new Object[] { d.getName(), "-" });
				System.err.println("Error visiting device: " + e1.getMessage());
			}
		}

		Object[][] vector = new Object[rows.size()][2];
		for (int i = 0; i < rows.size(); i++) {
			vector[i] = rows.get(i);
		}

		model.setDataVector(vector, new Object[] { "Device name", "Device status" });
	}

	@Override
	public void populateWindow() {
		JPanel mainPanel = UiFactory.createPanel();

		JButton btnMainPage = UiFactory.createNavButton("Dashboard", this.frame, new MainPage());

		JButton listbutton = UiFactory.createBasicButton();
		listbutton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				UiFactory.nav(frame, new DevicePage(listbutton.getText()));
			}
		});

		JScrollPane scroll = UiFactory.createScrollableTable(model, "Device name", UiFactory.createButtonEditor(listbutton));
		
		JButton createDevice = UiFactory.createStandardButton("Add Device");
		createDevice.addActionListener(e -> {
			deviceModal();
		});

		cn.fill = GridBagConstraints.HORIZONTAL;
		cn.gridx = 0;
		cn.gridy = 0;
		mainPanel.add(btnMainPage, cn);
		cn.gridx = 0;
		cn.gridy = 2;
		mainPanel.add(createDevice, cn);
		cn.gridx = 0;
		cn.gridy = 1;
		cn.weightx = 1;
		cn.weighty = 1;
		cn.fill = GridBagConstraints.BOTH;
		mainPanel.add(scroll, cn);

		frame.getContentPane().add(BorderLayout.CENTER, mainPanel);
	}

	@Override
	public void validatePage() {
		frame.getContentPane().validate();
	}

	private void deviceModal() {
		Object[] options = { "Lamp", "Switch", "Temperature Sensor", "Temperature Logger", "Luminosity Sensor",
				"Luminosity Logger", "Door Lock", "Other (Custom)" };
		String choice = (String) JOptionPane.showInputDialog(frame, "What type of device should be created?",
				"Device Creation", JOptionPane.PLAIN_MESSAGE, null, options, null);

		Programmable p;

		if (choice == null)
			return; // ignore because the user simply closed the modal

		if (!choice.equals("Other (Custom)")) {
			try {
				String pro = protocolModal();
				if (pro==null) return; //closed 2nd modal
				p = c.addDeviceByType(choice);
				p.setProtocol(pro);
				alert("The device '" + p.getName() + "' was created.");
				UiFactory.nav(frame, new DevicePage(p.getName()));
			} catch (UnsupportedUtilityException e) {
				// ignore because the user simply closed the modal
			}
		} else {
			customDeviceModal();
		}

	}
	
	private String protocolModal() {
		JTextField protocol = new JTextField();

		Object[] fields = { "Protocol: (empty = Universal)", protocol };
		
		int choice = JOptionPane.showConfirmDialog(frame, fields, "Device Creation", JOptionPane.OK_CANCEL_OPTION);
		
		String actualprotocol = protocol.getText();
		
		if (choice==JOptionPane.CLOSED_OPTION || choice==JOptionPane.CANCEL_OPTION) return null;
		else if (actualprotocol==null || actualprotocol.equals("")) return "Universal";
		else return actualprotocol;
	}

	private void customDeviceModal() {
		JTextField protocol = new JTextField();
		JCheckBox bs = new JCheckBox("Can send power on/off commands");
		JCheckBox bl = new JCheckBox("Can receive power on/off commands");
		JCheckBox ds = new JCheckBox("Can send numeric data");
		JCheckBox dl = new JCheckBox("Can read numeric data");
		JCheckBox ss = new JCheckBox("Can send textual messages");
		JCheckBox sl = new JCheckBox("Can log textual messages");

		Object[] fields = { "Protocol: (empty = Universal)", protocol, "Functionality:", bs, bl, ds, dl, ss, sl };

		JOptionPane.showConfirmDialog(frame, fields, "Device Creation", JOptionPane.OK_CANCEL_OPTION);

		String actualprotocol = protocol.getText();
		if (actualprotocol==null || actualprotocol.equals("")) actualprotocol = "Universal";
		Programmable p = c.addCustomDevice(
				actualprotocol, 
				new boolean[] {bs.isSelected(),bl.isSelected(),ds.isSelected(),
						dl.isSelected(),ss.isSelected(),sl.isSelected()});
		alert("The device '" + p.getName() + "' was created.");
		UiFactory.nav(frame, new DevicePage(p.getName()));

	}

}
