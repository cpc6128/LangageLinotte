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
import org.linotte.moteur.entites.Role;
import org.linotte.moteur.exception.Constantes;
import org.linotte.moteur.exception.ErreurException;
import org.linotte.moteur.xml.alize.kernel.Action;
import org.linotte.moteur.xml.alize.kernel.Job;
import org.linotte.moteur.xml.alize.kernel.RuntimeContext;
import org.linotte.moteur.xml.alize.kernel.i.IProduitCartesien;
import org.linotte.moteur.xml.analyse.ItemXML;
import org.linotte.moteur.xml.api.IHM;

import java.math.BigDecimal;

public class QuestionnerAction extends Action implements IProduitCartesien {

	public QuestionnerAction() {
		super();
	}

	@Override
	public String clef() {
		return "verbe questionner";
	}

	@Override
	public ETAT analyse(String param, Job job, ItemXML[] valeurs, String[] annotations) throws Exception {
		RuntimeContext runtimeContext = (RuntimeContext) job.getRuntimeContext();

		Acteur[] acteurs = extractionDesActeurs(Constantes.SYNTAXE_PARAMETRE_QUESTIONNER, job, valeurs);
		Acteur acteur2 = acteurs[0];
		Acteur acteur1 = acteurs[1];

		if (acteur1.getRole() == Role.TEXTE) {
			if (acteur2.getRole() == Role.TEXTE || acteur2.getRole() == Role.NOMBRE) {
				IHM ihm = runtimeContext.getIhm();

				String retour = ihm.questionne(acteur1.getValeur().toString(), acteur2.getRole(), acteur2.toString());
				if (retour != null) {
					if (acteur2.getRole() == Role.TEXTE)
						acteur2.setValeur(retour);
					else
						acteur2.setValeur(new BigDecimal(retour));
				}
			} else {
				throw new ErreurException(Constantes.SYNTAXE_PARAMETRE_QUESTIONNER, acteur2.toString());
			}
		} else {
			throw new ErreurException(Constantes.SYNTAXE_PARAMETRE_QUESTIONNER, acteur1.toString());
		}
		return ETAT.PAS_DE_CHANGEMENT;
	}

}
