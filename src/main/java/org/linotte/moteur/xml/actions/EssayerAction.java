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

import org.linotte.moteur.exception.Constantes;
import org.linotte.moteur.exception.ErreurException;
import org.linotte.moteur.xml.alize.kernel.Job;
import org.linotte.moteur.xml.alize.kernel.JobContext;
import org.linotte.moteur.xml.alize.kernel.processus.Processus;
import org.linotte.moteur.xml.alize.kernel.processus.ProcessusDispatcher;
import org.linotte.moteur.xml.analyse.ItemXML;
import org.linotte.moteur.xml.appels.Ring;

public class EssayerAction extends ConditionAction {

	public EssayerAction() {
		super();
	}

	@Override
	public String clef() {
		return "essayer";
	}

	@Override
	public ETAT analyse(String param, Job job, ItemXML[] valeurs, String[] annotations) throws Exception {
		JobContext jobContext = (JobContext) job.getContext();
		// Le verbe Essayer est une condition vraie !

		Ring codeProtege;
		ETAT retour;
		Processus etats = ((ProcessusDispatcher) job.getCurrentProcessus()).getProcessusSecondaire();
		if (etats.getAction() instanceof LireAction) {
			if ((Processus) ((ProcessusDispatcher) ((ProcessusDispatcher) job.getCurrentProcessus()).getProcessusSecondaire()).getFermer() == null) {
				throw new ErreurException(Constantes.SYNTAXE_SOUS_PARAGRAPHE);
			}
			codeProtege = new Ring((Processus) ((ProcessusDispatcher) ((ProcessusDispatcher) job.getCurrentProcessus()).getProcessusSecondaire()).getFermer()
					.getNextProcess());
			retour = super.analyse(param, job, valeurs, annotations);
			jobContext.getLivre().getKernelStack().ajouterAppel(codeProtege);
		} else {
			codeProtege = new Ring((Processus) (((ProcessusDispatcher) job.getCurrentProcessus()).getProcessusSecondaire()).getNextProcess());
			retour = super.analyse(param, job, valeurs, annotations);
			jobContext.getLivre().getKernelStack().ajouterAppel(codeProtege);
		}

		return retour;
	}
}
