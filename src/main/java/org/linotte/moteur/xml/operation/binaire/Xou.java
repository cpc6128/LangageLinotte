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

package org.linotte.moteur.xml.operation.binaire;

import java.math.BigDecimal;

import org.linotte.moteur.xml.operation.i.OperationBinaire;

public class Xou extends OperationBinaire {

	@Override
	public BigDecimal calculer(BigDecimal bd1, BigDecimal bd2) {
		return (bd1.intValue() != 0 ^ bd2.intValue() != 0) ? BigDecimal.ONE : BigDecimal.ZERO;
	}

}
