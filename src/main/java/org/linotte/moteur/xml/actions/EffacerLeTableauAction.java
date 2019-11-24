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

import org.linotte.moteur.exception.ErreurException;
import org.linotte.moteur.exception.StopException;
import org.linotte.moteur.xml.alize.kernel.Action;
import org.linotte.moteur.xml.alize.kernel.Job;
import org.linotte.moteur.xml.alize.kernel.RuntimeContext;
import org.linotte.moteur.xml.analyse.ItemXML;

public class EffacerLeTableauAction extends Action {

	public EffacerLeTableauAction() {
		super();
	}

	@Override
	public String clef() {
		return "verbe effacer le tableau";
	}

	@Override
	public ETAT analyse(String param, Job job, ItemXML[] valeurs, String[] annotations) throws ErreurException, StopException {
		RuntimeContext runtimeContext = (RuntimeContext) job.getRuntimeContext();

		runtimeContext.getIhm().effacer();

		return ETAT.PAS_DE_CHANGEMENT;
	}

}
