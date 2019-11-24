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

package org.linotte.moteur.xml.analyse;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.linotte.moteur.outils.Chaine;

public class Synonyme {

	private static Map<Chaine, Set<Chaine>> synonymes = new HashMap<Chaine, Set<Chaine>>();

	public static void addSynonyme(List<Chaine> l) {
		synchronized (synonymes) {
			for (int i = 0; i < l.size(); i++) {
				Set<Chaine> l2 = new HashSet<Chaine>();
				l2.addAll(l);
				if (synonymes.get(l.get(i)) != null)
					l2.addAll(synonymes.get(l.get(i)));
				synonymes.put(l.get(i), l2);
			}
		}
	}

	public static Chaine equals(Chaine token, String token2) {
		if (token.equals(token2))
			return token;
		Set<Chaine> l = (Set<Chaine>) synonymes.get(token);
		if (l != null) {
			for (Chaine object : l)
				if (object.equals(token2)) {
					return object;
				}
		}
		return null;
	}

	public static Set<Chaine> getSynonyme(String valeur) {
		String compare = valeur.toLowerCase();
		Set<Chaine> set = synonymes.keySet();
		for (Chaine chaine : set) {
			if (chaine.toString().toLowerCase().startsWith(compare)) {
				return synonymes.get(chaine);
			}
		}
		return null;
	}
}
