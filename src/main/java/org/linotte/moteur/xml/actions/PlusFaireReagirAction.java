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

import java.util.List;

import org.linotte.frame.listener.Listener;
import org.linotte.greffons.externe.Composant;
import org.linotte.moteur.entites.Acteur;
import org.linotte.moteur.entites.Prototype;
import org.linotte.moteur.entites.PrototypeGraphique;
import org.linotte.moteur.entites.Role;
import org.linotte.moteur.exception.Constantes;
import org.linotte.moteur.exception.ErreurException;
import org.linotte.moteur.xml.alize.kernel.Action;
import org.linotte.moteur.xml.alize.kernel.Job;
import org.linotte.moteur.xml.alize.kernel.RuntimeContext;
import org.linotte.moteur.xml.alize.kernel.i.IProduitCartesien;
import org.linotte.moteur.xml.analyse.ItemXML;

public class PlusFaireReagirAction extends Action implements IProduitCartesien {

	public PlusFaireReagirAction() {
		super();
	}

	@Override
	public String clef() {
		return "verbe ne plus faire réagir";
	}

	@Override
	public ETAT analyse(String param, Job job, ItemXML[] valeurs, String[] annotations) throws Exception {
		RuntimeContext runtimeContext = (RuntimeContext) job.getRuntimeContext();

		Acteur[] acteurs = extractionDesActeurs(Constantes.SYNTAXE_PARAMETRE_FAIRE_REAGIR, job, valeurs);

		Acteur a = acteurs[0];
		if (!(a.getRole() == Role.ESPECE))
			throw new ErreurException(Constantes.SYNTAXE_PARAMETRE_FAIRE_REAGIR, a.toString());
		Acteur evenement = acteurs[1];

		Prototype e = (Prototype) a;
		if (e.isEspeceGraphique()) {
			PrototypeGraphique eg = (PrototypeGraphique) e;
			List<Listener> t = null;

			if (!(evenement.getRole() == Role.TEXTE))
				throw new ErreurException(Constantes.SYNTAXE_PARAMETRE_FAIRE_REAGIR, evenement.toString());

			if ("souris entrante".equals(evenement.getValeur())) {
				t = eg.cleanEntreeListeners();
			} else if ("souris sortante".equals(evenement.getValeur())) {
				t = eg.cleanSortieListeners();
			} else if ("clic souris".equals(evenement.getValeur())) {
				t = eg.cleanCliqueListeners();
			} else if ("double clic souris".equals(evenement.getValeur())) {
				t = eg.cleanDoubleCliqueListeners();
			} else if ("glisser-déposer".equals(evenement.getValeur())) {
				t = eg.cleanDragAndDropListeners();
			} else if ("début glisser-déposer".equals(evenement.getValeur())) {
				t = eg.cleanDebutDragAndDropListeners();
			} else {
				throw new ErreurException(Constantes.SYNTAXE_PARAMETRE_FAIRE_REAGIR_EVENEMENT, evenement.toString());
			}

			if (t != null) {
				synchronized (t) {
					synchronized (runtimeContext.getEvenements()) {
						runtimeContext.getEvenements().removeAll(t);
					}
				}
			}

		} else if (e.getGreffon() != null && e.getGreffon() instanceof Composant) {

			Composant composant = (Composant) e.getGreffon();

			if ("souris entrante".equals(evenement.getValeur())) {
				composant.cleanEnterListeners();
			} else if ("souris sortante".equals(evenement.getValeur())) {
				composant.cleanSortieListeners();
			} else if ("clic souris".equals(evenement.getValeur())) {
				composant.cleanClicListener();
			} else if ("clic droit souris".equals(evenement.getValeur())) {
				composant.cleanClicDroitListener();
			} else if ("double clic souris".equals(evenement.getValeur())) {
				composant.cleanDoubleCliqueListeners();
			} else if ("glisser-déposer".equals(evenement.getValeur())) {
				composant.cleanDragAndDropListeners();
			} else {
				throw new ErreurException(Constantes.SYNTAXE_PARAMETRE_FAIRE_REAGIR_EVENEMENT, evenement.toString());
			}

		} else
			throw new ErreurException(Constantes.SYNTAXE_PARAMETRE_FAIRE_REAGIR, a.toString());

		return ETAT.PAS_DE_CHANGEMENT;

	}

}
