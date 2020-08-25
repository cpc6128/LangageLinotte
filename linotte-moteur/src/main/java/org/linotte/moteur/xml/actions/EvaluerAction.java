/***********************************************************************
 * Linotte                                                             *
 * Version release date : January 30, 2009                             *
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

import org.linotte.moteur.entites.Acteur;
import org.linotte.moteur.entites.Role;
import org.linotte.moteur.exception.Constantes;
import org.linotte.moteur.exception.ErreurException;
import org.linotte.moteur.xml.alize.kernel.Action;
import org.linotte.moteur.xml.alize.kernel.Job;
import org.linotte.moteur.xml.alize.kernel.i.IProduitCartesien;
import org.linotte.moteur.xml.analyse.ItemXML;

public class EvaluerAction extends Action implements IProduitCartesien {

	public EvaluerAction() {
		super();
	}

	@Override
	public String clef() {
		return "verbe évaluer";
	}

	@Override
	public ETAT analyse(String param, Job linotte, ItemXML[] valeurs, String[] annotations) throws Exception {

		Acteur acteur = extractionDesActeurs(Constantes.SYNTAXE_PARAMETRE_EVALUER, linotte, valeurs)[0];

		if (acteur.getRole() == Role.TEXTE) {
			String v = (String) acteur.getValeur();
			ItemXML itemXML = ItemXML.factory(v, ItemXML.VALEUR);
			v = String.valueOf(itemXML.retourneValeurTransformee(linotte));
			acteur.setValeur(v);
		} else
			throw new ErreurException(Constantes.SYNTAXE_PARAMETRE_EVALUER, acteur.toString());

		return ETAT.PAS_DE_CHANGEMENT;
	}

}
