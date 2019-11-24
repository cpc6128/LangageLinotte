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

package org.linotte.moteur.xml.operation.unaire;

import java.math.BigDecimal;

import org.linotte.moteur.entites.Acteur;
import org.linotte.moteur.xml.alize.kernel.Job;
import org.linotte.moteur.xml.analyse.Mathematiques;
import org.linotte.moteur.xml.analyse.Mathematiques.Temoin;
import org.linotte.moteur.xml.operation.i.OperationUnaire;

public class Unicode extends OperationUnaire {

	@Override
	public BigDecimal calculer(BigDecimal bd) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object calculer(Job moteur, Temoin temoin, Object token_courant) throws Exception {
		Object o;
		if (valeurBrute instanceof Object[])
			o = Mathematiques.shrink((Object[]) valeurBrute, moteur, temoin);
		else
			o = Mathematiques.shrink(valeurBrute, moteur, temoin);

		if (o instanceof Acteur) {
			// Traitement unicode :
			return Character.toString((char) Integer.parseInt((String) ((Acteur) o).getValeur(), 16));
		} else {
			return Character.toString((char) Integer.parseInt((String) o, 16));
		}
	}

}
