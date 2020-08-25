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

public class Cos extends OperationUnaire {

	public BigDecimal calculer(BigDecimal bd) {
		double angle = bd.doubleValue();
		double result;
		if (Mathematiques.angle == ANGLE.DEGREE) {
			if (angle % 90 == 0) {
				result = Math.round(Math.cos(Math.toRadians(angle)));
			} else if (angle % 60 == 0) {
				result = Math.floor(Math.cos(Math.toRadians(angle))) + 0.5;
			} else {
				result = Math.cos(Math.toRadians(angle));
			}
		} else {
			result = Math.cos(angle);
		}
		return new BigDecimal(result);
	}
}
