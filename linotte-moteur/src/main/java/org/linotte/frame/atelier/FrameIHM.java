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

import org.linotte.frame.moteur.FrameProcess;
import org.linotte.frame.tableau.TableauBatch;
import org.linotte.moteur.entites.Role;
import org.linotte.moteur.exception.StopException;
import org.linotte.moteur.xml.api.IHM;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;

/**
 * Implémentation de l'interface IHM en mode Swing
 */
public class FrameIHM implements IHM {

	static final boolean DEBUG = false;

	protected Atelier applet = null;

	private Frame frameReference = null;

	private FrameDialog dialog = null;

	private static boolean popupMessage = false;

	private TableauBatch batch = null;

	public FrameIHM(Atelier p) {
		applet = p;
		frameReference = p;
		dialog = new FrameDialog((Frame) p.getParent());
		// Service d'affichage dans le tableau :
		batch = new TableauBatch(50, p);
		batch.start();
	}

	public FrameIHM(Frame p) {
		frameReference = p;
		dialog = new FrameDialog((Frame) p.getParent());
	}

	public FrameIHM() {
		// Pour l'applet
		frameReference = null;
		dialog = new FrameDialog(null);
	}

	public boolean debug(String debug) {
		if (DEBUG)
			System.err.println(debug); // NOPMD by ronan.mounes on 01/08/07 18:16
		return true;
	}

	@Override
	public String demander(Role type, String acteur) throws StopException {
		dialog.initialiserFenetre(type);
		String message;
		if (type == Role.NOMBRE) {
			BigDecimal result = null;
			do {
				dialog.setVisible(true);
				message = dialog.retourneValeur();
				if (message == null) {
					FrameProcess.stopProcess();
					return null;
				}
				try {
					result = new BigDecimal(message);
				} catch (NumberFormatException e1) {
					afficher("Nombre non valide", Role.NOMBRE);
				}
			} while (result == null);
			message = result.toString();
		} else {
			dialog.setVisible(true);
			message = dialog.retourneValeur();
			if (message == null)
				FrameProcess.stopProcess();
		}
		//applet.ecrireln("> " + message);
		return message;
	}

	public String questionne(String question, Role type, String acteur) throws StopException {
		dialog.initialiserFenetre(question, type);
		String message;
		if (type == Role.NOMBRE) {
			BigDecimal result = null;
			do {
				dialog.setVisible(true);
				message = dialog.retourneValeur();
				if (message == null) {
					FrameProcess.stopProcess();
					return null;
				}
				try {
					result = new BigDecimal(message);
				} catch (NumberFormatException e1) {
					afficher("Nombre non valide", Role.TEXTE);
				}
			} while (result == null);
			message = result.toString();
		} else {
			dialog.setVisible(true);
			message = dialog.retourneValeur();
			if (message == null)
				FrameProcess.stopProcess();
		}
		return message;
	}

	public boolean afficher(String afficher, Role type) throws StopException {
		if (applet != null)
			batch.ecrireTableau(/*"< " + */afficher);
		if (popupMessage) {
			dialog.setSimpleMessage(afficher);
			dialog.setVisible(true);
		}
		return true;
	}

	public void close() {
		dialog.setVisible(false);
	}

	public static boolean isPopupMessage() {
		return popupMessage;
	}

	public static void setPopupMessage(boolean param) {
		popupMessage = param;
	}

	public boolean effacer() throws StopException {
		if (applet != null) {
			batch.effacerTableau();
			//applet.effacerTableau();
		}
		return true;
	}

	public boolean afficherErreur(String afficher) {
		if (applet != null)
			applet.ecrireErreurTableau(afficher);
		Object[] options = { "Ok" };// , "Pouce !"
		JOptionPane.showOptionDialog(frameReference, afficher, "Erreur !", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE, null, options,
				options[0]);
		return true;
	}

	public TableauBatch getTableauBatch() {
		return batch;
	}

}