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

package org.linotte.moteur.xml.outils;

import org.linotte.moteur.xml.analyse.ItemXML;

import java.util.Iterator;
import java.util.List;

public class Cloneur {

	@SuppressWarnings("unchecked")
	public static List clone(List l) {
		List nouveau = null;
		try {
			nouveau = l.getClass().newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		Iterator i = l.iterator();
		while (i.hasNext()) {
			Object o = i.next();
			if (o instanceof ItemXML)
				nouveau.add(((ItemXML) o).clone());
			else if (o instanceof List)
				nouveau.add(clone((List) o));
			else
				nouveau.add(o);
		}
		return nouveau;
	}
}
