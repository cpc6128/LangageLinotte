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
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class XMLIterator implements Iterator<Noeud>, Iterable<Noeud> {

	private int position = 0;

	private List<Noeud> fils = null;

	private boolean recommencer = false;

	// Cloneage :
	XMLIterator iterator_clonage = null;

	public XMLIterator(NodeList childNodes) {
		init(childNodes);
	}

	private XMLIterator(XMLIterator i) {		
		iterator_clonage = i;
	}

	private void copierValeurs(XMLIterator i) {
		//		synchronized (i) {
		position = 0;
		recommencer = false;
		fils = new ArrayList<Noeud>();
		Iterator<Noeud> i2 = i.fils.iterator();
		while (i2.hasNext())
			fils.add(((Noeud) i2.next()).cloner());
		iterator_clonage = null;
		//		}
	}

	private void init(NodeList liste) {
		int fin = liste.getLength(), tmp = 0;
		fils = new ArrayList<Noeud>();
		while (tmp < fin) {
			Node n = liste.item(tmp);
			if (n != null && !n.getNodeName().startsWith("#")) {
				fils.add(NoeudBuilder.buildNode(n));
			}
			tmp++;
		}
		return;
	}

	/**
	 * Vérifie s'il y a encore des éléments et initialise s'il le faut
	 * 
	 * @return
	 */
	public boolean hasNext() {
		if (iterator_clonage != null)
			copierValeurs(iterator_clonage);
		if (recommencer) {
			position = 0;
			Iterator<Noeud> i = fils.iterator();
			while (i.hasNext()) {
				i.next().recommencerFils();
			}
			recommencer = false;
		}
		return position < fils.size();
	}

	public Noeud next() {
		return (Noeud) fils.get(position++);
	}

	public void recommencer() {
		recommencer = true;
	}

	public XMLIterator cloner() {
		if (iterator_clonage == null)
			return new XMLIterator(this);
		else
			return new XMLIterator(iterator_clonage);
	}

	public void insererAvant(Noeud r, int pos) {
		if (iterator_clonage != null)
			copierValeurs(iterator_clonage);
		fils.add(pos, r);
		position++;
	}

	public void supprimer(int pos) {
		fils.remove(pos);
		position--;
	}

	public int getPosition() {
		return position;
	}

	public void remove() {
	}

	public Iterator<Noeud> iterator() {
		return this;
	}

}