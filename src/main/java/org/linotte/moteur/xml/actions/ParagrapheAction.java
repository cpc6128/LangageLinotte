/***********************************************************************
 * Linotte                                                             *
 * Version release date : September 01, 2006                           *
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

import java.math.BigDecimal;

import org.linotte.moteur.entites.Acteur;
import org.linotte.moteur.entites.Livre;
import org.linotte.moteur.entites.Paragraphe;
import org.linotte.moteur.entites.Role;
import org.linotte.moteur.exception.Constantes;
import org.linotte.moteur.exception.ErreurException;
import org.linotte.moteur.exception.FonctionDoublureException;
import org.linotte.moteur.outils.Chaine;
import org.linotte.moteur.xml.alize.kernel.Action;
import org.linotte.moteur.xml.alize.kernel.Job;
import org.linotte.moteur.xml.alize.kernel.JobContext;
import org.linotte.moteur.xml.alize.kernel.RuntimeContext;
import org.linotte.moteur.xml.analyse.ItemXML;
import org.linotte.moteur.xml.analyse.Mathematiques;
import org.linotte.moteur.xml.api.IHM;
import org.linotte.moteur.xml.appels.CalqueParagraphe;
import org.linotte.moteur.xml.appels.Fonction;

public class ParagrapheAction extends Action {

	public ParagrapheAction() {
		super();
	}

	@Override
	public String clef() {
		return "creation paragraphe";
	}

	@Override
	public ETAT analyse(String param, Job job, ItemXML[] valeurs, String[] annotations) throws Exception {
		JobContext jobContext = (JobContext) job.getContext();
		RuntimeContext runtimeContext = (RuntimeContext) job.getRuntimeContext();
		runtimeContext.paragrapheExecute++;

		Livre livre = jobContext.getLivre();
		String clef = "§";
		if (valeurs.length != 0)
			//throw new ErreurException(Constantes.IMPOSSIBLE_LIRE_LIVRE);
			/*String*/clef = valeurs[0].toString();

		//VERSION2.2, on ne clone plus le paragraphe
		CalqueParagraphe calqueParagraphe = new CalqueParagraphe(job.getCurrentProcessus().getPosition(), clef);
		livre.getKernelStack().ajouterAppel(calqueParagraphe);
		Paragraphe paragraphe = new Paragraphe(clef);
		livre.setParagraphe(paragraphe);

		// Linotte 2.1 :
		if (jobContext.getPrototype() != null) {
			// On force l'ajout de l'acteur moi qui représente
			paragraphe.addDoublure("moi", jobContext.getPrototype());
			calqueParagraphe.addActeurLocalDoublure(Chaine.produire("moi"), jobContext.getPrototype());
		}

		//
		/*if (valeurs.length == 1) {
			// Vérification si aucune doublure a été indiquée (Linotte 2.6.2) :
			Fonction fonction = livre.getKernelStack().recupereDerniereFonction();
			if (fonction != null) {
				int numero = jobContext.getPrototype() == null ? 0 : 1;
				if ((fonction.nombreParametres() - numero) > 0) {
					System.out.println(fonction.nombreParametres() - numero);
					throw new FonctionDoublureException(Constantes.ERREUR_DOUBLURE);
				}
			}
		}*/
		// Gestion de l'inférence des types lors des appels de fonction :
		if (valeurs.length > 1) {
			Fonction fonction = livre.getKernelStack().recupereDerniereFonction();
			int numero = jobContext.getPrototype() == null ? 0 : 1;
			if (fonction != null && (valeurs.length - 1 + numero) != fonction.nombreParametres()) {
				throw new FonctionDoublureException(Constantes.ERREUR_DOUBLURE);
			}
			for (int index = 1; index < valeurs.length; index++) {
				ItemXML parametre = valeurs[index];

				// vérifier paragraphe
				Acteur acteur = null;
				if (fonction != null) {
					acteur = fonction.getDoublure(numero);
				} else {
					// Recharger depuis le moteur (doublure d'un appel de fonction)
					acteur = jobContext.getDoublure(numero);
				}
				if (acteur != null) {
					paragraphe.addDoublure(parametre.getValeurBrute().toLowerCase(), acteur);
					calqueParagraphe.addActeurLocalDoublure(Chaine.produire(parametre.getValeurBrute()), acteur);
				} else {
					//Est-ce le premier paragraphe ?
					if (runtimeContext.paragrapheExecute == 1) {
						IHM ihm = runtimeContext.getIhm();
						String retour = ihm.questionne("Quelle est la valeur de l'acteur '" + parametre.getValeurBrute() + "' ?", Role.TEXTE, "");
						BigDecimal v = (retour != null && retour.length() > 0) ? Mathematiques.isBigDecimal(retour) : null;
						if (v != null) {
							acteur = new Acteur(Role.NOMBRE, v);
						} else {
							acteur = new Acteur(Role.TEXTE, retour);
						}
						paragraphe.addDoublure(parametre.getValeurBrute().toLowerCase(), acteur);
						calqueParagraphe.addActeurLocalDoublure(Chaine.produire(parametre.getValeurBrute()), acteur);
					} else {
						throw new ErreurException(Constantes.ERREUR_DOUBLURE);
					}
				}
				numero++;
			}

		}

		return ETAT.PAS_DE_CHANGEMENT;
	}

}