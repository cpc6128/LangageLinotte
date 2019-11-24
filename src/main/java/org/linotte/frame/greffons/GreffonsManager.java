package org.linotte.frame.greffons;

import static org.linotte.moteur.xml.actions.TildeAction.STATUS.A_JOUR;
import static org.linotte.moteur.xml.actions.TildeAction.STATUS.METTRE_A_JOUR;
import static org.linotte.moteur.xml.actions.TildeAction.STATUS.NOUVEAU;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.linotte.frame.Atelier;
import org.linotte.greffons.GreffonsChargeur;
import org.linotte.greffons.api.Greffon;
import org.linotte.moteur.outils.Ressources;
import org.linotte.moteur.xml.Linotte;
import org.linotte.moteur.xml.actions.TildeAction.STATUS;

@SuppressWarnings("serial")
public class GreffonsManager extends JPanel {

	private GreffonsManager(final JDialog pere, List<BeanGreffon> greffons, Linotte linotte) {
		super(new BorderLayout());

		JTable table = new JTable(new GreffonTableModel(greffons));
		table.setPreferredScrollableViewportSize(new Dimension(1000, 500));
		table.setFillsViewportHeight(true);
		table.setAutoCreateRowSorter(true);

		JScrollPane scrollPane = new JScrollPane(table);
		table.setDefaultRenderer(BeanGreffon.class, new JButtonRenderer());
		table.setDefaultEditor(BeanGreffon.class, new JButtonEditor(linotte));
		table.getColumn(table.getColumnName(1)).setCellEditor(new TextAreaCellEditor());
		table.getColumn(table.getColumnName(1)).setCellRenderer(new TextAreaCellRenderer());
		table.setRowHeight(64);
		add(scrollPane, BorderLayout.CENTER);
		JButton button = new JButton("Fermer le manageur");
		add(button, BorderLayout.SOUTH);
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				pere.setVisible(false);
				pere.dispose();
			}
		});
	}

	public static void ouvrir(Linotte linotte, Atelier atelier, String url) {
		JDialog jd = new JDialog(atelier, "Manageur de greffons", true);
		jd.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		jd.setIconImage(Ressources.getImageIcon("applications-other.png").getImage());

		BeanHandler handler = new BeanHandler();
		List<BeanGreffon> beans = handler.chargerBeanGreffon(url);
		for (BeanGreffon beanGreffon : beans) {
			Greffon greffon = GreffonsChargeur.getInstance().getGreffon(beanGreffon.getNom());
			double versionDemandee = Double.valueOf(beanGreffon.getVersion());
			STATUS etat;
			if (greffon == null) {
				// Greffon n'est pas present :
				etat = NOUVEAU;
			} else {
				// Mise à jour d'un greffon :
				double versionActuelle = Double.valueOf(greffon.getVersion());
				etat = versionDemandee > versionActuelle ? METTRE_A_JOUR : A_JOUR;
			}
			beanGreffon.setStatut(etat);
		}

		JComponent newContentPane = new GreffonsManager(jd, beans, linotte);
		jd.setContentPane(newContentPane);

		jd.setResizable(false);
		jd.pack();
		jd.setLocationRelativeTo(atelier);
		jd.setVisible(true);
	}

}
