package org.linotte.frame.greffons;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.table.TableModel;

import org.linotte.frame.latoile.Java6;
import org.linotte.greffons.GestionDesGreffons;
import org.linotte.moteur.xml.Linotte;
import org.linotte.moteur.xml.actions.TildeAction.STATUS;

@SuppressWarnings("serial")
public class JButtonEditor extends DefaultCellEditor {
	private JButton button;
	private BeanGreffon greffon;
	private Linotte linotte;
	private int column, row;
	private TableModel model;

	public JButtonEditor(Linotte plinotte) {
		super(new JCheckBox());
		linotte = plinotte;
		button = new JButton();
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fireEditingStopped();
			}
		});
	}

	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		if (isSelected) {
			button.setForeground(table.getSelectionForeground());
			button.setBackground(table.getSelectionBackground());
		} else {
			button.setForeground(table.getForeground());
			button.setBackground(table.getBackground());
		}
		this.greffon = (BeanGreffon) value;
		this.column = column;
		this.row = row;
		this.model = table.getModel();

		if (column != 0)
			button.setText("En cours de traitement...");

		return button;
	}

	public Object getCellEditorValue() {
		if (column == 0) {
			try {
				Java6.getDesktop().browse(new URI((greffon.getHome())));
			} catch (IOException e) {
				e.printStackTrace();
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
			return greffon;
		} else {
			try {
				String commande = "1 " + greffon.getNom() + " " + greffon.getVersion() + " \"" + greffon.getUrl() + "\"";
				System.out.println("Simulation : " + commande);
				boolean retour = GestionDesGreffons.chargerGreffon(commande, linotte, false);
				if (retour) {
					greffon.setStatut(STATUS.A_JOUR);
					model.setValueAt(STATUS.A_JOUR.name(), row, 3);
					return greffon;
				}
			} catch (Exception e) {
				e.printStackTrace();
				linotte.getIhm().afficherErreur("Impossible de vérifier le greffon : " + e.toString());
			}
			return greffon;
		}
	}

	public boolean stopCellEditing() {
		return super.stopCellEditing();
	}

	protected void fireEditingStopped() {
		super.fireEditingStopped();
	}
}
