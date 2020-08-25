/***********************************************************************
 * Linotte                                                             *
 * Version release date : April 1, 2011                                *
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

package org.linotte.moteur.xml.outils;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("unchecked")
public class FastEnumMap<K extends Enum<K>, V> implements Map<K, V> {

	V[] values;

	public FastEnumMap(Class<K> classe) {
		values = (V[]) new Object[classe.getEnumConstants().length];
	}

	@Override
	public V get(Object key) {
		return (V) values[((K) key).ordinal()];
	}

	@Override
	public V put(K key, V value) {
		return (V) (values[key.ordinal()] = value);
	}

	@Override
	public boolean containsKey(Object key) {
		return values[((K) key).ordinal()] != null;
	}

	/**
	 * Méthodes non utilisées :
	 */

	@Override
	@Deprecated
	public int size() {
		return 0;
	}

	@Override
	@Deprecated
	public boolean isEmpty() {
		return false;
	}

	@Override
	@Deprecated
	public boolean containsValue(Object value) {
		return false;
	}

	@Override
	@Deprecated
	public V remove(Object key) {
		return null;
	}

	@Override
	@Deprecated
	public void putAll(Map<? extends K, ? extends V> m) {
	}

	@Override
	@Deprecated
	public void clear() {
	}

	@Override
	@Deprecated
	public Set<K> keySet() {
		return null;
	}

	@Override
	@Deprecated
	public Collection<V> values() {
		return null;
	}

	@Override
	@Deprecated
	public Set<java.util.Map.Entry<K, V>> entrySet() {
		return null;
	}
}
