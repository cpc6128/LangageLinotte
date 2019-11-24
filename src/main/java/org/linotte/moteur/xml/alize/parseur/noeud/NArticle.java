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

import java.util.List;

import org.linotte.frame.coloration.StyleItem;
import org.linotte.frame.coloration.StyleItem.STYLE;
import org.linotte.moteur.xml.alize.parseur.ParserContext;
import org.linotte.moteur.xml.alize.parseur.ParserContext.MODE;
import org.linotte.moteur.xml.alize.parseur.a.Noeud;
import org.w3c.dom.Node;

public class NArticle extends Noeud {

	public NArticle(Node n) {
		super(n);
	}

	public NArticle(NArticle n) {
		super(n);
	}

	@Override
	public Noeud cloner() {
		return new NArticle(this);
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean parse(ParserContext pc) throws Exception {
		boolean flag = pc.lexer.isFaireExeptionFinDeLigne();
		pc.lexer.motSuivant();
		if (!org.linotte.moteur.xml.analyse.Article.isArticle(pc.lexer.getMot())) {
			pc.lexer.retour();
			pc.lexer.setFaireExeptionFinDeLigne(flag);
		} else {
			if (pc.mode == MODE.COLORATION) {
				int size = pc.lexer.getMot().length();
				((List<StyleItem>) pc.styles.peek()).add(new StyleItem(STYLE.ARTICLE, pc.lexer.getLastPosition(), size));
			}
			((List<String>) pc.phrase.peek()).add("article=" + pc.lexer.getMot());
		}
		return true;
	}

}
