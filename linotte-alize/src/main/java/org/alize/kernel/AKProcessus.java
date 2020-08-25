/***********************************************************************
 * Linotte                                                             *
 * Version release date : June 01, 2010                                *
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

/*
 * Cette classe fait partie de la réécriture du moteur de Linotte.
 * Le moteur Alizé doit alléger le code de Linotte, booster la vitesse de l'interprète  
 * 
 * AlizeEtatContainer contient un état. c'est un proxy
 * Lui seul connait l'état suivant
 * Il ne doit plus contenir de grammaire.
 */

package org.alize.kernel;

public abstract class AKProcessus {

	private AKProcessus next;

	public abstract AKProcessus execute(final AKJob job) throws Exception;

	public final void setNextProcess(AKProcessus pprocess) {
		next = pprocess;
	}

	public final AKProcessus getNextProcess() {
		return next;
	}
}
