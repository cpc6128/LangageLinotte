/***********************************************************************
 * Linotte                                                             *
 * Version release date : April 8, 2011                                *
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

import org.linotte.moteur.xml.alize.kernel.Job;
import org.linotte.moteur.xml.analyse.Mathematiques.Temoin;
import org.linotte.moteur.xml.operation.i.OperationUnaire;

import java.math.BigDecimal;

public class Hasard extends OperationUnaire {

	@Override
	public BigDecimal calculer(BigDecimal bd) {
		return new BigDecimal(Math.floor(bd.doubleValue() * Math.random()));
	}

	public Object calculer(Job moteur, Temoin temoin, Object token_courant) throws Exception {
		Object retour = super.calculer(moteur, temoin, token_courant);
		// On interdit de mettre en cache la valeur !
		temoin.setActeur(true);
		return retour;
	}

}
