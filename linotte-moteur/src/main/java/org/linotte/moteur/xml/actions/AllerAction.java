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

import org.linotte.moteur.exception.*;
import org.linotte.moteur.xml.alize.ParserEnvironnement;
import org.linotte.moteur.xml.alize.kernel.Action;
import org.linotte.moteur.xml.alize.kernel.Job;
import org.linotte.moteur.xml.alize.kernel.JobContext;
import org.linotte.moteur.xml.alize.kernel.i.ParserHandler;
import org.linotte.moteur.xml.alize.kernel.processus.Processus;
import org.linotte.moteur.xml.analyse.ItemXML;

public class AllerAction extends Action implements ParserHandler {

	public AllerAction() {
		super();
	}

	@Override
	public String clef() {
		return "verbe aller";
	}

	@Override
	public ETAT analyse(String param, Job job, ItemXML[] valeurs, String[] annotations) throws ErreurException, StopException {
		JobContext jobContext = (JobContext) job.getContext();
		jobContext.getLivre().getKernelStack().fermeCalqueParagraphe();
		return ETAT.PAS_DE_CHANGEMENT;
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
				processus.setNextProcess(temp);
			}
		} else {
			String paragraphe = processus.getValeurs()[0].toString().toLowerCase();
			Processus temp = context.getParagraphe(paragraphe);
			if (temp == null)
				throw new LectureException(new InconnuException(Constantes.MODE_RECHERCHE_IMPOSSIBLE, processus.getPosition(), paragraphe),
						processus.getPosition());
			processus.setNextProcess(temp);
		}

	}

}
