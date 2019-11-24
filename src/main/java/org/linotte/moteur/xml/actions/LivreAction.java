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

import org.linotte.moteur.xml.alize.kernel.Action;
import org.linotte.moteur.xml.alize.kernel.Job;
import org.linotte.moteur.xml.alize.kernel.JobContext;
import org.linotte.moteur.xml.alize.kernel.RuntimeContext;
import org.linotte.moteur.xml.analyse.ItemXML;

public class LivreAction extends Action {

	private static final String CLEF = "creation livre";

	public LivreAction() {
		super();
	}

	@Override
	public String clef() {
		return CLEF;
	}

	@Override
	public ETAT analyse(String param, Job l, ItemXML[] v, String[] a) {
		JobContext jobContext = (JobContext) l.getContext();
		RuntimeContext runtimeContext = (RuntimeContext) l.getRuntimeContext();
		String nom = ((runtimeContext.getReference() != null) ? runtimeContext.getReference().getName() : "Livre sans nom");
		if (v.length > 0)
			nom = v[0].getValeurBrute();
		jobContext.getLivre().setNom(nom, !jobContext.isChargementImport(), runtimeContext.getLibrairie().getToilePrincipale());
		jobContext.setCreationespece(false);
		return ETAT.PAS_DE_CHANGEMENT;
	}
}
