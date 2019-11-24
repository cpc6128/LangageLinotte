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

import org.linotte.frame.latoile.LaToile;
import org.linotte.moteur.exception.Constantes;
import org.linotte.moteur.exception.ErreurException;
import org.linotte.moteur.exception.StopException;
import org.linotte.moteur.xml.alize.kernel.Action;
import org.linotte.moteur.xml.alize.kernel.Job;
import org.linotte.moteur.xml.alize.kernel.RuntimeContext;
import org.linotte.moteur.xml.alize.kernel.i.IProduitCartesien;
import org.linotte.moteur.xml.alize.kernel.security.Habilitation;
import org.linotte.moteur.xml.analyse.ItemXML;

public class ViderTouchesAction extends Action implements IProduitCartesien {

	public ViderTouchesAction() {
		super();
	}

	@Override
	public String clef() {
		return "verbe vider les touches";
	}

	@Override
	public ETAT analyse(String param, Job job, ItemXML[] valeurs, String[] annotations) throws ErreurException, StopException {
		RuntimeContext runtimeContext = (RuntimeContext) job.getRuntimeContext();

		if (runtimeContext.canDo(Habilitation.LITTLE_BIRD)) {
			throw new ErreurException(Constantes.MODE_CONSOLE);			
		}		
		
		for (Object otoile : runtimeContext.getLibrairie().getToiles()) {
			((LaToile) otoile).getPanelLaToile().videTouches();
		}

		return ETAT.PAS_DE_CHANGEMENT;
	}

}
