/***********************************************************************
 * Linotte                                                             *
 * Version release date : January 11, 2009                             *
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
 *                                                                     *
 ***********************************************************************/

package org.linotte.moteur.xml.alize.kernel.processus;

import java.util.ArrayList;
import java.util.List;

import org.alize.kernel.AKJob;
import org.alize.kernel.AKProcessus;
import org.linotte.greffons.api.AKMethod;
import org.linotte.greffons.externe.Greffon.ObjetLinotte;
import org.linotte.greffons.outils.ObjetLinotteFactory;
import org.linotte.moteur.entites.Acteur;
import org.linotte.moteur.entites.Prototype;
import org.linotte.moteur.exception.ErreurException;
import org.linotte.moteur.exception.LinotteException;
import org.linotte.moteur.exception.RetournerException;
import org.linotte.moteur.xml.alize.kernel.Job;
import org.linotte.moteur.xml.alize.kernel.RuntimeContext;

public class ProcessusSlot extends Processus {

	private AKMethod method;
	private Acteur[] acteurs;
	private Acteur prototype;

	public ProcessusSlot(AKMethod method, Acteur[] t, Acteur prototype, int position) {
		super();
		this.method = method;
		this.acteurs = t;
		this.prototype = prototype;
		this.setPosition(position);

	}

	@Override
	public AKProcessus execute(AKJob job) throws Exception {
		List<ObjetLinotte> listActeurs = new ArrayList<ObjetLinotte>();
		for (int i = 0; acteurs[i] != null; i++) {
			listActeurs.add(ObjetLinotteFactory.copy(acteurs[i]));
		}
		try {
			ObjetLinotte retour;
			if (prototype instanceof Prototype)
				retour = method.appeler(((Prototype) prototype).getGreffon(), listActeurs.toArray(new ObjetLinotte[listActeurs.size()]));
			else
				retour = method.appeler(null, listActeurs.toArray(new ObjetLinotte[listActeurs.size()]));
			throw new RetournerException(ObjetLinotteFactory.copy((ObjetLinotte) retour, ((RuntimeContext) ((Job) job).getRuntimeContext()).getLibrairie()));
		} catch (RetournerException e) {
			throw e;
		} catch (LinotteException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw new ErreurException(0, e.getMessage());
		}
	}

}
