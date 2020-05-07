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

package org.linotte.moteur.xml.alize.parseur.a;

import org.linotte.moteur.outils.Chaine;
import org.w3c.dom.Node;

public abstract class NExpression extends Noeud {

	private Chaine valeur;
	
	public NExpression(Node node) {
		super(node);
		valeur = Chaine.produire(node.getTextContent().trim());
	}

	public NExpression(NExpression n) {
		super(n);
		valeur = n.valeur;
	}

	public Chaine getValeur() {
		return valeur;
	}

	public void setValeur(Chaine chaine) {
		valeur = chaine;
	}

}