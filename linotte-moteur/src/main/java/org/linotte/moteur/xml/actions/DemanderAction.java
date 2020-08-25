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

import java.math.BigDecimal;

public class DemanderAction extends Action implements IProduitCartesien {

	public DemanderAction() {
		super();
	}

	@Override
	public String clef() {
		return "verbe demander";
	}

	@Override
	public ETAT analyse(String param, Job job, ItemXML[] valeurs, String[] annotations) throws Exception {
		RuntimeContext runtimeContext = (RuntimeContext) job.getRuntimeContext();

		Acteur acteur = extractionDesActeurs(Constantes.SYNTAXE_PARAMETRE_DEMANDER, job, valeurs)[0];

		if (acteur.getRole() == Role.TEXTE || acteur.getRole() == Role.NOMBRE) {
			String retour = runtimeContext.getIhm().demander(acteur.getRole(), acteur.toString());
			if (retour != null) {
				if (acteur.getRole() == Role.TEXTE)
					acteur.setValeur(retour);
				else if (acteur.getRole() == Role.NOMBRE)					
					try {
						acteur.setValeur(new BigDecimal(retour));
					} catch (NumberFormatException e1) {
						throw new ErreurException(Constantes.SYNTAXE_NOMBRE_NON_VALIDE, retour);
					}
			}
		} else
			throw new ErreurException(Constantes.SYNTAXE_PARAMETRE_DEMANDER, acteur.toString());

		return ETAT.PAS_DE_CHANGEMENT;
	}

}
