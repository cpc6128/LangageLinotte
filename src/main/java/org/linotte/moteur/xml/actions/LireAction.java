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
import org.linotte.moteur.exception.StopException;
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
import org.linotte.moteur.xml.appels.SousParagraphe;

public class LireAction extends Action implements ParserHandler, ActionDispatcher {

	public LireAction() {
		super();
	}

	@Override
	public String clef() {
		return "§ début";
	}

	@Override
	public ETAT analyse(String param, Job job, ItemXML[] valeurs, String[] annotations) throws ErreurException, StopException {
		JobContext jobContext = (JobContext) job.getContext();

		if (((ProcessusDispatcher) job.getCurrentProcessus()).getProcessusPrimaire() == null) {
			throw new ErreurException(Constantes.SYNTAXE_SOUS_PARAGRAPHE);
		}

		if (((ProcessusDispatcher) job.getCurrentProcessus()).getProcessusPrimaire().getAction() instanceof BoucleAction) {
			Boucle boucle = jobContext.getLivre().getKernelStack().recupereDerniereBoucle();
			SousParagraphe sousParagraphe = new SousParagraphe(boucle, job.getCurrentProcessus().getPosition());
			jobContext.getLivre().getKernelStack().ajouterAppel(sousParagraphe);
			return ETAT.PAS_DE_CHANGEMENT;
		} else {
			SousParagraphe sousParagraphe = new SousParagraphe(job.getCurrentProcessus().getPosition());
			jobContext.getLivre().getKernelStack().ajouterAppel(sousParagraphe);
			return ETAT.PAS_DE_CHANGEMENT;
		}
	}

	@Override
	public void postAnalyse(Processus process, ParserEnvironnement context) {
		((ProcessusDispatcher) process).setFermer(context.getSousParagraphes().get(process));
	}
}