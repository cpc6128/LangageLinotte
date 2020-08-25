/***********************************************************************
 * JCubitainer                                                         *
 * Version release date : November 30, 2005                            *
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

package org.linotte.frame.moteur;

import org.alize.kernel.AKException;
import org.alize.kernel.AKRuntime;
import org.alize.security.Habilitation;
import org.linotte.frame.atelier.Atelier;
import org.linotte.moteur.exception.LectureException;
import org.linotte.moteur.exception.Messages;
import org.linotte.moteur.exception.StopException;
import org.linotte.moteur.xml.Linotte;
import org.linotte.moteur.xml.Version;
import org.linotte.moteur.xml.alize.kernel.ContextHelper;
import org.linotte.moteur.xml.alize.parseur.ParserContext;
import org.linotte.moteur.xml.alize.parseur.ParserContext.MODE;
import org.linotte.moteur.xml.alize.parseur.Parseur;

import java.util.ArrayList;
import java.util.List;

/**
 * Exécuteur des livres depuis le télétype
 */

public class ConsoleProcess extends Thread {

	static Linotte lecteur = null;

	StringBuilder buffer = null;

	public static Atelier applet = null;

	boolean start = false;

	private static ConsoleProcess this_ = null;

	private static AKRuntime runtime;

	public ConsoleProcess(Linotte param, Atelier ap) {
		super("ConsoleProcess");
		lecteur = param;
		applet = ap;
	}

	public static void go(StringBuilder f, Linotte param, Atelier ap) {
		this_ = new ConsoleProcess(param, ap);
			this_.buffer = new StringBuilder("faire :\n").append(f);
		if (!this_.start) {
			this_.start = true;
			this_.start();
		}
	}

	public void action() throws InterruptedException {
		try {
			Parseur moteurXML = new Parseur();
			ParserContext atelierOutils = new ParserContext(MODE.GENERATION_RUNTIME);
			atelierOutils.linotte = lecteur;
			runtime = moteurXML.parseLivre(buffer, atelierOutils);
			List<Habilitation> habilitations = new ArrayList<Habilitation>();
			habilitations.add(Habilitation.MEMORY_FULL_ACCESS);
			ContextHelper.populate(runtime.getContext(), lecteur, applet.getCahierCourant().getFichier(), habilitations);
			runtime.execute();
		} catch (LectureException e) {
			if (Version.isBeta())
				e.getException().printStackTrace();
			String erreur = "";
			if (e.getLivre() != null) {
				erreur = "Livre : " + e.getLivre() + "\n";
			}
			erreur += Messages.retourneErreur(String.valueOf(e.getErreur()));
			if (e.getException().getToken() != null)
				erreur += " : " + e.getException().getToken();
			applet.ecrireErreurTableau(erreur);
		} catch (StopException e) {
		} catch (AKException e) {
			applet.ecrireErreurTableau(e.getMessage());
			applet.ecrirelnTableau("");
		} catch (StackOverflowError e) {
			applet.ecrireErreurTableau("Trop de boucles imbriquées ! Le livre doit s'arrêter...");
			applet.ecrirelnTableau("");
		} catch (Throwable e) {
			applet.ecrireErreurTableau("Erreur technique...");
			applet.ecrirelnTableau("");
			e.printStackTrace();
		} finally {
		}
	}

	@Override
	public void run() {
		try {
			action();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}