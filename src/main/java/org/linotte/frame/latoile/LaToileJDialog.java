/***********************************************************************
 * JCubitainer                                                         *
 * Version release date : May 5, 2004                                  *
 * Author : Mounès Ronan metalm@users.berlios.de                       *
 *                                                                     *
 *     http://jcubitainer.berlios.de/                                  *
 *                                                                     *
 * This code is released under the GNU GPL license, version 2 or       *
 * later, for educational and non-commercial purposes only.            *
 * If any part of the code is to be included in a commercial           *
 * software, please contact us first for a clearance at                *
 * metalm@users.berlios.de                                             *
 *                                                                     *
 *   This notice must remain intact in all copies of this code.        *
 *   This code is distributed WITHOUT ANY WARRANTY OF ANY KIND.        *
 *   The GNU GPL license can be found at :                             *
 *           http://www.gnu.org/copyleft/gpl.html                      *
 *                                                                     *
 ***********************************************************************/

/* History & changes **************************************************
 *                                                                     *
 ******** May 5, 2004 **************************************************
 *   - First release                                                   *
 ***********************************************************************/

package org.linotte.frame.latoile;

import javax.swing.JDialog;
import javax.swing.JFrame;

import org.linotte.frame.outils.ITransparence;
import org.linotte.moteur.entites.PrototypeGraphique;
import org.linotte.moteur.outils.Preference;
import org.linotte.moteur.xml.Linotte;

@SuppressWarnings("serial")
public class LaToileJDialog extends JDialog implements LaToile {

	protected boolean affichable = false;

	private JFrame frame;

	private LaToileListener panelLaToile;

	private boolean barre;

	private boolean atelier;

	private Linotte interpreteur;

	// Utilisé par le Webonotte :
	private boolean toileInvisible = false;

	/**
	 * Utilisé par le greffon SwingToile
	 */
	private boolean affichageToileDeleguee = false;

	protected LaToileJDialog(String title, JFrame p, boolean modal) {
		super(modal ? p : null);
		// setTitle(NAME);
		frame = p;
		panelLaToile = new LaToileListener(this);
		getContentPane().add(panelLaToile);
		panelLaToile.init();
		//setVisible(true);
		//pack();
	}

	public LaToileListener getPanelLaToile() {
		return panelLaToile;
	}

	public JFrame getFrameParent() {
		return frame;
	}

	// GrandeTete2Linotte Java 6!
	protected LaToileJDialog(JFrame p, boolean rien) {
		super(p, java.awt.Dialog.ModalityType.MODELESS);
		setTitle("La toile");
		frame = p;
		panelLaToile = new LaToileListener(this);
		getContentPane().add(panelLaToile);
		panelLaToile.init();
		//pack();
	}

	public boolean isAffichable() {
		return affichable;
	}

	@Override
	public void setVisible(boolean visible) {

		if (toileInvisible) {
			super.setVisible(false);
			return;
		}

		if (visible) {
			affichable = true;
		}
		if ((visible && !affichageToileDeleguee) || !visible)
			super.setVisible(visible);
		Preference.getIntance().setBoolean(Preference.P_TOILE, visible);
	}

	public boolean isAtelier() {
		return atelier;
	}

	public boolean isBarreDesTaches() {
		return barre;
	}

	public void setAtelier(boolean patelier) {
		atelier = patelier;
	}

	public void setBarreDesTaches(boolean pbarre) {
		barre = pbarre;
	}

	public Linotte getInterpreteur() {
		return interpreteur;
	}

	public void setInterpreteur(Linotte pinterpreteur) {
		interpreteur = pinterpreteur;
	}

	@Override
	public PrototypeGraphique getElementDragAndDrop() {
		return panelLaToile.getElementDragAndDrop();
	}

	public void setAffichageToileDeleguee(boolean affichageToileDeleguee) {
		this.affichageToileDeleguee = affichageToileDeleguee;
		if (affichageToileDeleguee) {
			setVisible(false);
		}
	}

	public boolean isAffichageToileDeleguee() {
		return affichageToileDeleguee;
	}

	@Override
	public void setUndecorated(boolean valeur) {
		ITransparence.getTransparence().setOpaque(this, true);
		dispose();
		super.setUndecorated(valeur);
	}

	public void setInvisible() {
		this.toileInvisible = true;
	}

}