/***********************************************************************
 * Linotte                                                             *
 * Version release date : July 30, 2008                             *
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

package org.linotte.frame.latoile;

import java.awt.*;
import java.util.List;
import java.util.*;

/**
 * TODO La gestion des couleurs doit être remontée au niveau de la langue
 * @author CPC
 *
 */
public final class Couleur {

	private static Map<String, Color> couleurs = new HashMap<String, Color>();
	// Deux caches pour que les couleurs par nom soient plus rapide
	private static Map<String, Color> cache_couleurs = new HashMap<String, Color>();

	private Couleur() {
		//nop
	}

	public static void ajouterCouleur(String nom, Color color) {
		couleurs.put(nom, color);
	}

	public static Color retourneCouleur(String nom) {
		nom = nom.toLowerCase();
		Color c = couleurs.get(nom);
		if (c == null) {
			return traduitCouleur(nom);
		} else
			return c;
	}

	private static Color traduitCouleur(String nom) {
		Color color = cache_couleurs.get(nom);
		if (color != null) {
			return color;
		} else {
			int l = nom.length();
			int r = 0, v = 0, b = 0;
			int m = 0;
			int t;
			int c = 0;
			try {
				for (int i = l - 1; i >= 0; i--) {
					t = nom.charAt(i) - 48;
					if (t != -16) {
						switch (c) {
						case 0:
							b = (int) (b + t * Math.pow(10, m));
							break;
						case 1:
							v = (int) (v + t * Math.pow(10, m));
							break;
						case 2:
							r = (int) (r + t * Math.pow(10, m));
							break;
						}
						m++;
					} else {
						m = 0;
						c++;
					}
				}
				color = new Color(r, v, b);
			} catch (Exception e) {
				color = Color.BLACK;
			}
			cache_couleurs.put(nom, color);
			return color;
		}
	}

	public static List<String> retourneCouleurs() {
		List<String> retour = new ArrayList<String>();
		Set<String> set = couleurs.keySet();
		retour.addAll(set);
		Collections.sort(retour);
		return retour;
	}

	/**
	 * Utilisé lors du changement de couleur
	 */
	public static void nettoyageCouleurs() {
		couleurs.clear();
		cache_couleurs.clear();
	}
}
