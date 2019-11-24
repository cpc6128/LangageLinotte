/***********************************************************************
 * Linotte                                                             *
 * Version release date : September 8, 2008                            *
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

package org.alize.kernel;

/**
 * Cette classe est l'utilitaire à utiliser pour créer des runtimes et jobs
 *
 */

public final class AKLoader {

	public static AKRuntime produceRuntime() {
		AKRuntime akRuntime = new AKRuntime();
		AKPatrol.addRuntime(akRuntime);
		return akRuntime;
	}

	/**
	 * Utilise par le Webonotte
	 * @param pere
	 * @return
	 */
	public static AKRuntime produceRuntime(AKRuntime pere) {
		AKRuntime akRuntime = (AKRuntime) pere.clone(true);
		AKPatrol.addRuntime(akRuntime);
		return akRuntime;
	}

	/**
	 * Utiliser AKJob produceAKjob(AKRuntime runtime)
	 * @param job
	 * @return
	 */
	public static AKJob produceAKjob(AKJob job) {
		AKJob clone = (AKJob) job.clone(false);
		return clone;
	}

}