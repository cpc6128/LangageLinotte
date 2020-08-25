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

import org.linotte.moteur.xml.analyse.Mathematiques;
import org.linotte.moteur.xml.analyse.Mathematiques.ANGLE;
import org.linotte.moteur.xml.operation.i.OperationUnaire;

import java.math.BigDecimal;

public class Tan extends OperationUnaire {

	@Override
	public BigDecimal calculer(BigDecimal bd) {
		double angle = bd.doubleValue();
		double result;
		if (Mathematiques.angle == ANGLE.DEGREE) {
			if (angle % 45 == 0) {
				result = Math.round(Math.tan(Math.toRadians(angle)));
			} else {
				result = Math.tan(Math.toRadians(angle));
			}
		} else {
			result = Math.tan(angle);
		}
		return new BigDecimal(result);
	}

}
