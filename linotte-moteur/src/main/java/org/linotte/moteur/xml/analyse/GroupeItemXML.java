/***********************************************************************
 * Linotte                                                             *
 * Version release date : September 01, 2006                           *
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

public class GroupeItemXML extends ItemXML {

	public ItemXML[] items;

	public GroupeItemXML(ItemXML[] pitems) {
		super(pitems[0]);
		items = pitems;
	}

	@Override
	public GroupeItemXML clone() {
		ItemXML[] items2 = new ItemXML[items.length];
		for (int i = 0; i < items.length; i++) {
			items2[i] = items[i].clone();
		}
		return new GroupeItemXML(items2);
	}

}
