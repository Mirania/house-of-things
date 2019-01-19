package hot;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import devices.StatusVisitor;
import devices.UtilityFactory;
import exceptions.IllegalOperationException;
import exceptions.IncompatibleProtocolException;
import exceptions.UnsupportedUtilityException;
import interfaces.Group;
import ui.ButtonEditor;
import ui.ButtonRenderer;
import ui.UiFactory;
import static ui.UiFactory.alert;

public class GroupListPage extends Page {

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

		    for (Group p : c.getGroups()) {
		        try {
					rows.add(new Object[] { p.getName(), p.acceptVisitor(v) });
				} catch (IllegalOperationException | IncompatibleProtocolException e1) {
					rows.add(new Object[] { p.getName(), "-" });
				}
		    }
		    
		    Object[][] vector = new Object[rows.size()][2];
		    for (int i=0; i<rows.size(); i++) {
		    	vector[i] = rows.get(i);
		    }
		    
		    model.setDataVector(vector, new Object[] { "Group name", "Group details" });
	  }

	  @Override
	  public void populateWindow() {
	    JPanel mainPanel = UiFactory.createPanel();

	    JButton btnMainPage = UiFactory.createNavButton("Dashboard", this.frame, new MainPage());
	    
	    JButton listbutton = UiFactory.createBasicButton();
	    listbutton.addActionListener(new ActionListener() {
	        public void actionPerformed(ActionEvent e) {
	        	UiFactory.nav(frame, new GroupPage(listbutton.getText()));
	        }
	      });
	    
	    JScrollPane scroll = UiFactory.createScrollableTable(model, "Group name", UiFactory.createButtonEditor(listbutton));
	    
	    JButton createGroup = UiFactory.createStandardButton("Create Group");
	    createGroup.addActionListener(e -> {
	      groupModal();
	    });

	    cn.fill = GridBagConstraints.HORIZONTAL;
	    cn.gridx = 0;
	    cn.gridy = 0;
	    mainPanel.add(btnMainPage, cn);
	    cn.gridx = 0;
	    cn.gridy = 2;
	    mainPanel.add(createGroup, cn);
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
	  
	  private void groupModal() {
		  Object[] options = {"Powering on/off", "Numeric Data", "Messaging"};
		  int choice = JOptionPane.showOptionDialog(frame,
				    "What should the group be created for?","Group Creation",
				    JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,
				    null, options, null);
		  
		  Group g;
		  
		  try {
			g = c.addGroupByType(choice);
			alert("The group '"+g.getName()+"' was created.");
			UiFactory.nav(frame, new GroupPage(g.getName()));
		} catch (UnsupportedUtilityException e) {
			//ignore because the user simply closed the modal
		}
		  
		  
	  }

}
