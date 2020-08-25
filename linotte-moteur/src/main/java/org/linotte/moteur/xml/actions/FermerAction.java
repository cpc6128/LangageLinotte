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

import org.linotte.moteur.exception.Constantes;
import org.linotte.moteur.exception.ErreurException;
import org.linotte.moteur.xml.alize.ParserEnvironnement;
import org.linotte.moteur.xml.alize.kernel.Action;
import org.linotte.moteur.xml.alize.kernel.Job;
import org.linotte.moteur.xml.alize.kernel.JobContext;
import org.linotte.moteur.xml.alize.kernel.i.ActionDispatcher;
import org.linotte.moteur.xml.alize.kernel.i.ParserHandler;
import org.linotte.moteur.xml.alize.kernel.processus.Processus;
import org.linotte.moteur.xml.alize.kernel.processus.ProcessusDispatcher;
import org.linotte.moteur.xml.analyse.ItemXML;
import org.linotte.moteur.xml.appels.SousParagraphe;

import java.util.Map;
import java.util.Set;

public class FermerAction extends Action implements ParserHandler, ActionDispatcher {

	public FermerAction() {
		super();
	}

	@Override
	public String clef() {
		return "§ fin";
	}

	@Override
	public ETAT analyse(String param, Job job, ItemXML[] valeurs, String[] annotations) throws Exception {
		JobContext jobContext = (JobContext) job.getContext();
		SousParagraphe sousParag = jobContext.getLivre().getKernelStack().recupereDernierSousParagraphe();
		if (sousParag == null) {
			throw new ErreurException(Constantes.SYNTAXE_SOUS_PARAGRAPHE);
		} else {
			boolean retour = sousParag.next(job);
			if (retour) {
				sousParag.viderActeurs();
				// nettoyage de la pile des appels
				// Il peut rester des conditions
				jobContext.getLivre().getKernelStack().fermeSousParagraphe();
				jobContext.getLivre().getKernelStack().ajouterAppel(sousParag);
				return ETAT.SOUS_PARAGRAPHE;
			} else {
				jobContext.getLivre().getKernelStack().fermeSousParagraphe();
				if (sousParag.isBoucle()) {
					// Boucle
					jobContext.getLivre().getKernelStack().fermeBoucle();
				}
				// Fin de la boucle
			}
		}
		return ETAT.PAS_DE_CHANGEMENT;
	}

	@Override
	public void postAnalyse(Processus process, ParserEnvironnement context) {
		// On associe le double du paragraphe (lire/fermer)
		Set<Map.Entry<Processus, Processus>> set = context.getSousParagraphes().entrySet();
		for (Map.Entry<Processus, Processus> entry : set) {
			if (entry.getValue() == process) {
				((ProcessusDispatcher) process).setLire(entry.getKey());
				break;
			}
		}
	}
}
