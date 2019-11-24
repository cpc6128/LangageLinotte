/***********************************************************************
 * Linotte                                                             *
 * Version release date : Febrary 23 2015                              *
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

package org.linotte.moteur.outils;

import java.util.HashMap;
import java.util.Map;

import org.linotte.moteur.xml.Linotte;
import org.linotte.moteur.xml.analyse.multilangage.Langage;
import org.linotte.moteur.xml.api.IHM;
import org.linotte.moteur.xml.api.Librairie;

public class LangageSwitch {

	private Map<Langage, Linotte> cacheMoteurs = new HashMap<Langage, Linotte>(Langage.values().length);
	private Librairie<?> lib;
	private IHM ihm;

	public LangageSwitch(Librairie<?> plib, IHM pihm) {
		lib = plib;
		ihm = pihm;
	}

	public Linotte selectionLangage(Langage langage) {
		synchronized (cacheMoteurs) {
			if (cacheMoteurs.containsKey(langage)) {
				return cacheMoteurs.get(langage);
			} else {
				Linotte moteur = new Linotte(lib, ihm, langage);
				cacheMoteurs.put(langage, moteur);
				return moteur;
			}
		}
	}

}
