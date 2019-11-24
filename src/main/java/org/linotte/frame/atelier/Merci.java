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
import java.awt.Frame;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.border.Border;

import org.linotte.moteur.outils.Ressources;

@SuppressWarnings("serial")
public class Merci extends JDialog {

	private JLabel apropos;
	private String logo;

	public Merci(Frame parent) {
		super(parent, "Merci ...", true);
		setIconImage(Ressources.getImageIcon("fenetre/heart.png").getImage());
		Box b = Box.createHorizontalBox();
		logo = Ressources.getURL("LinotteFarvardin.gif").toString();

		apropos = new JLabel();
		apropos.setAlignmentX(Component.CENTER_ALIGNMENT);
		b.add(apropos);
		getContentPane().add(b);
		Border border = BorderFactory.createMatteBorder(10, 10, 10, 10, getBackground());
		b.setBorder(border);
		setDefaultCloseOperation(HIDE_ON_CLOSE);

		setSize(260, 130);
		setResizable(false);

		apropos.setText("<html><table cellpadding=10 ><tr><td>"
				+ htmlCentre(htmlGras("Merci d'utiliser l'Atelier Linotte !") + "<br><br>" + htmlCentre("<img src=\"" + logo + "\" alt=\"logo\" >") + "<br>"
						+ "Je voudrais remercier toute la communauté Linotte, <br>"
						+ "les utilisateurs, les contributeurs et particulièrement les personnes suivantes :<br>" + "<br>"
						+ "Simon, Pat, Farvardin, Dadodudou, Momo112,<br>Zamirh, Wam, LawNasK, Mony, Mimipunk<br>"
						+ "<br><br>Et enfin les sites Framasoft et Jesuislibre.org.<br>") + "<br></td></tr></table</html>");
		pack();
	}

	/**
	 * TODO: A déplacer (utilisée par @Apropos) mais je ne sais pas où
	 * 
	 * @param texte
	 * @return
	 */
	static String htmlCentre(String texte) {
		return "<center>" + texte + "</center>";
	}

	/**
	 * TODO: A déplacer (utilisée par @Apropos) mais je ne sais pas où
	 * 
	 * @param texte
	 * @return
	 */
	static String htmlGras(String texte) {
		return "<b>" + texte + "</b>";
	}

	@Override
	public void setVisible(boolean b) {
		this.setLocationRelativeTo(null);
		super.setVisible(b);
	}
}