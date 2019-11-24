/***********************************************************************
 * Linotte                                                             *
 * Version release date : February 20, 2008                            *
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

package org.linotte.frame.outils;

import java.awt.Font;
import java.util.HashMap;
import java.util.Map;

/**
 * Classe outil pour la gestion des polices
 */

public class FontHelper {

	private static Map<String, Font> fonts = new HashMap<String, Font>();

	private final static int STYLE = Font.PLAIN;
	private final static int SIZE = 15;

	public static Font createFont(String n, int taille) {
		String clef = n + taille;
		Font f = fonts.get(clef);
		if (f != null) {
			return f;
		} else {
			f = new Font(n, STYLE, SIZE + taille);
		}
		fonts.put(clef, f);
		return f;
	}
}
