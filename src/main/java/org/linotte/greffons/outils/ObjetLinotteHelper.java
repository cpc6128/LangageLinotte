/***********************************************************************
 * Linotte                                                             *
 * Version release date : March 08, 2012                               *
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
 *                                                                     *
 ***********************************************************************/

package org.linotte.greffons.outils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import org.linotte.greffons.externe.Greffon.GreffonException;
import org.linotte.greffons.externe.Greffon.ObjetLinotte;
import org.linotte.greffons.externe.Greffon.ROLE;
import org.linotte.moteur.exception.ErreurException;

public class ObjetLinotteHelper {

	/**
	 * Greffon V2
	 * @param a
	 * @return
	 * @throws ErreurException
	 * @throws GreffonException
	 */
	public static ObjetLinotte copy(String a) throws GreffonException {
		org.linotte.greffons.externe.Greffon.Acteur acteur = new org.linotte.greffons.externe.Greffon.Acteur(ROLE.TEXTE);
		acteur.setValeur(a);
		return acteur;
	}

	public static ObjetLinotte copy(Integer a) throws GreffonException {
		org.linotte.greffons.externe.Greffon.Acteur acteur = new org.linotte.greffons.externe.Greffon.Acteur(ROLE.NOMBRE);
		acteur.setValeur(new BigDecimal(a));
		return acteur;
	}

	public static ObjetLinotte copy(Long a) throws GreffonException {
		org.linotte.greffons.externe.Greffon.Acteur acteur = new org.linotte.greffons.externe.Greffon.Acteur(ROLE.NOMBRE);
		acteur.setValeur(new BigDecimal(a));
		return acteur;
	}

	public static ObjetLinotte copy(BigInteger a) throws GreffonException {
		org.linotte.greffons.externe.Greffon.Acteur acteur = new org.linotte.greffons.externe.Greffon.Acteur(ROLE.NOMBRE);
		acteur.setValeur(new BigDecimal(a));
		return acteur;
	}

	public static ObjetLinotte copy(BigDecimal a) throws GreffonException {
		org.linotte.greffons.externe.Greffon.Acteur acteur = new org.linotte.greffons.externe.Greffon.Acteur(ROLE.NOMBRE);
		acteur.setValeur(a);
		return acteur;
	}

	public static ObjetLinotte copy(Double a) throws GreffonException {
		org.linotte.greffons.externe.Greffon.Acteur acteur = new org.linotte.greffons.externe.Greffon.Acteur(ROLE.NOMBRE);
		acteur.setValeur(new BigDecimal(a));
		return acteur;
	}

	public static ObjetLinotte copy(Float a) throws GreffonException {
		org.linotte.greffons.externe.Greffon.Acteur acteur = new org.linotte.greffons.externe.Greffon.Acteur(ROLE.NOMBRE);
		acteur.setValeur(new BigDecimal(a));
		return acteur;
	}

	public static ObjetLinotte copy(Boolean a) throws GreffonException {
		org.linotte.greffons.externe.Greffon.Acteur acteur = new org.linotte.greffons.externe.Greffon.Acteur(ROLE.DRAPEAU);
		acteur.setValeur(a.booleanValue() ? new BigDecimal(1) : new BigDecimal(0));
		return acteur;
	}

	@SuppressWarnings("rawtypes")
	public static ObjetLinotte copy(List a) throws GreffonException {
		ROLE role;
		if (a.size() > 0 && a.get(0) instanceof String) {
			role = ROLE.TEXTE;
		} else {
			role = ROLE.NOMBRE;
		}
		org.linotte.greffons.externe.Greffon.Casier casier = new org.linotte.greffons.externe.Greffon.Casier(role);
		for (Object retour : a) {
			Object copy;
			if (retour instanceof String) {
				copy = copy((String) retour);
			} else if (retour instanceof Boolean) {
				copy = copy((Boolean) retour);
			} else if (retour instanceof Integer) {
				copy = copy((Integer) retour);
			} else if (retour instanceof BigDecimal) {
				copy = copy((BigDecimal) retour);
			} else if (retour instanceof Long) {
				copy = copy((Long) retour);
			} else if (retour instanceof Double) {
				copy = copy((Double) retour);
			} else {
				copy = copy((BigInteger) retour);
			}
			casier.add((ObjetLinotte) copy);
		}
		return casier;
	}

}
