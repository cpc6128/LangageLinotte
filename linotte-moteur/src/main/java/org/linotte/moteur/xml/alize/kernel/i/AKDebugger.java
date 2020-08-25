/***********************************************************************
 * Linotte                                                             *
 * Version release date : June 18, 2014                                *
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

package org.linotte.moteur.xml.alize.kernel.i;

import org.alize.kernel.AKJob;
import org.alize.kernel.AKProcessus;

/**
 * interface permettant d'afficher le debogage (graphique ou pas)
 * 
 * @author cpc
 * 
 */
public interface AKDebugger {

	/**
	 * C'est l'objet job qui doit être synchronisé et appeler la méthode wait.
	 * Je vais améliorer ça plus tard.
	 * 
	 * @param delay
	 * @param job
	 * @param akProcessus
	 */
	void showDebugger(int delay, AKJob job, AKProcessus akProcessus);

}
