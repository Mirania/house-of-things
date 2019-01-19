package hot;

import static ui.UiFactory.alert;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import javax.swing.table.TableModel;

import devices.ActionVisitor;
import devices.TableInfoVisitor;
import exceptions.IllegalOperationException;
import exceptions.IncompatibleProtocolException;
import exceptions.NoSuchElementException;
import interfaces.Group;
import interfaces.Programmable;
import ui.ButtonEditor;
import ui.ButtonRenderer;
import ui.RowButtonEditor;
import ui.UiFactory;

public class GroupPage extends Page {

	private JFrame frame;
	private GridBagConstraints cn;
	private String target;
	private CommandCenter c;
	private Group p;
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

	private GroupPage() {
	}

	public GroupPage(String t) {
		target = t;
	}

	@Override
	public void collectInformation() {
		try {
			p = c.getGroupByName(target);
		} catch (NoSuchElementException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			UiFactory.nav(this.frame, new GroupListPage());
		}

		model = new DefaultTableModel();
		Object[][] vector;

		try {
			vector = p.acceptVisitor(tv);
			model.setDataVector(vector, new Object[] { "Entry", "Action" });
			specificActions = p.acceptVisitor(av);
		} catch (IllegalOperationException | IncompatibleProtocolException e) {
			JOptionPane.showMessageDialog(null, "Error gathering info: " + e.getMessage());
			UiFactory.nav(this.frame, new GroupListPage());
		}

	}

	@Override
	public void populateWindow() {
		JPanel mainPanel = UiFactory.createPanel();

		JButton btnMainPage = UiFactory.createNavButton("Dashboard", this.frame, new MainPage());

		JButton btnGroupListPage = UiFactory.createNavButton("Group List", this.frame, new GroupListPage());

		JLabel name = new JLabel("Group name");
		JTextField nameinput = new JTextField(25);
		nameinput.setText(p.getName());
		nameinput.addActionListener(e -> {
			if (c.groupNameIsValid(nameinput.getText()))
				p.setName(nameinput.getText());
			else
				alert("This name is either invalid or already exists.");
		});

		JLabel protocol = new JLabel("Protocol");
		JLabel protocolvalue = new JLabel(p.getProtocol());

		JButton entrybutton = UiFactory.createBasicButton();
		entrybutton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					c.getGroupByName(entrybutton.getText());
					UiFactory.nav(frame, new GroupPage(entrybutton.getText()));
				} catch (NoSuchElementException e1) {
					UiFactory.nav(frame, new DevicePage(entrybutton.getText()));
				}
			}
		});

		RowButtonEditor actionbuttoneditor = UiFactory.createRowButtonEditor();
		actionbuttoneditor.addActionListener(e -> {
			String n = model.getValueAt(actionbuttoneditor.getRow(), 0).toString();
			c.removeFromGroupByName(p.getName(), n);
			alert("Device '" + n + "' removed.");
			UiFactory.nav(frame, new GroupPage(p.getName()));			
		});
		
		JScrollPane scroll = UiFactory.createScrollableTable(model, 
				"Entry", UiFactory.createButtonEditor(entrybutton),
				"Action", actionbuttoneditor);
		
		JButton copybtn = UiFactory.createStandardButton("Copy Group");
		copybtn.addActionListener(e -> {
			Group n = c.addGroup(p.copyGroup());
			if (n==null) {
				alert("The system could not successfully copy the intended group.");
				return;
			}
			alert("An independent copy named '" + n.getName() + "' was created.");
			UiFactory.nav(frame, new GroupPage(n.getName()));
		});

		JButton delbtn = UiFactory.createStandardButton("Delete Group");
		delbtn.addActionListener(e -> {
			c.deleteGroupByName(p.getName());
			alert("Group '" + p.getName() + "' deleted.");
			UiFactory.nav(this.frame, new GroupListPage());
		});
		
		JButton addbtn = UiFactory.createStandardButton("Add Device");
		addbtn.addActionListener(e -> {
			addToGroupProcedure(p);
		});

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
		cn.gridx = 0;
		cn.gridy = 4;
		mainPanel.add(addbtn, cn);
		cn.gridx = 1;
		cn.gridy = 4;
		mainPanel.add(copybtn, cn);
		cn.gridx = 2;
		cn.gridy = 4;
		mainPanel.add(delbtn, cn);
		
		for (int i=0; i<specificActions.size(); i++) {
			cn.gridx = i;
			cn.gridy = 5;
			mainPanel.add(specificActions.get(i), cn);
		}
		
		cn.gridwidth = GridBagConstraints.REMAINDER;
		cn.gridx = 1;
		cn.gridy = 2;
		mainPanel.add(protocolvalue, cn);
		cn.gridx = 1;
		cn.gridy = 1;
		mainPanel.add(nameinput, cn);
		cn.gridx = 1;
		cn.gridy = 0;
		mainPanel.add(btnGroupListPage, cn);
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
	
	private void addToGroupProcedure(Group g) {
		try {
			Object[] options = c.findPotentialGroupMembers(g).toArray();
			String choice = (String) JOptionPane.showInputDialog(frame, "Add which entity?",
					"Group Configuration", JOptionPane.PLAIN_MESSAGE, null, options, null);
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
			
			g.add(target);
			alert("The device '"+target.getName()+"' has been added.");
			UiFactory.nav(frame, new GroupPage(g.getName()));
		} catch (IllegalOperationException e) {
			alert(e.getMessage());
		} catch (IncompatibleProtocolException e1) {
			alert("Upon recalculation, it was noticed that this group and device do not have compatible protocols.");
			UiFactory.nav(frame, new GroupPage(g.getName()));
		}
	}

	private class ActionButtonEditor extends ButtonEditor {

		private static final long serialVersionUID = 4833917328751832004L;

		private int row;

		public ActionButtonEditor(JButton b, TableModel model) {
			super(b);
			button.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					String name = model.getValueAt(row, 0).toString();
					c.removeFromGroupByName(p.getName(), name);
					alert("Device '" + name + "' removed.");
					UiFactory.nav(frame, new GroupPage(p.getName()));
				}
			});
		}

		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row,
				int column) {
			this.row = row;
			return super.getTableCellEditorComponent(table, value, isSelected, row, column);
		}
	}

}
