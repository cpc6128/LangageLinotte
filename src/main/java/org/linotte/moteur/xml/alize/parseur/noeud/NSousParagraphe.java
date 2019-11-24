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

import java.util.HashSet;

import org.linotte.moteur.exception.Constantes;
import org.linotte.moteur.exception.ErreurException;
import org.linotte.moteur.xml.alize.parseur.ParserContext;
import org.linotte.moteur.xml.alize.parseur.ParserContext.Couple;
import org.linotte.moteur.xml.alize.parseur.ParserContext.MODE;
import org.linotte.moteur.xml.alize.parseur.ParserContext.Prototype;
import org.linotte.moteur.xml.alize.parseur.a.Noeud;
import org.w3c.dom.Node;

public class NSousParagraphe extends Noeud {

	public NSousParagraphe(Node n) {
		super(n);
	}

	public NSousParagraphe(NSousParagraphe n) {
		super(n);
	}

	@Override
	public Noeud cloner() {
		return new NSousParagraphe(this);
	}

	@Override
	public boolean parse(ParserContext parserContext) throws Exception {
		if (parserContext.mode == MODE.GENERATION_RUNTIME) {
			String etat = getAttribut("etat");
			if (etat.equals("ouverture")) {
				parserContext.constructionPileSousParagraphes.push(parserContext.lexer.getPosition());
			} else if (etat.equals("fermeture")) {
				if (parserContext.constructionPileSousParagraphes.size() == 0) {
					throw new ErreurException(Constantes.SYNTAXE_SOUS_PARAGRAPHE);
				}
				parserContext.constructionPileSousParagraphes.pop();
			}
		} else if (parserContext.mode == MODE.COLORATION) {
			String etat = getAttribut("etat");
			if (etat.equals("ouverture")) {
				// Gestion de la pile de contexte des variables :
				parserContext.constructionPileContexteVariables.push(new HashSet<Prototype>());

				// Gestion de la mise en valeur des paragraphes :
				Couple couple = new Couple();
				parserContext.constructionPileSousParagraphe.push(couple);
				couple.A = parserContext.lexer.getLastPosition();

			} else if (etat.equals("fermeture")) {
				// Gestion de la pile de contexte des variables :
				if (!parserContext.constructionPileContexteVariables.isEmpty()) {
					parserContext.constructionPileContexteVariables.pop();

					// Gestion de la mise en valeur des paragraphes :
					Couple couple = parserContext.constructionPileSousParagraphe.pop();
					couple.B = parserContext.lexer.getPosition();
					parserContext.constructionPileSousParagrapheSorted.add(couple);
				}
			}
		} else if (parserContext.mode == MODE.FORMATAGE) {
			String etat = getAttribut("etat");
			if (etat.equals("ouverture")) {
				parserContext.valeur++;
				parserContext.saute = true;
			} else if (etat.equals("fermeture")) {
				parserContext.valeur--;
			}
		}

		return true;
	}

}
