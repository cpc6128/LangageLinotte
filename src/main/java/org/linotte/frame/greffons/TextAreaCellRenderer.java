package org.linotte.frame.greffons;

import java.awt.Color;
import java.awt.Component;
import java.awt.Rectangle;

import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.TableCellRenderer;

@SuppressWarnings("serial")
class TextAreaCellRenderer extends JTextArea implements TableCellRenderer {
	private final JScrollPane scroll;

	TextAreaCellRenderer() {
		super();
		scroll = new JScrollPane(this);
		setLineWrap(true);
		setWrapStyleWord(true);
		setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		if (isSelected) {
			setForeground(table.getSelectionForeground());
			setBackground(table.getSelectionBackground());
		} else {
			setForeground(table.getForeground());
			setBackground(table.getBackground());
		}
		setFont(table.getFont());
		setText((value == null) ? "" : value.toString());
		return scroll;
	}

	//Overridden for performance reasons. ---->
	@Override
	public boolean isOpaque() {
		Color back = getBackground();
		Component p = getParent();
		if (p != null) {
			p = p.getParent();
		} // p should now be the JTable.
		boolean colorMatch = (back != null) && (p != null) && back.equals(p.getBackground()) && p.isOpaque();
		return !colorMatch && super.isOpaque();
	}

	@Override
	protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
		//String literal pool
		//if(propertyName=="document" || ((propertyName == "font" || propertyName == "foreground") && oldValue != newValue)) {
		if ("document".equals(propertyName) || (("font".equals(propertyName) || "foreground".equals(propertyName)) && oldValue != newValue)) {
			super.firePropertyChange(propertyName, oldValue, newValue);
		}
	}

	@Override
	public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {
	}

	@Override
	public void repaint(long tm, int x, int y, int width, int height) {
	}

	@Override
	public void repaint(Rectangle r) {
	}

	@Override
	public void repaint() {
	}

	@Override
	public void invalidate() {
	}

	@Override
	public void validate() {
	}

	@Override
	public void revalidate() {
	}
	//<---- Overridden for performance reasons.
}
