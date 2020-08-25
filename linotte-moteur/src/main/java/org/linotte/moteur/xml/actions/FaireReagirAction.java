/***********************************************************************
 * Linotte                                                             *
 * Version release date : January 30, 2009                             *
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

import org.linotte.frame.listener.Listener;
import org.linotte.greffons.externe.Composant;
import org.linotte.moteur.entites.Acteur;
import org.linotte.moteur.entites.Prototype;
import org.linotte.moteur.entites.PrototypeGraphique;
import org.linotte.moteur.entites.Role;
import org.linotte.moteur.exception.Constantes;
import org.linotte.moteur.exception.ErreurException;
import org.linotte.moteur.exception.InconnuException;
import org.linotte.moteur.exception.LectureException;
import org.linotte.moteur.xml.alize.ParserEnvironnement;
import org.linotte.moteur.xml.alize.kernel.Action;
import org.linotte.moteur.xml.alize.kernel.Job;
import org.linotte.moteur.xml.alize.kernel.RuntimeContext;
import org.linotte.moteur.xml.alize.kernel.i.ActionDispatcher;
import org.linotte.moteur.xml.alize.kernel.i.IProduitCartesien;
import org.linotte.moteur.xml.alize.kernel.i.ParserHandler;
import org.linotte.moteur.xml.alize.kernel.processus.Processus;
import org.linotte.moteur.xml.alize.kernel.processus.ProcessusDispatcher;
import org.linotte.moteur.xml.analyse.ItemXML;

import java.util.Arrays;

public class FaireReagirAction extends Action implements ParserHandler, IProduitCartesien, ActionDispatcher {

	public FaireReagirAction() {
		super();
	}

	@Override
	public String clef() {
		return "verbe faire réagir";
	}

	@Override
	public ETAT analyse(String param, Job job, ItemXML[] valeurs, String[] annotations) throws Exception {
		RuntimeContext runtimeContext = (RuntimeContext) job.getRuntimeContext();
		String paragraphe = valeurs[2].toString();

		ItemXML[] temp = Arrays.copyOfRange(valeurs, 0, 2);
		Acteur[] acteurs = extractionDesActeurs(Constantes.SYNTAXE_PARAMETRE_FAIRE_REAGIR, job, temp);

		Acteur a = acteurs[0];
		if (!(a.getRole() == Role.ESPECE))
			throw new ErreurException(Constantes.SYNTAXE_PARAMETRE_FAIRE_REAGIR, a.toString());

		Acteur evenement = acteurs[1];
		if (!(evenement.getRole() == Role.TEXTE))
			throw new ErreurException(Constantes.SYNTAXE_PARAMETRE_FAIRE_REAGIR, evenement.toString());

		Prototype e = (Prototype) a;
		if (e.isEspeceGraphique()) {
			PrototypeGraphique eg = (PrototypeGraphique) e;

			if ("souris entrante".equals(evenement.getValeur())) {
				eg.ajouterEntreeListeners(runtimeContext.jobPere, paragraphe, ((ProcessusDispatcher) job.getCurrentProcessus()).getAppeler());
			} else if ("souris sortante".equals(evenement.getValeur())) {
				eg.ajouterSortieListeners(runtimeContext.jobPere, paragraphe, ((ProcessusDispatcher) job.getCurrentProcessus()).getAppeler());
			} else if ("clic souris".equals(evenement.getValeur())) {
				eg.ajouterCliqueListeners(runtimeContext.jobPere, paragraphe, ((ProcessusDispatcher) job.getCurrentProcessus()).getAppeler());
			} else if ("clic droit souris".equals(evenement.getValeur())) {
				eg.ajouterClicDroitListeners(runtimeContext.jobPere, paragraphe, ((ProcessusDispatcher) job.getCurrentProcessus()).getAppeler());
			} else if ("double clic souris".equals(evenement.getValeur())) {
				eg.ajouterDoubleCliqueListeners(runtimeContext.jobPere, paragraphe, ((ProcessusDispatcher) job.getCurrentProcessus()).getAppeler());
			} else if ("glisser-déposer".equals(evenement.getValeur())) {
				eg.ajouterDragAndDropListeners(runtimeContext.jobPere, paragraphe, ((ProcessusDispatcher) job.getCurrentProcessus()).getAppeler());
			} else if ("début glisser-déposer".equals(evenement.getValeur())) {
				eg.ajouterDebutDragAndDropListeners(runtimeContext.jobPere, paragraphe, ((ProcessusDispatcher) job.getCurrentProcessus()).getAppeler());
			} else {
				throw new ErreurException(Constantes.SYNTAXE_PARAMETRE_FAIRE_REAGIR_EVENEMENT, evenement.toString());
			}

			runtimeContext.getEvenements().add(eg);
			// Optimisation 0.7.5
			eg.getToile().getPanelLaToile().setEvenement(true);
		} else {

			if (e.getGreffon() != null && e.getGreffon() instanceof Composant) {

				Listener listener = new Listener(e, paragraphe, ((ProcessusDispatcher) job.getCurrentProcessus()).getAppeler(), runtimeContext.jobPere);
				Composant composant = (Composant) e.getGreffon();

				if ("souris entrante".equals(evenement.getValeur())) {
					composant.addEnterListeners(listener);
				} else if ("souris sortante".equals(evenement.getValeur())) {
					composant.ajouterSortieListeners(listener);
				} else if ("clic souris".equals(evenement.getValeur())) {
					composant.addClicListener(listener);
				} else if ("clic droit souris".equals(evenement.getValeur())) {
					composant.addClicDroitListener(listener);
				} else if ("double clic souris".equals(evenement.getValeur())) {
					composant.ajouterDoubleCliqueListeners(listener);
				} else if ("glisser-déposer".equals(evenement.getValeur())) {
					composant.ajouterDragAndDropListeners(listener);
				} else if ("touche".equals(evenement.getValeur())) {
					composant.ajouterToucheListeners(listener);
				} else {
					throw new ErreurException(Constantes.SYNTAXE_PARAMETRE_FAIRE_REAGIR_EVENEMENT, evenement.toString());
				}

				// ((Composant) e.getGreffon()).ajouterClicListener(listener);

			} else
				throw new ErreurException(Constantes.SYNTAXE_PARAMETRE_FAIRE_REAGIR, a.toString());
		}

		return ETAT.PAS_DE_CHANGEMENT;

	}

	@Override
	public void postAnalyse(Processus processus, ParserEnvironnement context) throws Exception {
		if (processus.isProduitCartesien()) {
			for (ItemXML[] items : processus.getMatrice()) {
				String paragraphe = items[2].toString().toLowerCase();
				Processus temp = context.getParagraphe(paragraphe);
				if (temp == null)
					throw new LectureException(new InconnuException(Constantes.MODE_RECHERCHE_IMPOSSIBLE, processus.getPosition(), paragraphe),
							processus.getPosition());
				((ProcessusDispatcher) processus).setAppeler(temp);
			}
		} else {
			String paragraphe = processus.getValeurs()[2].toString().toLowerCase();
			Processus temp = context.getParagraphe(paragraphe);
			if (temp == null)
				throw new LectureException(new InconnuException(Constantes.MODE_RECHERCHE_IMPOSSIBLE, processus.getPosition(), paragraphe),
						processus.getPosition());
			((ProcessusDispatcher) processus).setAppeler(temp);
		}
	}

}
