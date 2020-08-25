/***********************************************************************
 * Linotte                                                             *
 * Version release date : September 01, 2006                           *
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

package org.linotte.moteur.xml.alize.parseur.a;

import org.w3c.dom.Node;

public abstract class NFormat extends NExpression {

	public enum TYPE {
		MOT, RESTE_LIGNE, JUSQUAUTOKEN
	};

	private TYPE valeurType = null;

	public NFormat(Node node) {
		super(node);
		initAttributs();
	}

	public NFormat(NFormat n) {
		super(n);
		valeurType = n.valeurType;
	}

	public void initAttributs() {
		if (getAttribut("type") != null) {
			String type = getAttribut("type");
			if ("resteligne".equals(type)) {
				valeurType = TYPE.RESTE_LIGNE;
			} else if ("mot".equals(type)) {
				valeurType = TYPE.MOT;
			} else if ("jusquautoken".equals(type)) {
				valeurType = TYPE.JUSQUAUTOKEN;
			}
		}
	}

	public TYPE getValeurType() {
		return valeurType;
	}

}
