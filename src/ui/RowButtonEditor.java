package ui;

import java.awt.Component;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.TableModel;

public class RowButtonEditor extends ButtonEditor {

	private static final long serialVersionUID = 4833917328751832004L;

	private int row;

	public RowButtonEditor(JButton b) {
		super(b);
	}
	
	public void addActionListener(ActionListener a) {
		button.addActionListener(a);
	}
	
	public int getRow() {
		return row;
	}

	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row,
			int column) {
		this.row = row;
		return super.getTableCellEditorComponent(table, value, isSelected, row, column);
	}
}
