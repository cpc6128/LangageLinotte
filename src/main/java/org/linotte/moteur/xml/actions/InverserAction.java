/***********************************************************************
 * Linotte                                                             *
 * Version release date : September 11, 2006                           *
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

import java.math.BigDecimal;

import org.linotte.moteur.entites.Acteur;
import org.linotte.moteur.entites.Casier;
import org.linotte.moteur.entites.Role;
import org.linotte.moteur.exception.Constantes;
import org.linotte.moteur.exception.ErreurException;
import org.linotte.moteur.xml.alize.kernel.Action;
import org.linotte.moteur.xml.alize.kernel.Job;
import org.linotte.moteur.xml.alize.kernel.i.IProduitCartesien;
import org.linotte.moteur.xml.analyse.ItemXML;

public class InverserAction extends Action implements IProduitCartesien {

	public InverserAction() {
		super();
	}

	@Override
	public String clef() {
		return "verbe inverser";
	}

	@Override
	public ETAT analyse(String param, Job linotte, ItemXML[] valeurs, String[] annotations) throws Exception {

		//		linotte.debug("On inverse : ");

		Acteur a = extractionDesActeurs(Constantes.SYNTAXE_PARAMETRE_INVERSER, linotte, valeurs)[0];

		if (a.getRole() == Role.ESPECE)
			throw new ErreurException(Constantes.SYNTAXE_PARAMETRE_INVERSER, a.toString());

		if (a.getRole() == Role.CASIER) {
			Casier c = (Casier) a;
			c.inverse();
		} else if (a.getRole() == Role.TEXTE) {
			a.setValeur(new StringBuilder((String) a.getValeur()).reverse().toString());
		} else if (a.getRole() == Role.NOMBRE) {
			a.setValeur(new BigDecimal(((BigDecimal) a.getValeur()).multiply(new BigDecimal(Math.random())).toBigInteger()));
		}

		return ETAT.PAS_DE_CHANGEMENT;
	}

}
