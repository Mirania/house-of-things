package hot;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import devices.ActionVisitor;
import devices.TableInfoVisitor;
import exceptions.IllegalOperationException;
import exceptions.IncompatibleProtocolException;
import exceptions.NoSuchElementException;
import exceptions.UnsupportedUtilityException;
import interfaces.Group;
import interfaces.Programmable;
import ui.UiFactory;
import static ui.UiFactory.alert;

public class DevicePage extends Page {

	private JFrame frame;
	private GridBagConstraints cn;
	private String target;
	private CommandCenter c;
	private Programmable p;
	private DefaultTableModel model;
	private TableInfoVisitor tv;
	private ActionVisitor av;
	List<JButton> specificActions;

	@Override
	public void setTarget(JFrame frame) {
		this.frame = frame;
		cn = new GridBagConstraints();
		c = CommandCenter.getInstance();
		tv = new TableInfoVisitor();
		av = new ActionVisitor(frame);
	}

	private DevicePage() {
	}

	public DevicePage(String t) {
		target = t;
	}

	@Override
	public void collectInformation() {
		try {
			p = c.getDeviceByName(target);
		} catch (NoSuchElementException e) {
			alert(e.getMessage());
			UiFactory.nav(this.frame, new DeviceListPage());
		}

		model = new DefaultTableModel();
		Object[][] vector;

		try {
			vector = p.acceptVisitor(tv);
			model.setDataVector(vector, new Object[] { "Description", "Value" });
			specificActions = p.acceptVisitor(av);
		} catch (IllegalOperationException | IncompatibleProtocolException e) {
			alert(e.getMessage());
			UiFactory.nav(this.frame, new DeviceListPage());
		}

	}

	@Override
	public void populateWindow() {
		JPanel mainPanel = UiFactory.createPanel();

		JButton btnMainPage = UiFactory.createNavButton("Dashboard", this.frame, new MainPage());

		JButton btnDeviceListPage = UiFactory.createNavButton("Devices List", this.frame, new DeviceListPage());

		
		JButton copybtn = UiFactory.createStandardButton("Copy Device");
		copybtn.addActionListener(e -> {
			Programmable n = c.add(p.copy());
			if (n==null) {
				alert("The system could not successfully copy the intended device.");
				return;
			}
			alert("An independent copy named '" + n.getName() + "' was created.");
			UiFactory.nav(frame, new DevicePage(n.getName()));
		});

		JButton delbtn = UiFactory.createStandardButton("Delete Device");
		delbtn.addActionListener(e -> {
			try {
				c.deleteByName(p.getName());
				alert("Device '" + p.getName() + "' deleted.");
				UiFactory.nav(this.frame, new DeviceListPage());
			} catch (IllegalOperationException | IncompatibleProtocolException e1) {
				alert(e1.getMessage());
			}
		});

		JLabel name = new JLabel("Device name");
		JTextField nameinput = new JTextField(25);
		nameinput.setText(p.getName());
		nameinput.addActionListener(e -> {
			if (c.nameIsValid(nameinput.getText()))
				p.setName(nameinput.getText());
			else
				alert("This name is either invalid or already exists.");
		});

		JLabel protocol = new JLabel("Protocol");
		JLabel protocolvalue = new JLabel(p.getProtocol());

		JScrollPane scroll = UiFactory.createScrollableTable(model);

		cn.fill = GridBagConstraints.HORIZONTAL;
		cn.gridx = 0;
		cn.gridy = 0;
		mainPanel.add(btnMainPage, cn);
		cn.gridx = 0;
		cn.gridy = 1;
		mainPanel.add(name, cn);
		cn.gridx = 0;
		cn.gridy = 2;
		mainPanel.add(protocol, cn);
		
		if (specificActions.size()>0) {
			cn.gridx = 0;
			cn.gridy = 4;
			mainPanel.add(specificActions.get(0), cn);	
			cn.gridx = 1;
			cn.gridy = 4;
			mainPanel.add(copybtn, cn);
		} else {
			cn.gridx = 0;
			cn.gridy = 4;
			mainPanel.add(copybtn, cn);
		}

		for (int i=1; i<specificActions.size(); i++) {
			if (i<5) {
				cn.gridx = i-1;
				cn.gridy = 5;
				mainPanel.add(specificActions.get(i), cn);
			} else {
				cn.gridx = i-5;
				cn.gridy = 6;
				mainPanel.add(specificActions.get(i), cn);
			}
		}
		
		cn.gridwidth = GridBagConstraints.REMAINDER;
		cn.gridx = 2;
		cn.gridy = 4;
		mainPanel.add(delbtn, cn);
		cn.gridx = 1;
		cn.gridy = 0;
		mainPanel.add(btnDeviceListPage, cn);
		cn.gridx = 1;
		cn.gridy = 2;
		mainPanel.add(protocolvalue, cn);
		cn.gridx = 1;
		cn.gridy = 1;
		mainPanel.add(nameinput, cn);
		cn.gridx = 0;
		cn.gridy = 3;
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

}
