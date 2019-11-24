/***********************************************************************
 * Linotte                                                             *
 * Version release date : July 22, 2013                                *
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
import java.util.LinkedHashSet;
import java.util.Set;

@SuppressWarnings("serial")
public class LinkedHashMap<K, V> extends HashMap<K, V> {

	private Set<K> mElements = new LinkedHashSet<K>();

	@Override
	public V put(K key, V value) {

		if (!mElements.contains(key)) {
			mElements.add(key);
		}

		return super.put(key, value);

	}

	public Set<K> getElements() {
		return mElements;
	}

}
