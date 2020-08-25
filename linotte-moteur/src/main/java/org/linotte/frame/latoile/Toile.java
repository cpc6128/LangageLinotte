/***********************************************************************
 * Linotte                                                             *
 * Version release date : July 30, 2008                             *
 * Author : Mounes Ronan ronan.mounes@amstrad.eu                        *
 *                                                                     *
 *     http://langagelinotte.free.fr                                   *
 *                                                                     *
 * This code is released under the GNU GPL license, version 2 or       *
 * later, for educational and non-commercial purposes only.            *
 * If any part of the code is to be included in a commercial           *
 * software, please contact us first for a clearance at                *
 *   ronan.mounes@amstrad.eu                                            *
 *                                                                     *
 *   This notice must remain intact in all copies of this code.        *
 *   This code is distributed WITHOUT ANY WARRANTY OF ANY KIND.        *
 *   The GNU GPL license can be found at :                             *
 *           http://www.gnu.org/copyleft/gpl.html                      *
 *                                                                     *
 ***********************************************************************/

package org.linotte.frame.latoile;

import org.linotte.frame.atelier.Atelier;
import org.linotte.moteur.outils.Ressources;
import org.linotte.moteur.xml.Linotte;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public final class Toile {

	public static final String TOILEWEB = "toileweb";

	private static final class CloseListener implements WindowListener {

		LaToile toile;

		public CloseListener(LaToile p) {
			toile = p;
		}

		public void windowActivated(WindowEvent e) {
			if (toile != null && toile.isAffichable()) {
				toile.setVisible(true);
				toile.getPanelLaToile().focus();
			}
		}

		public void windowClosed(WindowEvent e) {
		}

		public void windowClosing(WindowEvent e) {
			System.exit(0);
		}

		public void windowDeactivated(WindowEvent e) {
		}

		public void windowDeiconified(WindowEvent e) {
			if (toile != null && toile.isAffichable()) {
				toile.setVisible(true);
				toile.getPanelLaToile().focus();
			}
		}

		public void windowIconified(WindowEvent e) {
			if (toile != null) {
				toile.setVisible(false);
			}
		}

		public void windowOpened(WindowEvent e) {
		}

		public void setToile(LaToile t) {
			toile = t;
		}

	}

	// private static LaToile toile = null;

	// private static boolean atelier;
	//
	private static JFrame barreDesTaches;
	//
	// private static boolean barre;

	private static boolean applet = false;

	public static synchronized void initApplet(LaToile laToile) {

		try {
			if (!applet)
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
		}

		// Initialisation des objets graphiques :
		// EspeceGraphique.init();
		applet = true;

	}

	/**
	 * Méthode utilisée pour créer une toile fille
	 * @param nom
	 * @param frame
	 * @return
	 */
	public static LaToile initToile(Linotte linotte, String nom, JFrame frame) {
		LaToile toile = initToile(linotte, true, frame, false, false, true, 100, 100);
		return toile;
	}

	public static LaToile initToile(Linotte linotte, boolean p, JFrame frame, boolean systray, boolean modal, boolean visible, int tx, int ty) {

		LaToile toile;

		try {
			if (!p)
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
		}

		CloseListener closeListener = new CloseListener(null);
		// Affichage de la toile
		if (frame == null && (systray || modal)) {
			frame = new JFrame("Initialisation de la machine virtuelle Linotte");
			frame.setIconImage(Ressources.getImageIcon("linotte.png").getImage());
			frame.dispose();
			frame.setUndecorated(true);
			frame.pack();
			frame.repaint();
			frame.setVisible(true);
			frame.addWindowListener(closeListener);
			barreDesTaches = frame;

		}
		if (frame instanceof Atelier) {
			toile = new LaToileJDialog(frame, true);
		} else
			toile = new LaToileJDialog("La toile", frame, systray);

		toile.setAtelier(p);
		toile.setBarreDesTaches(modal);
		toile.setInterpreteur(linotte);

		if (toile.isAtelier()) {
			Java6.setIconImage((Dialog) toile, Ressources.getImageIcon("linotte_toile.png").getImage());
		} else {
			Java6.setIconImage((Dialog) toile, Ressources.getImageIcon("linotte.png").getImage());
			toile.addWindowListener(new CloseListener(toile));
		}
		if (closeListener != null)
			closeListener.setToile(toile);
		// toile.init();
		// Display the window.
		toile.setResizable(false);
		toile.pack();
		toile.getPanelLaToile().repaint();

		// Au centre :
		if (!toile.isAtelier()) {
			toile.setLocationRelativeTo(null);
		}

		if (tx != 0 && ty != 0) {
			toile.setLocation(tx, ty);
		}
		if (toile.isAtelier()) {
		} else {
			toile.setVisible(false);
		}
		// Initialisation des objets graphiques :
		// EspeceGraphique.init();
		if (systray)
			IconSysTray.init();

		return toile;
	}

	public static void fermerParent(LaToile parent) {
		if (parent != null && parent.isVisible() && !parent.isBarreDesTaches())
			parent.setVisible(false);
	}

	public static void setTitre(String titre, LaToile parent) {
		if (parent != null && !parent.isAtelier()) {
			parent.setTitle(titre);
			IconSysTray.setTitre(titre);
			if (parent.getFrameParent() != null && parent.getFrameParent().isVisible())
				parent.getFrameParent().setTitle(titre);
		}
	}

	public static void supprimeBarre(final LaToile parent) {
		if (barreDesTaches != null) {
			barreDesTaches.setVisible(false);
			barreDesTaches.dispose();
		}
	}

	public static void toFront(final LaToile parent) {
		if (parent != null) {
			if (parent.getFrameParent() != null && parent.getFrameParent().isVisible()) {
				java.awt.EventQueue.invokeLater(new Runnable() {
					@Override
					public void run() {
						((JFrame) parent.getFrameParent()).toFront();
					}
				});
			}
		}
	}

	public static boolean isApplet() {
		return applet;
	}

}