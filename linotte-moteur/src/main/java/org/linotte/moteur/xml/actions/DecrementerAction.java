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

import java.math.BigDecimal;

public class DecrementerAction extends Action implements IProduitCartesien {

	public DecrementerAction() {
		super();
	}

	@Override
	public String clef() {
		return "verbe décrémenter";
	}

	@Override
	public ETAT analyse(String param, Job runtime, ItemXML[] valeurs, String[] annotations) throws Exception {

		Acteur a = extractionDesActeurs(Constantes.SYNTAXE_PARAMETRE_DECREMENTER, runtime, valeurs)[0];

		if (!(a.getRole() == Role.NOMBRE))
			throw new ErreurException(Constantes.SYNTAXE_PARAMETRE_DECREMENTER, a.toString());

		a.setValeur(((BigDecimal) a.getValeur()).subtract(new BigDecimal(1)));

		return ETAT.PAS_DE_CHANGEMENT;
	}
}
