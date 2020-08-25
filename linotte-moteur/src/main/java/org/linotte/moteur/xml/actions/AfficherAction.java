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

import org.linotte.moteur.entites.Acteur;
import org.linotte.moteur.entites.Casier;
import org.linotte.moteur.entites.Prototype;
import org.linotte.moteur.entites.Role;
import org.linotte.moteur.exception.Constantes;
import org.linotte.moteur.exception.ErreurException;
import org.linotte.moteur.exception.StopException;
import org.linotte.moteur.xml.alize.kernel.Action;
import org.linotte.moteur.xml.alize.kernel.Job;
import org.linotte.moteur.xml.alize.kernel.RuntimeContext;
import org.linotte.moteur.xml.alize.kernel.i.IProduitCartesien;
import org.linotte.moteur.xml.analyse.ItemXML;
import org.linotte.moteur.xml.api.IHM;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class AfficherAction extends Action implements IProduitCartesien {

	public AfficherAction() {
		super();
	}

	@Override
	public String clef() {
		return "verbe afficher";
	}

	@Override
	public ETAT analyse(String param, Job job, ItemXML[] valeurs, String[] annotations) throws Exception {
		RuntimeContext runtimeContext = (RuntimeContext) job.getRuntimeContext();

		if (annotations.length == 0) {
			Acteur a = extractionDesActeurs(Constantes.SYNTAXE_PARAMETRE_AFFICHER, job, valeurs)[0];
			afficher(a, runtimeContext.getIhm());
		} else {
			runtimeContext.getIhm().afficher(valeurs[0].getValeurBrute(), Role.TEXTE);
		}

		return ETAT.PAS_DE_CHANGEMENT;

	}

	public static void afficher(Acteur acteur, IHM ihm) throws ErreurException, StopException {
		if (acteur instanceof Prototype) {
			Prototype espece = (Prototype) acteur;
			Set<String> acteurs = espece.retourAttributs();
			for (String acteur2 : acteurs) {
				afficher(espece.retourneAttribut(acteur2), ihm);
			}
		} else if (acteur instanceof Casier) {
			Casier casier = (Casier) acteur;
			List<?> acteurs = (List<?>) casier.getValeur();
			for (Iterator<?> i = acteurs.iterator(); i.hasNext();) {
				afficher((Acteur) i.next(), ihm);
			}
		} else {
			ihm.afficher(acteur.getValeur().toString(), acteur.getRole());
		}
	}

}