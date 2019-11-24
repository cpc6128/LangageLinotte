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

package org.linotte.greffons.impl.swing;

import java.awt.Component;
import java.awt.Frame;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.border.Border;

import org.linotte.moteur.outils.Ressources;
import org.linotte.moteur.xml.Version;

@SuppressWarnings("serial")
public class Apropos extends JDialog {

	JLabel apropos;
	String logo;
	String COPYRIGHT = "\u00a9";

	public Apropos(Frame frame) {
		super(frame, "A propos du langage Linotte", true);
		setIconImage(Ressources.getImageIcon("help-browser.png").getImage());
		logo = Ressources.getURL("linotte_new.png").toString();
		Box b = Box.createHorizontalBox();

		apropos = new JLabel();
		apropos.setAlignmentX(Component.CENTER_ALIGNMENT);
		b.add(apropos);
		getContentPane().add(b);
		Border border = BorderFactory.createMatteBorder(10, 10, 10, 10, getBackground());
		b.setBorder(border);
		setDefaultCloseOperation(HIDE_ON_CLOSE);

		setSize(260, 130);
		setResizable(false);

		construireTexte();
	}

	/**
	 * 
	 */
	private void construireTexte() {
		apropos.setText("<html><b>" + "<center><img src=\"" + logo + "\" ><br>"
				+ "&nbsp;Logiciel créé avec le <b><font color=blue>langage L</font></b><small>&nbsp;inotte</small><br>" + Version.getVersion() + "</b>"
				+ "<center>" + "" + COPYRIGHT + " " + Version.DATE + " " + Version.AUTEUR + "<br><br>" + "<br><table><tr><td>"
				+ "<b>Licence :</b> GPL version 3" + "<br>" + "<b>Java :</b> " + System.getProperty("java.version") + "</td></tr></table>");
		pack();
	}

	@Override
	public void setVisible(boolean b) {
		if (b)
			construireTexte();
		this.setLocationRelativeTo(null);
		super.setVisible(b);
	}
}
