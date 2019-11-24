/***********************************************************************
 * Linotte                                                             *
 * Version release date : September 01, 2006                           *
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

package org.linotte.moteur.xml.actions;

import java.util.Arrays;

import org.linotte.moteur.entites.Acteur;
import org.linotte.moteur.entites.Prototype;
import org.linotte.moteur.entites.Role;
import org.linotte.moteur.exception.Constantes;
import org.linotte.moteur.exception.RoleException;
import org.linotte.moteur.xml.alize.ParserEnvironnement;
import org.linotte.moteur.xml.alize.kernel.Action;
import org.linotte.moteur.xml.alize.kernel.Job;
import org.linotte.moteur.xml.alize.kernel.i.ActionDispatcher;
import org.linotte.moteur.xml.alize.kernel.i.ParserHandler;
import org.linotte.moteur.xml.alize.kernel.processus.Processus;
import org.linotte.moteur.xml.alize.kernel.processus.ProcessusDispatcher;
import org.linotte.moteur.xml.analyse.ItemXML;

public class AttacherAction extends Action implements ParserHandler, ActionDispatcher {

	public AttacherAction() {
		super();
	}

	@Override
	public String clef() {
		return "verbe attacher";
	}

	@Override
	public ETAT analyse(String param, Job job, ItemXML[] valeurs, String[] annotations) throws Exception {
		if (((ProcessusDispatcher) job.getCurrentProcessus()).getAttacher() != null) {
			// On attache un méthode fonctionnelle
			Acteur a = extractionDesActeurs(Constantes.SYNTAXE_PARAMETRE_PARCOURIR, job, Arrays.copyOfRange(valeurs, 1, valeurs.length))[0];
			Processus cible = ((ProcessusDispatcher) job.getCurrentProcessus()).getAttacher();
			String paragraphe = ((ProcessusDispatcher) job.getCurrentProcessus()).getParagraphe();
			a.ajouterSlot(paragraphe, cible);
		} else {
			// On attache un paragraphe
			Acteur[] acteurs = extractionDesActeurs(Constantes.SYNTAXE_PARAMETRE_PARCOURIR, job, valeurs);
			Acteur caracteristique = acteurs[0];
			Acteur a = acteurs[1];
			if (a.getRole() != Role.ESPECE)
				throw new RoleException(Constantes.SYNTAXE_PARAMETRE_ATTACHER, a.toString(),Role.ESPECE,a.getRole());
			Prototype e = (Prototype) a;
			e.addAttribut(caracteristique.copierCommeAttribut(e));
		}
		return ETAT.PAS_DE_CHANGEMENT;
	}

	@Override
	public void postAnalyse(Processus processus, ParserEnvironnement context) throws Exception {
		if (processus.isProduitCartesien()) {
			for (ItemXML[] items : processus.getMatrice()) {
				String paragraphe = items[0].toString().toLowerCase();
				Processus temp = context.getParagraphe(paragraphe);
				if (temp != null)
					((ProcessusDispatcher) processus).setAttacher(temp);
			}
		} else {
			String paragraphe = processus.getValeurs()[0].toString().toLowerCase();
			Processus temp = context.getParagraphe(paragraphe);
			if (temp != null) {
				((ProcessusDispatcher) processus).setAttacher(temp);
				((ProcessusDispatcher) processus).setParagraphe(paragraphe);
			}
		}
	}

}
