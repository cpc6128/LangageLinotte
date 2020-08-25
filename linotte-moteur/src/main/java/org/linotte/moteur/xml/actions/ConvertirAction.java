/***********************************************************************
 * Linotte                                                             *
 * Version release date : September 01, 2006                           *
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
import org.linotte.moteur.xml.analyse.Mathematiques;

import java.math.BigDecimal;

public class ConvertirAction extends Action implements IProduitCartesien {

	public ConvertirAction() {
		super();
	}

	@Override
	public String clef() {
		return "verbe convertir";
	}

	@Override
	public ETAT analyse(String param, Job linotte, ItemXML[] valeurs, String[] annotations) throws Exception {
		//		linotte.debug("on convertie...");

		Acteur[] acteurs = extractionDesActeurs(Constantes.SYNTAXE_PARAMETRE_CONVERTIR, linotte, valeurs);
		Acteur acteur1 = acteurs[0];
		Acteur acteur2 = acteurs[1];

		if (acteur1.getRole() == Role.NOMBRE && acteur2.getRole() == Role.TEXTE) {
			// On peut copier un nombre dans un texte
			acteur2.setValeur(acteur1.getValeur().toString());
		} else if (acteur1.getRole() == Role.TEXTE && acteur2.getRole() == Role.NOMBRE) {
			try {
				acteur2.setValeur(Mathematiques.isBigDecimal(acteur1.getValeur().toString()));
			} catch (NumberFormatException e1) {
				acteur2.setValeur(new BigDecimal(0));
			}
		} else
			throw new ErreurException(Constantes.SYNTAXE_PARAMETRE_CONVERTIR);

		return ETAT.PAS_DE_CHANGEMENT;
	}

}
