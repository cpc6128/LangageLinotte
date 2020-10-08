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
import org.linotte.moteur.exception.SyntaxeException;
import org.linotte.moteur.outils.Chaine;
import org.linotte.moteur.xml.actions.StructureGlobaleAction;
import org.linotte.moteur.xml.alize.kernel.Action;
import org.linotte.moteur.xml.alize.parseur.ParserContext;
import org.linotte.moteur.xml.alize.parseur.ParserContext.MODE;
import org.linotte.moteur.xml.alize.parseur.a.NExpression;
import org.linotte.moteur.xml.alize.parseur.a.Noeud;
import org.linotte.moteur.xml.analyse.Synonyme;
import org.linotte.moteur.xml.exception.NouvelleLigne;
import org.linotte.moteur.xml.exception.XMLGroupeException;
import org.w3c.dom.Node;

import java.util.List;

public class NToken extends NExpression {

	public NToken(Node n) {
		super(n);
	}

	public NToken(NToken n) {
		super(n);
	}

	@Override
	public Noeud cloner() {
		return new NToken(this);
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean parse(ParserContext pc) throws Exception {

		if (!hack) {
			boolean flag = pc.lexer.isFaireExeptionFinDeLigne();
			boolean boucle = true;
			while (boucle)
				try {
					pc.lexer.motSuivant();
					boucle = false;
				} catch (NouvelleLigne e) {
				}
			int debut_style = pc.lexer.getLastPosition();

			Chaine token = getValeur();
			if (isObligatoire()) {
				Chaine synonyme;
				if ((synonyme = Synonyme.equals(token, pc.lexer.getMot())) == null) {
					// token non valide, node obligatoire
					throw new SyntaxeException(Constantes.TOKEN_ABSENT_OBLIGATOIRE, pc.lexer.getMot(), debut_style);
				}
				if (getAttribut("annotation") != null) {
					((List<String>) pc.annotations.peek()).add(getAttribut("annotation"));
				}
				NGroupe temp = (NGroupe) pc.groupes.peek();
				if (temp != null && !temp.isControle()) {
					temp.setControle(true);
				}
				// Style :
				if (pc.mode == MODE.COLORATION) {
					int size = synonyme.length() + 1;
					//					System.out.println("style : " + token.toString() + " " + debut_style + ":" + size);
					if ("livre".equals(getAttribut("style")))
						((List<StyleItem>) pc.styles.peek()).add(new StyleItem(STYLE.LIVRE, debut_style, size));
					else if ("sousparagraphe".equals(getAttribut("style")))
						((List<StyleItem>) pc.styles.peek()).add(new StyleItem(STYLE.SOUS_PARAGRAPHE, debut_style, size));
					else if ("structure".equals(getAttribut("style")))
						((List<StyleItem>) pc.styles.peek()).add(new StyleItem(STYLE.STRUCTURE, debut_style, size));
					else if (getAttribut("style") != null)
						((List<StyleItem>) pc.styles.peek()).add(new StyleItem(STYLE.SYSTEME, debut_style, size));
					else
						((List<StyleItem>) pc.styles.peek()).add(new StyleItem(STYLE.TOKEN, debut_style, size));
				}
				// Amélioration de l'affichage des erreurs :
				((List<String>) pc.phrase.peek()).add("token=" + token.toString());

			} else {
				NGroupe temp = (NGroupe) pc.groupes.peek();
				Chaine synonyme;
				boolean egalite = (synonyme = Synonyme.equals(token, pc.lexer.getMot())) != null;
				if (temp != null && !egalite && !temp.isControle()) {
					pc.lexer.retour();
					pc.lexer.setFaireExeptionFinDeLigne(flag);
					// token non valide, groupe non obligatoire
					throw new XMLGroupeException();
				}
				if (!egalite) {
					pc.lexer.retour();
					pc.lexer.setFaireExeptionFinDeLigne(flag);
				} else if (getAttribut("annotation") != null) {
					((List<String>) pc.annotations.peek()).add(getAttribut("annotation"));
				}
				// Style :
				if (pc.mode == MODE.COLORATION && synonyme != null) {
					int size = synonyme.length();
					((List<StyleItem>) pc.styles.peek()).add(new StyleItem(STYLE.TOKEN, debut_style, size));
				}
				// Amélioration de l'affichage des erreurs :
				((List<String>) pc.phrase.peek()).add("token=" + token.toString());

			}
		} else {
			// Objet déjà parsé suite au hack :
			if (getAttribut("annotation") != null) {
				((List<String>) pc.annotations.peek()).add(getAttribut("annotation"));
			}
			NGroupe temp = (NGroupe) pc.groupes.peek();
			if (temp != null && !temp.isControle()) {
				temp.setControle(true);
			}

			// Style pour palier le hack:
			if (pc.mode == MODE.COLORATION) {
				if ("sousparagraphe".equals(getAttribut("style")))
					((List<StyleItem>) pc.styles.peek()).add(new StyleItem(STYLE.SOUS_PARAGRAPHE, pc.lexer.getLastPosition(), 3));
			}

		}

		if (pc.mode == MODE.FORMATAGE) {
			if (("structure".equals(getAttribut("style")))) {
				// c
				pc.formatageActeursgloblauxLinotte3 = false;
			}
		}
		return true;
	}

}