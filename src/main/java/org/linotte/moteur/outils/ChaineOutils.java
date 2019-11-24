/***********************************************************************
 * Linotte                                                             *
 * Version release date : November 24, 2005                            *
 * Author : Mounes Ronan ronan.mounes@amstrad.eu                        *
 *                                                                     *
 *     http://langagelinotte.free.fr                                   *
 *                                                                     *
 * This code is released under the GNU GPL license, version 2 or       *
 * later, for educational and non-commercial purposes only.            *
 * If any part of the code is to be included in a commercial           *
 * software, please contact us first for a clearance at                *
 *   ronan.mounes@amstrad.eu                                            *
 *                                                                     *
 *   This notice must remain intact in all copies of this code.        *
 *   This code is distributed WITHOUT ANY WARRANTY OF ANY KIND.        *
 *   The GNU GPL license can be found at :                             *
 *           http://www.gnu.org/copyleft/gpl.html                      *
 *                                                                     *
 ***********************************************************************/

package org.linotte.moteur.outils;

public class ChaineOutils {

	// http://www.cs.wisc.edu/~shavlik/cs760/AgentWorld/Utils.java-sample
	public static int indexOfIgnoreCase(String str, String query, int fromIndex) {
		int qlength = query.length();
		int max = (str.length() - qlength);
		char schar, qchar;
		int k, j, n;
		test: for (int i = ((fromIndex < 0) ? 0 : fromIndex); i <= max; i++) {
			k = 0;
			j = i;
			n = qlength;
			while (n-- > 0) {
				schar = str.charAt(j++);
				qchar = query.charAt(k++);
				if ((schar != qchar) && (Character.toLowerCase(schar) != Character.toLowerCase(qchar)))
					continue test;
			}
			return i;
		}
		return -1;
	}

	public static boolean findInArray(String[] tab, String v) {
		if (tab != null)
			for (String s : tab) {
				if (s.equals(v))
					return true;
			}
		return false;
	}
}
