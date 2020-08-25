package org.linotte.greffons.impl.swing;

import org.linotte.greffons.externe.Composant;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author R.M
 * 
 */
public class Table extends ComposantDeplacable {

	private JTable table;
	private JScrollPane scrollPane;
	private JTableModel model;

	@Override
	public void initialisation() throws GreffonException {
		if (table != null) {
			super.initEvenement();
			return;
		}
		setVisible(getAttributeAsString("visible").equals("oui"));
		model = new JTableModel();
		table = new JTable(model);
		scrollPane = new JScrollPane(table);
		table.setFillsViewportHeight(true);
		// Position :
		x = getAttributeAsBigDecimal("x").intValue();
		y = getAttributeAsBigDecimal("y").intValue();
		// Dimensions :
		int hauteur = getAttributeAsBigDecimal("hauteur").intValue();
		int largeur = getAttributeAsBigDecimal("largeur").intValue();
		scrollPane.setPreferredSize(new Dimension(largeur, hauteur));
		table.setMaximumSize(new Dimension(largeur, hauteur));

		table.setVisible(isVisible());
		super.initEvenement();
	}

	@Override
	public void ajouterComposant(Composant pcomposant) throws GreffonException {
	}

	@Override
	public JComponent getJComponent() throws GreffonException {
		return scrollPane;
	}

	@Override
	public boolean fireProperty(String clef) throws GreffonException {
		if (!super.fireProperty(clef)) {
		}
		return false;
	}

	@Override
	public void destruction() throws GreffonException {
		if (table != null) {
			// menu.dispose();
			table = null;
		}
	}

	@Override
	public boolean enregistrerPourDestruction() {
		return true;
	}

	/*
	 * Méthodes fonctionnelles :
	 */

	@Slot
	public boolean colonnes(List<String> colonnes) {
		model.columnNames = colonnes.toArray(new String[colonnes.size()]);
		model.fireTableStructureChanged();
		return true;
	}

	@Slot
	public boolean ajouterligne() {
		model.matrix.add(new ArrayList<TableData>());
		model.fireTableDataChanged();
		return true;
	}
	
	@Slot
	public boolean supprimerligne(int index) throws GreffonException {	
		try {
			model.matrix.remove(index);
			model.fireTableDataChanged();
		} catch (Exception e) {
			throw new GreffonException("Impossible de supprimer la ligne");
		}
		return true;
	}	

	@Slot
	public boolean valeur(int ligne, int col, Acteur valeur) throws GreffonException {
		if (valeur.getRole() == ROLE.TEXTE || valeur.getRole() == ROLE.NOMBRE) {
			try {
				model.setValueAt(valeur.getValeur(), ligne, col, valeur.getRole());
			} catch (Exception e) {
				throw new GreffonException("Impossible d'affecter cette valeur dans le tableau");
			}
		}
		return true;
	}
	
	@Slot(nom = "retounesélection")
	public List<BigDecimal> retouneselection() {		
		
		int[] intList = table.getSelectedRows();
		
		List<BigDecimal> lignes = new ArrayList<BigDecimal>();
		for (int index = 0; index < intList.length; index++)
		{
			int i = intList[index];
			BigDecimal j = new BigDecimal(i);
			lignes.add(j);
		}

		return lignes;
	}

	@Slot
	public Acteur retournevaleur(int ligne, int col) throws GreffonException {
		try {
			TableData o = model.getValueAtData(ligne, col);
			return new Acteur(o.role, "", o.o);
		} catch (Exception e) {
			throw new GreffonException("Impossible de récupérer cette valeur dans le tableau");
		}
	}

	@Slot(nom = "écriture")
	public boolean ecriture(int ligne, int col, boolean etat) throws GreffonException {
		try {
			TableData o = model.getValueAtData(ligne, col);
			if (o != null) {
				o.editable = etat;
			}
			return true;
		} catch (Exception e) {
			throw new GreffonException("Impossible d'affecter cette valeur dans le tableau");
		}
	}

	/*
	 * Classes internes
	 */

	private class TableData {

		public TableData(Object o, boolean editable, ROLE role) {
			super();
			this.o = o;
			this.editable = editable;
			this.role = role;
		}

		Object o;
		boolean editable;
		ROLE role;

	}

	@SuppressWarnings("serial")
	private class JTableModel extends AbstractTableModel {
		private String[] columnNames = { "vide" };
		private List<List<TableData>> matrix = new ArrayList<List<TableData>>();

		public int getColumnCount() {
			return columnNames.length;
		}

		public int getRowCount() {
			return matrix.size();
		}

		public String getColumnName(int col) {
			return columnNames[col];
		}

		public Object getValueAt(int row, int col) {
			try {
				return matrix.get(row).get(col).o;
			} catch (Exception e) {
				return null;
			}
		}

		public TableData getValueAtData(int row, int col) {
			try {
				return matrix.get(row).get(col);
			} catch (Exception e) {
				return null;
			}
		}

		/*
		 * JTable uses this method to determine the default renderer/
		 * editor for each cell.  If we didn't implement this method,
		 * then the last column would contain text ("true"/"false"),
		 * rather than a check box.
		 */
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public Class getColumnClass(int c) {
			Object o = null;
			try {
				o = getValueAtData(0, c).o;
			} catch (Exception e) {
			}
			if (o != null)
				return o.getClass();
			else
				return String.class;
		}

		/*
		 * Don't need to implement this method unless your table's
		 * editable.
		 */
		public boolean isCellEditable(int row, int col) {
			return getValueAtData(row, col).editable;
		}

		public void setValueAt(Object value, int row, int col) {
			getValueAtData(row, col).o = value;
		}

		/*
		* Don't need to implement this method unless your table's
		* data can change.
		*/
		public void setValueAt(Object value, int row, int col, ROLE role) {
			if (matrix.size() < row) {
				int s = matrix.size();
				for (int i = s; i <= row; i++) {
					matrix.add(new ArrayList<TableData>());
				}
			}
			List<TableData> ligne = matrix.get(row);
			if (ligne.size() <= col) {
				int s = ligne.size();
				for (int i = s; i <= col; i++) {
					ligne.add(null);
				}
			}
			TableData ne_w = new TableData(value, false, role);
			TableData old = ligne.set(col, ne_w);
			if (old != null) {
				ne_w.editable = old.editable;
			}
			fireTableCellUpdated(row, col);
		}	
	}

}
