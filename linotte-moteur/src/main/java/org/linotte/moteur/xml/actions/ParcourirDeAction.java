/***********************************************************************
 * Linotte                                                             *
 * Version release date : July 17, 2008                                *
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

import org.alize.kernel.AKRuntime;
import org.alize.security.Habilitation;
import org.linotte.moteur.entites.Acteur;
import org.linotte.moteur.exception.Constantes;
import org.linotte.moteur.exception.ErreurException;
import org.linotte.moteur.exception.LectureException;
import org.linotte.moteur.exception.RetourException;
import org.linotte.moteur.xml.alize.kernel.Action;
import org.linotte.moteur.xml.alize.kernel.Job;
import org.linotte.moteur.xml.alize.kernel.JobContext;
import org.linotte.moteur.xml.alize.kernel.RuntimeContext;
import org.linotte.moteur.xml.alize.kernel.processus.Processus;
import org.linotte.moteur.xml.analyse.ItemXML;

import java.util.Arrays;

public class ParcourirDeAction extends Action {

	public ParcourirDeAction() {
		super();
	}

	@Override
	public String clef() {
		return "verbe parcourir de";
	}

	@Override
	public ETAT analyse(String param, Job job, ItemXML[] valeurs, String[] annotations) throws Exception {
		RuntimeContext runtimeContext = (RuntimeContext) job.getRuntimeContext();

		if (runtimeContext.canDo(Habilitation.LITTLE_BIRD)) {
			throw new ErreurException(Constantes.MODE_CONSOLE);			
		}			

		String paragraphe;
		String livre;
		ItemXML p = valeurs[0];
		ItemXML l = valeurs[1];
		paragraphe = p.getValeurBrute();
		livre = l.getValeurBrute();
		AKRuntime runtimeFils = runtimeContext.retourLivre(livre);
		if (runtimeFils == null) {
			throw new ErreurException(Constantes.MODE_RECHERCHE_IMPOSSIBLE, livre);
		}
		try {

			RuntimeContext contextFils = (RuntimeContext) runtimeFils.getContext();
			Processus run = contextFils.getEnvironnment().getParagraphe(paragraphe.toLowerCase());
			if (run == null) {
				throw new ErreurException(Constantes.MODE_RECHERCHE_IMPOSSIBLE, paragraphe);
			}
			Job jobFils = (Job) runtimeFils.getJob();
			jobFils.setFirstProcessus(run);
			jobFils.setRunning(true);
			// Recherche des paramètres :
			if (valeurs.length > 2) {
				Acteur[] acteurs = extractionDesActeurs(Constantes.SYNTAXE_PARAMETRE_PARCOURIR, job, Arrays.copyOfRange(valeurs, 2, valeurs.length));
				JobContext jobcontextfils = (JobContext) jobFils.getContext();
				jobcontextfils.setDoublure(acteurs);
			}
			jobFils.execute(contextFils);

		} catch (RetourException e) {
			// rien à faire... il faut continuer !
		} catch (LectureException e) {
			e.printStackTrace();
			throw e;
			// throw new StopImportException(e, runtimeFils);
		}
		return ETAT.PAS_DE_CHANGEMENT;
	}
}
