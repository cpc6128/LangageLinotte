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

import org.linotte.frame.coloration.StyleItem;
import org.linotte.frame.coloration.StyleItem.STYLE;
import org.linotte.moteur.exception.Constantes;
import org.linotte.moteur.exception.FinException;
import org.linotte.moteur.exception.SyntaxeException;
import org.linotte.moteur.outils.Chaine;
import org.linotte.moteur.xml.alize.parseur.ParserContext;
import org.linotte.moteur.xml.alize.parseur.ParserContext.MODE;
import org.linotte.moteur.xml.alize.parseur.a.NFormat;
import org.linotte.moteur.xml.alize.parseur.a.Noeud;
import org.linotte.moteur.xml.analyse.ItemXML;
import org.linotte.moteur.xml.analyse.Synonyme;
import org.linotte.moteur.xml.exception.NouvelleLigne;
import org.w3c.dom.Node;

import java.util.List;

public class NValeur extends NFormat {

	public NValeur(Node n) {
		super(n);
	}

	public NValeur(NValeur n) {
		super(n);
	}

	@Override
	public Noeud cloner() {
		return new NValeur(this);
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean parse(ParserContext parserContext) throws Exception {
		String result = null;

		int debut_style = parserContext.lexer.getPosition();
		int fin_style = debut_style;

		switch (getValeurType()) {
		case RESTE_LIGNE:
			result = parserContext.lexer.resteLigne();
			fin_style = parserContext.lexer.getPositionAvantCommentaire();// decoupe.getPosition();
			((List<String>) parserContext.phrase.peek()).add("valeur=" + result);
			break;
		case MOT:
			boolean boucle = true;
			while (boucle)
				try {
					result = parserContext.lexer.motSuivant();
					boucle = false;
				} catch (NouvelleLigne e) {
				}
			fin_style = parserContext.lexer.getPosition();
			((List<String>) parserContext.phrase.peek()).add("valeur=" + result);
			break;
		case JUSQUAUTOKEN:
			StringBuilder valeur = new StringBuilder();
			Chaine token = getValeur();
			Chaine synonyme;
			String temp = parserContext.lexer.motSuivant();
			debut_style = parserContext.lexer.getLastPosition();
			try {
				while ((synonyme = Synonyme.equals(token, temp)) == null) {
					valeur.append(parserContext.lexer.getMot()).append(" ");
					fin_style = parserContext.lexer.getPosition();
					temp = parserContext.lexer.motSuivant();
				}
			} catch (FinException e) {
				throw new SyntaxeException(Constantes.TOKEN_ABSENT_OBLIGATOIRE, valeur.toString(),// token,
						debut_style);
			}
			if (parserContext.mode == MODE.COLORATION) {
				((List<StyleItem>) parserContext.styles.peek()).add(new StyleItem(STYLE.TOKEN, parserContext.lexer.getLastPosition(), synonyme.length()));
			}
			result = valeur.toString().trim();
			((List<String>) parserContext.phrase.peek()).add("valeur=" + result);
			((List<String>) parserContext.phrase.peek()).add("token=" + token.toString());
		}

		if (isObligatoire() && result.length() == 0) {
			// La valeur est obligatoire !
			throw new SyntaxeException(Constantes.TOKEN_ABSENT_OBLIGATOIRE, debut_style);
		}

		((List<ItemXML>) parserContext.valeurs.peek()).add(ItemXML.factory(result, ItemXML.VALEUR));

		if (parserContext.mode == MODE.COLORATION) {
			if ("livre".equals(getAttribut("style"))) {
				((List<StyleItem>) parserContext.styles.peek()).add(new StyleItem(STYLE.LIVRE, debut_style, fin_style - debut_style));
			} else if ("structure".equals(getAttribut("style")))
				((List<StyleItem>) parserContext.styles.peek()).add(new StyleItem(STYLE.STRUCTURE, debut_style, fin_style - debut_style));
			else if ("paragraphe".equals(getAttribut("style"))) {
				((List<StyleItem>) parserContext.styles.peek()).add(new StyleItem(STYLE.CHAINE, debut_style, fin_style - debut_style - 1, result));
			} else if ("tests".equals(getAttribut("style")))
				((List<StyleItem>) parserContext.styles.peek()).add(new StyleItem(STYLE.STRING, debut_style, fin_style - debut_style));
			else if (getAttribut("style") != null) {
				String nomCourt = result;
				String prototype = null;
				if (result.indexOf(NMarqueur.IDENTIFIANT_DE) != -1) {
					int pos = result.indexOf(NMarqueur.IDENTIFIANT_DE);
					prototype = nomCourt.substring(pos + NMarqueur.IDENTIFIANT_DE.length());
					nomCourt = nomCourt.substring(0, pos);
				}
				if (prototype == null)
					((List<StyleItem>) parserContext.styles.peek()).add(new StyleItem(STYLE.SYSTEME, debut_style, fin_style - debut_style));
				else {
					// Espece
					((List<StyleItem>) parserContext.styles.peek()).add(new StyleItem(STYLE.LIVRE, debut_style, fin_style - debut_style));
					// De
					((List<StyleItem>) parserContext.styles.peek()).add(new StyleItem(STYLE.STRUCTURE, debut_style, fin_style - debut_style
							- prototype.length() - 1));
					// Fonction
					((List<StyleItem>) parserContext.styles.peek()).add(new StyleItem(STYLE.SYSTEME, debut_style, fin_style - debut_style - prototype.length()
							- NMarqueur.IDENTIFIANT_DE.length()));
				}
			} else
				((List<StyleItem>) parserContext.styles.peek()).add(new StyleItem(STYLE.CHAINE, debut_style, fin_style - debut_style));
		}
		return true;
	}

}
