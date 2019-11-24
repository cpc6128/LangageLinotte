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

package org.linotte.moteur.xml.alize.parseur.noeud;

import org.linotte.moteur.xml.alize.parseur.ParserContext;
import org.linotte.moteur.xml.alize.parseur.a.Noeud;
import org.w3c.dom.Node;

public class NSouffleur extends Noeud {

	public NSouffleur(Node n) {
		super(n);
	}

	public NSouffleur(NSouffleur n) {
		super(n);
	}

	@Override
	public Noeud cloner() {
		return new NSouffleur(this);
	}

	@Override
	public boolean parse(ParserContext parserContext) throws Exception {
		parserContext.souffleur = getAttribut("valeur").equals("oui");
		if (parserContext.souffleur) {
			// on le garde pour bypasser les souffleurs.
			parserContext.avantSouffleur = parserContext.lastProcessus;
		} else {
			parserContext.lastProcessus = parserContext.avantSouffleur;
		}
		return true;
	}

}
