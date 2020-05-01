/***********************************************************************
 * Linotte                                                             *
 * Version release date : June 01, 2010                                *
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

/*
 * Cette classe fait partie de la réécriture du moteur de Linotte.
 * Le moteur Alizé doit alléger le code de Linotte, booster la vitesse de l'interprète  
 * 
 * AlizeRuntime est le moteur d'exécution des états (analyse sémantique).
 * Il ne doit plus contenir de grammaire.
 * Il est produit par MoteurXML (analyse syntaxique)
 */

package org.alize.kernel;

public final class AKRuntime {

	private AKRuntimeContextI runtimeContext;

	private AKJob job;

	private AKException akException;

	// Permet d'acceder au job depuis les greffons
	public static ThreadLocal<AKJob> staticCurrentJob = new ThreadLocal<AKJob>();

	AKRuntime() {
	}

	public void execute() throws Exception {
		assert runtimeContext != null : "Le context ne peut être nul !";
		assert job != null : "Le job ne peut être nul !";
		runtimeContext.initializeRuntime(this);
		try {
			job.execute(runtimeContext);
		} finally {
			job.stop();
			runtimeContext.closeRuntime(this);
			AKPatrol.runtimes.remove(this);
			if (akException != null) {
				throw akException;
			}
		}
	}

	public AKRuntimeContextI getContext() {
		return runtimeContext;
	}

	public void setContext(AKRuntimeContextI pcontext) {
		runtimeContext = pcontext;
	}

	public void setJob(AKJob pjob) {
		job = pjob;
	}

	public AKJob getJob() {
		return job;
	}

	public void stopAll() {
		job.stop();
		runtimeContext.closeRuntime(this);
	}

	public void throwException(AKException pakException) {
		akException = pakException;
	}

	public Object clone(boolean strict) {
		AKRuntime akRuntime = new AKRuntime();
		akRuntime.setContext((AKRuntimeContextI) getContext().clone());
		akRuntime.setJob((AKJob) getJob().clone(strict));
		return akRuntime;
	}

}