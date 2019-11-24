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

import java.awt.Color;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.linotte.frame.coloration.StyleItem;
import org.linotte.frame.coloration.StyleItem.STYLE;
import org.linotte.frame.coloration.StyleMathematiques;
import org.linotte.moteur.entites.Livre;
import org.linotte.moteur.entites.Prototype;
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

public class NActeur extends NFormat {

	public NActeur(Node n) {
		super(n);
	}

	public NActeur(NActeur n) {
		super(n);
	}

	@Override
	public Noeud cloner() {
		return new NActeur(this);
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean parse(ParserContext pc) throws Exception {
		boolean variable_locale = false;
		boolean doublure = false;
		int debut_style = pc.lexer.getPosition();
		int fin_style = pc.lexer.getPosition();
		String result = null;

		switch (getValeurType()) {
		case JUSQUAUTOKEN: {
			StringBuilder acteur = new StringBuilder();
			Chaine synonyme;
			Chaine token = getValeur();
			try {
				// article a vérifier ?
				boolean premier = true;
				while ((synonyme = Synonyme.equals(token, pc.lexer.motSuivant())) == null) {
					if (pc.lexer.getMot().equals("*")) {
						if ( pc.linotte.getLangage().isForceParametreEnligne()) {
							throw new SyntaxeException(Constantes.ERREUR_SYNTAXE_DOUBLE_INTERDIT, acteur.toString(),// token,
									debut_style);
						}
						if (pc.mode == MODE.COLORATION && premier) {
							debut_style = pc.lexer.getPosition();
							((List<StyleItem>) pc.styles.peek()).add(new StyleItem(STYLE.DOUBLURE, pc.lexer.getLastPosition(), 1));
							doublure = true;
						}
					} else if (pc.lexer.getMot().equals("§")) {
						if (pc.mode == MODE.COLORATION && premier) {
							debut_style = pc.lexer.getPosition();
							((List<StyleItem>) pc.styles.peek()).add(new StyleItem(STYLE.DOUBLURE, pc.lexer.getLastPosition(), 1));
							variable_locale = true;
						}
					}
					acteur.append(pc.lexer.getMot()).append(" ");
					fin_style = pc.lexer.getPosition();
					premier = false;
				}
				if (acteur.indexOf(",") != -1 && acteur.indexOf("\"") == -1 && acteur.indexOf("{") == -1 && acteur.indexOf("[") == -1 && acteur.indexOf("(") == -1) {
					/*
					 * Linotte 2.1.1 A est un X, T vaut V passe ici : A vaut X
					 * On ne vient ici que si la virgule n'est pas présente dans
					 * le nom de l'acteur
					 * https://code.google.com/p/langagelinotte/issues/detail?id=97
					 */
					throw new SyntaxeException(Constantes.TOKEN_ABSENT_OBLIGATOIRE, acteur.toString(),// token,
							debut_style);
				}

				if (!doublure && getAttribut("doublure") != null) {
					doublure = true;
				}

				if (doublure && pc.mode == MODE.COLORATION && acteur.length() != 0) {
					ajouterDoublure(pc, acteur);
				}
			} catch (FinException e) {
				throw new SyntaxeException(Constantes.TOKEN_ABSENT_OBLIGATOIRE, acteur.toString(),// token,
						debut_style);
			}
			if (pc.mode == MODE.COLORATION) {
				// Pour le token
				((List<StyleItem>) pc.styles.peek()).add(new StyleItem(STYLE.TOKEN, pc.lexer.getLastPosition(), synonyme.length()));
			}
			result = acteur.toString().trim();
			// Amélioration de l'affichage des erreurs :
			((List<String>) pc.phrase.peek()).add("acteur=" + result);
			((List<String>) pc.phrase.peek()).add("token=" + token.toString());

			// pour la complétion :
			if (pc.mode == MODE.COLORATION && !pc.motsLivre.contains(result.toLowerCase()) && !result.contains("&")) {
				String t = result.toLowerCase();
				if (t.startsWith("§") || t.startsWith("*"))
					t = t.substring(1).trim();
				pc.motsLivre.add(t);
			}
		}
			break;
		case RESTE_LIGNE: {
			StringBuilder acteur = new StringBuilder();
			try {
				while (true) {
					acteur.append(pc.lexer.motSuivant()).append(" ");
				}
			} catch (FinException e) {
			}
			fin_style = pc.lexer.getPositionAvantCommentaire();
			result = acteur.toString().trim();
			// Amélioration de l'affichage des erreurs :
			((List<String>) pc.phrase.peek()).add("acteur=" + result);
			if (getAttribut("doublure") != null && pc.mode == MODE.COLORATION && acteur.length() != 0) {
				ajouterDoublure(pc, acteur);
			}
			break;
		}
		case MOT: {
			boolean boucle = true;
			while (boucle)
				try {
					result = pc.lexer.motSuivant();
					boucle = false;
				} catch (NouvelleLigne e) {
				}
			fin_style = pc.lexer.getPosition();
			// Amélioration de l'affichage des erreurs :
			((List<String>) pc.phrase.peek()).add("acteur=" + result);
		}
		}

		NGroupe temp_group = (NGroupe) pc.groupes.peek();
		if (temp_group != null && !temp_group.isControle()) {
			temp_group.setControle(true);
		}

		// Gestion des caractères "fin de ligne"
		// Ces caractères sont interdits dans le nom des acteurs !
		// version 0.7.5
		Set<String> fdl = pc.linotte.getGrammaire().getFindeligne();
		if (fdl.contains(result)) {
			throw new SyntaxeException(Constantes.TOKEN_ABSENT_OBLIGATOIRE, result, debut_style);
		}

		if (isObligatoire() && result.length() == 0) {
			// L'acteur est obligatoire !
			throw new SyntaxeException(Constantes.TOKEN_ABSENT_OBLIGATOIRE, debut_style);
		}

		String annotation = getAttribut("annotation");
		if (annotation != null) {
			((List<String>) pc.annotations.peek()).add(annotation);
			if (annotation.equals("évoquer")) {
				// Limite du parseur... conflit entre le verbe "évoquer" et la section rôles dans un sous paragraphe !
				if (result.indexOf("valant") != -1)
					throw new SyntaxeException(Constantes.TOKEN_ABSENT_OBLIGATOIRE, debut_style);
			}
		}
		((List<ItemXML>) pc.valeurs.peek()).add(ItemXML.factory(result, ItemXML.ACTEUR));

		if (pc.mode == MODE.COLORATION) {
			// Style :
			if ("livre".equals(getAttribut("style"))) {
				// pour la vérification des attributs des espèces :
				if ("prototype".equals(getAttribut("completion"))) {
					pc.prototype = result.toLowerCase();
				}
				Color color = null;
				if ("heritage".equals(getAttribut("completion")) || "prototype".equals(getAttribut("completion"))) {
					if (!validerPrototype(pc, result.toLowerCase()))
						color = Color.RED;
				}
				((List<StyleItem>) pc.styles.peek()).add(new StyleItem(STYLE.LIVRE, debut_style, fin_style - debut_style, color));
			} else if ("sousparagraphe".equals(getAttribut("style"))) // 
				((List<StyleItem>) pc.styles.peek()).add(new StyleItem(STYLE.SOUS_PARAGRAPHE, debut_style, fin_style - debut_style - 1));
			else if (variable_locale) {
				((List<StyleItem>) pc.styles.peek()).add(new StyleItem(STYLE.VARIABLE_LOCALE, debut_style, fin_style - debut_style - 1));
			} else if (doublure) {
				((List<StyleItem>) pc.styles.peek()).add(new StyleItem(STYLE.VARIABLE_DOUBLURE, debut_style, fin_style - debut_style - 1));
			} else {
				try {
					if (Livre.isActeurSystem(Chaine.produire(result))) {
						((List<StyleItem>) pc.styles.peek()).add(new StyleItem(STYLE.VARIABLE_SYSTEME, debut_style, fin_style - debut_style));
					} else
						// Rustine pour faire fonctionne les couleurs
						// correctement dans l'Atelier :
						// On informe qu c'est un attribut d'une espece :
						pc.prototype_attribut = "nomatribut".equals(getAttribut("completion"));
					((List<StyleMathematiques>) pc.styles_formules.peek()).add(new StyleMathematiques(pc.lexer.subString(debut_style, fin_style),
							((List<StyleItem>) pc.styles.peek()), debut_style, getAttribut("surligne") == null ? pc : null, pc.prototype_attribut));
					pc.prototype_attribut = false;
				} catch (Exception e) {
					throw new SyntaxeException(Constantes.SYNTAXE_MINI_FORMULE, pc.lexer.getLastPosition());
				}
			}
		}

		return true;

	}

	private void ajouterDoublure(ParserContext pc, StringBuilder acteur) {
		// TODO à supprimer pour ne pas avoir un traitement doublé :
		if (getAttribut("doublure") != null)
			pc.sommaire.addDoublure(acteur.toString().trim());
		else
			pc.sommaire.addDoublure(acteur.toString().substring(2).trim());
		// FIN TODO
		String clef;
		if (pc.methodeFonctionnellePrototype != null)
			clef = pc.methodeFonctionnellePrototype + pc.dernierParagraphe;
		else
			clef = pc.dernierParagraphe;
		Set<String> methodes = pc.methodes_doublures.get(clef);
		if (methodes == null) {
			methodes = new HashSet<String>();
			pc.methodes_doublures.put(clef, methodes);
		}
		if (getAttribut("doublure") != null)
			methodes.add(acteur.toString().trim());
		else
			methodes.add(acteur.toString().substring(2).trim());

	}

	private static boolean validerPrototype(ParserContext parserContext, String ptype) {
		for (Prototype i : parserContext.linotte.especeModeleMap) {
			if (i.getType().equalsIgnoreCase(ptype)) {
				return true;
			}
		}
		// On regarde au niveau des
		// prototypes définis dans le
		// livre :
		return parserContext.types_prototypes.get(ptype) != null;
	}

}