/***********************************************************************
 * Linotte                                                             *
 * Version release date : October 26, 2005                             *
 * Author : Mounes Ronan ronan.mounes@amstrad.eu                       *
 *                                                                     *
 *     http://langagelinotte.free.fr                                   *
 *                                                                     *
 * This code is released under the GNU GPL license, version 2 or       *
 * later, for educational and non-commercial purposes only.            *
 * If any part of the code is to be included in a commercial           *
 * software, please contact us first for a clearance at                *
 *   ronan.mounes@amstrad.eu                                           *
 *                                                                     *
 *   This notice must remain intact in all copies of this code.        *
 *   This code is distributed WITHOUT ANY WARRANTY OF ANY KIND.        *
 *   The GNU GPL license can be found at :                             *
 *           http://www.gnu.org/copyleft/gpl.html                      *
 *                                                                     *
 ***********************************************************************/

package org.linotte.frame.atelier;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.border.Border;

import org.linotte.frame.Atelier;
import org.linotte.greffons.GreffonsChargeur;
import org.linotte.greffons.api.Greffon;
import org.linotte.moteur.entites.Prototype;
import org.linotte.moteur.outils.FichierOutils;
import org.linotte.moteur.outils.Ressources;
import org.linotte.moteur.xml.Version;

/**
 * Affichage de la boite A Propos
 *
 */

@SuppressWarnings("serial")
public class Apropos extends JDialog {

	private JLabel apropos;
	private String logo;
	private String COPYRIGHT = "\u00a9";
	private JTabbedPane tabbedPane;
	private JTextArea jTextArea;

	public Apropos(Atelier parent) {
		super(parent, "A propos de l'" + Atelier.getTitre(), true);
		setIconImage(Ressources.getImageIcon("help-browser.png").getImage());
		setLayout(new GridBagLayout());
		logo = Ressources.getURL("linotte_hd.png").toString();

		// Onglet
		tabbedPane = new JTabbedPane();
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());

		apropos = new JLabel();
		apropos.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel.add(apropos);

		tabbedPane.addTab("A propos", panel);

		jTextArea = new JTextArea();
		jTextArea.setEditable(false);
		jTextArea.setFont(Font.getFont(Font.SANS_SERIF));
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setViewportView(jTextArea);
		scrollPane.setPreferredSize(new Dimension(600, 600));
		tabbedPane.addTab("Nouveautés", scrollPane);

		getContentPane().add(tabbedPane);
		Border border = BorderFactory.createMatteBorder(10, 10, 10, 10, getBackground());
		apropos.setBorder(border);

		setDefaultCloseOperation(HIDE_ON_CLOSE);
		setResizable(false);

		construireTexte();
	}

	/**
	 * 
	 */
	private void construireTexte() {
		List<Prototype> prototypes_java = new ArrayList<Prototype>();
		List<Prototype> prototypes_python = new ArrayList<Prototype>();
		List<Prototype> prototypes_ruby = new ArrayList<Prototype>();
		List<Prototype> prototypes_linotte = new ArrayList<Prototype>();

		for (Prototype prototype : Atelier.linotte.especeModeleMap) {
			if (prototype.getGreffon() != null) {
				Greffon greffon = GreffonsChargeur.getInstance().getGreffon(prototype.getNom().toString());
				if ("python".equalsIgnoreCase(greffon.getLang()))
					prototypes_python.add(prototype);
				else if ("ruby".equalsIgnoreCase(greffon.getLang()))
					prototypes_ruby.add(prototype);
				else
					prototypes_java.add(prototype);
			} else // On ajoute les greffons en langage Linotte :
			if (!prototype.retourneSlotsLinotte().isEmpty()) {
				prototypes_linotte.add(prototype);
			}
		}

		trierGreffons(prototypes_java);
		trierGreffons(prototypes_linotte);
		trierGreffons(prototypes_python);
		trierGreffons(prototypes_ruby);

		String greffons_java = construireTexteGreffons(prototypes_java);
		String greffons_python = construireTexteGreffons(prototypes_python);
		String greffons_linotte = construireTexteGreffons(prototypes_linotte);

		String langageLinotte = Merci.htmlGras("Atelier de programmation ") + Atelier.linotte.getLangage().getNom();

		apropos.setText("<html>"
				+ Merci.htmlCentre(Merci.htmlGras("<img src=\"" + logo + "\" width=200 height=200 ><br>" + "&nbsp;" + langageLinotte + "<br>"
						+ Version.getVersion() + "<br/>" + COPYRIGHT + " " + Version.DATE + " " + Version.AUTEUR))
				+ "<br><br>" + //
				Merci.htmlGras("Greffons <font color=blue>Java</font> :<br>") + greffons_java + "<br>" + //
				Merci.htmlGras("Greffons <font color=blue>Python</font> :<br>") + greffons_python + "<br>" + //
				//Merci.htmlGras("Greffons <font color=blue>Ruby</font> :<br>") + greffons_ruby + "<br>" + //
				Merci.htmlGras("Greffons <font color=blue>Linotte</font> :<br>") + greffons_linotte + "<br><br>" + //
				Merci.htmlGras("Licence : ") + " GPL version 3");
		pack();
	}

	/**
	 * @param greffons_linotte
	 * @param prototypes
	 */
	private String construireTexteGreffons(List<Prototype> prototypes) {
		String greffons = "";
		int boucle = 1, taille = prototypes.size();
		if (taille > 0)
			for (Prototype prototype : prototypes) {

				Greffon greffon = GreffonsChargeur.getInstance().getGreffon(prototype.getNom().toString());

				if (greffon == null) {
					// greffon Linotte 
					greffons += prototype.getNom().toString();
				} else {
					greffons += greffon.getNom() + " " + greffon.getVersion();
				}

				if (boucle != taille)
					greffons += ", ";
				if (boucle % 7 == 0 && boucle != taille) // On saute une ligne tous les 5 greffons 
					greffons += "<br>";

				boucle++;
			}
		else
			greffons = "aucun";
		return greffons;
	}

	/**
	 * @param prototypes_java
	 */
	private void trierGreffons(List<Prototype> prototypes_java) {
		Collections.sort(prototypes_java, new Comparator<Prototype>() {
			@Override
			public int compare(Prototype o1, Prototype o2) {
				return o1.getNom().toString().compareTo(o2.getNom().toString());
			}
		});
	}

	@Override
	public void setVisible(boolean b) {
		if (b) {
			jTextArea.setText("");
			construireTexte();
		}
		this.setLocationRelativeTo(null);
		if (b) {
			// Il faut charger le texte après la fonction pack() !
			try {
				jTextArea.setText(FichierOutils.lire(Ressources.getFromRessources("ALIRE.TXT")));
				jTextArea.setCaretPosition(0);
			} catch (Exception e1) {
				e1.printStackTrace();
				if (tabbedPane.getTabCount() > 1)
					tabbedPane.removeTabAt(1);
			}
		}
		super.setVisible(b);
	}
}