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

import java.math.BigDecimal;

import org.linotte.moteur.entites.Acteur;
import org.linotte.moteur.entites.Role;
import org.linotte.moteur.xml.alize.kernel.Job;
import org.linotte.moteur.xml.analyse.Mathematiques;
import org.linotte.moteur.xml.analyse.Mathematiques.Temoin;

public abstract class OperationBinaire implements Operation {

	public Object valeurBrute1;
	public Object valeurBrute2;

	public abstract BigDecimal calculer(BigDecimal bd1, BigDecimal bd2);

	public Object calculer(Job moteur, Temoin temoin, Object token_courant) throws Exception {
		BigDecimal bd1, bd2;

		Temoin temoin1 = new Temoin();
		Object o1;
		if (valeurBrute1 instanceof Object[])
			o1 = Mathematiques.shrink((Object[]) valeurBrute1, moteur, temoin1);
		else
			o1 = Mathematiques.shrink(valeurBrute1, moteur, temoin1);
		if (!temoin1.isActeur()) {
			// Pas besoin de recalculer au prochain passage
			valeurBrute1 = o1;
		} else {
			temoin.setActeur(true);
		}

		Temoin temoin2 = new Temoin();
		Object o2;
		if (valeurBrute2 instanceof Object[])
			o2 = Mathematiques.shrink((Object[]) valeurBrute2, moteur, temoin2);
		else if (valeurBrute2 instanceof Operation)
			o2 = ((Operation) valeurBrute2).calculer(moteur, temoin, token_courant);
		else
			o2 = Mathematiques.shrink(valeurBrute2, moteur, temoin2);
		if (!temoin2.isActeur()) {
			// Pas besoin de recalculer au prochain passage
			valeurBrute2 = o2;
		} else {
			temoin.setActeur(true);
		}

		if (o1 instanceof BigDecimal) {
			bd1 = (BigDecimal) o1;
		} else if (o1 instanceof Acteur && ((Acteur) o1).getRole() == Role.NOMBRE) {
			bd1 = (BigDecimal) ((Acteur) o1).getValeur();
		} else {
			throw new Exception("Opération mathématique '" + token_courant + "' interdite avec " + o1);
		}

		if (o2 instanceof BigDecimal) {
			bd2 = (BigDecimal) o2;
		} else if (o2 instanceof Acteur && ((Acteur) o2).getRole() == Role.NOMBRE) {
			bd2 = (BigDecimal) ((Acteur) o2).getValeur();
		} else {
			throw new Exception("Opération mathématique '" + token_courant + "' interdite avec " + o2);
		}

		return calculer(bd1, bd2);
	}
	
	public String toString() {
		return getClass().getSimpleName().toLowerCase();
	}
	
}