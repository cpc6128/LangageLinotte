/***********************************************************************
 * JCubitainer                                                         *
 * Version release date : May 5, 2004                                  *
 * Author : MounÃ¨s Ronan metalm@users.berlios.de                       *
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
 ******** May 6, 2004 **************************************************
 *   - Ajout du drage'n'drop pour les thÃ¨mes                           *
 ******** May 12, 2004 **************************************************
 *   - AmÃ©lioration de la gestion des touches                          *
 ***********************************************************************/

package org.linotte.greffons.impl.xyz.outils;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import org.linotte.frame.latoile.LaToileListener;
import org.linotte.moteur.outils.Preference;

public class KeyListener3D implements KeyListener, Runnable {

	// http://forum.java.sun.com/thread.jsp?forum=406&thread=478606

	private LaToileListener toileListener;

	public KeyListener3D(LaToileListener toileListener) {
		initListener();
		this.toileListener = toileListener;
	}

	public static final String TOUCHE_ENTREE = "EntrÃ©e";

	public static final String TOUCHE_ESPACE = "Espace";

	public static final String TOUCHE_DEL = "Retour arriÃ¨re";

	private long KEY_DELAY = 10;

	private Thread thread = null;

	private char chars[] = new char[256];
	private int keys[] = new int[256];

	private final int TOUCHE_NON_ACTIVE = 0;

	private final int TOUCHE_NON_ACTIVE_ET_NON_HONOREE = 1;

	private final int TOUCHE_ACTIVE_ET_HONOREE = 2;

	private final int TOUCHE_ACTIVE_ET_NON_HONOREE = 3;

	public static String SOURIS_CLIQUE = "clique";

	private boolean run = true;

	// Afin de savoir si le buffer est vidÃ© :
	//private long lastTime;
	//private long lastTimeVerification;

	// Buffer des touches communs Ã  toutes les toiles :

	private void initListener() {
		int v = Preference.getIntance().getInt(Preference.P_VITESSE_TOUCHE);
		if (v == 0) {
			Preference.getIntance().setInt(Preference.P_VITESSE_TOUCHE, (int) KEY_DELAY);
		} else {
			KEY_DELAY = v;
		}
		thread = new Thread(this);
		thread.start();
	}

	/*
	 * (non-Javadoc)
	 */
	public void run() {

		while (run) {
			// On boucle sur les 256 touches... c'est bourrin, je sais mais
			// c'est pour avoir un code gÃ©nÃ©rique.
			boolean b;
			String s;
			int v;
			for (int i = 0; i < keys.length; i++) {
				if (isDown(i)) {
					up(i);
					v = Integer.valueOf(chars[i]).intValue();
					b = v > 31 && v < 300;
					s = b ? String.valueOf(chars[i]) : KeyEvent.getKeyText(i);
					//toileListener.ajouteTouche(s);
					reveillerMoteur();
				}
			}
			try {
				Thread.sleep(KEY_DELAY);
			} catch (InterruptedException e) {
			}
		}

	}

	public void reveillerMoteur() {
		//		synchronized (this) {
		//			synchronized (getFrameParent().getInterpreteur().getRegistreDesEtats().getTemporiser()) {
		//				getFrameParent().getInterpreteur().getRegistreDesEtats().getTemporiser().notifyAll();
		//			}
		//		}
	}

	public void keyPressed(KeyEvent e) {
		keys[e.getKeyCode() & 0xff] = TOUCHE_ACTIVE_ET_NON_HONOREE;
		chars[e.getKeyCode() & 0xff] = e.getKeyChar();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
	 */
	public void keyReleased(KeyEvent e) {
		if (keys[e.getKeyCode() & 0xff] == TOUCHE_ACTIVE_ET_NON_HONOREE)
			keys[e.getKeyCode() & 0xff] = TOUCHE_NON_ACTIVE_ET_NON_HONOREE;
		else if (keys[e.getKeyCode() & 0xff] != TOUCHE_NON_ACTIVE_ET_NON_HONOREE)
			keys[e.getKeyCode() & 0xff] = TOUCHE_NON_ACTIVE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
	 */
	public void keyTyped(KeyEvent arg0) {

	}

	/**
	 * @param key
	 */
	private boolean isDown(int key) {
		int valeur = keys[key & 0xff];
		boolean retour = valeur != TOUCHE_NON_ACTIVE;
		if (valeur == TOUCHE_ACTIVE_ET_NON_HONOREE)
			keys[key & 0xff] = TOUCHE_ACTIVE_ET_HONOREE;
		if (valeur == TOUCHE_NON_ACTIVE_ET_NON_HONOREE)
			keys[key & 0xff] = TOUCHE_NON_ACTIVE;
		return retour;
	}

	private void up(int key) {
		keys[key & 0xff] = TOUCHE_NON_ACTIVE;
	}

	public void terminer() {
		run = false;
	}

}