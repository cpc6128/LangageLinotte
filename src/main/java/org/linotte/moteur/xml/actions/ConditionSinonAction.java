/***********************************************************************
 * Linotte                                                             *
 * Version release date : October 01, 2011                             *
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
import org.linotte.moteur.xml.alize.kernel.Job;
import org.linotte.moteur.xml.alize.kernel.JobContext;
import org.linotte.moteur.xml.analyse.ItemXML;
import org.linotte.moteur.xml.appels.Condition;
import org.linotte.moteur.xml.appels.Condition.ETAT_CONDITION;

public class ConditionSinonAction extends ConditionAction {

	@Override
	public String clef() {
		return "sinon";
	}

	@Override
	public ETAT analyse(String param, Job job, ItemXML[] valeurs, String[] annotations) throws Exception {

		JobContext jobContext = (JobContext) job.getContext();
		Condition condition = jobContext.getLivre().getKernelStack().recupereDerniereCondition();
		if (condition == null || condition.getEtat() == ETAT_CONDITION.VRAI) {
			throw new ErreurException(Constantes.SYNTAXE_SINON);
		} else {
			return super.analyse(param, job, valeurs, annotations);
		}

	}
}