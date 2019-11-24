/***********************************************************************
 * Linotte                                                             *
 * Version release date : December 07, 2008                            *
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

package org.linotte.moteur.xml.alize.kernel;

import org.alize.kernel.AKLoader;
import org.linotte.moteur.entites.Acteur;
import org.linotte.moteur.exception.Constantes;
import org.linotte.moteur.exception.ErreurException;
import org.linotte.moteur.exception.LectureException;
import org.linotte.moteur.exception.RetourException;
import org.linotte.moteur.exception.RetournerException;
import org.linotte.moteur.exception.StopException;
import org.linotte.moteur.xml.alize.kernel.processus.Processus;

/**
 * Représente un {@link Job} parallélisé dans un thread
 * @author CPC
 *
 */
public class ThreadLinotte implements Runnable {

	private Job job;

	private Job jobParent;

	private Processus processus;

	private Acteur[] acteurs = null;

	/**
	 * Ce booleen est ajouté dans le cas où le job n'est pas encore construit,
	 * c'est à dire, job = null, impossible de le stopper car il n'existe pas encore !
	 */
	public boolean forceToStop = false;

	public Thread threadCourant;

	private String paragraphe;

	public ThreadLinotte(Job pmoteurXMLParent, Processus pprocessus, Acteur[] pacteurs, String pparagraphe) {
		jobParent = pmoteurXMLParent;
		acteurs = pacteurs;
		processus = pprocessus;
		paragraphe = pparagraphe;
		job = (Job) AKLoader.produceAKjob(jobParent);
	}

	public void run() {
		threadCourant = Thread.currentThread();
		threadCourant.setName(processus.toString());
		RuntimeContext runtimeContext = (RuntimeContext) jobParent.getRuntimeContext();
		try {
			if (forceToStop)
				job.stop();
			job.setFirstProcessus(processus);
			job.setParallelise();
			((JobContext) job.getContext()).setDoublure(acteurs);
			job.execute(runtimeContext);
		} catch (RetournerException e) {
			jobParent.stopWithException(new LectureException(new ErreurException(Constantes.SYNTAXE_PARAMETRE_RETOURNER_INTERDIT), job.getCurrentProcessus()
					.getPosition()));
			runtimeContext.reveille();
		} catch (RetourException e) {
			//jobParent.stop(new ThreadException(new LectureException(new ErreurException(Constantes.SYNTAXE_PARAMETRE_RETOURNER_INTERDIT), 0)));
			// Le verbe revenir arrête le verbe temporiser....
			//runtimeContext.reveille();
		} catch (StopException e) {
			job.stopWithException(e);
			runtimeContext.reveille();
		} catch (LectureException e) {
			// Il faut réveiller les threads endormis !
			jobParent.stopWithException(e);
			runtimeContext.reveille();
		} catch (Exception e) {
			jobParent.stopWithException(e);
			runtimeContext.reveille();
		} finally {
			//TODO à remonter dans Alizé
			jobParent.getSons().remove(job);
			job.setRunning(false);
			synchronized (this) {
				notify();
			}
			if (runtimeContext.getThreads() != null)
				runtimeContext.getThreads().get(paragraphe).remove(this);
		}
	}

	public boolean isRunning() {
		return job.isRunning();
	}

	public Job getJob() {
		return job;
	}

	@Override
	public String toString() {
		return paragraphe;
	}

}
