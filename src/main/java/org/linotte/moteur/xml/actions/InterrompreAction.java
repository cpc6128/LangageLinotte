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

import org.alize.kernel.AKProcessus;
import org.linotte.moteur.exception.Constantes;
import org.linotte.moteur.exception.ErreurException;
import org.linotte.moteur.xml.alize.kernel.Job;
import org.linotte.moteur.xml.alize.kernel.JobContext;
import org.linotte.moteur.xml.alize.kernel.i.ActionDynamic;
import org.linotte.moteur.xml.alize.kernel.processus.ProcessusDispatcher;
import org.linotte.moteur.xml.analyse.ItemXML;
import org.linotte.moteur.xml.appels.Boucle;

public class InterrompreAction extends ActionDynamic {

	public InterrompreAction() {
		super();
	}

	@Override
	public String clef() {
		return "verbe interrompre";
	}

	@Override
	public AKProcessus analyseDynamic(String param, Job job, ItemXML[] valeurs, String[] annotations) throws Exception {
		JobContext jobContext = (JobContext) job.getContext();
		Boucle boucle = jobContext.getLivre().getKernelStack().recupereDerniereBoucle();
		if (boucle == null) {
			throw new ErreurException(Constantes.SYNTAXE_BOUCLE);
		} else {
			Boucle temp = null;
			//jobContext.getLivre().getKernelStack().fermeSousParagraphe();
			while (temp != boucle)
				temp = jobContext.getLivre().getKernelStack().fermeBoucle();
			if (((ProcessusDispatcher) ((ProcessusDispatcher) boucle.getProcessus()).getProcessusSecondaire()).getFermer() == null)
				return ((ProcessusDispatcher) ((ProcessusDispatcher) boucle.getProcessus()).getProcessusSecondaire()).getNextProcess();
			else
				return ((ProcessusDispatcher) ((ProcessusDispatcher) boucle.getProcessus()).getProcessusSecondaire()).getFermer().getNextProcess();
		}
	}
}
