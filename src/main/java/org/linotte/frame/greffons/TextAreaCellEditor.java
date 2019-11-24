package org.linotte.frame.greffons;

import java.awt.Component;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.table.TableCellEditor;

@SuppressWarnings("serial")
class TextAreaCellEditor extends JTextArea implements TableCellEditor {
	private final JScrollPane scroll;

	public TextAreaCellEditor() {
		scroll = new JScrollPane(this);
		setLineWrap(true);
		setWrapStyleWord(true);
		KeyStroke enter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.CTRL_MASK);
		getInputMap(JComponent.WHEN_FOCUSED).put(enter, new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				stopCellEditing();
			}
		});
		setEditable(false);
	}

	@Override
	public Object getCellEditorValue() {
		return getText();
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		setFont(table.getFont());
		setText((value != null) ? value.toString() : "");
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				setCaretPosition(getText().length());
				requestFocusInWindow();
			}
		});
		return scroll;
	}

	@Override
	public boolean isCellEditable(final EventObject e) {
		if (e instanceof MouseEvent) {
			return ((MouseEvent) e).getClickCount() >= 2;
		}
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				if (e instanceof KeyEvent) {
					KeyEvent ke = (KeyEvent) e;
					char kc = ke.getKeyChar();
					if (Character.isUnicodeIdentifierStart(kc)) {
						setText(getText() + kc);
					}
				}
			}
		});
		return true;
	}

	//Copid from AbstractCellEditor
	//protected EventListenerList listenerList = new EventListenerList();
	transient protected ChangeEvent changeEvent = null;

	@Override
	public boolean shouldSelectCell(EventObject e) {
		return true;
	}

	@Override
	public boolean stopCellEditing() {
		fireEditingStopped();
		return true;
	}

	@Override
	public void cancelCellEditing() {
		fireEditingCanceled();
	}

	@Override
	public void addCellEditorListener(CellEditorListener l) {
		listenerList.add(CellEditorListener.class, l);
	}

	@Override
	public void removeCellEditorListener(CellEditorListener l) {
		listenerList.remove(CellEditorListener.class, l);
	}

	public CellEditorListener[] getCellEditorListeners() {
		return listenerList.getListeners(CellEditorListener.class);
	}

	protected void fireEditingStopped() {
		// Guaranteed to return a non-null array
		Object[] listeners = listenerList.getListenerList();
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == CellEditorListener.class) {
				// Lazily create the event:
				if (changeEvent == null)
					changeEvent = new ChangeEvent(this);
				((CellEditorListener) listeners[i + 1]).editingStopped(changeEvent);
			}
		}
	}

	protected void fireEditingCanceled() {
		// Guaranteed to return a non-null array
		Object[] listeners = listenerList.getListenerList();
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == CellEditorListener.class) {
				// Lazily create the event:
				if (changeEvent == null)
					changeEvent = new ChangeEvent(this);
				((CellEditorListener) listeners[i + 1]).editingCanceled(changeEvent);
			}
		}
	}
}
