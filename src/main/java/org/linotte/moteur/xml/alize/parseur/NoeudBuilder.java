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

package org.linotte.moteur.xml.alize.parseur;

import org.linotte.moteur.xml.alize.parseur.a.Noeud;
import org.linotte.moteur.xml.alize.parseur.noeud.*;
import org.w3c.dom.Node;

public final class NoeudBuilder {

	private NoeudBuilder() {
	}

	public static Noeud buildNode(Node node) {
		String nom = node.getNodeName();
		if (nom.equals("pointderetour")) {
			return new NPointDeRetour(node);
		} else if (nom.equals("marqueur")) {
			return new NMarqueur(node);
		} else if (nom.equals("groupe")) {
			return new NGroupe(node);
		} else if (nom.equals("ligne")) {
			return new NLigne(node);
		} else if (nom.equals("fin")) {
			return new NFin(node);
		} else if (nom.equals("choix")) {
			return new NChoix(node);
		} else if (nom.equals("token")) {
			return new NToken(node);
		} else if (nom.equals("valeur")) {
			return new NValeur(node);
		} else if (nom.equals("acteur")) {
			return new NActeur(node);
		} else if (nom.equals("etat")) {
			return new NEtat(node);
		} else if (nom.equals("souffleur")) {
			return new NSouffleur(node);
		} else if (nom.equals("sousparagraphe")) {
			return new NSousParagraphe(node);
		}
		return new NNoeud(node);
	}
}
