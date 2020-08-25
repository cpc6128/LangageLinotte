/***********************************************************************
 * Linotte                                                             *
 * Version release date : April 1, 2011                                *
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

package org.linotte.moteur.xml.operation.i;

import org.linotte.moteur.entites.Acteur;
import org.linotte.moteur.entites.Role;
import org.linotte.moteur.xml.alize.kernel.Job;
import org.linotte.moteur.xml.analyse.Mathematiques;
import org.linotte.moteur.xml.analyse.Mathematiques.Temoin;

import java.math.BigDecimal;

public abstract class OperationUnaire implements Operation {

	public Object valeurBrute;

	public abstract BigDecimal calculer(BigDecimal bd);

	public Object calculer(Job moteur, Temoin temoin, Object token_courant) throws Exception {
		Temoin temoin1 = new Temoin();
		Object o;
		if (valeurBrute instanceof Object[])
			o = Mathematiques.shrink((Object[]) valeurBrute, moteur, temoin1);
		else if (valeurBrute instanceof Operation)
			o = ((Operation) valeurBrute).calculer(moteur, temoin, token_courant);
		else
			o = Mathematiques.shrink(valeurBrute, moteur, temoin1);
		if (!temoin1.isActeur()) {
			// Pas besoin de recalculer au prochain passage
			valeurBrute = o;
		}
		if (temoin1.isActeur()) {
			temoin.setActeur(true);
		}
		if (o instanceof BigDecimal) {
			return calculer((BigDecimal) o);
		} else if (o instanceof Acteur && ((Acteur) o).getRole() == Role.NOMBRE) {
			return calculer((BigDecimal) ((Acteur) o).getValeur());
		} else {
			throw new Exception("Opération mathématique '" + token_courant + "' interdite avec " + o);
		}

	}
	
	public String toString() {
		return getClass().getSimpleName().toLowerCase();
	}


}