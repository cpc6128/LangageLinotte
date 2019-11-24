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
 * AlizeRuntime est le moteur d'exécution des états (analyse sémantique).
 * Il ne doit plus contenir de grammaire.
 * Il est produit par MoteurXML (analyse syntaxique)
 */

package org.alize.kernel;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class AKJob {

	private AKProcessus first;

	private AKJobContext context;

	private AKRuntimeContextI runtimeContext;

	private List<AKJob> sons = new CopyOnWriteArrayList<AKJob>();

	private boolean running = true;

	public AKJob(AKJobContext pcontext) {
		assert pcontext != null : "Le context ne peut être nul !";
		context = pcontext;
	}

	public void setFirstProcessus(AKProcessus process) {
		first = process;
	}

	public AKProcessus getFirstProcessus() {
		return first;
	}

	public final void execute(AKRuntimeContextI pruntimeContext) throws Exception {
		AKRuntime.staticCurrentJob.set(this);
		runtimeContext = pruntimeContext;
		assert first != null : "Le premier processus ne peut être nul !";
		context.initializeJob(this);
		try {
			execute(context);
		} finally {
			context.closeJob(this);
		}
	}

	/**
	 * Doit exécuter le premier processus
	 * @param context
	 * @throws Exception 
	 */
	public abstract void execute(AKContextI context) throws Exception;

	public AKContextI getContext() {
		return context;
	}

	public final void execute(AKJob pjob) throws Exception {
		pjob.execute(runtimeContext);
	}

	public AKRuntimeContextI getRuntimeContext() {
		return runtimeContext;
	}

	public Object clone(boolean strict) {
		return null;
	}

	public void stop() {
		running = false;
		for (AKJob job : sons) {
			if (job != null && !job.isDead())
				job.stop();
		}
	}

	public List<AKJob> getSons() {
		return sons;
	}

	public int countSons() {
		int n = sons.size();
		for (AKJob job : sons) {
			if (job != null)
				n = n + job.countSons();
		}
		return n;
	}

	public boolean isDead() {
		return !running;
	}

	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

}