package hot;

import static ui.UiFactory.alert;
import static ui.UiFactory.scrollAlert;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import devices.UtilityFactory;
import exceptions.UnsupportedUtilityException;
import ui.UiFactory;

public class MainPage extends Page {
	JFrame frame;
	CommandCenter c;
	GridBagConstraints cn;
	Logger logger;

	@Override
	public void setTarget(JFrame frame) {
		this.frame = frame;
		c = CommandCenter.getInstance();
		logger = Logger.getInstance();
		cn = new GridBagConstraints();
	}

	@Override
	public void collectInformation() {
	}

	@Override
	public void populateWindow() {
		JPanel mainPanel = UiFactory.createPanel();

		JButton btnListDevices = UiFactory.createNavButton("List Devices", this.frame, new DeviceListPage());

		JButton btnListGroups = UiFactory.createNavButton("List Groups", this.frame, new GroupListPage());

		JButton btnsave = UiFactory.createStandardButton("Create Restore Point");
		btnsave.addActionListener(e -> {
			saveMementoProcedure();
		});

		JButton btnload = UiFactory.createStandardButton("Load Restore Point");
		btnload.addActionListener(e -> {
			loadMementoProcedure();
		});
		
		JButton btnrlogs = UiFactory.createStandardButton("Redirect Logs");
		btnrlogs.addActionListener(e -> {
			redirectLogsProcedure();
		});
		
		JButton btnlogs = UiFactory.createStandardButton("Check Logs");
		btnlogs.addActionListener(e -> {
			StringBuilder sb = new StringBuilder();
			for (String l: logger.getLogs())
				sb.append(l).append("\n");
			scrollAlert(sb.toString());
		});

		cn.fill = GridBagConstraints.HORIZONTAL;
		cn.gridx = 0;
		cn.gridy = 0;
		mainPanel.add(btnListDevices, cn);
		cn.gridx = 0;
		cn.gridy = 1;
		mainPanel.add(btnListGroups, cn);
		cn.gridx = 0;
		cn.gridy = 2;
		mainPanel.add(btnsave, cn);
		cn.gridx = 0;
		cn.gridy = 3;
		mainPanel.add(btnload, cn);
		cn.gridx = 0;
		cn.gridy = 4;
		mainPanel.add(btnlogs, cn);
		cn.gridx = 0;
		cn.gridy = 5;
		mainPanel.add(btnrlogs, cn);

		frame.getContentPane().add(BorderLayout.CENTER, mainPanel);
	}

	@Override
	public void validatePage() {
		frame.getContentPane().validate();
	}
	
	private void redirectLogsProcedure() {
		Object[] options = new Object[] { "Slack", "Facebook", "Telegram", "Nowhere" };
		int choice = JOptionPane.showOptionDialog(frame, "Where else should logs be sent to?", "Logger Configuration",
				JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, null);
		
		try {
			switch (choice) {
			case 0:
				logger.setState(UtilityFactory.createLoggerState("Slack"));
				break;
			case 1:
				logger.setState(UtilityFactory.createLoggerState("Facebook"));
				break;
			case 2:
				logger.setState(UtilityFactory.createLoggerState("Telegram"));
				break;
			case 3:
				logger.setState(UtilityFactory.createLoggerState("Offline"));
				break;
			case JOptionPane.CLOSED_OPTION:
				return;
			}
			alert("The logger has been redirected.");
		} catch (UnsupportedUtilityException e) {
			alert(e.getMessage());
		}

	}

	private void saveMementoProcedure() {
		JTextField name = new JTextField();

		Object[] fields = { "Give this restore point a name: (optional)", name };

		int choice = JOptionPane.showConfirmDialog(frame, fields, "Restore Point Creation", JOptionPane.OK_CANCEL_OPTION);
		
		if (choice==JOptionPane.CANCEL_OPTION || choice==JOptionPane.CLOSED_OPTION) return;

		String n = name.getText();
		if (n == null || n.equals(""))
			n = "no name";
		c.save(n);
		alert("A new restore point has been created.");
	}

	private void loadMementoProcedure() {
		List<String> entries = new ArrayList<>();
		for (CommandCenter.Memento mem : c.getRestorePoints())
			entries.add(mem.getDate() + " [" + mem.getName() + "]");

		Object[] options = entries.toArray();
		String choice = (String) JOptionPane.showInputDialog(frame, "Which restore point?", "Load Restore Point",
				JOptionPane.PLAIN_MESSAGE, null, options, null);
		if (choice == null)
			return;

		for (int i = 0; i < options.length; i++) {
			if (choice.equals(options[i])) {
				try {
					c.load(i);
					alert("The system has been restored to a previous moment.");
					return;
				} catch (ArrayIndexOutOfBoundsException e) {
					alert("Could not find and apply the chosen restore point.");
					return;
				}
			}
		}

		alert("Could not find and apply the chosen restore point.");
	}

}
