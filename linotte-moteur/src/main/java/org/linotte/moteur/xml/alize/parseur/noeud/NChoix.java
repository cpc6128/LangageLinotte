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

import org.linotte.moteur.exception.SyntaxeException;
import org.linotte.moteur.xml.alize.parseur.ParserContext;
import org.linotte.moteur.xml.alize.parseur.ParserContext.MODE;
import org.linotte.moteur.xml.alize.parseur.a.Noeud;
import org.linotte.moteur.xml.exception.XMLGroupeException;
import org.w3c.dom.Node;

public class NChoix extends Noeud {

	public NChoix(Node n) {
		super(n);
	}

	public NChoix(NChoix n) {
		super(n);
	}

	@Override
	public Noeud cloner() {
		return new NChoix(this);
	}

	@Override
	public boolean parse(ParserContext pc) throws Exception {
		boolean multiple_attribut = getAttribut("multiple") != null;
		boolean multiple = true; // Il faut entrer au moins une fois
		boolean valide = false;

		while (multiple) {
			multiple = multiple_attribut;
			int position_courante = pc.lexer.getPosition();
			boolean phraseAnalysee = true;
			SyntaxeException erreur1 = null;
			XMLGroupeException erreur2 = null;
			if (multiple) {
				getFils().recommencer();
			}
			while (phraseAnalysee && getFils().hasNext()) {
				boolean garder_style = false;
				try {
					pc.lexer.setPosition(position_courante);
					for (Noeud fils : getFils()) {
						if (fils.parse(pc)) {
							valide = true;
							garder_style = true;
							break;
						}
					}
					phraseAnalysee = false;
				} catch (XMLGroupeException e) {
					pc.lexer.setPosition(position_courante);
					NGroupe temp = (NGroupe) pc.groupes.peek();
					if (temp.isObligatoire()) {
						erreur2 = e;
					}
				} catch (SyntaxeException e) {
					pc.lexer.setPosition(position_courante);
					erreur1 = e;
					// Ce n'est pas le bon choix , erreur de syntaxe!
				}
				if (pc.mode == MODE.COLORATION) {
					if (!garder_style && !pc.styles.isEmpty()) {
						// Pas de phrase analysée, style invalide :
						//System.out.println(((List<StyleItem>) pc.styles.peek()).size());
						///((List<StyleItem>) pc.styles.peek()).clear();
						//((List<StyleItem>) pc.styles_formules.peek()).clear();
					}
				}
			}
			if (phraseAnalysee && !valide) {
				pc.lexer.setPosition(position_courante);
				// Aucun choix valide
				if (erreur1 != null) {
					pc.setDerniereErreur(erreur1);
					throw erreur1;
				} else {
					throw erreur2;
				}
			} else {
				if (position_courante == pc.lexer.getPosition()) {
					// On avance plus !
					multiple = false;
				}

			}
		}
		return true;
	}

}
