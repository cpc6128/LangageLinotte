/***********************************************************************
 * Linotte                                                             *
 * Version release date : September 01, 2006                           *
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

package org.linotte.moteur.xml.actions;

import org.linotte.moteur.entites.Acteur;
import org.linotte.moteur.entites.Casier;
import org.linotte.moteur.entites.Role;
import org.linotte.moteur.exception.Constantes;
import org.linotte.moteur.exception.ErreurException;
import org.linotte.moteur.outils.ChaineOutils;
import org.linotte.moteur.xml.alize.kernel.Action;
import org.linotte.moteur.xml.alize.kernel.Job;
import org.linotte.moteur.xml.alize.kernel.JobContext;
import org.linotte.moteur.xml.alize.kernel.i.ActionDispatcher;
import org.linotte.moteur.xml.alize.kernel.processus.Processus;
import org.linotte.moteur.xml.alize.kernel.processus.ProcessusDispatcher;
import org.linotte.moteur.xml.analyse.ItemXML;
import org.linotte.moteur.xml.appels.Boucle;
import org.linotte.moteur.xml.appels.TantQue;

public class BoucleAction extends Action implements ActionDispatcher {

	public BoucleAction() {
		super();
	}

	@Override
	public String clef() {
		return "boucle";
	}

	@Override
	public ETAT analyse(String pParam, Job job, ItemXML[] valeurs, String[] annotations) throws Exception {
		JobContext jobContext = (JobContext) job.getContext();

		Acteur acteurs[] = extractionDesActeurs(0, job, valeurs);

		Boucle b;
		if (ChaineOutils.findInArray(annotations, "tant")) {
			// Boucle : tant que
			b = new TantQue(job.getCurrentProcessus(), pParam, valeurs);
		} else if (acteurs.length == 1) {
			// Boucle : pour chaque
			Acteur a = acteurs[0];
			b = new Boucle(job.getCurrentProcessus(), a);
		} else if (acteurs.length == 3) {
			// Boucle : Pour A de B à C
			Acteur a1 = acteurs[0];
			Acteur a2 = acteurs[1];
			Acteur a3 = acteurs[2];
			// Il faut vérifier les types :
			if (!(a1.getRole() == Role.NOMBRE && a2.getRole() == Role.NOMBRE && a3.getRole() == Role.NOMBRE))
				throw new ErreurException(Constantes.SYNTAXE_BOUCLE);
			b = new Boucle(job.getCurrentProcessus(), a2, a3, a1);
		} else if (acteurs.length == 4) {
			// Boucle : Pour A de B à C suivant D
			Acteur a1 = acteurs[0];
			Acteur a2 = acteurs[1];
			Acteur a3 = acteurs[2];
			// Il faut vérifier les types :
			if (!(a1.getRole() == Role.NOMBRE && a2.getRole() == Role.NOMBRE && a3.getRole() == Role.NOMBRE))
				throw new ErreurException(Constantes.SYNTAXE_BOUCLE);
			b = new Boucle(job.getCurrentProcessus(), a2, a3, a1, valeurs[3]);
		} else if (acteurs.length == 2 && ChaineOutils.findInArray(annotations, "pour")) {
			// Boucle : De ... à
			Acteur a1 = acteurs[0];
			Acteur a2 = acteurs[1];
			// Il faut vérifier les types :
			if (!(a2.getRole() == Role.CASIER))
				throw new ErreurException(Constantes.SYNTAXE_BOUCLE);
			Casier c = (Casier) a2;
			b = new Boucle(job.getCurrentProcessus(), a1, c);
		} else {
			// Boucle : De ... à
			Acteur a1 = acteurs[0];
			Acteur a2 = acteurs[1];
			// Il faut vérifier les types :
			if (!(a1.getRole() == Role.NOMBRE && a2.getRole() == Role.NOMBRE))
				throw new ErreurException(Constantes.SYNTAXE_BOUCLE);
			b = new Boucle(job.getCurrentProcessus(), a1, a2);
		}
		boolean retour = b.suiteBoucle(job);

		Processus etats = ((ProcessusDispatcher) job.getCurrentProcessus()).getProcessusSecondaire();

		if (etats.getAction() instanceof AllerAction) {
			throw new ErreurException(Constantes.ALLER_INTERDIT);
		}

		if (retour) {
			// Faire les boucles :
			jobContext.getLivre().getKernelStack().ajouterAppel(b);

			if (etats.getAction() instanceof LireAction || etats.getAction() instanceof ParcourirAction) {
				return ETAT.ETAT_SECONDAIRE;
			} else {
				while (retour) {
					etats.execute(job);
					job.verifierSiTropDeBoucles();
					job.afficheDebogueur();
					retour = b.suiteBoucle(job);
					// Pour éviter une boucle incontrolable, on va vérifier
					// l'état du job :
					if (!job.isRunning()) {
						return ETAT.PAS_DE_CHANGEMENT;
					}
				}
				jobContext.getLivre().getKernelStack().fermeBoucle();
				return ETAT.PAS_DE_CHANGEMENT;
			}

		} else {
			// On termine la boucle :
			if (etats.getAction() instanceof LireAction) {
				return ETAT.SAUTER_PARAGRAPHE;
			} else {
				return ETAT.PAS_DE_CHANGEMENT;
			}
		}
	}
}