/***********************************************************************
 * Linotte                                                             *
 * Version release date : October 26, 2005                             *
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

package org.linotte.moteur.xml.analyse;

import org.linotte.moteur.outils.Chaine;

public class Article {

	private static Chaine[] articles = new Chaine[100];
	private static int taille = 0;
	private static int max_taille = 0;

	public static void addArticle(Chaine article) {
		articles[taille] = article;
		taille++;
		int t = article.length();
		if (t > max_taille)
			max_taille = t;
	}

	public static boolean isArticle(String article) {
		if (article.length() <= max_taille) {
			for (int i = 0; i < taille; i++)
				if (articles[i].equals(article))
					return true;
		}
		return false;
	}
}
