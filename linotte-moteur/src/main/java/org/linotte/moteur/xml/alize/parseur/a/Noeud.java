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

import org.linotte.moteur.xml.alize.parseur.ParserContext;
import org.linotte.moteur.xml.alize.parseur.XMLIterator;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.util.HashMap;
import java.util.Map;

public abstract class Noeud {

	private Node node = null;

	private XMLIterator fils = null;

	private boolean obligatoire = false;

	// Cache des attributs :
	Map<String, String> cacheattributs = null;
	
	// Pour utiliser la syntaxe des conditions sans virgules :
	public boolean hack = false;

	public Noeud(Node p) {
		node = p;
		fils = new XMLIterator(node.getChildNodes());
		initAttributs(node.getAttributes());
		obligatoire = "oui".equals(getAttribut("obligatoire"));
	}

	public Noeud(Noeud n) {
		node = n.node;
		fils = n.fils.cloner();
		obligatoire = n.obligatoire;
		cacheattributs = n.cacheattributs;
		hack = n.hack;
	}

	private void initAttributs(NamedNodeMap attributs) {
		cacheattributs = new HashMap<String, String>();
		int m = attributs.getLength();
		for (int i = 0; i < m; i++) {
			Node tp = attributs.item(i);
			if (tp != null) {
				cacheattributs.put(tp.getNodeName(), tp.getNodeValue());
			}
		}
	}

	public String getNom() {
		return node.getNodeName();
	}

	public XMLIterator getFils() {
		return fils;
	}

	public String getAttribut(String key) {
		return cacheattributs.get(key);
	}

	public boolean isObligatoire() {
		return obligatoire;
	}

	public abstract Noeud cloner();

	public void insererAvant(Noeud r, int i) {
		getFils().insererAvant(r, i);
	}

	public void supprimer(int i) {
		getFils().supprimer(i);
	}

	public void recommencerFils() {
		fils.recommencer();
	}
	
	public void copieAttributs(Noeud original) {
		cacheattributs.putAll(original.cacheattributs);
	}

	public abstract boolean parse(ParserContext parserContext) throws Exception;

}