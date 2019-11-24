package org.linotte.frame.greffons;

import java.awt.Component;

import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellRenderer;

import org.linotte.moteur.outils.Ressources;
import org.linotte.moteur.xml.actions.TildeAction.STATUS;

@SuppressWarnings("serial")
public class JButtonRenderer extends JButton implements TableCellRenderer {

	public JButtonRenderer() {
	}

	public Component getTableCellRendererComponent(JTable table, Object obj, boolean isSelected, boolean hasFocus, int row, int column) {
		BeanGreffon beanGreffon = (BeanGreffon) obj;
		if (beanGreffon != null) {
			if (column == 0) {
				setText(beanGreffon.getNom());
				setEnabled(true);
				setIcon(Ressources.getImageIcon("post.png"));
				setHorizontalAlignment(SwingConstants.LEFT);
			} else {
				STATUS s = beanGreffon.getStatut();
				if (s == STATUS.METTRE_A_JOUR || s == STATUS.NOUVEAU) {
					setText("Télécharger...");
					setEnabled(true);
					setIcon(Ressources.getImageIcon("applications-other.png"));
				} else {
					setEnabled(false);
					setText("");
					setIcon(null);
				}
			}
		} else {
			setEnabled(false);
			setText("");
		}
		return this;
	}
}
