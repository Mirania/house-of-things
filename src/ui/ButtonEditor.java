package ui;

import java.awt.Component;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JTable;

public class ButtonEditor extends DefaultCellEditor {

	private static final long serialVersionUID = 4833917328751832004L;

	protected JButton button;
	protected String label;

	public ButtonEditor(JButton buttonWithActionListener) {
		super(new JCheckBox());
		this.button = buttonWithActionListener;
		this.button.setOpaque(true);
	}

	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		if (isSelected) {
			button.setForeground(table.getSelectionForeground());
			button.setBackground(table.getSelectionBackground());
		} else {
			button.setForeground(table.getForeground());
			button.setBackground(table.getBackground());
		}
		label = (value == null) ? "" : value.toString();
		button.setText(label);

		return button;
	}

	public Object getCellEditorValue() {
		return new String(label);

	}

}