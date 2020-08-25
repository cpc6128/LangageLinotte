/***********************************************************************
 * Linotte                                                             *
 * Version release date : November 23, 2005                            *
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

import java.util.HashMap;
import java.util.Map;

/**
 * Chaîne est une classe représentant une {@link String} sans que la case soit
 * prise en compte.
 * 
 * @author CPC
 *
 */

public class Chaine {

	// Optimisation :
	private static Map<String, Chaine> cacheChaine = new HashMap<String, Chaine>(1000);

	public static Chaine produire(String chaine) {
		Chaine produit = cacheChaine.get(chaine);
		if (produit == null) {
			cacheChaine.put(chaine, produit = new Chaine(chaine));
		}
		return produit;

	}

	String string;

	int hash, length;

	private Chaine(String s) {
		string = s;
		length = string == null ? 0 : string.length();
		hash = string == null ? 0 : string.toLowerCase().hashCode();
	}

	@Override
	public boolean equals(Object o) {
		return hashCode() == o.hashCode();
	}

	public boolean equals(String o) {
		return string.equalsIgnoreCase(o);
	}

	@Override
	public String toString() {
		return string;
	}

	public int length() {
		return length;
	}

	@Override
	public int hashCode() {
		return hash;
	}

	public char charAt(int i) {
		return string.charAt(i);
	}

}