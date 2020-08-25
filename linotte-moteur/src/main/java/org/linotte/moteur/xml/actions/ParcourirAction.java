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
import org.linotte.moteur.exception.Constantes;
import org.linotte.moteur.exception.InconnuException;
import org.linotte.moteur.exception.LectureException;
import org.linotte.moteur.xml.alize.ParserEnvironnement;
import org.linotte.moteur.xml.alize.kernel.Action;
import org.linotte.moteur.xml.alize.kernel.Job;
import org.linotte.moteur.xml.alize.kernel.JobContext;
import org.linotte.moteur.xml.alize.kernel.i.ActionDispatcher;
import org.linotte.moteur.xml.alize.kernel.i.ParserHandler;
import org.linotte.moteur.xml.alize.kernel.processus.Processus;
import org.linotte.moteur.xml.alize.kernel.processus.ProcessusDispatcher;
import org.linotte.moteur.xml.analyse.ItemXML;
import org.linotte.moteur.xml.appels.Boucle;
import org.linotte.moteur.xml.appels.Fonction;

import java.util.Arrays;

public class ParcourirAction extends Action implements ParserHandler, ActionDispatcher {

	public ParcourirAction() {
		super();
	}

	@Override
	public String clef() {
		return "verbe parcourir";
	}

	@Override
	public ETAT analyse(String param, Job job, ItemXML[] valeurs, String[] annotations) throws Exception {
		JobContext jobContext = (JobContext) job.getContext();

		if (((ProcessusDispatcher) job.getCurrentProcessus()).getProcessusPrimaire() != null
				&& ((ProcessusDispatcher) job.getCurrentProcessus()).getProcessusPrimaire().getAction() instanceof BoucleAction) {
			Boucle boucle = jobContext.getLivre().getKernelStack().recupereDerniereBoucle();

			// Recherche des paramètres :
			Fonction fonction;
			if (valeurs.length > 1) {
				Acteur[] acteurs = extractionDesActeurs(Constantes.SYNTAXE_PARAMETRE_PARCOURIR, job, Arrays.copyOfRange(valeurs, 1, valeurs.length));
				fonction = new Fonction(boucle, job.getCurrentProcessus(), jobContext.getLivre().getParagraphe(), acteurs);
			} else {
				fonction = new Fonction(boucle, job.getCurrentProcessus(), jobContext.getLivre().getParagraphe());
			}
			jobContext.getLivre().getKernelStack().ajouterAppel(fonction);
		} else {
			// Recherche des paramètres :
			Fonction fonction;
			if (valeurs.length > 1) {
				Acteur[] acteurs = extractionDesActeurs(Constantes.SYNTAXE_PARAMETRE_PARCOURIR, job, Arrays.copyOfRange(valeurs, 1, valeurs.length));
				fonction = new Fonction(job.getCurrentProcessus(), jobContext.getLivre().getParagraphe(), acteurs);
			} else {
				fonction = new Fonction(job.getCurrentProcessus(), jobContext.getLivre().getParagraphe());
			}

			jobContext.getLivre().getKernelStack().ajouterAppel(fonction);
		}
		return ETAT.PARCOURIR;
	}

	@Override
	public void postAnalyse(Processus processus, ParserEnvironnement context) throws Exception {
		if (processus.isProduitCartesien()) {
			for (ItemXML[] items : processus.getMatrice()) {
				String paragraphe = items[0].toString().toLowerCase();
				Processus temp = context.getParagraphe(paragraphe);
				if (temp == null)
					throw new LectureException(new InconnuException(Constantes.MODE_RECHERCHE_IMPOSSIBLE, processus.getPosition(), paragraphe),
							processus.getPosition());
				((ProcessusDispatcher) processus).setParcourir(temp);
			}
		} else {
			String paragraphe = processus.getValeurs()[0].toString().toLowerCase();
			Processus temp = context.getParagraphe(paragraphe);
			if (temp == null)
				throw new InconnuException(Constantes.MODE_RECHERCHE_IMPOSSIBLE, processus.getPosition(), paragraphe);
			((ProcessusDispatcher) processus).setParcourir(temp);
		}
	}

}
