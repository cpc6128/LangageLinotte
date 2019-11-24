package org.linotte.frame.greffons;

import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.linotte.moteur.xml.actions.TildeAction.STATUS;

@SuppressWarnings("serial")
class GreffonTableModel extends AbstractTableModel {
	private String[] columnNames = { "Nom", "Description", "Version", "Etat", "Action" };
	private Object[][] data;

	public GreffonTableModel(List<BeanGreffon> greffons) {
		data = new Object[greffons.size()][columnNames.length];
		int i = 0;
		for (BeanGreffon beanGreffon : greffons) {
			data[i][0] = beanGreffon;
			data[i][1] = beanGreffon.getDescription();
			data[i][2] = beanGreffon.getVersion();
			data[i][3] = beanGreffon.getStatut().name();
			data[i][4] = beanGreffon;
			i++;
		}
	}

	public int getColumnCount() {
		return columnNames.length;
	}

	public int getRowCount() {
		return data.length;
	}

	public String getColumnName(int col) {
		return columnNames[col];
	}

	public Object getValueAt(int row, int col) {
		return data[row][col];
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Class getColumnClass(int c) {
		Object cz = getValueAt(0, c);
		if (cz != null)
			return cz.getClass();
		return null;
	}

	public boolean isCellEditable(int row, int col) {
		try {
			return col == 0
					|| col == 1
					|| (col == 4 && ((((BeanGreffon) getValueAt(row, col)).getStatut() == STATUS.METTRE_A_JOUR) || (((BeanGreffon) getValueAt(row, col))
							.getStatut() == STATUS.NOUVEAU)));
		} catch (Exception e) {
			return false;
		}
	}

	public void setValueAt(Object value, int row, int col) {
		data[row][col] = value;
		fireTableCellUpdated(row, col);
	}

}
