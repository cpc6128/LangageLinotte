/***********************************************************************
 * Linotte                                                             *
 * Version release date : July 30, 2008                                *
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

package org.linotte.frame.coloration;

import java.util.ArrayList;
import java.util.List;

public class StyleBuffer {

	private List<Style> list = new ArrayList<Style>();

	public List<Style> retourneStyles() {
		return list;
	}

	public void ajouteStyle(Style style) {
		synchronized (list) {
			list.add(style);
		}
	}

	/**
	 * Pour ne pas effacer les derniers styles (bogue des commentaires non vert à la fin du fichier)
	 * @param style
	 */
	public void ajouteStyleEnPremier(Style style) {
		synchronized (list) {
			list.add(0,style);
		}
	}

	public void effacerStyle() {
		list.clear();
	}

}