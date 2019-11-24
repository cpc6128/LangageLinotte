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

import java.util.List;

import org.linotte.moteur.exception.Constantes;
import org.linotte.moteur.exception.ErreurException;
import org.linotte.moteur.exception.InconnuException;
import org.linotte.moteur.exception.LectureException;
import org.linotte.moteur.exception.StopException;
import org.linotte.moteur.xml.alize.ParserEnvironnement;
import org.linotte.moteur.xml.alize.kernel.Action;
import org.linotte.moteur.xml.alize.kernel.Job;
import org.linotte.moteur.xml.alize.kernel.RuntimeContext;
import org.linotte.moteur.xml.alize.kernel.ThreadLinotte;
import org.linotte.moteur.xml.alize.kernel.i.IProduitCartesien;
import org.linotte.moteur.xml.alize.kernel.i.ParserHandler;
import org.linotte.moteur.xml.alize.kernel.processus.Processus;
import org.linotte.moteur.xml.analyse.ItemXML;

public class ObserverAction extends Action implements IProduitCartesien, ParserHandler {

	public ObserverAction() {
		super();
	}

	@Override
	public String clef() {
		return "verbe observer";
	}

	@Override
	public ETAT analyse(String param, Job job, ItemXML[] valeurs, String[] annotations) throws ErreurException, StopException {
		RuntimeContext runtimeContext = (RuntimeContext) job.getRuntimeContext();

		try {
			ItemXML p = valeurs[0];
			String paragraphe = p.getValeurBrute();
			List<ThreadLinotte> list = runtimeContext.getThreads().get(paragraphe);
			if (list != null) {
				while (!list.isEmpty()) {
					ThreadLinotte thread = list.get(0);
					if (thread != null) {
						synchronized (thread) {
							try {
								while (thread.isRunning()) {
									thread.wait(50);
									if (!job.isRunning()) {
										// Une erreur a été remontée, on arrête le verbe Observer 
										// pour laisser le noyau gérer l'erreur
										return ETAT.PAS_DE_CHANGEMENT;
									}
								}
							} catch (Exception e) {
							}
						}
					}
				}
			}
		} catch (Exception e) {
			//e.printStackTrace();
		}
		return ETAT.PAS_DE_CHANGEMENT;
	}

	@Override
	public void postAnalyse(Processus processus, ParserEnvironnement context) throws Exception {
		if (processus.isProduitCartesien()) {
			for (ItemXML[] items : processus.getMatrice()) {
				String paragraphe = items[0].toString().toLowerCase();
				Processus temp = context.getParagraphe(paragraphe);
				if (temp == null)
					throw new LectureException(new InconnuException(Constantes.MODE_RECHERCHE_IMPOSSIBLE, processus.getPosition(), paragraphe),
							processus.getPosition());
			}
		} else {
			String paragraphe = processus.getValeurs()[0].toString().toLowerCase();
			Processus temp = context.getParagraphe(paragraphe);
			if (temp == null)
				throw new LectureException(new InconnuException(Constantes.MODE_RECHERCHE_IMPOSSIBLE, processus.getPosition(), paragraphe),
						processus.getPosition());
		}

	}

}
