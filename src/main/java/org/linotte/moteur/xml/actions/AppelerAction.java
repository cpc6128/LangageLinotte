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

package org.linotte.moteur.xml.actions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.linotte.moteur.entites.Acteur;
import org.linotte.moteur.entites.Role;
import org.linotte.moteur.exception.Constantes;
import org.linotte.moteur.exception.ErreurException;
import org.linotte.moteur.exception.InconnuException;
import org.linotte.moteur.exception.LectureException;
import org.linotte.moteur.exception.RoleException;
import org.linotte.moteur.xml.alize.ParserEnvironnement;
import org.linotte.moteur.xml.alize.kernel.Action;
import org.linotte.moteur.xml.alize.kernel.Job;
import org.linotte.moteur.xml.alize.kernel.RuntimeContext;
import org.linotte.moteur.xml.alize.kernel.ThreadLinotte;
import org.linotte.moteur.xml.alize.kernel.i.ActionDispatcher;
import org.linotte.moteur.xml.alize.kernel.i.ParserHandler;
import org.linotte.moteur.xml.alize.kernel.processus.Processus;
import org.linotte.moteur.xml.alize.kernel.processus.ProcessusDispatcher;
import org.linotte.moteur.xml.analyse.ItemXML;

public class AppelerAction extends Action implements ParserHandler, ActionDispatcher {

	public AppelerAction() {
		super();
	}

	@Override
	public String clef() {
		return "verbe appeler";
	}

	@Override
	public ETAT analyse(String param, Job job, ItemXML[] valeurs, String[] annotations) throws Exception {
		ItemXML p = valeurs[0];
		String paragraphe = p.getValeurBrute();
		// Recherche des paramètres :
		Acteur[] acteurs = null;
		Processus destination;

		RuntimeContext context = (RuntimeContext) job.getRuntimeContext();
		if (paragraphe.trim().startsWith("<")) {
			/*Ajout des paragraphes dynamiques */
			Acteur a = extractionDesActeurs(Constantes.SYNTAXE_PARAMETRE_APPELER, job, valeurs)[0];
			if (!(a.getRole() == Role.TEXTE)) {
				throw new RoleException(Constantes.SYNTAXE_PARAMETRE_APPELER, a.toString(), Role.TEXTE, a.getRole());
			}
			ParserEnvironnement environnement = context.getEnvironnment();
			destination = environnement.getParagraphe((String) a.getValeur());
			if (destination == null) {
				throw new ErreurException(Constantes.MODE_RECHERCHE_IMPOSSIBLE, (String) a.getValeur());
			}
		} else {
			if (valeurs.length > 1) {
				acteurs = extractionDesActeurs(Constantes.SYNTAXE_PARAMETRE_APPELER, job, Arrays.copyOfRange(valeurs, 1, valeurs.length));
			}
			// Si nous sommes dans le cas d'un boucle :
			if (((ProcessusDispatcher) job.getCurrentProcessus()).getProcessusSecondaire() != null)
				destination = ((ProcessusDispatcher) ((ProcessusDispatcher) job.getCurrentProcessus()).getProcessusSecondaire()).getAppeler();
			else
				destination = ((ProcessusDispatcher) job.getCurrentProcessus()).getAppeler();
		}
		execution(context.jobPere, acteurs, paragraphe, destination);
		return ETAT.PAS_DE_CHANGEMENT;
	}

	public void execution(Job job, Acteur[] acteurs, String paragraphe, Processus processus) {
		RuntimeContext runtimeContext = (RuntimeContext) job.getRuntimeContext();
		ThreadLinotte threadLinotte = new ThreadLinotte(job, processus, acteurs, paragraphe);
		Map<String, List<ThreadLinotte>> threads = runtimeContext.getThreads();
		synchronized (threads) {
			List<ThreadLinotte> list = threads.get(paragraphe);
			if (list == null) {
				list = new ArrayList<ThreadLinotte>();
			}
			list.add(threadLinotte);
			threads.put(paragraphe, list);
		}
		new Thread(threadLinotte).start();
		//Executor executor = Executors.newSingleThreadExecutor();
		//executor.execute(threadLinotte);
	}

	@Override
	public void postAnalyse(Processus processus, ParserEnvironnement context) throws Exception {
		if (processus.isProduitCartesien()) {
			for (ItemXML[] items : processus.getMatrice()) {
				String paragraphe = items[0].toString().toLowerCase();
				Processus temp = context.getParagraphe(paragraphe);
				if (temp == null && !paragraphe.trim().startsWith("<") /*Ajout des paragraphes dynamiques */)
					throw new LectureException(new InconnuException(Constantes.MODE_RECHERCHE_IMPOSSIBLE, processus.getPosition(), paragraphe),
							processus.getPosition());
				((ProcessusDispatcher) processus).setAppeler(temp);
			}
		} else {
			String paragraphe = processus.getValeurs()[0].toString().toLowerCase();
			Processus temp = context.getParagraphe(paragraphe);
			if (temp == null && !paragraphe.trim().startsWith("<") /*Ajout des paragraphes dynamiques */)
				throw new LectureException(new InconnuException(Constantes.MODE_RECHERCHE_IMPOSSIBLE, processus.getPosition(), paragraphe),
						processus.getPosition());
			((ProcessusDispatcher) processus).setAppeler(temp);
		}
	}

}
