package ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagLayout;
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
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import hot.DevicePage;
import hot.Page;

//simple factory
public class UiFactory {

	public static JButton createStandardButton(String str) {
		JButton button = new JButton(str);
		button.setAlignmentX(-0.5f);
		button.setAlignmentY(0.0f);

		return button;
	}

	public static JButton createNavButton(String str, JFrame frame, Page page) {
		JButton button = createStandardButton(str);
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				nav(frame, page);
			}
		});

		return button;
	}

	public static JButton createBasicButton() {
		return new JButton();
	}

	public static RowButtonEditor createRowButtonEditor() {
		return new RowButtonEditor(new JButton());
	}

	public static ButtonEditor createButtonEditor(JButton buttonWithActionListener) {
		return new ButtonEditor(buttonWithActionListener);
	}

	public static JScrollPane createScrollableTable(TableModel model) {
		return new JScrollPane(new JTable(model));
	}

	public static JScrollPane createScrollableTable(TableModel model, String col1, ButtonEditor editor1) {
		JTable table = new JTable(model);

		table.getColumn(col1).setCellRenderer(new ButtonRenderer());
		table.getColumn(col1).setCellEditor(editor1);

		return new JScrollPane(table);
	}

	public static JScrollPane createScrollableTable(TableModel model, String col1, ButtonEditor editor1, 
			String col2, ButtonEditor editor2) {
		JTable table = new JTable(model);

		table.getColumn(col1).setCellRenderer(new ButtonRenderer());
		table.getColumn(col1).setCellEditor(editor1);
		table.getColumn(col2).setCellRenderer(new ButtonRenderer());
		table.getColumn(col2).setCellEditor(editor2);

		return new JScrollPane(table);
	}

	public static JFrame createFrame(String name) {
		JFrame frame = new JFrame(name);
		frame.setSize(new Dimension(600, 600));
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		return frame;
	}

	public static JPanel createPanel() {
		JPanel mainPanel = new JPanel(new GridBagLayout());
		mainPanel.setSize(600, 600);
		return mainPanel;
	}

	public static void nav(JFrame frame, Page page) {
		// cleans the page, removing all components before showing the new page
		Component[] components = frame.getContentPane().getComponents();
		for (Component comp : components) {
			frame.remove(comp);
		}
		page.build(frame);
	}

	public static void scrollAlert(String msg) {
		JTextArea area = new JTextArea(msg);
		JScrollPane pane = new JScrollPane(area) {

			private static final long serialVersionUID = 1L;

			@Override
			public Dimension getPreferredSize() {
				return new Dimension(480, 320);
			}
		};

		alert(pane);
	}

	public static void alert(List<Object> msgs) {
		JOptionPane.showMessageDialog(null, msgs.toArray());
	}

	public static void alert(Object msg) {
		JOptionPane.showMessageDialog(null, msg);
	}

	public static String createHtmlString(String msg, int width) {
		return "<html><body width='" + width + "'><p>" + msg.replaceAll("\n", "<p>");
	}

}
