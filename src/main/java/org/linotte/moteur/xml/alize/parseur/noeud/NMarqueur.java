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
import java.util.List;

import org.linotte.moteur.exception.Constantes;
import org.linotte.moteur.exception.SyntaxeException;
import org.linotte.moteur.xml.alize.parseur.ParserContext;
import org.linotte.moteur.xml.alize.parseur.ParserContext.MODE;
import org.linotte.moteur.xml.alize.parseur.ParserContext.Prototype;
import org.linotte.moteur.xml.alize.parseur.a.Noeud;
import org.linotte.moteur.xml.analyse.ItemXML;
import org.w3c.dom.Node;

public class NMarqueur extends Noeud {

	public static final String IDENTIFIANT_DE = " de ";

	public NMarqueur(Node n) {
		super(n);
	}

	public NMarqueur(NMarqueur n) {
		super(n);
	}

	@Override
	public Noeud cloner() {
		return new NMarqueur(this);
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean parse(ParserContext parserContext) throws Exception {
		if ("livre".equals(getAttribut("type"))) {
			for (Noeud fils : getFils()) {
				fils.parse(parserContext);
			}
			if (parserContext.mode == MODE.COLORATION) {
				if (((List<?>) parserContext.valeurs.peek()).size() > 0) {
					String titre = ((List<?>) parserContext.valeurs.peek()).get(0).toString();
					try {
						parserContext.sommaire.setTitre(titre);
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					parserContext.sommaire.setTitre("£");
					parserContext.piedDeMouche = true;
				}
			} else {
				if (((List<?>) parserContext.valeurs.peek()).size() == 0) {
					((List<ItemXML>) parserContext.valeurs.peek()).add(ItemXML.factory("£", ItemXML.VALEUR));
					parserContext.piedDeMouche = true;
				}
			}
		} else {
			int position_a_retenir = parserContext.lexer.getPosition();
			for (Noeud fils : getFils()) {
				fils.parse(parserContext);
			}
			parserContext.nbParagraphes++;
			String nomCourt = null;
			String prototype = null;
			if (((List<?>) parserContext.valeurs.peek()).size() > 0) {
				if (parserContext.piedDeMouche) {
					throw new SyntaxeException(Constantes.STRUCTURE_PARAGRAPHE, parserContext.lexer.getPosition());
				}
				String nomParagraphe = ((List<?>) parserContext.valeurs.peek()).get(0).toString();
				nomCourt = nomParagraphe;
				prototype = null;
				parserContext.methodeFonctionnellePrototype = null;
				if (nomParagraphe.indexOf(IDENTIFIANT_DE) != -1) {
					int pos = nomParagraphe.indexOf(IDENTIFIANT_DE);
					prototype = nomCourt.substring(pos + IDENTIFIANT_DE.length()).toLowerCase();
					nomCourt = nomCourt.substring(0, pos);
					parserContext.methodeFonctionnellePrototype = prototype;
				}
				parserContext.dernierParagraphe = nomCourt;
			} else {
				parserContext.dernierParagraphe = "§";
				nomCourt = "§";
			}
			if (parserContext.mode == MODE.COLORATION) {
				try {
					// Gestion de la pile de contexte des variables :
					if (parserContext.constructionPileContexteVariables.isEmpty()) {
						parserContext.constructionPileContexteVariables.push(new HashSet<Prototype>());
					} else {
						parserContext.constructionPileContexteVariables.pop();
						parserContext.constructionPileContexteVariables.push(new HashSet<Prototype>());
					}

					if (prototype == null)
						parserContext.sommaire.ajouterParagraphe(nomCourt, position_a_retenir);
					else {
						// Prototype
						parserContext.sommaire.ajouterParagraphe(prototype, nomCourt, "[" + prototype + "] " + nomCourt, position_a_retenir);
						if (parserContext.types_prototypes.get(prototype) != null) {
							parserContext.types_prototypes.get(prototype).add(nomCourt);
							parserContext.prototypes.add(new Prototype(prototype, "moi", position_a_retenir));
							if (!parserContext.constructionPileContexteVariables.isEmpty())
								parserContext.constructionPileContexteVariables.peek().add(new Prototype(prototype, "moi", position_a_retenir));
						}
					}
					// pour la complétion :
					if (!parserContext.motsLivre.contains(nomCourt.toLowerCase()))
						parserContext.motsLivre.add(nomCourt.toLowerCase());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return true;
	}

}
