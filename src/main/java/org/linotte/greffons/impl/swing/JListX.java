package org.linotte.greffons.impl.swing;

import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.JList;
import javax.swing.ListModel;

/**
 * 
 * http://www.exampledepot.com/egs/javax.swing/list_ListTt.html
 *
 */
@SuppressWarnings("serial")
public class JListX extends JList {

	private HashMap<Integer, String> tooltips = new HashMap<Integer, String>();

	public JListX() {
	}

	@SuppressWarnings("unchecked")
	public JListX(ListModel dataModel) {
		super(dataModel);
	}

	@SuppressWarnings("unchecked")
	public JListX(Object[] listData) {
		super(listData);
	}

	@SuppressWarnings("unchecked")
	public JListX(Vector<?> listData) {
		super(listData);
	}

	public void setToolTip(Integer pos, String value) {
		tooltips.put(pos, value);
	}

	// This method is called as the cursor moves within the list.
	public String getToolTipText(MouseEvent evt) {
		// Get item index
		int index = locationToIndex(evt.getPoint());

		// Get item
		// Object item = getModel().getElementAt(index);
		String text = tooltips.get(index);

		// Return the tool tip text
		return text;
	}

}
